package com.ptoceti.osgi.pi.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Pi
 * FILENAME : PiService.java
 * 
 * This file is part of the Ptoceti project. More information about
 * this project can be found here: http://www.ptoceti.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2013 - 2014 ptoceti
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
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.log.LogService;
import org.osgi.service.wireadmin.BasicEnvelope;
import org.osgi.service.wireadmin.Consumer;
import org.osgi.service.wireadmin.Envelope;
import org.osgi.service.wireadmin.Producer;
import org.osgi.service.wireadmin.Wire;
import org.osgi.service.wireadmin.WireConstants;
import org.osgi.util.measurement.State;

import com.pi4j.system.SystemInfo;
import com.pi4j.temperature.TemperatureConversion;
import com.ptoceti.osgi.control.ExtendedUnit;
import com.ptoceti.osgi.control.Measure;
import com.ptoceti.osgi.pi.impl.Activator;

public class PiService implements ManagedService, Consumer, Producer{

	public static final String COMPOSITEIDENTITY = "com.ptoceti.osgi.pi.compositeidentity";
	public static final String SCOPE = "*";
	
	
	public static final String PISYSTEMNETWORKSCOPE = "pi.system.network.info";
	public static final String PISYSTEMSCOPE = "pi.system.info";
	
	public static final String SYSTEMINFOMIPS = "pi.system.info.mips";
	public static final String SYSTEMINFOCLOCKARM = "pi.system.info.clock.arm";
	public static final String SYSTEMINFOCLOCKCORE = "pi.system.info.clock.core";
	public static final String SYSTEMINFOCPUTEMP = "pi.system.info.cpu.temp";
	public static final String SYSTEMINFOCPUVOLT = "pi.system.info.cpu.volts";
	public static final String SYSTEMINFOMSDRAMCVOLT = "pi.system.info.sdramc.volts";
	public static final String SYSTEMINFOMSDRAMIVOLT = "pi.system.info.sdrami.volts";
	public static final String SYSTEMINFOMSDRAMPVOLT = "pi.system.info.sdramp.volts";
	
	public static final String CONFIGURATION_KEY_REFRESHRATE= "com.ptoceti.osgi.pi.refreshRate";
		
	// a reference to the service registration for the Controller object.
	ServiceRegistration sReg = null;
	
	// The list of wires this consumer service is connected to. This list is
	// usefull
	// if the service decide to poll the wires or one in particular.
	private Wire producerWires[] = null;
	// The list of wires this producer service is connected to.
	private Wire consumerWires[] = null;
	// keep track of all prvious values sent to avoid sending non-changed values
	private HashMap<Object, Envelope> lastEnvelopes = new HashMap<Object,Envelope>();
	
	private Timer refreshTimer = new Timer();
	private TimerTask pushWiresThread = null;
		
	public PiService() {
		
		// The type of objects that can be accepted through the wire.
		Class<?>[] flavors = new Class[] { Envelope[].class, Envelope.class,
				Measure.class, State.class, };
		// The scopes of measurements that this consumer is able to consume from
		// the wire.
		String[] producerScopes = new String[] { PISYSTEMSCOPE };
		String[] consumerScopes = new String[] {};
		
		// The composite identification of this Consumer service.
		String[] composites = new String[] { COMPOSITEIDENTITY };
		
		
		String[] clazzes = new String[] { ManagedService.class.getName(), Producer.class.getName(),Consumer.class.getName() };
		// register the class as a managed service.
		Hashtable<String, Object> properties = new Hashtable<String,Object>();
		properties.put(Constants.SERVICE_PID, this.getClass().getName());
		properties.put(WireConstants.WIREADMIN_CONSUMER_SCOPE, consumerScopes);
		properties.put(WireConstants.WIREADMIN_CONSUMER_COMPOSITE,composites);
		properties.put(WireConstants.WIREADMIN_CONSUMER_FLAVORS, flavors);

		properties.put(WireConstants.WIREADMIN_PRODUCER_SCOPE, producerScopes);
		properties.put(WireConstants.WIREADMIN_PRODUCER_COMPOSITE,composites);
		properties.put(WireConstants.WIREADMIN_PRODUCER_FLAVORS, flavors);

		
		sReg = Activator.bc.registerService(clazzes, this, properties);

		Activator.log(LogService.LOG_INFO, "Registered "
				+ this.getClass().getName() +  ", Pid = "
				+ (String) properties.get(Constants.SERVICE_PID));
	}
	
