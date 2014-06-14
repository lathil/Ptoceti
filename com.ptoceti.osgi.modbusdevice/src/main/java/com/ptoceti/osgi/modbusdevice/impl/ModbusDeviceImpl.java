

package com.ptoceti.osgi.modbusdevice.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : ModbusDevice
 * FILENAME : ModbusDeviceImpl.java
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

import com.ptoceti.osgi.modbusdevice.ModbusDevice;
import com.ptoceti.osgi.modbus.ModbusDriver;

import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.log.LogService;
import org.osgi.service.wireadmin.Producer;
import org.osgi.service.wireadmin.Consumer;
import org.osgi.service.wireadmin.Wire;
import org.osgi.service.wireadmin.WireConstants;
import org.osgi.service.wireadmin.Envelope;
import org.osgi.service.wireadmin.BasicEnvelope;

import java.util.Hashtable;
import java.util.Dictionary;
import java.util.ArrayList;

/**
 * modbusdevice.impl class
 * The modbusdevice.impl implements the ModbusDevice interface; It represents an device on the modbus network. The modbusdevice.impl
 * uses the ModbusDriver service to gain access to the modbus network. The ModbusDriver represents the master on the bus while the
 * ModbusDevice represents a slave. There can be more than one ModbusDevice attached to a ModbusDriver service; Each is recognised
 * by an unique ID ( that must also be different from the one of the ModbusDriver service ). Direct access to the slave devices on
 * the modbus network could also be gained through the api of the ModbusDriver service; However these are low level call and require
 * knowledge of the modbus networking messages. The modbusdevice.impl service implements the Producer interface to allow access to
 * the ModbusDriver service at a high level. Users that want to collect infomation froma modbus device only have to configure data
 * sampling that will map information from the adress space of a device in the network available as a Measurement or State object
 * through the Wire object of the WireAdmin service paradigme.
 *
 * The modbusdevice.impl service implement a Composite Producer service. This is because the values that can be read from a modbus
 * device are multiples. Every value is then returned as an Envelope object (see org.osgi.services.measurement bundle ); Therefore
 * it is important when the user configure the measurement in the cm store to include scope and identification values precisely. 
 *
 *
 * @author Laurent Thil
 * @version 1.0
 */
public class ModbusDeviceImpl extends ModbusDeviceAbstractImpl {

	
	// A thread object that get hold of an Modbus driver service and delegate the reading to the MeasurementFactory and StateFactory.
	private ModbusDriverCommunicator mdbCommunicator = null;
	
	/**
	 * ModbusDevice
	 *
	 *
	 *
	 */
	public ModbusDeviceImpl( String pid, String compositeIdentity, String modbusPort, int modbusId, int  modbusPoolingRateS, ArrayList mdbReferenceList, ArrayList mdbMeasurementList, ArrayList mdbStateList ) throws Exception {
		
		// Force the list of wires to null. It will get initialiwed by the wire admin.
		consumerWires = null;
		// Initialise the list of ModbusData to an empty list. We'll feed it latter.
		modbusData = new ArrayList();
		// Crate the reference data buffer ..
		modbusRDataBuffer = new ReferenceDataBuffer();
		// Create the measurement data buffer ..
		modbusMDataBuffer = new MeasurementDataBuffer();
		// and the state data buffer now. We'll need to give them as delegates to the modbus data objects.
		modbusSDataBuffer = new StateDataBuffer();
	
		init(pid, compositeIdentity, mdbReferenceList, mdbMeasurementList, mdbStateList);
		
		// Create a new ModbusDriverCommunicator. Will be responsible to communicate with the device.
		mdbCommunicator = new ModbusDriverCommunicator(modbusPort, modbusId, modbusPoolingRateS);

	}
	
	/**
	 * Stop this ModbusDevice; Stopping consist in stopping any threads instanciated by this service. Here we
	 * stop the ModbusCommunicator thread that pool on the ModbusDriver service. Everything else is taken care
	 * of by the framework.
	 *
	 *
	 */
	public void stop() {
	
		mdbCommunicator.disconnect();
		sReg.unregister();
		Activator.log(LogService.LOG_INFO, "Unregistered " + this.getClass().getName());
	}
	 
	public int getId() {
		return mdbCommunicator.getId();
	}
	
	public String getPortName() {
		return mdbCommunicator.getPortName();
	}
	
	/**
	  * The ReferenceDataBuffer hold a array of bytes that is read from the modbus device through the ModbusDriver service.
	  * The data is read back from the device holding registers. The access to its internal data is controlled through its
	  * two synchronized method, so that a read cannot be done while the data is updated from the device. The size of the
	  * array of data read back from the device is set through the init method.
	  *
	  */
	private class ReferenceDataBuffer implements ModbusDataBufferDelegate{
	
		private int offset = 0;
		private int count = 0;
		private int registers[];
		private long updateTime = 0;
	
