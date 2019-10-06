package com.ptoceti.osgi.data.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Data
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


import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.log.LogService;
//import org.osgi.service.device.Device;
//import org.osgi.service.device.Constants;

import com.ptoceti.osgi.data.JdbcDevice;


public class Activator implements BundleActivator{

	//a reference to this service bundle context.
	static BundleContext bc = null;
	// a reference to the logging service.
	static LogService logSer;
	// the name of the logging service in the osgi framework.
	static private final String logServiceName = org.osgi.service.log.LogService.class.getName();
	// a reference to the service registration for the Controller object.
	ServiceRegistration sReg = null;
	// the DataSource factory service
	private DataSourceFactory dataSourceFactory;
	// The name of the jdbc driver used by the data source
	private String dataSourceDriverName;
	
	
	public Activator()
	{
		//obixService = new ObixServiceImpl();
	}
	
	/**
	 * Called by the framework for initialisation when the Activator class is loaded.
	 * The method first get a service reference on the osgi logging service, used for
	 * logging whithin the bundle.
	 *
	 * If the method cannot get a reference to the logging service, a NullPointerException is thrown.
	 * @param context
	 * @throws BundleException
	 */
	public void start(BundleContext context) throws BundleException {
		
		Activator.bc = context;
			
		// we construct a listener to detect if the log service appear or disapear.
		String logFilter = "(objectclass=" + logServiceName + ")";
		ServiceListener logServiceListener = new LogServiceListener();
		try {
			bc.addServiceListener( logServiceListener, logFilter);
			// in case the service is already registered, we send a REGISTERED event to its listener.
			ServiceReference srLog = bc.getServiceReference( logServiceName );
			if( srLog != null ) {
				logServiceListener.serviceChanged(new ServiceEvent( ServiceEvent.REGISTERED, srLog ));
			}
		} catch ( InvalidSyntaxException e ) {
			throw new BundleException("Error in filter string while registering LogServiceListener." + e.toString());
		}
		
		//String dataSourceFactFilter = "(&(objectclass=" + DataSourceFactory.class.getName() + ")(" +
		//		DataSourceFactory.OSGI_JDBC_DRIVER_CLASS + "=com.ptoceti.osgi.sqlite.SQLiteJDBC))";
		String dataSourceFactFilter = "(&(objectclass=" + DataSourceFactory.class.getName() + "))";
		ServiceListener dataSourceFactoryServiceListener = new DataSourceFactoryServiceListener();
		try {
			bc.addServiceListener( dataSourceFactoryServiceListener, dataSourceFactFilter);
			// in case the service is already registered, we send a REGISTERED event to its listener.
			ServiceReference srLog = bc.getServiceReference( DataSourceFactory.class.getName() );
			if( srLog != null ) {
				dataSourceFactoryServiceListener.serviceChanged(new ServiceEvent( ServiceEvent.REGISTERED, srLog ));
			}
		} catch ( InvalidSyntaxException e ) {
			throw new BundleException("Error in filter string while registering DataSourceFactory ServiceListener." + e.toString());
		}
			
	}
	
	private void registerService() {
		
		JdbcDevice device = new JdbcDeviceImpl(dataSourceFactory, dataSourceDriverName);
		
		// register the class as a managed service.
		String[] clazzes = new String[]{JdbcDevice.class.getName()};
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		sReg = Activator.bc.registerService(clazzes, device, properties );		
		
		log(LogService.LOG_INFO, "Registering service " + JdbcDevice.class.getName());
	}
	
	public static String getProperty(String propertyName){
		
		return (String)bc.getProperty(propertyName);
	}
	
	public static String getManifestProperty(String propertyName){
		
		return (String)bc.getBundle().getHeaders().get(propertyName);
	}
	
	/**
	 * Called by the framework when the bundle is stopped.
	 *
	 * @param context
	 * @throws BundleException
	 */
	public void stop( BundleContext context ) throws BundleException {
	
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
	 * Getter
	 * @return the datasource factory service
	 */
	public DataSourceFactory getDataSourceFactory() {
		return dataSourceFactory;
	}


	/**
	 * Listen to registration and unregistration of the log service
	 * 
	 * @author LATHIL
	 *
	 */
	private class LogServiceListener implements ServiceListener {
		
		/**
		 * Unique method of the ServiceListener interface.
		 *
		 */
		public void serviceChanged( ServiceEvent event ) {
			
				ServiceReference sr = event.getServiceReference();
				switch(event.getType()) {
					case ServiceEvent.REGISTERED: {
						logSer = (LogService) bc.getService(sr);
						if( getDataSourceFactory() != null && logSer != null ){
							registerService();
						}
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
	 * Listen to registration and unregistration of the data source factory service
	 * 
	 * @author LATHIL
	 *
	 */
	private class DataSourceFactoryServiceListener implements ServiceListener {

		@Override
		public void serviceChanged(ServiceEvent event) {
			ServiceReference sr = event.getServiceReference();
			switch(event.getType()) {
				case ServiceEvent.REGISTERED: {
					dataSourceFactory = ((DataSourceFactory) bc.getService(sr));
					if( getDataSourceFactory() != null && logSer != null ){
						dataSourceDriverName = (String)sr.getProperty(DataSourceFactory.OSGI_JDBC_DRIVER_CLASS);
						registerService();
					}
				}
				break;
				case ServiceEvent.UNREGISTERING: {
					dataSourceFactory = null;
					dataSourceDriverName = null;
				}
				break;
			}
		}
		
	}
}
