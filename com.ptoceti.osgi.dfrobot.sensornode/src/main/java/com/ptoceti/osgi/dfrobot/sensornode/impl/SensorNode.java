package com.ptoceti.osgi.dfrobot.sensornode.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : SensorNode
 * FILENAME : SensorNode.java
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



import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;
import org.osgi.service.wireadmin.BasicEnvelope;
import org.osgi.service.wireadmin.Envelope;
import org.osgi.service.wireadmin.Producer;
import org.osgi.service.wireadmin.Wire;
import org.osgi.service.wireadmin.WireConstants;
import org.osgi.util.measurement.State;

import com.ptoceti.osgi.control.ExtendedUnit;
import com.ptoceti.osgi.control.Measure;
import com.ptoceti.osgi.control.StatusCode;

public class SensorNode implements Producer, ServiceListener, Runnable {

	/**
	 * configuration key for node id
	 */
	public static final String SENSORNODE_ID = "com.ptoceti.osgi.sensornode.id";
	/**
	 * configuration key for port to be used
	 */
	public static final String SENSORNODE_PORT = "com.ptoceti.osgi.sensornode.port";
	/**
	 * configuration key for port  pooling rate
	 */
	public static final String SENSORNODE_POOLING_RATE = "com.ptoceti.osgi.sensornode.poolingrate";
	/**
	 * configuration key for the node composit identity
	 */
	public static final String SENSORNODE_COMPOSITE_IDENTITY = "com.ptoceti.osgi.sensornode.compositeidentity";
	/**
	 * The url to the config files for the measurements
	 */
	public static final String SENSORNODE_MEASUREMENT_CONFIGFILE = "com.ptoceti.osgi.sensornode.measurement.configfile";
	
	/**
	 * The id of the sensor node on the RS-485 bus
	 */
	protected Integer id;
	
	/**
	 * The pooling rate at which to read the device
	 */
	protected Integer poolRate;
	
	private ServiceRegistration sReg;
	private SensorData[] sensorDatas;  
	/**
	 * The driver associated to the serial port to be used to communicate with the sensor node
	 */
	private SensorNodeDriver sensorNodeDriver;
	/**
	 * the collection of wires the service must update with new values as it produces them.
	 */
	protected Wire consumerWires[];
	/**
	 * The thread that pool the device
	 */
	Thread poolThread = null;
	
	/**
	 * a flag asking to suspend communicating with the device.
	 */
	private boolean disconnect = false;
	
	
	
	public SensorNode(String pid, Integer nodeId, String port, Integer rate, String compositeIdentity, SensorData[] sensorDatas){
		this.id = nodeId;
		this.poolRate = rate;
		this.sensorDatas = sensorDatas;
		
		List<String> scopes = new ArrayList<String>();
		
		
		for( int i = 0; i < sensorDatas.length; i++) {
			String scope = sensorDatas[i].getScope();
			if(! scopes.contains(scope)){
				scopes.add(scope);
			}
		}
		
		String[] producerScopes = scopes.toArray(new String[scopes.size()]);
		
		// Then we need to register our service into the framework.
		// We put here the name of the services interfaces under which to register this service.
		String[] interfaces = new String[] {
			Producer.class.getName(),
			SensorNode.class.getName()
		};
		
		// The composite identification of this Producer service.
		String[] composites = new String[] {
			compositeIdentity
		};
		
		// The type of objects that will be returned through the wire.
		Class<?>[] flavors = new Class[] {
			Envelope.class
		};
		
		// put here the properties of the services.
		Dictionary<String, Object> props = new Hashtable<String, Object>();
		// set producer properties
		props.put( WireConstants.WIREADMIN_PRODUCER_COMPOSITE, composites);
		props.put( WireConstants.WIREADMIN_PRODUCER_SCOPE, producerScopes);
		props.put( WireConstants.WIREADMIN_PRODUCER_FLAVORS, flavors);
		
		props.put( Constants.SERVICE_PID, pid);
		props.put( Constants.SERVICE_DESCRIPTION, "SensorNode service");
		sReg = Activator.bc.registerService( interfaces, this, props );
		
		Activator.log(LogService.LOG_INFO, "Registered " + this.getClass().getName()
				+ " as " +  SensorNode.class.getName() + ", Pid = " + pid);
		
		// We then need to get a reference to a sensor node factory. We try to get this reference dynamically
		// by constructing a listener that will detect when the sensor node driver service appear or disapear.
		String filter = "(&(objectclass=" + SensorNodeDriver.class.getName() + ")"
			+ "(" + SensorNodeDriver.SENSORNODE_PORT + "=" + port + "))";
	
		try {
			Activator.bc.addServiceListener( this, filter);
			// in case the service is already registered, we send a REGISTERED event to its listener.
			ServiceReference srModbus[] = Activator.bc.getServiceReferences( SensorNodeDriver.class.getName(), filter );
			if( srModbus != null ) {
				this.serviceChanged(new ServiceEvent( ServiceEvent.REGISTERED, srModbus[0] ));
			}
		} catch ( InvalidSyntaxException e ) {
			// We known there shouldn't be an exception thrown here since we made the filter string.
		}
	}
	
	
	public void stop(){
		disconnect = true;
		sReg.unregister();
	}


