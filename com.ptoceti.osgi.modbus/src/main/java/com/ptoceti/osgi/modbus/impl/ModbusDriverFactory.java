

package com.ptoceti.osgi.modbus.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Modbus
 * FILENAME : ModbusDriverFactory.java
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

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.log.LogService;

import java.util.Hashtable;
import java.util.Dictionary;
import java.util.Enumeration;

/**
 * ModbusDriverFactory obey the factory design pattern. It is a factory for creating ModusDriver 
 * implementations classes ( the ModbusDriver classes are the real device services used by other services ).
 * The class also implement the ManagedServiceFactory, which means it can receive configuration from
 * ConfigurationManager service of the framework for each object it has to create. This information is
 * passed on through the updated(..) method.
 * At initialisation, the class does not create any object, it only registers itself as a ManagedServiceFactory
 * for receiving the configuration. It then create the objects as the configuration is loaded.
 * It is slightly different from a normal factory. The class will create the objects as the configuration
 * manager indicate. The objects ( instances of ModbusDriver ) will then be registerd in the framework for
 * use by other services.
 *
 * @author Laurent Thil
 * @version 1.0b
 */
public class ModbusDriverFactory implements org.osgi.service.cm.ManagedServiceFactory {

	// the hashtable contain the references to all ModbusDrivers instances created.
	Hashtable modbusDrivers;
	// a reference to the service registration for the ModbusDriverFactory.
	ServiceRegistration modbusDriverFactoryReg = null;
	
	/**
	 * Create a new ModbusDriverFactory instance. Register the class instance as a ManagedServiceFactory.
	 * The class will be recognised as such by the framework allowing it to pass on configuration data.
	 *
	 * @throws Exception
	 */
	public ModbusDriverFactory() throws Exception {
		// create a new hastable that will contain references to all the modbusdriver services.
		modbusDrivers = new Hashtable();
		// register the class as a service factory.
		Hashtable properties = new Hashtable();
		properties.put( Constants.SERVICE_PID, this.getClass().getName());
		modbusDriverFactoryReg = Activator.bc.registerService(ManagedServiceFactory.class.getName(),
				this, properties );
		
		Activator.log(LogService.LOG_INFO, "Registered " + ModbusDriverFactory.class.getName()
			+ " as " + ManagedServiceFactory.class.getName());
	}
	
	/**
	 * Uregistered the class from the service registration system.
	 *
	 *
	 */
	public void stop() {
		// Unregister the factory first ..
		modbusDriverFactoryReg.unregister();
		// .. second, stop all the ModbusDriver services.
		for( Enumeration mdbDrivs = modbusDrivers.elements(); mdbDrivs.hasMoreElements(); ) {
			ModbusDriverImpl mdbDrvImpl= (ModbusDriverImpl) mdbDrivs.nextElement();
			mdbDrvImpl.stop();
		}

		Activator.log(LogService.LOG_INFO, "Unregistered " + ModbusDriverFactory.class.getName());
	}
	
