

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
import com.ptoceti.osgi.modbusdevice.ModbusDevice;
import com.ptoceti.osgi.modbusdevice.ModbusCommand;
import org.osgi.framework.*;
import org.osgi.service.dal.Device;
import org.osgi.service.dal.DeviceException;
import org.osgi.service.device.Constants;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.*;

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
public class ModbusDeviceImpl implements ModbusDevice, ModbusDriverListener, Device {


    String pid;
    String name;
    String serialPort;
    Integer modbusPoolingRate;

    Integer deviceStatus = Device.STATUS_PROCESSING;
    Integer deviceStatusDetails = Device.STATUS_DETAIL_INITIALIZING;

    ServiceRegistration sReg;
    ServiceTracker modbusDriverTracker;


    ExecutorService executorService;

    /**
     * ModbusDevice
     */
    public ModbusDeviceImpl(String pid, String name, String serialPort, Integer modbusPoolingRate) {
        this.pid = pid;
        this.name = name;
        this.serialPort = serialPort;
        this.modbusPoolingRate = modbusPoolingRate;

    }

    public void start() {

        executorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<Runnable>(10, new PriorityFutureComparator())) {

            protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
                RunnableFuture<T> newTaskFor = super.newTaskFor(callable);
                return new PriorityFuture<T>(newTaskFor, ((ModbusCommand) callable).getPriority());
            }

        };


        String[] clazzes = new String[]{
                ModbusDevice.class.getName(),
                Device.class.getName()
        };


        Dictionary props = new Hashtable();
        props.put(Device.SERVICE_DRIVER, ModbusDriver.MODBUS_DRIVER_NAME);
        props.put(Device.SERVICE_UID, ModbusDriver.MODBUS_DRIVER_NAME + ":" + name);
        props.put(Device.SERVICE_NAME, name);
        props.put(Device.SERVICE_DESCRIPTION, "Modbus DAL device");
        props.put(Device.SERVICE_STATUS, deviceStatus);
        props.put(Device.SERVICE_STATUS_DETAIL, deviceStatusDetails);
        props.put(org.osgi.framework.Constants.SERVICE_PID, this.pid);
        props.put(org.osgi.service.device.Constants.DEVICE_CATEGORY, new String[]{Device.DEVICE_CATEGORY});


        sReg = Activator.getBc().registerService(clazzes, this, props);

        Activator.getLogger().info("Registered " + this.getClass().getName());

        String modbusDeviceFactoryFilterSpec = "(&(objectClass=" + ModbusDriver.class.getName() + ")(" + ModbusDriver.MODBUS_DRIVER_SERVICE_PORT + "=" + serialPort + "))";
        try {
            ModbusDriverListener modbusDriverListener = this;
            Filter deviceFilter = Activator.getBc().createFilter(modbusDeviceFactoryFilterSpec);
            modbusDriverTracker = new ServiceTracker(Activator.getBc(), deviceFilter, null) {
                @Override
                public Object addingService(ServiceReference reference) {
                    Object device = super.addingService(reference);
                    ((ModbusDriver) device).addListener(modbusDriverListener);
                    Activator.getLogger().info("ModbusDevice detect driver added: " + reference.getProperty(Constants.DRIVER_ID));
                    return device;
                }

                @Override
                public void removedService(ServiceReference reference, Object service) {
                    ((ModbusDriver) service).removeListener(modbusDriverListener);
                    super.removedService(reference, service);
                    Activator.getLogger().info("ModbusDevice detect driver removed: " + reference.getProperty(Constants.DRIVER_ID));
                }
            };
            modbusDriverTracker.open();


        } catch (InvalidSyntaxException ex) {
            Activator.getLogger().error("Error creating ModbusDriver tracker: " + ex.toString());
        }

    }

    public void stop() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
        executorService = null;
        modbusDriverTracker.close();
        sReg = null;
    }

    public Future submitCommand(ModbusCommand command) {
        return executorService.submit(command);
    }

    @Override
    public Object getServiceProperty(String s) {
        return sReg.getReference().getProperty(s);
    }

    @Override
    public String[] getServicePropertyKeys() {
        return sReg.getReference().getPropertyKeys();
    }

    @Override
    public void remove() throws DeviceException {
        deviceStatus = Device.STATUS_REMOVED;
        deviceStatusDetails = null;
        updateServiceProperties();
        sReg.unregister();
        stop();
    }

    @Override
    public void modbusDriverConnected() {
        deviceStatus = Device.STATUS_ONLINE;
        deviceStatusDetails = null;
        updateServiceProperties();
        Activator.getLogger().debug("ModbusDevice detect driver connected.");
    }

    @Override
    public void modbusDriverDisconnected() {
        deviceStatus = Device.STATUS_OFFLINE;
        deviceStatusDetails = Device.STATUS_DETAIL_BROKEN;
        updateServiceProperties();
        Activator.getLogger().debug("ModbusDevice detect driver disconnected.");
    }

    protected void updateServiceProperties() {

        if (sReg != null) {
            Dictionary props = new Hashtable();
            props.put(Device.SERVICE_DRIVER, ModbusDriver.MODBUS_DRIVER_NAME);
            props.put(Device.SERVICE_UID, ModbusDriver.MODBUS_DRIVER_NAME + ":" + name);
            props.put(Device.SERVICE_NAME, name);
            props.put(Device.SERVICE_DESCRIPTION, "Modbus DAL device");
            props.put(Device.SERVICE_STATUS, deviceStatus);
            props.put(org.osgi.framework.Constants.SERVICE_PID, this.pid);
            if (deviceStatusDetails != null) {
                props.put(Device.SERVICE_STATUS_DETAIL, deviceStatusDetails);
            }
            props.put(org.osgi.service.device.Constants.DEVICE_CATEGORY, new String[]{Device.DEVICE_CATEGORY});

            sReg.setProperties(props);
        }
    }

    public ModbusDriver getModbusDriver() {
        return (ModbusDriver) modbusDriverTracker.getService();
    }
}
