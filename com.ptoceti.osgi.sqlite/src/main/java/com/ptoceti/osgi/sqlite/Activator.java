 package com.ptoceti.osgi.sqlite;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : SQLite
 * FILENAME : Activator.java
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


import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.service.device.Constants;
import org.osgi.service.device.Driver;


/**
 * Activator class implement the BundleActivator interface. This class load the bundle in the framework.
 * Task is to register the SQLite Driver as a Driver service into the framework
 *
 * @author Laurent Thil
 * @version 1.0
 */

public class Activator implements BundleActivator{

	/**
	 *  a reference to this service bundle context.
	 */
	static BundleContext bc = null;
	/**
	 * a reference to the logging service.
	 */
	static LogService logSer;
	/**
	 * the name of the logging service in the osgi framework.
	 */
	static private final String logServiceName = org.osgi.service.log.LogService.class.getName();
	/**
	 * the service itself.
	 */
	private static SQLiteDriver sqliteService;
	
	/**
	 * Activator creator. Just create a base ObixServiceImpl object instance.
	 *
	 */
	public Activator()
	{
		
	}
	
	/**
	 * Called by the framework for initialisation when the Activator class is loaded.
	 * The method first get a service reference on the osgi logging service, used for
	 * logging whithin the bundle.
	 *
	 * If the method cannot get a reference to the logging service, a NullPointerException is thrown.
	 * @param context the bundle context
	 * @throws BundleException
	 */
	public void start(final BundleContext context) throws BundleException {
		
		Activator.bc = context;
		
		// we construct a listener to detect if the log service appear or disapear.
		String filter = "(objectclass=" + logServiceName + ")";
		ServiceListener logServiceListener = new LogServiceListener();
		try {
			bc.addServiceListener( logServiceListener, filter);
			// in case the service is already registered, we send a REGISTERED event to its listener.
			ServiceReference srLog = bc.getServiceReference( logServiceName );
			if( srLog != null ) {
				logServiceListener.serviceChanged(new ServiceEvent( ServiceEvent.REGISTERED, srLog ));
			}
		} catch ( InvalidSyntaxException e ) {
			throw new BundleException("Error in filter string while registering LogServiceListener." + e.toString());
		}
			
		try {
			sqliteService = new SQLiteDriver();
			String[] clazzes = new String[] {Driver.class.getName()};
			// register the class as a managed service.
			Hashtable<String, String> properties = new Hashtable<String, String>();
			properties.put( Constants.DRIVER_ID, "com.ptoceti.osgi.sqlite");
			// register the driver
			Activator.bc.registerService(clazzes, sqliteService, properties );
			
		} catch( Exception e) {
			throw new BundleException( e.toString() );
		}
			
		log(LogService.LOG_INFO, "Starting version " + bc.getBundle().getHeaders().get("Bundle-Version"));
			
	}
	
	/**
	 * Getter return a bundle property
	 * 
	 * @param propertyName the name of the prperty
	 * @return Sting the property value
	 */
	public static String getProperty(final String propertyName){
		return (String)bc.getProperty(propertyName);
	}
	
	/**
	 * Getter return a property from the bundle manifest
	 * 
	 * @param propertyName the name of the property
	 * @return the property value
	 */
	public static String getManifestProperty(final String propertyName){
		return (String)bc.getBundle().getHeaders().get(propertyName);
	}
	
	/**
	 * Called by the framework to stop the service
	 *
	 * @param context the bundle context
	 * @throws BundleException if exception occurs while stopping the service
	 */
	public void stop( final BundleContext context ) throws BundleException {
	
		log(LogService.LOG_INFO, "Stopping");
		Activator.bc = null;
	}
		
	/**
	 * Class method for logging to the logservice. This method can be accessed from every class
	 * in the bundle by simply invoking Activator.log(..).
	 *
	 * @param logLevel : the level to use when togging this message.
	 * @param message : the message to log.
	 */
	static public void log(final int logLevel, final String message ) {
		if( logSer != null )
			logSer.log( logLevel, message );
	}
	
	/**
	 * Internel listener class that receives framework event when the log service is registered
	 * in the the framework and when it is being removed from it. The framework is a dynamic place
	 * and it is important to note when services appear and disappear.
	 * This inner class update the outer class reference to the log service in concordance.
	 *
	 */
	private class LogServiceListener implements ServiceListener {
		
		/**
		 * Unique method of the ServiceListener interface.
		 * 
		 * @param event the service event
		 *
		 */
		public void serviceChanged( final ServiceEvent event ) {
			
				ServiceReference sr = event.getServiceReference();
				switch(event.getType()) {
					case ServiceEvent.REGISTERED: {
						logSer = (LogService) bc.getService(sr);
					}
					break;
					case ServiceEvent.UNREGISTERING: {
						logSer = null;
					}
					break;
				}
		}
	}
}
