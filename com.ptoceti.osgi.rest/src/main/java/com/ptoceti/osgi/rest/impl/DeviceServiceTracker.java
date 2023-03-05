package com.ptoceti.osgi.rest.impl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class DeviceServiceTracker extends ServiceTracker {
    public DeviceServiceTracker(BundleContext context, Filter filter, ServiceTrackerCustomizer customizer) {
        super(context, filter, customizer);
    }

    @Override
    public Object addingService(ServiceReference reference) {
        Object device = super.addingService(reference);
        return device;
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
        super.removedService(reference, service);
    }
}
