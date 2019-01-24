
package com.ptoceti.osgi.modbus.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Modbus
 * FILENAME : Activator.java
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

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.log.LogService;

/**
 * Activator class implement the BundleActivator interface. This class load the bundle in the framework.
 * Its main task is to create an instance of the ModbusDriverFactory ans ask it to register itself in the
 * the framework.
 *
 * @author Laurent Thil
 * @version 1.0b
 */
public class Activator implements BundleActivator {

	// a reference to this service bundle context.
	static BundleContext bc = null;
	// a reference to the logging service.
	static LogService logSer;
	// the name of the logging service in the osgi framework.
	static private final String logServiceName = org.osgi.service.log.LogService.class.getName();
	// a reference to the ModbusDriverFactory service created by this bundle.
	private ModbusDriverFactory modbusDFact = null;
	
	/**
	 * Called by the framework for initialisation when the Activator class is loaded.
	 * The method first get a service reference on the osgi logging service, used for
	 * logging whithin the bundle. Then it creates an instance of the ModbusDriverFactory
	 * and asks it to register itself.
	 *
	 * If the method cannot get a reference to the logging service, a NullPointerException is thrown.
	 * Similarly, a BundleException exception is thrown if the ModbusDriverFactory cannot be started.
	 * @param context the bundle context
	 * @throws BundleException thrown when factory could not be instanciated
	 */
	public void start(BundleContext context) throws BundleException {
	
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
		
		log(LogService.LOG_INFO, "Starting version " + bc.getBundle().getHeaders().get("Bundle-Version"));
		
		try {
			modbusDFact = new ModbusDriverFactory();
		} catch ( Exception e ) {
			throw new BundleException( e.toString() );
		}
		
	}
	
	/**
	 * Called by the framework when the bundle is stopped. The method first forward the stop
	 * message to the ModbusDriverFactory instance, then stop the log service.
	 *
	 * @param context the bundle context
	 */
	public void stop( BundleContext context ) {
	
		if( modbusDFact != null ) modbusDFact.stop();
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
	static public void log( int logLevel, String message ) {
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
	public class LogServiceListener implements ServiceListener {
		
		/**
		 * Unique method of the ServiceListener interface.
		 *
		 */
		public void serviceChanged( ServiceEvent event ) {
			
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
