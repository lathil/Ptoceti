package com.ptoceti.osgi.pi.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Pi
 * FILENAME : WireHandler.java
 * 
 * This file is part of the Ptoceti project. More information about
 * this project can be found here: http://www.ptoceti.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2013 - 2015 ptoceti
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;
import org.osgi.service.wireadmin.BasicEnvelope;
import org.osgi.service.wireadmin.Consumer;
import org.osgi.service.wireadmin.Envelope;
import org.osgi.service.wireadmin.Producer;
import org.osgi.service.wireadmin.Wire;
import org.osgi.service.wireadmin.WireConstants;
import org.osgi.util.measurement.State;

import com.pi4j.system.SystemInfo;
import com.ptoceti.osgi.control.ExtendedUnit;
import com.ptoceti.osgi.control.Measure;
import com.ptoceti.osgi.control.Switch;

/**
 * The wire handler manage pushing pins values to wires and update pins from wires notifications.
 * 
 * @author lor
 *
 */
public class WireHandler implements Consumer, Producer {

	public static final String SYSTEMINFOMIPS = "pi.system.info.mips";
	public static final String SYSTEMINFOCLOCKARM = "pi.system.info.clock.arm";
	public static final String SYSTEMINFOCLOCKCORE = "pi.system.info.clock.core";
	public static final String SYSTEMINFOCPUTEMP = "pi.system.info.cpu.temp";
	public static final String SYSTEMINFOCPUVOLT = "pi.system.info.cpu.volts";
	public static final String SYSTEMINFOMSDRAMCVOLT = "pi.system.info.sdramc.volts";
	public static final String SYSTEMINFOMSDRAMIVOLT = "pi.system.info.sdrami.volts";
	public static final String SYSTEMINFOMSDRAMPVOLT = "pi.system.info.sdramp.volts";

	public static final String COMPOSITEIDENTITY = "com.ptoceti.osgi.pi.compositeidentity";
	public static final String SCOPE = "*";

	public static final String PISYSTEMNETWORKSCOPE = "pi.system.network.info";
	public static final String PISYSTEMSCOPE = "pi.system.info";

	private Timer refreshTimer = new Timer();
	private TimerTask pushWiresThread = null;
	private List<PiPin> pins = null;

	// The list of wires this consumer service is connected to. This list is
	// usefull
	// if the service decide to poll the wires or one in particular.
	private Wire producerWires[] = null;
	// The list of wires this producer service is connected to.
	private Wire consumerWires[] = null;
	// keep track of all prvious values sent to avoid sending non-changed values
	private HashMap<Object, Envelope> lastEnvelopes = new HashMap<Object, Envelope>();

	ServiceRegistration sReg;

	/**
	 * Creator. Register this class as a Consumer and Producer.
	 * 
	 * @param newRefreshRate
	 * @param pins
	 */
	public WireHandler(Integer newRefreshRate, List<PiPin> pins) {

		this.pins = pins;

		// The type of objects that can be accepted through the wire.
		Class<?>[] consumerFlavors = new Class[] { Envelope[].class, Envelope.class, Switch.class};
		Class<?>[] producerFlavors = new Class[] { Envelope[].class, Envelope.class, Measure.class, Switch.class, State.class};
		
		// build the list of consumer and producer scopes from system scope and scopes specific to the wires.
		List<String> producerScopes = new ArrayList<String>();
		List<String> consumerScopes = new ArrayList<String>();
		producerScopes.add(PISYSTEMSCOPE);
		for (PiPin pin : pins) {
			// All pins are producer. We do that so that we can inform consumers of existence of output pins
			if( !producerScopes.contains(pin.getScope()))
				producerScopes.add(pin.getScope());
			// the output pins are consumer
			if (!pin.directionIn()) {
				if(!consumerScopes.contains(pin.getScope()))
					consumerScopes.add(pin.getScope());
			}
		}

		// The composite identification of this Consumer service.
		String[] composites = new String[] { COMPOSITEIDENTITY };

		String[] clazzes = new String[] { Producer.class.getName(), Consumer.class.getName() };
		// register the class as a managed service.
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put(Constants.SERVICE_PID, this.getClass().getName());
		properties.put(WireConstants.WIREADMIN_CONSUMER_SCOPE, consumerScopes.toArray(new String[consumerScopes.size()]));
		properties.put(WireConstants.WIREADMIN_CONSUMER_COMPOSITE, composites);
		properties.put(WireConstants.WIREADMIN_CONSUMER_FLAVORS, consumerFlavors);

		properties.put(WireConstants.WIREADMIN_PRODUCER_SCOPE, producerScopes.toArray(new String[producerScopes.size()]));
		properties.put(WireConstants.WIREADMIN_PRODUCER_COMPOSITE, composites);
		properties.put(WireConstants.WIREADMIN_PRODUCER_FLAVORS, producerFlavors);

		sReg = Activator.bc.registerService(clazzes, this, properties);

		pushWiresThread = new PushThread();
		refreshTimer.scheduleAtFixedRate(pushWiresThread, 100, newRefreshRate.longValue());
	}