	public void stop() {
		if( pushWiresThread != null) pushWiresThread.cancel();
		if( refreshTimer != null ) refreshTimer.cancel();
	}
	
	
	@Override
	public void updated(Dictionary properties) throws ConfigurationException {
		
		Activator.log(LogService.LOG_DEBUG, "Configuration update.");
		
		if( properties != null && properties.get(CONFIGURATION_KEY_REFRESHRATE) != null ) {
			Integer newRefreshRate = (Integer)properties.get(CONFIGURATION_KEY_REFRESHRATE);
			
			if( pushWiresThread != null ) {
				pushWiresThread.cancel();
			}
			
			pushWiresThread = new PushThread();
			refreshTimer.scheduleAtFixedRate( pushWiresThread, 100, newRefreshRate.longValue());
			
			Activator.log(LogService.LOG_DEBUG, "Refresh Thread configured at intervals of : "+ newRefreshRate.toString() + " ms.");
		}
		
	}
	
	/**
	 * Refresh all the wires that this Producer service is connected to. This list of wires is provided by the
	 * WireAdmnin service when invoquing the consumerConnected method of this object. This method then call the
	 * update() method on each Wire. Before updating, checks are done to insure that the value type is 
	 * included in the Consumer's flavors. The update() method on the wire is called with an Envelope object
	 * as argument.
	 *
	 */
	protected void refreshWires() {
		if( consumerWires != null ) {
			synchronized( this.consumerWires ) {
			
				// We must update every wire in the wire collection
				for( int i = 0; i < consumerWires.length; i++ ) {
					Wire wire = consumerWires[i];
					// Get the Consumer service flavors. Accessible from the Wire's getFlavors() method.
					Class<?> flavors[] = wire.getFlavors();
					// .. but check that the wire scope is within that of the wire.
					if( flavors != null && wire.hasScope(PISYSTEMSCOPE)) {
						List<Envelope> systemInfos = getSystemInfo();
						for( Envelope envelope : systemInfos){
							boolean knownEnveloppe = lastEnvelopes.containsKey(envelope.getIdentification());
							if(( !knownEnveloppe) || ( knownEnveloppe &&
									((Measure)lastEnvelopes.get(envelope.getIdentification()).getValue()).getValue() != ((Measure)envelope.getValue()).getValue())){

								// if the Enveloppe type is included in the Consumer properties, we send it i to it.
								for(int k = 0; k < flavors.length; k++){
									if( flavors[k].isInstance(envelope)) {
										wire.update( envelope );
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

			if( wire.hasScope(PISYSTEMSCOPE)){
				return getSystemInfo();
			}
		
		return null;
	}
	
	private List<Envelope> getSystemInfo() {
		List<Envelope> result = new ArrayList<Envelope>();
		
		try {
			long timeStamp = Calendar.getInstance().getTime().getTime();
			result.add(new BasicEnvelope(new Measure((Double.valueOf(SystemInfo.getBogoMIPS())).doubleValue(),0,null,timeStamp),SYSTEMINFOMIPS,PISYSTEMSCOPE));
			result.add(new BasicEnvelope(new Measure((Double.valueOf(SystemInfo.getClockFrequencyArm())).doubleValue(),0.0,ExtendedUnit.Hz,timeStamp),SYSTEMINFOCLOCKARM,PISYSTEMSCOPE));
			result.add(new BasicEnvelope(new Measure((Double.valueOf(SystemInfo.getClockFrequencyCore())).doubleValue(),0.0,ExtendedUnit.Hz,timeStamp),SYSTEMINFOCLOCKCORE,PISYSTEMSCOPE));
			
			result.add(new BasicEnvelope(new Measure((Double.valueOf(SystemInfo.getCpuTemperature())).doubleValue(),0,ExtendedUnit.celsius,timeStamp),SYSTEMINFOCPUTEMP,PISYSTEMSCOPE));
			
			result.add(new BasicEnvelope(new Measure((Double.valueOf(SystemInfo.getCpuVoltage())).doubleValue(),0,ExtendedUnit.V,timeStamp),SYSTEMINFOCPUVOLT,PISYSTEMSCOPE));
			result.add(new BasicEnvelope(new Measure((Double.valueOf(SystemInfo.getMemoryVoltageSDRam_C())).doubleValue(),0,ExtendedUnit.V,timeStamp),SYSTEMINFOMSDRAMCVOLT,PISYSTEMSCOPE));
			result.add(new BasicEnvelope(new Measure((Double.valueOf(SystemInfo.getMemoryVoltageSDRam_I())).doubleValue(),0,ExtendedUnit.V,timeStamp),SYSTEMINFOMSDRAMIVOLT,PISYSTEMSCOPE));
			result.add(new BasicEnvelope(new Measure((Double.valueOf(SystemInfo.getMemoryVoltageSDRam_P())).doubleValue(),0,ExtendedUnit.V,timeStamp),SYSTEMINFOMSDRAMPVOLT,PISYSTEMSCOPE));
		} catch (IOException e) {
			Activator.log(LogService.LOG_ERROR, "Error accessing pi's internal: " + e.getMessage());
		} catch (InterruptedException e) {
			Activator.log(LogService.LOG_ERROR, "Interupt exception while accessing pi's internal: " + e.getMessage());
		}
		return result;
	}

	/**
	 * Method of the Producer Interface. Called by the framework with the collection of
	 * Wires objects the producer has to update with new values ( Measurement, Date,  .. )
	 * This list id built by the WireAdmin from the configuration it has found. If the configuration
	 * has been erased, this method is called with a null object.
	 *
	 * @param Wire[] an Array ow wires this Producer is connected to.
	 *
	 */
	public void consumersConnected( Wire[] wires ) {
		// simply remember the whole collection. Discard any previous.
		
		if( wires == null){
			if( this.consumerWires != null){
				synchronized(this.consumerWires)
				{
					for (int i = 0; i< this.consumerWires.length; i++) {
						Activator.log( LogService.LOG_INFO, "Wire PID:"
						+ this.consumerWires[i].getProperties().get(WireConstants.WIREADMIN_PID)
						+ " disconnected.");
					}
				}
				this.consumerWires = null;
			}
		} else if( this.consumerWires == null ) {
			this.consumerWires = wires;
			synchronized( this.consumerWires ) {
				if( this.consumerWires != null){
					for (int i = 0; i< wires.length; i++) {
						Activator.log( LogService.LOG_INFO, "Wire PID:"
						+ wires[i].getProperties().get(WireConstants.WIREADMIN_PID)
						+ " connected.");
					}
				}
			}
		} else {
			synchronized( this.consumerWires ) {
				this.consumerWires = wires;
				if( this.consumerWires != null){
					for (int i = 0; i< wires.length; i++) {
						Activator.log( LogService.LOG_INFO, "Wire PID:"
						+ wires[i].getProperties().get(WireConstants.WIREADMIN_PID)
						+ " connected.");
					}
				}
			}
		}
	}

	public void updated(Wire wire, Object value) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Method of the Consumer Interface. Called by the framework with the collection of
	 * Wires objects.
	 * This method is called when the Consumer service is first registered and subsequently whenever
	 * a Wire associated with this Consumer service becomes connected, is modified or becomes
	 * disconnected. The Wire Admin service must call this method asynchronously. This implies
	 * that implementors of Consumer can be assured that the callback will not take place during
	 * registration when they execute the registration in a synchronized method. 
	 *
	 * @param Wire[] an Array ow wires this Consumer is connected to.
	 *
	 */
	public void producersConnected(Wire[] wires ) {
		
		if( wires == null){
			if( this.producerWires != null){
				synchronized(this.producerWires)
				{
					for (int i = 0; i< this.producerWires.length; i++) {
						Activator.log( LogService.LOG_INFO, "Wire PID:"
						+ this.producerWires[i].getProperties().get(WireConstants.WIREADMIN_PID)
						+ " disconnected.");
					}
				}
				this.producerWires = null;
			}
		} else if( this.producerWires == null ) {
			this.producerWires = wires;
			synchronized( this.producerWires ) {
				if( this.producerWires != null){
					for (int i = 0; i< wires.length; i++) {
						Activator.log( LogService.LOG_INFO, "Wire PID:"
						+ wires[i].getProperties().get(WireConstants.WIREADMIN_PID)
						+ " connected.");
					}
				}
			}
		} else {
			synchronized( this.producerWires ) {
				this.producerWires = wires;
				if( this.producerWires != null){
					for (int i = 0; i< wires.length; i++) {
						Activator.log( LogService.LOG_INFO, "Wire PID:"
						+ wires[i].getProperties().get(WireConstants.WIREADMIN_PID)
						+ " connected.");
					}
				}
			}
		}
	}

	private class PushThread extends TimerTask {

		@Override
		public void run() {		
			refreshWires();
		}
	}
}
