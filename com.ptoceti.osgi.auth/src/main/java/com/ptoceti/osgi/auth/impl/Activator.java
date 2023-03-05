package com.ptoceti.osgi.auth.impl;

import com.ptoceti.osgi.auth.AuthService;
import org.osgi.framework.*;
import org.osgi.service.log.LogService;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

public class Activator implements BundleActivator {

    // a reference to this service bundle context.
    static BundleContext bc = null;
    // a reference to the logging service.
    static LoggerFactory logFactory;
    static Logger logger;
    // the name of the logging service in the osgi framework.
    static final String logFactoryName = org.osgi.service.log.LoggerFactory.class.getName();

    // a reference to the managed rest service
    static AuthServiceImpl authService = null;

    protected static UserAdminserviceListener userAdminServiceListener = null;


    /**
     * Called by the framework for initialisation when the Activator class is
     * loaded. The method first get a service reference on the osgi logging
     * service, used for logging whithin the bundle.
     * <p>
     * If the method cannot get a reference to the logging service, a
     * NullPointerException is thrown.
     *
     * @param context
     * @throws BundleException
     */
    public void start(BundleContext context) throws BundleException, InvalidSyntaxException {

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

        userAdminServiceListener = new UserAdminserviceListener(context);

    }

    public static String getProperty(String propertyName) {

        return (String) bc.getProperty(propertyName);
    }

    public static String getManifestProperty(String propertyName) {

        return (String) bc.getBundle().getHeaders().get(propertyName);
    }

    /**
     * Called by the framework when the bundle is stopped.
     *
     * @param context
     * @throws BundleException
     */
    public void stop(BundleContext context) throws BundleException {

        if (logFactory != null) {
            getLogger().info("Stopping");
        }

        if (authService != null) {
            authService.stop();
        }

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

    static public BundleContext getBundleContext() {
        return bc;
    }


    /**
     * Internel listener class that receives framework event when the log service is registered
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
                    authService = new AuthServiceImpl();

                }
                break;
                case ServiceEvent.UNREGISTERING: {
                    logFactory = null;
                    authService.stop();
                }
                break;
            }
        }
    }

    public static UserAdminserviceListener getUserAdminServiceListener() {
        return userAdminServiceListener;
    }

    public static AuthService getAuthService() {
        return authService;
    }
}
