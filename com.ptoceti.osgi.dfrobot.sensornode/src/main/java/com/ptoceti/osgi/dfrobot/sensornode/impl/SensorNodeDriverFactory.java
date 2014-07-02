package com.ptoceti.osgi.dfrobot.sensornode.impl;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.log.LogService;


/**
 * 
 * Factory for SensorNodedriver objects.
 * 
 * @author LATHIL
 *
 */
public class SensorNodeDriverFactory implements org.osgi.service.cm.ManagedServiceFactory {

	/**
	 * the hashtable contain the references to all ModbusDrivers instances created.
	 */
	Hashtable<String,SensorNodeDriver> sensorNodeDrivers;
	/**
	 * a reference to the service registration for the ModbusDriverFactory.
	 */
	ServiceRegistration sReg = null;
	
	/**
	 * 
	 * Create the SensorNode factory instance. register this one in the framework as a ManagedServiceFactory so that
	 * it will receive factory instances configuration.
	 * 
	 */
	SensorNodeDriverFactory() {
		// create a new hastable that will contain references to all the sensorNodedDriver services.
		sensorNodeDrivers = new Hashtable<String, SensorNodeDriver>();
		// register the class as a service factory.
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put( Constants.SERVICE_PID, this.getClass().getName());
		sReg = Activator.bc.registerService(ManagedServiceFactory.class.getName(),
				this, properties );
		
		Activator.log(LogService.LOG_INFO, "Registered " + SensorNodeDriverFactory.class.getName()
			+ " as " + ManagedServiceFactory.class.getName());
	}
	
	
	/**
	 * ManagedServiceFactory Interface method
	 *
	 * @return the name of this factory.
	 */
	public String getName() {
		return this.getName();
	}

	/**
	 * Called by the framework configuration admin with configuration required to build a new SensorNodedriver.
	 * 
	 * @param pid of the configuration under which to create the node driver
	 * @param properties the properties from the configuration store.
	 * 
	 */
	public void updated(final String pid, final Dictionary properties) throws ConfigurationException {
		
		final Integer id = Integer.valueOf((String) properties.get(SensorNodeDriver.SENSORNODE_ID));
		final String port = (String) properties.get(SensorNodeDriver.SENSORNODE_PORT);
		final Object rate = properties.get(SensorNodeDriver.SENSORNODE_BAUDRATE);
		final Integer baudRate = rate instanceof Integer ? (Integer) rate : Integer.parseInt(rate.toString());
		final Object up = properties.get(SensorNodeDriver.SENSORNODE_USESPARITY);
		final Boolean usesParity = up instanceof Boolean ? (Boolean) up: Boolean.parseBoolean(up.toString());
		final Object ep =  properties.get(SensorNodeDriver.SENSORNODE_EVENPARITY);
		final Boolean evenParity = ep instanceof Boolean ? (Boolean) ep: Boolean.parseBoolean(ep.toString());
		
		if( port != null && baudRate != null && usesParity != null && evenParity != null ){
			
			SensorNodeDriver driver = sensorNodeDrivers.get(pid);
			if( driver != null ){
				driver.stop();
				sensorNodeDrivers.remove(pid);
				driver = null;
			}
			
			try {
				driver = new SensorNodeDriver(id, port, baudRate, usesParity, evenParity);
				if( driver != null){
					Activator.log(LogService.LOG_INFO,"Created SensorNodeDriver: " + driver.getClass().getName()
							 + ", port: " + port + ", service factory pid: " + pid);
					sensorNodeDrivers.put(pid, driver);
					driver.start();
				}
			} catch ( Exception ex) {
				Activator.log(LogService.LOG_INFO, "Could not create SensorNodeDriver: " + ex.getMessage());
			}
			
		} else {
			Activator.log(LogService.LOG_ERROR, "SensorNodeDriverFactory: incorects parameters for factory.");
		}
	}

	/**
	 * ManagedServiceFactory Interface method
	 * Called by the framewok when one of the service instance created by
	 * the factory is removed.
	 *
	 * @param pid: the service instance persistant identificator
	 */
	public void deleted(String pid) {
		SensorNodeDriver driver = sensorNodeDrivers.get(pid);
		if( driver != null) {
			driver.stop();
			sensorNodeDrivers.remove(pid);
			Activator.log(LogService.LOG_INFO,"Removed SensorNodeDriver: " + driver.getClass().getName()
					+ ", service factory pid: " + pid );
		}
		
	}

}
