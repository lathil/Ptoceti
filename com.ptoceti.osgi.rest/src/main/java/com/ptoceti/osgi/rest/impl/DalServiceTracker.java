package com.ptoceti.osgi.rest.impl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dal.Device;
import org.osgi.service.dal.Function;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventProperties;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import java.util.HashMap;

public class DalServiceTracker extends ServiceTracker {

    static final String DAL_DEVICE_ADDED = "com/ptoceti/osgi/rest/DAL_DEVICE_ADDED";
    static final String DAL_DEVICE_REMOVED = "com/ptoceti/osgi/rest/DAL_DEVICE_REMOVED";

    EventAdminEventHandler eventHandler;

    public DalServiceTracker(BundleContext context, Filter filter, ServiceTrackerCustomizer customizer) {
        super(context, filter, customizer);
    }

    @Override
    public Object addingService(ServiceReference reference) {
        Object device = super.addingService(reference);
        if (eventHandler != null) {
            HashMap<String, String> properties = new HashMap();
            properties.put(Device.SERVICE_UID, reference.getProperty(Device.SERVICE_UID).toString());
            EventProperties eventProps = new EventProperties(properties);
            Event event = new Event(DAL_DEVICE_ADDED, eventProps);
            eventHandler.handleEvent(event);
        }
        return device;
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
        super.removedService(reference, service);
        if (eventHandler != null) {
            HashMap<String, String> properties = new HashMap();
            properties.put(Device.SERVICE_UID, reference.getProperty(Device.SERVICE_UID).toString());
            EventProperties eventProps = new EventProperties(properties);
            Event event = new Event(DAL_DEVICE_ADDED, eventProps);
            eventHandler.handleEvent(event);
        }
    }

    public void setEventHandler(EventAdminEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }
}
