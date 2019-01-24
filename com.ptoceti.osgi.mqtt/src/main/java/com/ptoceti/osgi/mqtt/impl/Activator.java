package com.ptoceti.osgi.mqtt.impl;

import java.net.URL;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import com.ptoceti.osgi.mqtt.IMqttMessageFomatter;
import com.ptoceti.osgi.mqtt.impl.formater.internal.InternalMessageFormater;

/**
 * Activator class implement the BundleActivator interface. This class load the bundle in the framework.
 * Its main task is to create an instance of the MqttClientFactory  and ask it to register itself in the
 * the framework.
 *
 * @author Laurent Thil
 * @version 1.0
 */
public class Activator implements BundleActivator {

	/**
	 * a reference to this service bundle context.
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
	 * The factory for the nodes
	 */
	MqttClientFactory mqttClientFactory;
	
	InternalMessageFormater mqttMessageForrmater;
	
	/**
	 * Called by the framework for initialisation when the Activator class is loaded.
	 * The method first get a service reference on the osgi logging service, used for
	 * logging whithin the bundle. Then it creates an instance of the SensorNodeDriverFactory
	 * and asks it to register itself.
	 *
	 * If the method cannot get a reference to the logging service, a NullPointerException is thrown.
	 * Similarly, a BundleException exception is thrown if the MqttClientFactory cannot be started.
	 * @param context the bundle context
	 */
	public void start(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
		Activator.bc = context;

		// we construct a listener to detect if the log service appear or
		// disapear.
		String filter = "(objectclass=" + logServiceName + ")";
		ServiceListener logServiceListener = new LogServiceListener();
		try {
			bc.addServiceListener(logServiceListener, filter);
			// in case the service is already registered, we send a REGISTERED
			// event to its listener.
			ServiceReference srLog = bc.getServiceReference(logServiceName);
			if (srLog != null) {
				logServiceListener.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, srLog));
			}
		} catch (InvalidSyntaxException e) {
			throw new BundleException("Error in filter string while registering LogServiceListener." + e.toString());
		}

		// Create the default mqtt message formater
		mqttMessageForrmater = new InternalMessageFormater();
		// and register it as a service
		String[] clazzes = new String[]{IMqttMessageFomatter.class.getName()};
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put(IMqttMessageFomatter.MESSAGEFORMATTERNAME, InternalMessageFormater.name);
		Activator.bc.registerService(clazzes, mqttMessageForrmater, properties );		
		
		log(LogService.LOG_INFO, "Registering service " + IMqttMessageFomatter.class.getName());

		mqttClientFactory = new MqttClientFactory();

		log(LogService.LOG_INFO, "Starting version " + bc.getBundle().getHeaders().get("Bundle-Version"));
	}

	/**
	 * Called by the framework when the bundle is stopped. 
	 *
	 * @param context the bundle context
	 */
	public void stop(BundleContext context)  {
		
		log(LogService.LOG_INFO, "Stopping");
		
		mqttClientFactory.stop();
		
		Activator.bc = null;
	}

	/**
	 * Fetch the resource from the bundle's resources and open a stream on it.
	 * 
	 * @param resourceName name of the resource to get the url
	 * @return URL for the resource
	 * 
	 */
	static public URL getResourceStream(String resourceName) {

		return bc.getBundle().getResource(resourceName);
	}

	/**
	 * Class method for logging to the logservice. This method can be accessed
	 * from every class in the bundle by simply invoking Activator.log(..).
	 * 
	 * @param logLevel
	 *            : the level to use when togging this message.
	 * @param message
	 *            : the message to log.
	 */
	static public void log(int logLevel, String message) {
		if (logSer != null)
			logSer.log(logLevel, message);
	}

	/**
	 * Internel listener class that receives framework event when the log
	 * service is registered in the the framework and when it is being removed
	 * from it. The framework is a dynamic place and it is important to note
	 * when services appear and disappear. This inner class update the outer
	 * class reference to the log service in concordance.
	 * 
	 */
	private class LogServiceListener implements ServiceListener {

		/**
		 * Unique method of the ServiceListener interface.
		 * 
		 */
		public void serviceChanged(ServiceEvent event) {

			ServiceReference sr = event.getServiceReference();
			switch (event.getType()) {
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

