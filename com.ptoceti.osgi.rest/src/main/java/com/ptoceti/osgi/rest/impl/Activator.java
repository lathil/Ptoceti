package com.ptoceti.osgi.rest.impl;

import org.osgi.framework.*;
import org.osgi.service.dal.Function;
import org.osgi.service.device.Device;
import org.osgi.service.device.Driver;
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
    static RestService restService = null;

    static WireAdminServiceListener wireAdminListener = null;
    static MqttServiceListener mqttServiceListener = null;
    static TimeSeriesServiceListener timeSeriesServiceListener = null;
    static DriverServiceTracker driverServiceTracker = null;
    static DeviceServiceTracker deviceServiceTracker = null;
    static DalServiceTracker dalServiceTracker = null;
    static FunctionServiceTracker functionServiceTracker = null;
    static ConfigAdminServiceListener configurationAdminlistener = null;
    static MetaTypeServiceListener metaTypeServiceListener = null;
    static DeviceAdminServiceListener deviceAdminServiceListener = null;
    static EventAdminEventHandler eventAdminEventHandler = null;
    static DeviceAccessServiceListener deviceAccessServiceListener = null;
    static AuthServiceListener authServiceListener = null;

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

        configurationAdminlistener = new ConfigAdminServiceListener(context);

        wireAdminListener = new WireAdminServiceListener(context);

        mqttServiceListener = new MqttServiceListener(context);

        timeSeriesServiceListener = new TimeSeriesServiceListener(context);

        metaTypeServiceListener = new MetaTypeServiceListener(context);

        deviceAdminServiceListener = new DeviceAdminServiceListener(context);

        deviceAccessServiceListener = new DeviceAccessServiceListener(context);

        authServiceListener = new AuthServiceListener(context);

        eventAdminEventHandler = new EventAdminEventHandler();

        String driverFilterSpec = "(objectClass=" + Driver.class.getName() + ")";
        Filter driverFilter = Activator.bc.createFilter(driverFilterSpec);
        driverServiceTracker = new DriverServiceTracker(Activator.getBundleContext(), driverFilter, null);
        driverServiceTracker.open();

        String deviceFilterSpec = "(|(objectClass=" + Device.class.getName() + ")(&(DEVICE_CATEGORY=*)(!(DEVICE_CATEGORY=DAL))) )";
        Filter deviceFilter = Activator.bc.createFilter(deviceFilterSpec);
        deviceServiceTracker = new DeviceServiceTracker(Activator.bc, deviceFilter, null);
        deviceServiceTracker.open();

        String dalFilterSpec = "(objectClass=" + org.osgi.service.dal.Device.class.getName() + ")";
        Filter dalFilter = Activator.bc.createFilter(dalFilterSpec);
        dalServiceTracker = new DalServiceTracker(Activator.getBundleContext(), dalFilter, null);
        dalServiceTracker.setEventHandler(eventAdminEventHandler);
        dalServiceTracker.open();

        String functionFilterSpec = "(objectClass=" + Function.class.getName() + ")";
        Filter functionFilter = Activator.bc.createFilter(functionFilterSpec);
        functionServiceTracker = new FunctionServiceTracker(Activator.getBundleContext(), functionFilter, null);
        functionServiceTracker.setEventHandler(eventAdminEventHandler);
        functionServiceTracker.open();

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

        if (deviceServiceTracker != null) {
            deviceServiceTracker.close();
        }
        if (logFactory != null) {
            getLogger().info("Stopping");
        }

        if (restService != null) {
            restService.stop();
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

    static public WireAdminServiceListener getWireAdminServiceListener() {
        return wireAdminListener;
    }

    static public MqttServiceListener getMqttServiceListener() {
        return mqttServiceListener;
    }

    static public TimeSeriesServiceListener getTimeSeriesServiceListener() {
        return timeSeriesServiceListener;
    }

    static public DriverServiceTracker getDriverServiceTracker() {
        return driverServiceTracker;
    }

    static public DeviceServiceTracker getDeviceServiceTracker() {
        return deviceServiceTracker;
    }

    static public DalServiceTracker getDalServiceTracker() {
        return dalServiceTracker;
    }

    static public FunctionServiceTracker getFunctionServiceTracker() {
        return functionServiceTracker;
    }

    static public ConfigAdminServiceListener getConfigurationAdminlistener() {
        return configurationAdminlistener;
    }

    static public MetaTypeServiceListener getMetaTypeServiceListener() {
        return metaTypeServiceListener;
    }

    static public DeviceAdminServiceListener getDeviceAdminServiceListener() {
        return deviceAdminServiceListener;
    }

    static public DeviceAccessServiceListener getDeviceAccessServiceListener() {
        return deviceAccessServiceListener;
    }

    static public AuthServiceListener getAuthServiceListener() {
        return authServiceListener;
    }

    static public EventAdminEventHandler getEventAdminEventHandler() {
        return eventAdminEventHandler;
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
                    restService = new RestService();

                }
                break;
                case ServiceEvent.UNREGISTERING: {
                    logFactory = null;
                    restService.stop();
                }
                break;
            }
        }
    }
}
