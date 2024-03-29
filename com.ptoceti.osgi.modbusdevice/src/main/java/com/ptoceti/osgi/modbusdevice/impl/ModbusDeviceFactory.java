

package com.ptoceti.osgi.modbusdevice.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : ModbusDevice
 * FILENAME : ModbusDeviceFactory.java
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

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;

import java.util.Hashtable;
import java.util.Dictionary;
import java.util.Enumeration;

/**
 * ModbusDeviceFactory obey the factory design pattern. It is a factory for creating ModusDevice 
 * implementations classes .
 * The class also implement the ManagedServiceFactory, which means it can receive configuration from
 * ConfigurationManager service of the framework for each object it has to create. This information is
 * passed on through the updated(..) method.
 * At initialisation, the class does not create any object, it only registers itself as a ManagedServiceFactory
 * for receiving the configuration. It then create the objects as the configuration is loaded.
 * It is slightly different from a normal factory. The class will create the objects as the configuration
 * manager indicate. The objects ( instances of ModbusDevice ) will then be registerd in the framework for
 * use by other services.
 *
 * @author Laurent Thil
 * @version 1.0
 */

public class ModbusDeviceFactory implements org.osgi.service.cm.ManagedServiceFactory {

    // the hashtable contain the references to all ModbusDevice instances created.
    Hashtable<String, ModbusDeviceImpl> modbusDevServices;
    // a reference to the service registration for the ModbusDeviceFactory.
    ServiceRegistration modbusDevFactoryReg = null;

    public static String modbusConfigProperty = "com.ptoceti.osgi.modbusdevice.config";

    public static String nameKey = "com.ptoceti.osgi.modbusdevice.name";
    public static String portNameKey = "com.ptoceti.osgi.modbusdevice.portName";
    public static String poolingRateKey = "com.ptoceti.osgi.modbusdevice.poolingRate";


    /**
     * Create a new ModbusDeviceFactory instance. Register the class instance as a ManagedServiceFactory.
     * The class will be recognised as such by the framework allowing it to pass on configuration data.
     */
    public ModbusDeviceFactory() {
        // create a new hastable that will contain references to all the ModbusDevice modules.
        modbusDevServices = new Hashtable<String, ModbusDeviceImpl>();
        // register the class as a service factory.
        Hashtable properties = new Hashtable();
        properties.put(Constants.SERVICE_PID, "com.ptoceti.osgi.modbusdevice.ModbusDeviceFactory");
        modbusDevFactoryReg = Activator.getBc().registerService(ManagedServiceFactory.class.getName(),
                this, properties);

        Activator.getLogger().info("Registered " + ModbusDeviceFactory.class.getName()
                + " as " + ManagedServiceFactory.class.getName() + ", Pid = " + (String) properties.get(Constants.SERVICE_PID));
    }
	
	/**
	 * Uregistered the class from the service registration system.
	 * 
	 * 
	 */
	public void stop() {

        // Unregister the factory first ..
        modbusDevFactoryReg.unregister();
        // .. second, stop all the ModbusDevice services.
        for (Enumeration<ModbusDeviceImpl> mdbServs = modbusDevServices.elements(); mdbServs.hasMoreElements(); ) {
            ModbusDeviceImpl mdbDev = mdbServs.nextElement();
            mdbDev.stop();
        }

        Activator.getLogger().info("Unregistered " + ModbusDeviceFactory.class.getName());
    }

	/**
	 * ManagedServiceFactory Interface method
	 * Called by the framewok when the configuration manager has fond new configuration for this service.
	 *
	 * @param pid The persistent identificator of the ModbusDevice service to update.
	 * @param properties The new properties for this service.
	 */
	public void updated(String pid, Dictionary properties) {


        String portName = (String) properties.get(portNameKey);
        String name = (String) properties.get(nameKey);
        Object rate = properties.get(poolingRateKey);
        Integer poolingRate = rate instanceof Integer ? (Integer) rate : Integer.parseInt(rate.toString());


        // We need to check if the servive with the given pid already exist in our collection. This would
        // mean that the configuration has been updated.
        ModbusDeviceImpl modbusDevSer = (ModbusDeviceImpl) modbusDevServices.get(pid);
        if (modbusDevSer != null) {
            // in which case, the simplest is to get rid of the existing instance, and recreate a brand new one.
            modbusDevSer.stop();
            modbusDevServices.remove(modbusDevSer);
        }

        modbusDevSer = new ModbusDeviceImpl(pid, name, portName, poolingRate);
        add(pid, modbusDevSer);
        modbusDevSer.start();

    }

    /**
     * Add a ModbusDevice object to the internal list
     *
     * @param pid    : the persistant identifier of the device class.
     * @param device : the ModbusDevice object to add.
     */
    protected void add(String pid, ModbusDeviceImpl device) {

        // add this instance to the hashtable.
        modbusDevServices.put(pid, device);
        Activator.getLogger().info("ModbusDeviceFactory: created ModbusDevice, pid=" + pid);
    }

    /**
     * ManagedServiceFactory Interface method
     * Called by the framewok when one of the service instance created by
     * the factory is removed.
     *
	 * @param pid: the service instance persistant identificator
	 */
	public void deleted(String pid) {

        ModbusDeviceImpl modbusDevSer = modbusDevServices.get(pid);
        // simple precaution, we first check that we effectively got an instance with this pid
        if (modbusDevSer != null) {
            // then we got rid of it.
            modbusDevSer.stop();
            modbusDevServices.remove(pid);
            Activator.getLogger().info("Removed ModbusDevice type: " + modbusDevSer.getClass().getName() + ", service pid: " + pid);
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
