

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
import com.ptoceti.osgi.modbus.ModbusDriverListener;
import com.ptoceti.osgi.modbus.impl.connection.ModbusConnection;
import com.ptoceti.osgi.modbus.impl.connection.ModbusSerialASCIIConnection;
import com.ptoceti.osgi.modbus.impl.connection.ModbusSerialRTUConnection;
import com.ptoceti.osgi.modbus.impl.message.ModbusMessageRequest;
import com.ptoceti.osgi.modbus.impl.message.ModbusMessageResponse;

import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import org.osgi.service.device.Constants;
import org.osgi.service.device.Device;
import org.osgi.service.device.Driver;
import org.osgi.service.serial.SerialDevice;
import org.osgi.service.serial.SerialEventListener;
import org.osgi.util.tracker.ServiceTracker;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Dictionary;
import java.util.Set;

/**
 * Abstract class for the ModbusDriver. Implement common functionalities for the ModbuMaster ans ModbusSlave
 * classes.
 *
 * @author Laurent Thil
 * @version 1.0b
 */

public abstract class ModbusDriverImpl implements ModbusDriver, Driver {

    ServiceRegistration sReg;
    byte id;

    String pid;

    String portName;
    String encoding;
    int baudRate;
    boolean usesParity;
    boolean evenParity;
    boolean echo;

    protected ServiceReference sRegSerialDevice;
    protected SerialDevice serialDevice;

    protected ServiceRegistration sRegSerialListener;

    ModbusConnection mdbConnection;

    String filterSpec = "(&(objectclass=" + SerialDevice.class.getName() + ")" + "(DEVICE_CATEGORY=" + SerialDevice.DEVICE_CATEGORY + "))";
    Filter filter;

    ServiceTracker serialDeviceTracker;

    Set<ModbusDriverListener> modbusDriverListeners = new HashSet<ModbusDriverListener>();


    public ModbusDriverImpl(String pid, int id, String portName, String encoding, int baudRate, boolean usesParity, boolean evenParity) throws Exception {
        this.pid = pid;
        this.setID((byte) id);

        filter = Activator.bc.createFilter(filterSpec);

        this.portName = portName;
        this.encoding = encoding;
        this.baudRate = baudRate;
        this.usesParity = usesParity;
        this.evenParity = evenParity;
        this.echo = echo;
    }

	public void start() {

        String[] clazzes = new String[]{
                ModbusDriver.class.getName(),
                Driver.class.getName()
        };

        Dictionary props = new Hashtable();
        props.put(org.osgi.framework.Constants.SERVICE_DESCRIPTION, "ModbusDriver service implements a Driver interface.");
        props.put(ModbusDriver.MODBUS_DRIVER_SERVICE_PORT, portName);
        props.put(Constants.DRIVER_ID, ModbusDriver.class.getPackage().getName() + "." + portName);
        props.put(org.osgi.framework.Constants.SERVICE_PID, this.pid);

        sReg = Activator.bc.registerService(clazzes, this, props);

        Activator.getLogger().info("Registered " + this.getClass().getName());

    }
	
	public void stop() {

        if (sRegSerialListener != null) {
            sRegSerialListener.unregister();
            sRegSerialListener = null;
        }
        if (mdbConnection != null) {
            mdbConnection.close();
            mdbConnection = null;
        }
        sReg.unregister();

        Activator.getLogger().info("Unregistered " + this.getClass().getName());
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
	 * Set the modbus identificator for this modbus driver. The identificator is used when communicating
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
	public synchronized ModbusMessageResponse sendMessage(ModbusMessageRequest message) {
		return mdbConnection.sendMessage(message);
	}


	@Override
	public int match(ServiceReference serviceReference) throws Exception {
		Activator.getLogger().debug("matching " + Activator.deviceDetails(serviceReference));
		if (filter.match(serviceReference)) {
			String serialDeviceComPort = Activator.getSerialEventListenerComPort(serviceReference);
			if ((serialDeviceComPort != null) && (serialDeviceComPort.equals(portName)))
				return 10;
			else return Device.MATCH_NONE;
		} else
			return Device.MATCH_NONE;
	}

	@Override
	public String attach(ServiceReference serviceReference) throws Exception {
		Activator.getLogger().info("Attached to device " + Activator.deviceDetails(serviceReference));
		sRegSerialDevice = serviceReference;
		serialDevice = (SerialDevice) Activator.bc.getService(sRegSerialDevice);

		if (encoding.equals(ModbusDriver.RTU_ENCODING))
			mdbConnection = new ModbusSerialRTUConnection(serialDevice, portName, baudRate, usesParity, evenParity, false);
		else if (encoding.equals(ModbusDriver.ASCII_ENCODING))
            mdbConnection = new ModbusSerialASCIIConnection(serialDevice, portName, baudRate, usesParity, evenParity, false);

        // set mdbConnection to listen to data available events.
        String[] clazzes = new String[]{
                SerialEventListener.class.getName()
        };
        Dictionary props = new Hashtable();
        props.put(SerialEventListener.SERIAL_COMPORT, portName);
        sRegSerialListener = Activator.bc.registerService(clazzes, mdbConnection, props);

        createSerialDeviceTracker(serviceReference);

        return null;
    }

    protected void createSerialDeviceTracker(ServiceReference serviceReference) {
        // track in case he serial device diseapear ..
        if (serialDeviceTracker != null) {
            serialDeviceTracker.close();
        }
        serialDeviceTracker = new ServiceTracker(Activator.bc, serviceReference, null) {
            @Override
            public Object addingService(ServiceReference reference) {
                Object device = super.addingService(reference);

                modbusDriverListeners.forEach(listener -> listener.modbusDriverConnected());
                return device;
            }

            @Override
            public void removedService(ServiceReference reference, Object service) {
                super.removedService(reference, service);

                modbusDriverListeners.forEach(listener -> listener.modbusDriverDisconnected());

                if (sRegSerialListener != null) {
                    sRegSerialListener.unregister();
                    sRegSerialListener = null;
                }
                if (mdbConnection != null) {
                    mdbConnection.close();
                    mdbConnection = null;
                }
                sRegSerialDevice = null;
                serialDevice = null;
            }
        };
        serialDeviceTracker.open();
    }

    public void addListener(ModbusDriverListener listener) {
        modbusDriverListeners.add(listener);
    }

    public void removeListener(ModbusDriverListener listener) {
        modbusDriverListeners.remove(listener);
    }
}