		/**
		 * Setup the size and starting offset of the buffer array.
		 *
		 * @param count The number of bytes to read.
		 * @param offset The adress of the first byte to read in the device adress space.
		 */
		public void init(int offset, int count){
	
			this.count = count;
			this.offset = offset;
			
		}
		
		/**
		 * Read a value from the buffered array.
		 *
		 * @param adress The adress of the calue to read
		 * @param size The size of the value to read in bits
		 * @return The value.
		 */
		public synchronized int read( int adress, int size ) {
		
			if( registers != null ) {
				if((adress >= offset ) && ( adress <= ( offset + count ))) {
					return registers[adress - offset];
				} else
					return 0;
			} else
				return 0;
		}
		
		public long getLastUpdateTime() {
			return updateTime;
		}
		
		
		/**
		 * Update the internal array buffer from the modbus device.
		 *
		 * @param mdbDriver An instance of the ModbusDriver service.
		 * @param id The identification of the slave device on the modbus network.
		 */
		synchronized void update( ModbusDriver mdbDriver, int id ) {
			// ensure we got a driver
			if( mdbDriver != null) {
				int regs[] = mdbDriver.readHoldingRegisters((byte)id, offset, count);
				if( regs != null && (regs.length == count)) {
					registers = regs;
					updateTime = System.currentTimeMillis();
				}
			}
		}
	}
	
	 /**
	  * The MeasuermentDataBuffer hold a array of bytes that is read from the modbus device through the ModbusDriver service.
	  * The data is read back from the device holding registers. The access to its internal data is controlled through its
	  * two synchronized method, so that a read cannot be done while the data is updated from the device. The size of the
	  * array of data read back from the device is set through the init method.
	  *
	  */
	private class MeasurementDataBuffer implements ModbusDataBufferDelegate{
	
		private int offset = 0;
		private int count = 0;
		private int registers[];
		private long updateTime = 0;
	
		/**
		 * Setup the size and starting offset of the buffer array.
		 *
		 * @param count The number of bytes to read.
		 * @param offset The adress of the first byte to read in the device adress space.
		 */
		public void init(int offset, int count){
	
			this.count = count;
			this.offset = offset;
			
		}
		
		/**
		 * Read a value from the buffered array.
		 *
		 * @param adress The adress of the calue to read
		 * @param size The size of the value to read in bits
		 * @return The value.
		 */
		public synchronized int read( int adress, int size ) {
		
			if( registers != null ) {
				if((adress >= offset ) && ( adress <= ( offset + count ))) {
					return registers[adress - offset];
				} else
					return 0;
			} else
				return 0;
		}
		
		public long getLastUpdateTime() {
			return updateTime;
		}
		
		
		/**
		 * Update the internal array buffer from the modbus device.
		 *
		 * @param mdbDriver An instance of the ModbusDriver service.
		 * @param id The identification of the slave device on the modbus network.
		 */
		synchronized void update( ModbusDriver mdbDriver, int id ) {
			// ensure we got a driver
			if( mdbDriver != null) {
				int regs[] = mdbDriver.readHoldingRegisters((byte)id, offset, count);
				if( regs != null && (regs.length == count)) {
					registers = regs;
					updateTime = System.currentTimeMillis();
				}
			}
		}
	}
	
	 /**
	  * The StateDataBuffer hold a array of bytes that is read from the modbus device through the ModbusDriver service.
	  * The data is read back from the device inputs registers. The access to its internal data is controlled through its
	  * two synchronized method, so that a read cannot be done while the data is updated from the device. The size of the
	  * array of data read back from the device is set through the init method.
	  *
	  */
	private class StateDataBuffer implements ModbusDataBufferDelegate{
	
		
		private int offset = 0;
		private int count = 0;
		private int registers[];
		private long updateTime = 0;
		
		/**
		 * Setup the size and starting offset of the buffer array.
		 *
		 * @param count The number of bytes to read.
		 * @param offset The adress of the first byte to read in the device adress space.
		 */
		public void init(int offset, int count){
			
			this.count = count;
			this.offset = offset;
		}
		
		/**
		 * Read a value from the buffered array.
		 *
		 * @param adress The adress of the calue to read
		 * @param size The size of the value to read in bits
		 * @return The value.
		 */
		public synchronized int read( int adress, int size ) {
			
			if( registers != null ) {
				if((adress >= offset ) && ( adress <= ( offset + count ))) {
					return registers[adress - offset];
				} else
					return 0;
			} else
				return 0;
		}
		
		public long getLastUpdateTime() {
			return updateTime;
		}
		
