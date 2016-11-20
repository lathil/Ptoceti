package com.ptoceti.osgi.influxdb.impl;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;

import com.ptoceti.osgi.influxdb.InfluxDbService;

public class Activator implements BundleActivator{

	//a reference to this service bundle context.
	static BundleContext bc = null;
	// a reference to the logging service.
	static LogService logSer;
	// the name of the logging service in the osgi framework.
	static private final String logServiceName = org.osgi.service.log.LogService.class.getName();
	// a reference to the service registration for the Controller object.
	ServiceRegistration sReg = null;
	
	private InfluxDbService influx = null;
	
	
	
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
		
			
		influx = new InfluxDbServiceImpl();
		
		log(LogService.LOG_INFO, "Starting version " + bc.getBundle().getHeaders().get("Bundle-Version"));
	}
	
	private void registerService() {
		
		
		// register the class as a managed service.
		//String[] clazzes = new String[]{JdbcDevice.class.getName()};
		//Hashtable<String, Object> properties = new Hashtable<String, Object>();
		//sReg = Activator.bc.registerService(clazzes, device, properties );		
		
		//log(LogService.LOG_INFO, "Registering service " + JdbcDevice.class.getName());
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