	/**
	 * ManagedServiceFactory Interface method
	 * Called by the framewok when the configuration manager has fond new configuration for this service.
	 * Four properties are expected from each configuration set: modbus_port, modbus_slave_master, 
	 * modbus_id and modbus_encoding ( theses values are described in the ModbusDriver interface ). For
	 * each configuration set, an ModbusDriver instance is created with the above properties. The instance
	 * will register itself as an available service to the framework.
	 *
	 * @param pid The persistent identificator of the service to update.
	 * @param properties The new properties collection for this service.
	 */
	public void updated(String pid, Dictionary properties) {
	
		String port = (String) properties.get(ModbusDriver.MODBUS_PORT);
		String type = (String) properties.get(ModbusDriver.MODBUS_SLAVE_MASTER);
		Object id = properties.get(ModbusDriver.MODBUS_ID);
		Integer modbusID = id instanceof Integer ? (Integer) id: Integer.parseInt(id.toString());
		String encoding = (String) properties.get(ModbusDriver.MODBUS_ENCODING);
		Object rate = properties.get(ModbusDriver.MODBUS_BAUDRATE);
		Integer baudRate = rate instanceof Integer ? (Integer) rate: Integer.parseInt(rate.toString());
		
		final Object up = properties.get(ModbusDriver.MODBUS_USESPARITY);
		final Boolean usesParity = up instanceof Boolean ? (Boolean) up: Boolean.parseBoolean(up != null ? up.toString(): "false");
		final Object ep =  properties.get(ModbusDriver.MODBUS_EVENPARITY);
		final Boolean evenParity = ep instanceof Boolean ? (Boolean) ep: Boolean.parseBoolean(ep != null ? ep.toString(): "false");
		
		final Object ec = properties.get(ModbusDriver.MODBUS_ECHO);
		Boolean echo = ec instanceof Boolean ? (Boolean) ec : Boolean.parseBoolean(ec != null ? ec.toString(): "false");

		// First check that we have all the configuration data necessary.
		if((port != null ) && (type != null) && (modbusID != null) && (encoding != null)
			&& (baudRate != null) && (usesParity != null) && (evenParity != null) && (echo != null)) {
		
			int modbusIDInt = ( modbusID ).intValue();
			int baudRateInt = ( baudRate ).intValue();
			boolean usesParityBool = ( usesParity).booleanValue();
			boolean evenParityBool = ( evenParity).booleanValue();
			boolean echoBool = ( echo ).booleanValue();
			
			// Then check that the configuration data is valid.
			if(((modbusIDInt > 0) && (modbusIDInt <= 254 )) &&
				( type.equals(ModbusDriver.MASTER) || type.equals(ModbusDriver.SLAVE))  &&
				( encoding.equals(ModbusDriver.RTU_ENCODING) || encoding.equals(ModbusDriver.ASCII_ENCODING)) )
			{
				
				// check if we have already created a ModbusDriver with this particular configuration pid.
				ModbusDriverImpl mdbDriver = (ModbusDriverImpl) modbusDrivers.get(pid);
				// if we already got it, we need to stop it. This will unregister the service from the framework.
				if( mdbDriver != null ) {
					mdbDriver.stop();
					// remove the old driver from the list
					modbusDrivers.remove(pid);
					// and clear the reference
					mdbDriver = null;
				}
				// then we need to create an new instance of a ModbusDriver, either master or slave kind. There could be errors when 
				// opening the serial port.
				try {
					if( type.equals( ModbusDriver.MASTER )) {
						mdbDriver = new ModbusMaster(modbusIDInt, port, encoding, baudRateInt, usesParityBool, evenParityBool, echoBool);
						Activator.log(LogService.LOG_INFO,"Created ModbusDriver type: " + mdbDriver.getClass().getName()
						+ ", encoding: " + encoding + ", port: " + port + ", id: " + modbusID + ", echo: " + echo  + ", service factory pid: " + pid);
					}
					else if( type.equals( ModbusDriver.SLAVE )) {
						mdbDriver = new ModbusSlave(modbusIDInt, port, encoding, baudRateInt, usesParityBool, evenParityBool);
						Activator.log(LogService.LOG_INFO,"Created ModbusDriver type: " + mdbDriver.getClass().getName()
						+ ", encoding: " + encoding + ", port: " + port + ", id: " + modbusID + ", service factory pid: " + pid);
					}
					else return;
				} catch ( Exception e ) {
					Activator.log(LogService.LOG_INFO, "Could not create ModbusDriver port. Reason: " + e.toString());
					mdbDriver = null;
				}
				// if we managed to create the modbus driver, we need to keep track of the instance.
				if( mdbDriver != null ) {
					// keep track of this instance.
					modbusDrivers.put(pid, mdbDriver);
					// and ask it to self register.
					mdbDriver.start();
				}
			}
			else {
				Activator.log(LogService.LOG_INFO,"Cannot create ModbusDriver service: bad configuration data.");
			}
			
		}
		else {
			String missingParam = "";
			if(port == null) missingParam = ModbusDriver.MODBUS_PORT;
			else if (type == null) missingParam = ModbusDriver.MODBUS_SLAVE_MASTER;
			else if (modbusID == null) missingParam = ModbusDriver.MODBUS_ID;
			else if (encoding == null) missingParam = ModbusDriver.MODBUS_ENCODING;
			else if (baudRate == null) missingParam = ModbusDriver.MODBUS_BAUDRATE;
			else if (usesParity == null) missingParam = ModbusDriver.MODBUS_USESPARITY;
			else if (evenParity == null) missingParam = ModbusDriver.MODBUS_EVENPARITY;
			else if (echo == null) missingParam = ModbusDriver.MODBUS_ECHO;
			Activator.log(LogService.LOG_INFO,"Cannot create MobusDriver service: configuration data missing: " + missingParam);
		}
	}
	
	/**
	 * ManagedServiceFactory Interface method
	 * Called by the framewok when one of the service instance created by
	 * the factory is removed.
	 *
	 * @param pid: the service instance persistant identificator
	 */
	public void deleted(String pid ) {
		ModbusDriverImpl mdbDriver = (ModbusDriverImpl) modbusDrivers.get(pid);
		// simple precaution, we first check that we effectively got an instance with this pid
		if( mdbDriver != null ) {
			// then we got rid of it.
			modbusDrivers.remove(pid);
			mdbDriver.stop();
			Activator.log(LogService.LOG_INFO,"Removed ModbusDriver type: " + mdbDriver.getClass().getName()
				+ ", service factory pid: " + pid );
		}
	}
	
	/**
	 * ManagedServiceFactory Interface method
	 *
	 * @return the name of this factory.
	 */
	public String getName() {
	
		return( this.getName());
	}
}