		/**
		 * Update the internal array buffer from the modbus device.
		 *
		 * @param mdbDriver An instance of the ModbusDriver service.
		 * @param id The identification of the slave device on the modbus network.
		 */
		synchronized void update( ModbusDriver mdbDriver, int id ) {
	
			// ensure we got a driver
			if( mdbDriver != null) {
				int regs[] = mdbDriver.readInputRegisters((byte)id, offset, count);
				if( regs != null && (regs.length == count)) {
					registers = regs;
					updateTime = System.currentTimeMillis();
				}
			}
		}
	}
	
	
	
	/**
	 * ModbusDriverCommunicator managed the communication with the modbus device; Its send modbus requests
	 * to get state of the holding and input registers, and stores the response in the outer class HoldingRegisters
	 * and InputRegisters objects. The class own a thread that pool the device at regular time.
	 * The communication to the modbus link is done through the ModbusDriver service. The class is registered as a Listener
	 * to the service, so it will received even when the ModbusDriver service is registered and de-registered from
	 * the framework. The class will only send messages to the Modbus link if it got a valid instance of a 
	 * ModbusDriver service.
	 *
	 */
	
	private class ModbusDriverCommunicator implements Runnable, ServiceListener {
	
		// the id of the ModbusDevice
		private int id;
		// the port name to use.
		private String portName;
		// a reference to the modbus driver service
		private ModbusDriver modbusDr;
		// a flag asking to suspend communicating with the device.
		private boolean disconnect = false;
		// pooling time between successive communications with the modbus device.
		private long poolingTimeMill = 1000;
		// the thread that manage the communucation work with the device.
		Thread myThread = null;
		
		public ModbusDriverCommunicator( String modbusPort, int modbusId, int  modbusPoolingRateS ) {
			this.id = modbusId;
			this.portName = modbusPort;
			this.poolingTimeMill = ((long)modbusPoolingRateS);
			
			// We first need to get a reference to the modbus driver service. We try to get this reference dynamically
			// by constructing a listener that will detect when the modbus driver service appear or disapear.
			String filter = "(&(objectclass=" + modbusDriverServiceName + ")"
				+ "(" + ModbusDriver.MODBUS_DRIVER_SERVICE_PORT + "=" + modbusPort + "))";
		
			try {
				Activator.bc.addServiceListener( this, filter);
				// in case the service is already registered, we send a REGISTERED event to its listener.
				ServiceReference srModbus[] = Activator.bc.getServiceReferences( modbusDriverServiceName, filter );
				if( srModbus != null ) {
					this.serviceChanged(new ServiceEvent( ServiceEvent.REGISTERED, srModbus[0] ));
				}
			} catch ( InvalidSyntaxException e ) {
				// We known there shouldn't be an exception thrown here since we made the filter string.
			}
		}
		
		public int getId(){
			return this.id;
		}
		
		public String getPortName() {
			return this.portName;
		}
		
		public void disconnect(){
			disconnect = true;
		}
		
		/**
		 * Unique method of the Runnable interface. 
		 * Excecute the class ModbusDriverCommunicator's thread that will send messages
		 * to the actual device in order to actualise it's buffered state in this class.
		 * After the thread has sent all necessary messages, it will pause for a
		 * predefined time.
		 *
		 */
		public void run() {
			while(!disconnect){
				if(modbusDr != null ) {
					((MeasurementDataBuffer)modbusMDataBuffer).update(modbusDr, id);
					((ReferenceDataBuffer)modbusRDataBuffer).update(modbusDr, id);
					((StateDataBuffer)modbusSDataBuffer).update(modbusDr, id);
					refreshWires();
				}
				try {
					Thread.sleep( poolingTimeMill );
				} catch (InterruptedException e) {
					break;
				}
			}
		}
		
		/**
		 * Unique method of the ServiceListener interface. The framework invoke this method when
		 * a event has been posted. Since we registered this listener for registered and unregistered
		 * event from the modbus driver service, we will receive notification of theses here.
		 *
		 */
		public void serviceChanged( ServiceEvent event ) {
			
				ServiceReference sr = event.getServiceReference();
				switch(event.getType()) {
					case ServiceEvent.REGISTERED: {
						modbusDr = (ModbusDriver) Activator.bc.getService(sr);
						myThread = new Thread(this);
						myThread.start();
						Activator.log( LogService.LOG_INFO, "Getting instance of service: " + (String) sr.getProperty( Constants.SERVICE_PID)
							+ ", " + Constants.SERVICE_ID + "=" + ((Long)sr.getProperty(Constants.SERVICE_ID)).toString() );
					}
					break;
					case ServiceEvent.UNREGISTERING: {
						Activator.log( LogService.LOG_INFO, "Releasing instance of service: " + (String) sr.getProperty( Constants.SERVICE_PID)
							+ ", " + Constants.SERVICE_ID + "=" + ((Long)sr.getProperty(Constants.SERVICE_ID)).toString());
						modbusDr = null;
						myThread.interrupt();
					}
					break;
				}
		}
	}
	
}
