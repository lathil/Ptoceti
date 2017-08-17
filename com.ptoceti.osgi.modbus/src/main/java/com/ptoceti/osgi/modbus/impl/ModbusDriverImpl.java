

package com.ptoceti.osgi.modbus.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Modbus
 * FILENAME : ModbusDriverImpl.java
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

import com.ptoceti.osgi.modbus.ModbusDriver;
import com.ptoceti.osgi.modbus.impl.connection.ModbusConnection;
import com.ptoceti.osgi.modbus.impl.message.ModbusMessageRequest;
import com.ptoceti.osgi.modbus.impl.message.ModbusMessageResponse;

import org.osgi.framework.ServiceRegistration;

import org.osgi.service.log.LogService;

import java.util.Hashtable;
import java.util.Dictionary;

/**
 * Abstract class for the ModbusDriver. Implement common functionalities for the ModbuMaster ans ModbusSlave
 * classes.
 *
 *
 * @author Laurent Thil
 * @version 1.0b
 */
 
public abstract class ModbusDriverImpl implements ModbusDriver {

	ServiceRegistration sReg;
	byte id;
		
	ModbusConnection mdbConnection;

	public void start() {
	
		String[] clazzes = new String[] {
			ModbusDriver.class.getName()
		};
			
		Dictionary props = new Hashtable();
		props.put( org.osgi.framework.Constants.SERVICE_PID, ModbusDriver.class.getName());
		props.put( org.osgi.framework.Constants.SERVICE_DESCRIPTION, "ModbusDriver service implements a Device interface.");
		props.put( ModbusDriver.MODBUS_DRIVER_SERVICE_PORT, mdbConnection.getPortName());
		
		sReg = Activator.bc.registerService( clazzes, this, props );
		
		Activator.log(LogService.LOG_INFO, "Registered " + this.getClass().getName());

	}
	
	public void stop() {
	
		if( mdbConnection != null ) mdbConnection.close();
		sReg.unregister();
		
		Activator.log(LogService.LOG_INFO, "Unregistered " + this.getClass().getName());
	}
	
	/**
	 * Must be implemented by derived class. Indicate whether the sub class is master or slave type.
	 *
	 */
	public abstract boolean isMaster();
	
	/**
	 * Must be implemented by derived class. Indicate whether the sub class is master or slave type.
	 *
	 */
	public abstract boolean isSlave();

	/**
	 * Set the modbus identificator for this lodbus driver. The identificator is used when communicating
	 * on the bus.
	 *
	 * @param id: the modbus identificator
	 */
	public void setID(byte id ) {
		this.id = id;
	}
	
	/**
	 * Return this modbus driver's identificator
	 *
	 * @return the modbus identificator.
	 */
	public byte getID() {
		return this.id;
	}
	
	/**
	 * Must be implemented by derived class. Indicate the port used by the connection.
	 */
	public abstract String getPortName();
	
	/**
	 * Return the encoding type used to send frames on the modbus seial bus. This would be either ascii or rtu.
	 *
	 * @return  ModbusDriver.ASCII_ENCODING or ModusDriver.RTU_ENCODING
	 */
	public String getEncodingType() {
	
		if( mdbConnection != null ) return mdbConnection.getEncodingType();
		else return null;
	}
	
	/**
	 * Send a ModbusMessage request over the modbus serial bus and return a ModbusMessage response.
	 *
	 * @param message: the message to send to a slave modbus driver
	 * @return the response as a modbus message from the the slave. If no response was sent back from
	 * the slave, null is returned.
	 */
	public synchronized ModbusMessageResponse sendMessage(ModbusMessageRequest message ) {
		return mdbConnection.sendMessage( message );
	}
	
	/**
	 * Device interface's method. Called back from the Device manager to say it has not found any driver. We do not care too
	 * much here because we do not expect ant (have not written a driver anyway !).
	 *
	 */
	public void noDriverFound() {}
	
}