	/**
	 * Method of the Producer Interface. There is two method for updating the wires: push and pull.
	 * With the push method, the producer updates itself the wires ( the list of wires is keept internally )
	 * with the new values. With the pull method, the consumer ask the framework for a new value on a wire 
	 * (it does that because it does not know the producer, this is of the framework domain). The framework
	 * in turn poll the producer with the wire reference. The producer can then update the referenced wire.
	 *
	 *
	 * @param wire the wire that invoque this method.
	 * @return An Envelope[] Array.
	 */
	public Object polled(Wire wire) {
		
		if( wire != null ) {
			List<BasicEnvelope> envelopeList = new ArrayList<BasicEnvelope>();
			
			// Because the service is a composite Producer, we must pass in review each value and see if its scope marches
			// that of the  wire.
			for( int i = 0; i < sensorDatas.length; i++ ) {
				
				SensorData sensorData = sensorDatas[i];
				if( wire.hasScope( sensorData.getScope())) {
					// ... if it does, we include it in the result list. Returned values must be Envelope objects.
					BasicEnvelope envValue = new BasicEnvelope( sensorData.getValue(), sensorData.getIdentification(), sensorData.getScope());
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
	 * Method of the Producer Interface. Called by the framework with the collection of
	 * Wires objects the producer has to update with new values ( Measurement, Date,  .. )
	 * This list id built by the WireAdmin from the configuration it has found. If the configuration
	 * has been erased, this method is called with a null object.
	 *
	 * @param wires an Array ow wires this Producer is connected to.
	 *
	 */
	public void consumersConnected(Wire[] wires) {
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
	 * Unique method of the ServiceListener interface. The framework invoke this method when
	 * a event has been posted. Since we registered this listener for registered and unregistered
	 * event from the sensor node driver service, we will receive notification of theses here.
	 *
	 */
	public void serviceChanged(ServiceEvent event) {
		ServiceReference sr = event.getServiceReference();
		switch(event.getType()) {
			case ServiceEvent.REGISTERED: {
				sensorNodeDriver = (SensorNodeDriver) Activator.bc.getService(sr);
				poolThread = new Thread(this);
				poolThread.start();
				Activator.log( LogService.LOG_INFO, "Getting instance of service: " + (String) sr.getProperty( Constants.SERVICE_PID)
					+ ", " + Constants.SERVICE_ID + "=" + ((Long)sr.getProperty(Constants.SERVICE_ID)).toString() );
			}
			break;
			case ServiceEvent.UNREGISTERING: {
				Activator.log( LogService.LOG_INFO, "Releasing instance of service: " + (String) sr.getProperty( Constants.SERVICE_PID)
					+ ", " + Constants.SERVICE_ID + "=" + ((Long)sr.getProperty(Constants.SERVICE_ID)).toString());
				sensorNodeDriver = null;
				poolThread.interrupt();
			}
			break;
		}
		
	}


	/**
	 * Unique method of the Runnable interface. 
	 * Pool the sensor node driver periodically to get new data and refresh local buffer; Push data
	 * to connected wires.
	 *
	 */
	public void run() {
		while(!disconnect){
			if(sensorNodeDriver != null ) {
				Integer[] datas = sensorNodeDriver.getAllValues(id); 
				if( datas != null) {
					// the config of contentdata drive the data to use
					for( SensorData sensorData : sensorDatas){
						int dataIndex = sensorData.getId().intValue();
						if( dataIndex >= 0 && dataIndex < datas.length){
							sensorData.setValue(new Integer(datas[dataIndex]));
						}
					}
				}
				refreshWires();
			}
			try {
				Thread.sleep( poolRate.longValue() );
			} catch (InterruptedException e) {
				break;
			}
		}
		
	}
	
	protected int getSensorStatus(){
		for( int j = 0; j < sensorDatas.length; j++ ) {
			// Try to update the wire with in every ModbusData value in our collection ..
			SensorData sensorData = ((SensorData)sensorDatas[j]);
			if( sensorData.getId().intValue() == 3 && sensorData.getValue() != null){
				return sensorData.getValue().intValue();
			}
		}
		
		return 0;
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
					
					if( flavors != null ) {
						SensorData sensorData = null;
						for( int j = 0; j < sensorDatas.length; j++ ) {
							// Try to update the wire with in every ModbusData value in our collection ..
							sensorData = ((SensorData)sensorDatas[j]);
							
							if( sensorData.getValue() != null){
								// .. but check that the wire scope is within that of the wire.
								if( wire.hasScope( sensorData.getScope())) {
									int sensorDataId = sensorData.getId().intValue();
									if( sensorDataId != 0 && sensorDataId != 3)
									{
										Date now = Calendar.getInstance().getTime();
										double value = (sensorData.getScale().doubleValue() * sensorData.getValue().doubleValue()) + sensorData.getOffset().doubleValue();
										double lastValue = -1;
										if( sensorData.getLastValue() != null){
											lastValue = (sensorData.getScale().doubleValue() * sensorData.getLastValue().doubleValue()) + sensorData.getOffset().doubleValue();
										} 
										if( lastValue < 0 || (Math.abs(lastValue - value) >= 0.01)) {
											Measure measure = new Measure(value, 0, ExtendedUnit.findUnit(sensorData.getUnit()), now.getTime());
											measure.setStatus(StatusCode.getStatusFromId(getSensorStatus()));
											Envelope enValue = new BasicEnvelope( measure, sensorData.getIdentification(), sensorData .getScope());
											// if the Enveloppe type is included in the Consumer properties, we send it i to it.
											for(int k = 0; k < flavors.length; k++){
												if( flavors[k].isInstance(enValue)) {
													wire.update( enValue );
													break;
												}
											}
											// remember last value
											sensorData.setLastValue(sensorData.getValue());
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
}
