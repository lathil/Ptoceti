package com.ptoceti.osgi.obix.backbones.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix Backbones
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
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.log.LogService;


public class Activator implements BundleActivator {

	// a reference to this service bundle context.
	static BundleContext bc = null;
	// the http service listener
	static HttpServiceListener httpSrvLst;
	// a reference to the logging service.
	static LogService logSer;
	
	ClientApplicationHandler clientHandler = null;
	
	public void start(BundleContext context) throws Exception {
		
		Activator.bc = context;
		
		// we construct a listener to detect if the log service appear or disapear.
		String filter = "(objectclass=" + LogService.class.getName() + ")";
		ServiceListener logServiceListener = new LogServiceListener();
		try {
			bc.addServiceListener( logServiceListener, filter);
			// in case the service is already registered, we send a REGISTERED event to its listener.
			ServiceReference srLog = bc.getServiceReference( LogService.class.getName() );
			if( srLog != null ) {
				logServiceListener.serviceChanged(new ServiceEvent( ServiceEvent.REGISTERED, srLog ));
			}
		} catch ( InvalidSyntaxException e ) {
			throw new BundleException("Error in filter string while registering LogServiceListener." + e.toString());
		}
		
		clientHandler = new ClientApplicationHandler();
		
		String servletfilter = "(objectclass=" + HttpService.class.getName()+ ")";
		try {
			httpSrvLst = new HttpServiceListener(clientHandler);
			Activator.bc.addServiceListener(httpSrvLst, servletfilter);
			// In case the HttpService is already register, we force an event to
			// ourselves.
			ServiceReference servletSer[] = Activator.bc.getServiceReferences(HttpService.class.getName(), null);
			if (servletSer != null) {
				httpSrvLst.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, servletSer[0]));
			}
		} catch (InvalidSyntaxException e) {
			// The shouldn't be any exception comming here.
		}
		
		log(LogService.LOG_INFO, "Starting version " + bc.getBundle().getHeaders().get("Bundle-Version"));
		
	}

	public void stop(BundleContext context) throws Exception {
		
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
	
	/**
	 * Internal listener for the osgi http service. Listen for start and stop events. Notify the provided listener when it happen.
	 * 
	 * 
	 * @author lor
	 *
	 */
	public class HttpServiceListener implements ServiceListener {

		ClientApplicationHandler httpHandler = null;
		
		public HttpServiceListener(ClientApplicationHandler httpHandler) {
			this.httpHandler = httpHandler;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.osgi.framework.ServiceListener#serviceChanged(org.osgi.framework
		 * .ServiceEvent)
		 */
		public void serviceChanged(ServiceEvent event) {
			ServiceReference sr = event.getServiceReference();
			switch (event.getType()) {
			case ServiceEvent.REGISTERED: {
				httpHandler.setHttpService((HttpService) Activator.bc.getService(sr));
				Activator.log(LogService.LOG_INFO,
						"Getting instance of service: "
								+ HttpService.class.getName()
								+ ","
								+ Constants.SERVICE_PID
								+ "="
								+ (String) sr.getProperty(Constants.SERVICE_PID)
								+ " from "
								+ sr.getBundle().getSymbolicName());
			}
				break;
			case ServiceEvent.UNREGISTERING: {
				Activator.log(LogService.LOG_INFO, "Releasing service: "
						+ HttpService.class.getName() + ","
						+ Constants.SERVICE_PID + "="
						+ (String) sr.getProperty(Constants.SERVICE_PID));

				// httpService.unregister(obixServletPath);
				httpHandler.setHttpService(null);
			}
				break;
			}
		}
	}

}
