package com.ptoceti.osgi.ebus.impl;

import org.osgi.framework.*;
import org.osgi.service.log.LogService;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;
import org.osgi.service.serial.SerialEventListener;

/**
 * Activator class implement the BundleActivator interface. This class load the bundle in the framework.
 * Its main task is to create an instance of the EbusDriverFactory ans ask it to register itself in the
 * the framework.
 *
 * @author Laurent Thil
 * @version 1.0b
 */
public class Activator implements BundleActivator {

    // a reference to this service bundle context.
    static BundleContext bc = null;
    // a reference to the logging service.
    static LoggerFactory logFactory;
    static Logger logger;
    // the name of the logging service in the osgi framework.
    static final String logFactoryName = org.osgi.service.log.LoggerFactory.class.getName();
    // a reference to the EbusDriverFactory service created by this bundle.
    private EbusDriverFactory ebusDFact = null;

    /**
     * Called by the framework for initialisation when the Activator class is loaded.
     * The method first get a service reference on the osgi logging service, used for
     * logging whithin the bundle. Then it creates an instance of the EbusDriverFactory
     * and asks it to register itself.
     * <p>
     * If the method cannot get a reference to the logging service, a NullPointerException is thrown.
     * Similarly, a BundleException exception is thrown if the ModbusDriverFactory cannot be started.
     *
     * @param context The execution context of the bundle being started.
     * @throws BundleException error when starting bundle
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
     * @param context The execution context of the bundle being started.
     */
    public void stop(BundleContext context) {

        if (ebusDFact != null) ebusDFact.stop();
        Activator.getLogger().info("Stopping");

        Activator.bc = null;
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

                    if (ebusDFact == null) {
                        ebusDFact = new EbusDriverFactory();
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

    public static String deviceDetails(ServiceReference sRef) {

        StringBuffer buffer = new StringBuffer();

        Object objectClass = sRef.getProperty(Constants.OBJECTCLASS);
        if (objectClass != null) {
            buffer.append(Constants.OBJECTCLASS + "=");
            if (objectClass instanceof String) {
                buffer.append(objectClass);
            } else if (objectClass instanceof String[]) {
                buffer.append("[");
                for (String category : (String[]) objectClass) {
                    buffer.append(category);
                    buffer.append(" ");
                }
                buffer.append("]");
            }
            buffer.append(" ");
        }

        Object devCategory = sRef.getProperty(org.osgi.service.device.Constants.DEVICE_CATEGORY);
        if (devCategory != null) {
            buffer.append(org.osgi.service.device.Constants.DEVICE_CATEGORY + "=");
            if (devCategory instanceof String) {
                buffer.append(devCategory);
            } else if (devCategory instanceof String[]) {
                buffer.append("[");
                for (String category : (String[]) devCategory) {
                    buffer.append(category);
                    buffer.append(" ");
                }
                buffer.append("]");
            }
        }

        return buffer.toString();
    }

    public static String getSerialEventListenerComPort(ServiceReference sRef) {
        String comPortProps = null;
        Object serialComport = sRef.getProperty(SerialEventListener.SERIAL_COMPORT);
        if (serialComport instanceof String) {
            comPortProps = (String) serialComport;
        }
        return comPortProps;
    }
}

