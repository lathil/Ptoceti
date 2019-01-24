package com.ptoceti.osgi.obix.impl.service;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;
import org.osgi.service.wireadmin.Consumer;
import org.osgi.service.wireadmin.Envelope;
import org.osgi.service.wireadmin.Producer;
import org.osgi.service.wireadmin.Wire;
import org.osgi.service.wireadmin.WireConstants;
import org.osgi.util.measurement.State;

import com.ptoceti.osgi.control.Measure;
import com.ptoceti.osgi.control.Reference;
import com.ptoceti.osgi.control.StatusCode;
import com.ptoceti.osgi.control.Switch;

import com.ptoceti.osgi.obix.impl.guice.GuiceContext;
import com.ptoceti.osgi.obix.impl.transverse.UnitConverter;

public class WireHandler implements Consumer, Producer {

	public static final String COMPOSITEIDENTITY = "com.ptoceti.osgi.obixservice.compositeidentity";
	public static final String SCOPE = "*";

	// a reference to the service registration for the Controller object.
	ServiceRegistration sReg = null;

	// The list of wires this consumer service is connected to. This list is
	// usefull
	// if the service decide to poll the wires or one in particular.
	private List<Wire> producerWires = null;
	// The list of wires this producer service is connected to.
	private List<Wire> consumerWires = null;

	private boolean respondToUpdates = false;


	public WireHandler() {

		String[] wiresClazzes = new String[] { Producer.class.getName(), Consumer.class.getName() };
		// The type of objects that can be accepted through the wire.
		Class[] flavors = new Class[] { Envelope[].class, Envelope.class, Measure.class, State.class, Switch.class};
		// The scopes of measurements that this consumer is able to consume from
		// the wire.
		String[] scopes = new String[] { SCOPE };
		// The composite identification of this Consumer service.
		String[] composites = new String[] { COMPOSITEIDENTITY };
		// register the class as a managed service.
		Hashtable<String, Object> wiresProperties = new Hashtable<String, Object>();
		wiresProperties.put(WireConstants.WIREADMIN_CONSUMER_SCOPE, scopes);
		wiresProperties.put(WireConstants.WIREADMIN_CONSUMER_COMPOSITE, composites);
		wiresProperties.put(WireConstants.WIREADMIN_CONSUMER_FLAVORS, flavors);

		wiresProperties.put(WireConstants.WIREADMIN_PRODUCER_SCOPE, scopes);
		wiresProperties.put(WireConstants.WIREADMIN_PRODUCER_COMPOSITE, composites);
		wiresProperties.put(WireConstants.WIREADMIN_PRODUCER_FLAVORS, flavors);

		wiresProperties.put(Constants.SERVICE_PID, this.getClass().getName());
		sReg = Activator.bc.registerService(wiresClazzes, this, wiresProperties);

		Activator.log(LogService.LOG_INFO,
				"Registered " + this.getClass().getName() + ", Pid = " + (String) wiresProperties.get(Constants.SERVICE_PID));

	}

	public void setRespondToUpdates(boolean respond) {
		respondToUpdates = respond;
	}

