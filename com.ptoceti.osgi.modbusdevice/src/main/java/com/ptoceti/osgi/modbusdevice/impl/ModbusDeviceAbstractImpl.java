package com.ptoceti.osgi.modbusdevice.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : ModbusDevice
 * FILENAME : ModbusDeviceAbstractImpl.java
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


import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;
import org.osgi.service.wireadmin.BasicEnvelope;
import org.osgi.service.wireadmin.Consumer;
import org.osgi.service.wireadmin.Envelope;
import org.osgi.service.wireadmin.Producer;
import org.osgi.service.wireadmin.Wire;
import org.osgi.service.wireadmin.WireConstants;

import com.ptoceti.osgi.modbusdevice.ModbusDevice;

public abstract class ModbusDeviceAbstractImpl implements ModbusDevice{
	
	ServiceRegistration sReg;
	
	
	// the name of the logging service in the osgi framework.
	static protected final String modbusDriverServiceName = com.ptoceti.osgi.modbus.ModbusDriver.class.getName();

	// the collection of wires the service must update with new values as it produces them.
	protected Wire consumerWires[];
	// the collection of wires the service is connected to as a consumer.
	protected Wire producerWires[];
	
	// the collection of ModbusData objects that represent readings from a modbus device?
	protected ArrayList modbusData;
	// an object that invoke the Modbus driver service for reading and writing Reference to and from a modbus device.
	protected ModbusDataBufferDelegate modbusRDataBuffer;
	// an object that invoke the Modbus driver service for reading a Measurement data from a modbus device, and buffer the value internaly.
	protected ModbusDataBufferDelegate modbusMDataBuffer;
	// an object that invoke the Modbus driver service for reading a State data from a modbus device, and buffer the value internaly.
	protected ModbusDataBufferDelegate modbusSDataBuffer;
	
	
	protected void init( String pid, String compositeIdentity, ArrayList mdbReferenceList, ArrayList mdbMeasurementList, ArrayList mdbStateList) {
		
		// Parse the two Measurement ans State ArrayLists. We need to build up the internal ModbuData list, set the delegate on each ModbusData
		// object, build up the scope list with each ModbusData name and update the two factory with the size of the data they need to read
		// from the modbus device.
		
		// Because the service is a composite producer, we need to produce a scope list that will indicate the intent of each data measurement
		// the service is producing. The intent is simply the name of each data measurement.
		
		String[] producerScopes = new String[mdbReferenceList.size() + mdbMeasurementList.size() + mdbStateList.size()];
		String[] consumerScopes = new String[] {"*"};
		
		int j = 0;
		
		int lowAdd = 65535, highAdd = 0, newAdd;
		for( int i = 0; i < mdbMeasurementList.size(); i++) {
			ModbusData mdbData = (ModbusData) mdbMeasurementList.get(i);
			// set the delegate to the MeasurementFactory. Will be used for reading buffered data from the modbus device.
			mdbData.setReader( modbusMDataBuffer );
			// add the ModbusData name to the scope list.
			producerScopes[j++] = mdbData.getScope();
			// and add the object to the ModbusData internal list.
			modbusData.add( mdbData );
			newAdd = mdbData.getAdress();
			
			if( newAdd < lowAdd ) lowAdd = newAdd;
			if( newAdd > highAdd ) highAdd = newAdd;
			modbusMDataBuffer.init(lowAdd, 1 + highAdd - lowAdd);
		}
		
//		 Do the same thing for the ModbusReference objects.
		lowAdd = 65535; highAdd = 0;
		for( int i = 0; i < mdbReferenceList.size(); i++ ) {
			ModbusData mdbData = (ModbusData) mdbReferenceList.get(i);
			// set the delegate to the StateFactory.
			mdbData.setReader(modbusMDataBuffer);
			// add the ModbusData name to the scope list.
			producerScopes[j++] = mdbData.getScope();
			// and add the object to the ModbusData internal list.
			modbusData.add( mdbData );
			newAdd = mdbData.getAdress();
			
			if( newAdd < lowAdd ) lowAdd = newAdd;
			if( newAdd > highAdd ) highAdd = newAdd;
			modbusRDataBuffer.init(lowAdd, 1 + highAdd - lowAdd);
		}
		
//		 Do the same thing for the State objects.
		lowAdd = 65535; highAdd = 0;
		for( int i = 0; i < mdbStateList.size(); i++ ) {
			ModbusData mdbData = (ModbusData) mdbStateList.get(i);
			// set the delegate to the StateFactory.
			mdbData.setReader(modbusSDataBuffer);
			// add the ModbusData name to the scope list.
			producerScopes[j++] = mdbData.getScope();
			// and add the object to the ModbusData internal list.
			modbusData.add( mdbData );
			newAdd = mdbData.getAdress();
			
			if( newAdd < lowAdd ) lowAdd = newAdd;
			if( newAdd > highAdd ) highAdd = newAdd;
			modbusSDataBuffer.init(lowAdd, 1 + highAdd - lowAdd);
		}
		
		// Then we need to register our service into the framework.
		// We put here the name of the services interfaces under which to register this service.
		String[] interfaces = new String[] {
			Producer.class.getName(),
			Consumer.class.getName(),
			ModbusDevice.class.getName()
		};
		
		// The composite identification of this Producer service.
		String[] composites = new String[] {
			compositeIdentity
		};
		
		// The type of objects that will be returned through the wire.
		Class[] flavors = new Class[] {
			Envelope.class
		};
		
		// put here the properties of the services.
		Dictionary props = new Hashtable();
		// set producer properties
		props.put( WireConstants.WIREADMIN_PRODUCER_COMPOSITE, composites);
		props.put( WireConstants.WIREADMIN_PRODUCER_SCOPE, producerScopes);
		props.put( WireConstants.WIREADMIN_PRODUCER_FLAVORS, flavors);
		// set consumer properties
		props.put( WireConstants.WIREADMIN_CONSUMER_COMPOSITE, composites);
		props.put( WireConstants.WIREADMIN_CONSUMER_SCOPE, consumerScopes);
		props.put( WireConstants.WIREADMIN_CONSUMER_FLAVORS, flavors);
		
		props.put( Constants.SERVICE_PID, pid);
		props.put( Constants.SERVICE_DESCRIPTION, "ModbusDevice service");
		sReg = Activator.bc.registerService( interfaces, this, props );
		
		Activator.log(LogService.LOG_INFO, "Registered " + this.getClass().getName()
				+ " as " +  ModbusDevice.class.getName() + ", Pid = " + pid);
		
		
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
	
	/** 
	 * Method of the Consumer Interface. Called by the Wire object whenever its value is updated by
	 * the Producer.
	 * 
	 * @param wire
	 * @param value
	 */
	public void updated(Wire wire, Object value ) {
		
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
			if( this.consumerWires != null) {
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
	
	/**
	 * Method of the Producer Interface. There is two method for updating the wires: push and pull.
	 * With the push method, the producer updates itself the wires ( the list of wires is keept internally )
	 * with the new values. With the pull method, the consumer ask the framework for a new value on a wire 
	 * (it does that because it does not know the producer, this is of the framework domain). The framework
	 * in turn poll the producer with the wire reference. The producer can then update the referenced wire.
	 *
	 *
	 * @param Wire the wire that invoque this method.
	 * @return An Envelope[] Array.
	 */
	public Object polled( Wire wire ) {
	
		if( wire != null ) {
			ArrayList envelopeList = new ArrayList();
			ModbusData mdbData = null;
			
			// Because the service is a composite Producer, we must pass in review each value and see if its scope marches
			// that of the  wire.
			for( int i = 0; i < modbusData.size(); i++ ) {
				
				mdbData = ((ModbusData)modbusData.get(i));
				if( wire.hasScope( mdbData.getScope())) {
					// ... if it does, we include it in the result list. Returned values must be Envelope objects.
					BasicEnvelope envValue = new BasicEnvelope( mdbData.getValue(), mdbData.getIdentification(), mdbData.getScope());
					envelopeList.add( envValue );
				}
			}
			
			// return everything as an array of Envelope objects.
			return envelopeList.toArray(new Envelope[0]); 
		} else {
			return null;
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
					Class flavors[] = wire.getFlavors();
					
					if( flavors != null ) {
						ModbusData mdbData = null;
						for( int j = 0; j < modbusData.size(); j++ ) {
							// Try to update the wire with in every ModbusData value in our collection ..
							mdbData = ((ModbusData)modbusData.get(j));
							// .. but check that the wire scope is within that of the wire.
							if( wire.hasScope( mdbData.getScope())) {
								Envelope enValue = new BasicEnvelope( mdbData.getValue(), mdbData.getIdentification(), mdbData.getScope());
								// if the Enveloppe type is included in the Consumer properties, we send it i to it.
								for(int k = 0; k < flavors.length; k++){
									if( flavors[k].isInstance(enValue)) {
										wire.update( enValue );
										break;
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
