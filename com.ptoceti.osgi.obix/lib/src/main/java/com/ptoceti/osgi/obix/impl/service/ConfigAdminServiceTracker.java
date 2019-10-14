package com.ptoceti.osgi.obix.impl.service;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class ConfigAdminServiceTracker extends ServiceTracker {

    private static final String CONFIG_ADMIN_SERVICE_NAME = ConfigurationAdmin.class.getName();


    ConfigAdminServiceTracker(BundleContext bundleContext) {
        super(bundleContext, CONFIG_ADMIN_SERVICE_NAME, null);

    }

    @Override
    public final Object addingService(ServiceReference serviceReference) {
        ConfigurationAdmin service = (ConfigurationAdmin) super.addingService(serviceReference);


        Activator.log(LogService.LOG_INFO, "ConfigAdmin Service added");
        return service;
    }

    @Override
    public void removedService(ServiceReference serviceReference, Object service) {

        Activator.log(LogService.LOG_INFO, "ConfigAdmin Service removed");
        context.ungetService(serviceReference);
    }
}