	protected void stop() {
		if (pushWiresThread != null)
			pushWiresThread.cancel();
		if (refreshTimer != null)
			refreshTimer.cancel();

		Activator.bc.ungetService(sReg.getReference());
	}

	/**
	 * Refresh all the wires that this Producer service is connected to. This
	 * list of wires is provided by the WireAdmnin service when invoquing the
	 * consumerConnected method of this object. This method then call the
	 * update() method on each Wire. Before updating, checks are done to insure
	 * that the value type is included in the Consumer's flavors. The update()
	 * method on the wire is called with an Envelope object as argument.
	 * 
	 */
	protected void pushPiSystemValues() {
		if (consumerWires != null) {
			synchronized (this.consumerWires) {

				// We must update every wire in the wire collection
				for (int i = 0; i < consumerWires.length; i++) {
					Wire wire = consumerWires[i];
					// Get the Consumer service flavors. Accessible from the
					// Wire's getFlavors() method.
					Class<?> flavors[] = wire.getFlavors();
					// .. but check that the wire scope is within that of the
					// wire.
					if (flavors != null && wire.hasScope(PISYSTEMSCOPE)) {
						List<Envelope> systemInfos = getSystemInfo();
						for (Envelope envelope : systemInfos) {
							boolean knownEnveloppe = lastEnvelopes.containsKey(envelope.getIdentification());
							if ((!knownEnveloppe)
									|| (knownEnveloppe && ((Measure) lastEnvelopes.get(envelope.getIdentification()).getValue()).getValue() != ((Measure) envelope
											.getValue()).getValue())) {

								// if the Enveloppe type is included in the
								// Consumer properties, we send it i to it.
								for (int k = 0; k < flavors.length; k++) {
									if (flavors[k].isInstance(envelope)) {
										wire.update(envelope);
										break;
									}
								}
								lastEnvelopes.put(envelope.getIdentification(), envelope);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Push each pin values to the wires. This is a wasy to notify consumer that
	 * the pins exists.
	 * 
	 */
	private void updateWiresWithPins() {
		for (PiPin pin : pins) {
			pushPinValues(pin);
		}
	}

	protected void pushPinValues(PiPin pin) {
		if (consumerWires != null) {
			synchronized (this.consumerWires) {

				// We must update every wire in the wire collection
				for (int i = 0; i < consumerWires.length; i++) {
					Wire wire = consumerWires[i];
					// Get the Consumer service flavors. Accessible from the
					// Wire's getFlavors() method.
					Class<?> flavors[] = wire.getFlavors();
					// .. but check that the wire scope is within that of the
					// wire.
					if (flavors != null && wire.hasScope(pin.getScope())) {
						Envelope enveloppe = pin.getValue();
						// if the Enveloppe type is included in the Consumer
						// properties, we send it i to it.
						for (int k = 0; k < flavors.length; k++) {
							if (flavors[k].isInstance(enveloppe)) {
								wire.update(enveloppe);
								break;
							}
						}
					}
				}
			}
		}
	}

	private List<Envelope> getSystemInfo() {
		List<Envelope> result = new ArrayList<Envelope>();

		try {
			long timeStamp = Calendar.getInstance().getTime().getTime();
			result.add(new BasicEnvelope(new Measure((Double.valueOf(SystemInfo.getBogoMIPS())).doubleValue(), 0, null, timeStamp), SYSTEMINFOMIPS,
					PISYSTEMSCOPE));
			result.add(new BasicEnvelope(new Measure((Double.valueOf(SystemInfo.getClockFrequencyArm())).doubleValue(), 0.0, ExtendedUnit.Hz,
					timeStamp), SYSTEMINFOCLOCKARM, PISYSTEMSCOPE));
			result.add(new BasicEnvelope(new Measure((Double.valueOf(SystemInfo.getClockFrequencyCore())).doubleValue(), 0.0, ExtendedUnit.Hz,
					timeStamp), SYSTEMINFOCLOCKCORE, PISYSTEMSCOPE));

			result.add(new BasicEnvelope(new Measure((Double.valueOf(SystemInfo.getCpuTemperature())).doubleValue(), 0, ExtendedUnit.celsius,
					timeStamp), SYSTEMINFOCPUTEMP, PISYSTEMSCOPE));

			result.add(new BasicEnvelope(new Measure((Double.valueOf(SystemInfo.getCpuVoltage())).doubleValue(), 0, ExtendedUnit.V, timeStamp),
					SYSTEMINFOCPUVOLT, PISYSTEMSCOPE));
			result.add(new BasicEnvelope(new Measure((Double.valueOf(SystemInfo.getMemoryVoltageSDRam_C())).doubleValue(), 0, ExtendedUnit.V,
					timeStamp), SYSTEMINFOMSDRAMCVOLT, PISYSTEMSCOPE));
			result.add(new BasicEnvelope(new Measure((Double.valueOf(SystemInfo.getMemoryVoltageSDRam_I())).doubleValue(), 0, ExtendedUnit.V,
					timeStamp), SYSTEMINFOMSDRAMIVOLT, PISYSTEMSCOPE));
			result.add(new BasicEnvelope(new Measure((Double.valueOf(SystemInfo.getMemoryVoltageSDRam_P())).doubleValue(), 0, ExtendedUnit.V,
					timeStamp), SYSTEMINFOMSDRAMPVOLT, PISYSTEMSCOPE));
		} catch (IOException e) {
			Activator.log(LogService.LOG_ERROR, "Error accessing pi's internal: " + e.getMessage());
		} catch (InterruptedException e) {
			Activator.log(LogService.LOG_ERROR, "Interupt exception while accessing pi's internal: " + e.getMessage());
		}
		return result;
	}

	private class PushThread extends TimerTask {

		@Override
		public void run() {
			pushPiSystemValues();
		}
	}

	/**
	 * Method of the Producer Interface. There is two method for updating the
	 * wires: push and pull. With the push method, the producer updates itself
	 * the wires ( the list of wires is keept internally ) with the new values.
	 * With the pull method, the consumer ask the framework for a new value on a
	 * wire (it does that because it does not know the producer, this is of the
	 * framework domain). The framework in turn poll the producer with the wire
	 * reference. The producer can then update the referenced wire.
	 * 
	 * 
	 * @param Wire
	 *            the wire that invoque this method.
	 * @return An Envelope[] Array.
	 */
	public Object polled(Wire wire) {

		if (wire.hasScope(PISYSTEMSCOPE)) {
			return getSystemInfo();
		}

		updateWiresWithPins();

		return null;
	}

	/**
	 * Method of the Producer Interface. Called by the framework with the
	 * collection of Wires objects the producer has to update with new values (
	 * Measurement, Date, .. ) This list id built by the WireAdmin from the
	 * configuration it has found. If the configuration has been erased, this
	 * method is called with a null object.
	 * 
	 * @param Wire
	 *            [] an Array ow wires this Producer is connected to.
	 * 
	 */
	public void consumersConnected(Wire[] wires) {
		// simply remember the whole collection. Discard any previous.

		if (wires == null) {
			if (this.consumerWires != null) {
				synchronized (this.consumerWires) {
					for (int i = 0; i < this.consumerWires.length; i++) {
						Activator.log(LogService.LOG_INFO, "Wire PID:" + this.consumerWires[i].getProperties().get(WireConstants.WIREADMIN_PID)
								+ " disconnected.");
					}
				}
				this.consumerWires = null;
			}
		} else if (this.consumerWires == null) {
			this.consumerWires = wires;
			synchronized (this.consumerWires) {
				if (this.consumerWires != null) {
					for (int i = 0; i < wires.length; i++) {
						Activator.log(LogService.LOG_INFO, "Wire PID:" + wires[i].getProperties().get(WireConstants.WIREADMIN_PID) + " connected.");
					}
					// update consumers with pins values
					updateWiresWithPins();
				}
			}
		} else {
			synchronized (this.consumerWires) {
				this.consumerWires = wires;
				if (this.consumerWires != null) {
					for (int i = 0; i < wires.length; i++) {
						Activator.log(LogService.LOG_INFO, "Wire PID:" + wires[i].getProperties().get(WireConstants.WIREADMIN_PID) + " connected.");
					}
					// update consumers with pins values
					updateWiresWithPins();
				}
			}
		}
	}

	/**
	 * Method of the Consumer Interface.
	 * 
	 * We are only listening for update of the output pins. Thoses updates come
	 * with Command objects embeded in basic envelopes.
	 * 
	 * @param Wire
	 *            the wire through which the update has come.
	 * @param Object
	 *            the update value
	 */
	public void updated(Wire wire, Object value) {
		if (value instanceof BasicEnvelope) {
			BasicEnvelope envelope = (BasicEnvelope) value;
			if (envelope.getValue() instanceof Switch) {
				for (PiPin pin : pins) {
					if (envelope.getScope().equals(pin.getScope()) && envelope.getIdentification().equals(pin.getIdentification())) {
						pin.setValue(envelope);
						break;
					}
				}
			}
		}

	}

	/**
	 * Method of the Consumer Interface. Called by the framework with the
	 * collection of Wires objects. This method is called when the Consumer
	 * service is first registered and subsequently whenever a Wire associated
	 * with this Consumer service becomes connected, is modified or becomes
	 * disconnected. The Wire Admin service must call this method
	 * asynchronously. This implies that implementors of Consumer can be assured
	 * that the callback will not take place during registration when they
	 * execute the registration in a synchronized method.
	 * 
	 * @param Wire
	 *            [] an Array ow wires this Consumer is connected to.
	 * 
	 */
	public void producersConnected(Wire[] wires) {

		if (wires == null) {
			if (this.producerWires != null) {
				synchronized (this.producerWires) {
					for (int i = 0; i < this.producerWires.length; i++) {
						Activator.log(LogService.LOG_INFO, "Wire PID:" + this.producerWires[i].getProperties().get(WireConstants.WIREADMIN_PID)
								+ " disconnected.");
					}
				}
				this.producerWires = null;
			}
		} else if (this.producerWires == null) {
			this.producerWires = wires;
			synchronized (this.producerWires) {
				if (this.producerWires != null) {
					for (int i = 0; i < wires.length; i++) {
						Activator.log(LogService.LOG_INFO, "Wire PID:" + wires[i].getProperties().get(WireConstants.WIREADMIN_PID) + " connected.");
					}
				}
			}
		} else {
			synchronized (this.producerWires) {
				this.producerWires = wires;
				if (this.producerWires != null) {
					for (int i = 0; i < wires.length; i++) {
						Activator.log(LogService.LOG_INFO, "Wire PID:" + wires[i].getProperties().get(WireConstants.WIREADMIN_PID) + " connected.");
					}
				}
			}
		}
	}

}
