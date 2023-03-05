
package com.ptoceti.osgi.deviceadmin.impl;

import org.osgi.framework.*;
import org.osgi.service.log.LogService;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import java.net.URL;

/**
 * Activator class implement the BundleActivator interface. This class load the bundle in the framework.
 * Its main task is to create an instance of the DeDietrichFactory ans ask it to register itself in the
 * the framework.
 *
 * @author Laurent Thil
 * @version 1.0
 */
public class Activator implements BundleActivator {

    // a reference to this service bundle context.
    static protected BundleContext bc = null;
    // a reference to the logging service.
    static LoggerFactory logFactory;
    static Logger logger;
    // the name of the logging service in the osgi framework.
    static final String logFactoryName = org.osgi.service.log.LoggerFactory.class.getName();

    private DeviceAdminImpl deviceAdmin;


    /**
     * Called by the framework for initialisation when the Activator class is loaded.
     * The method first get a service reference on the osgi logging service, used for
     * logging whithin the bundle. Then it creates an instance of the ModbusDriverFactory
     * and asks it to register itself.
     * <p>
     * If the method cannot get a reference to the logging service, a NullPointerException is thrown.
     * Similarly, a BundleException exception is thrown if the ModbusDriverFactory cannot be started.
     *
     * @param context the bundle context
     * @throws BundleException thrown if failed to instanciate the factory
     */
    public void start(BundleContext context) throws BundleException {

        Activator.bc = context;

        // we construct a listener to detect if the log service appear or disapear.
        String filter = "(objectclass=" + logFactoryName + ")";
        ServiceListener logFactoryListener = new LoggerFactoryListener();
        try {
            bc.addServiceListener(logFactoryListener, filter);
            // in case the service is already registered, we send a REGISTERED event to its listener.
            ServiceReference srLog = bc.getServiceReference(logFactoryName);
            if (srLog != null) {
                logFactoryListener.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, srLog));
            }
        } catch (InvalidSyntaxException e) {
            throw new BundleException("Error in filter string while registering LogServiceListener." + e.toString());
        }

    }

    /**
     * Called by the framework when the bundle is stopped. The method first forward the stop
     * message to the ModbusDriverFactory instance, then stop the log service.
     *
     * @param context the bundle context
     */
    public void stop(BundleContext context) {

        if (logFactory != null) {
            getLogger().info("Stopping");
        }

        if (deviceAdmin != null) {
            deviceAdmin.stop();
            deviceAdmin = null;
        }

        Activator.getLogger().info("Stopping");
        Activator.bc = null;
    }

    /**
     * Fetch the resource from the bundle's resources and open a stream on it.
     *
     * @param resourceName the name of the resource
     * @return URL url for the resource in the bundle
     */
    static public URL getResourceStream(String resourceName) {

        return bc.getBundle().getResource(resourceName);
    }

    /**
     * Class method for retrieving the Activator logger This method can be accessed
     * from every class in the bundle by simply invoking Activator.getLogger()...
     *
     * @return the Activator logger
     */
    static public Logger getLogger() {
        if (logger == null && logFactory != null) {
            logger = logFactory.getLogger(Activator.class);
        }
        return logger;
    }

    /**
     * Internal listener class that receives framework event when the log service is registered
     * in the the framework and when it is being removed from it. The framework is a dynamic place
     * and it is important to note when services appear and disappear.
     * This inner class update the outer class reference to the log service in concordance.
     */
    public class LoggerFactoryListener implements ServiceListener {

        /**
         * Unique method of the ServiceListener interface.
         */
        public void serviceChanged(ServiceEvent event) {

            ServiceReference sr = event.getServiceReference();
            switch (event.getType()) {
                case ServiceEvent.REGISTERED: {
                    logFactory = (LogService) bc.getService(sr);
                    Activator.getLogger().info("Starting version " + bc.getBundle().getHeaders().get("Bundle-Version"));

                    if (deviceAdmin == null) {
                        // create a instance of the ModusDevice factory.
                        deviceAdmin = new DeviceAdminImpl();
                    }

                }
                break;
                case ServiceEvent.UNREGISTERING: {
                    logFactory = null;

                }
                break;
            }
        }
    }

}