	/**
	 * Method of the Producer Interface. Called by the framework with the
	 * collection of Wires objects the producer has to update with new values (
	 * Measurement, Date, .. ) This list id built by the WireAdmin from the
	 * configuration it has found. If the configuration has been erased, this
	 * method is called with a null object.
	 * 
	 * @param wires an Array ow wires this Producer is connected to.
	 * 
	 */
	public void consumersConnected(Wire[] wires) {

		if (wires == null) {
			if (this.consumerWires != null) {
				for (int i = 0; i < this.consumerWires.size(); i++) {
					Wire nextWire = (Wire) this.consumerWires.get(i);
					// send a little message.
					Activator.log(LogService.LOG_INFO, "Wire PID:" + nextWire.getProperties().get(WireConstants.WIREADMIN_PID) + " disconnected.");
				}
				// before clearing the wire list.
				this.consumerWires = null;
			}
		} else if (this.consumerWires == null) {
			this.consumerWires = new ArrayList<Wire>();

			for (int i = 0; i < wires.length; i++) {
				Wire newWire = wires[i];
				// add the wire to the wire list, ..
				this.consumerWires.add(newWire);
				// Send a little message
				Activator.log(LogService.LOG_INFO, "Wire PID:" + newWire.getProperties().get(WireConstants.WIREADMIN_PID) + " connected.");
			}
		} else {

			// We parse each wire in the new list.
			for (int i = 0; i < wires.length; i++) {
				Wire nextWire = wires[i];
				// If a wire in the new list is not in the internal one, ..
				if (!this.consumerWires.contains(nextWire)) {
					// we add it.
					this.consumerWires.add(nextWire);
					// Add a little message.
					Activator.log(LogService.LOG_INFO, "Wire PID:" + nextWire.getProperties().get(WireConstants.WIREADMIN_PID) + " connected.");
				}
			}
			// We parse each wire in the internal list
			for (int i = 0; i < this.consumerWires.size(); i++) {
				// If a wire on the internal list ..
				Wire nextWire = (Wire) this.consumerWires.get(i);
				boolean found = false;
				for (int j = 0; j < wires.length; j++) {
					if (wires[j] == nextWire) {
						found = true;
						break;
					}
				}
				// .. is not on the new one
				if (!found) {
					// and the wire itself from our internal list
					this.consumerWires.remove(nextWire);
					// send a little message.
					Activator.log(LogService.LOG_INFO, "Wire PID:" + nextWire.getProperties().get(WireConstants.WIREADMIN_PID) + " disconnected.");
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
	 * @param wire the wire that invoque this method.
	 * @return An Envelope[] Array.
	 */
	public Object polled(Wire wire) {

		return null;
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
	 * @param wires an Array ow wires this Consumer is connected to.
	 * 
	 */
	public void producersConnected(Wire[] wires) {

		if (wires == null) {
			if (this.producerWires != null) {
				for (int i = 0; i < this.producerWires.size(); i++) {
					Wire nextWire = (Wire) this.producerWires.get(i);
					// send a little message.
					Activator.log(LogService.LOG_INFO, "Wire PID:" + nextWire.getProperties().get(WireConstants.WIREADMIN_PID) + " disconected.");
				}
				// before clearing the wire list.
				this.producerWires = null;
			}
		} else if (this.producerWires == null) {
			this.producerWires = new ArrayList<Wire>();

			for (int i = 0; i < wires.length; i++) {
				Wire newWire = wires[i];
				// add the wire to the wire list, ..
				this.producerWires.add(newWire);
				// Send a little message
				Activator.log(LogService.LOG_INFO, "Wire PID:" + newWire.getProperties().get(WireConstants.WIREADMIN_PID) + " connected.");
			}
		} else {

			// We parse each wire in the new list.
			for (int i = 0; i < wires.length; i++) {
				Wire nextWire = wires[i];
				// If a wire in the new list is not in the internal one, ..
				if (!this.producerWires.contains(nextWire)) {
					// we add it.
					this.producerWires.add(nextWire);
					// Add a little message.
					Activator.log(LogService.LOG_INFO, "Wire PID:" + nextWire.getProperties().get(WireConstants.WIREADMIN_PID) + " connected.");
				}
			}
			// We parse each wire in the internal list
			for (int i = 0; i < this.producerWires.size(); i++) {
				// If a wire on the internal list ..
				Wire nextWire = (Wire) this.producerWires.get(i);
				boolean found = false;
				for (int j = 0; j < wires.length; j++) {
					if (wires[j] == nextWire) {
						found = true;
						break;
					}
				}
				// .. is not on the new one
				if (!found) {
					// and the wire itself from our internal list
					this.producerWires.remove(nextWire);
					// send a little message.
					Activator.log(LogService.LOG_INFO, "Wire PID:" + nextWire.getProperties().get(WireConstants.WIREADMIN_PID) + " disconnected.");
				}
			}
		}
	}

	/*
	 * updated method from Consumer interface. Called by the Wire Admin service
	 * when a new value is pushed on the wire by the producer.
	 */
	public void updated(Wire wire, Object object) {

		if (!respondToUpdates)
			return;

		EventUpdateHandler evtHandler = GuiceContext.Instance.getInjector().getInstance(EventUpdateHandler.class);
		
		
		
		if (object instanceof Envelope[]) {
			Envelope[] envelopes = (Envelope[]) object;
			for (int i = 0; i < envelopes.length; i++) {
				Envelope env = envelopes[i];
				Activator.getObixService().getEventUpdateHandler().update(env, wire);
			}
		} else if (object instanceof Envelope) {
			Activator.getObixService().getEventUpdateHandler().update((Envelope) object, wire);
		}
	}
	
	/**
	 * Update consumer wires with same scope as the enveloppe with new value.
	 * 
	 * @param env the envelope to be sent
	 * @return true if sent
	 */
	protected boolean updateWire( Envelope env) {
		boolean result = false;
		for( Wire wire : consumerWires){
			if( wire.isConnected() && wire.hasScope(env.getScope())){
				wire.update(env);
				result = true;
			}
		}
		
		return result;
	}
}
