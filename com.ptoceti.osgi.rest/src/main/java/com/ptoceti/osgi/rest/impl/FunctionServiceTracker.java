package com.ptoceti.osgi.rest.impl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dal.Function;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventProperties;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import java.util.HashMap;

public class FunctionServiceTracker extends ServiceTracker {
    EventAdminEventHandler eventHandler;

    static final String DAL_FUNCTION_ADDED = "com/ptoceti/osgi/rest/DAL_FUNCTION_ADDED";
    static final String DAL_FUNCTION_REMOVED = "com/ptoceti/osgi/rest/DAL_FUNCTION_REMOVED";

    public FunctionServiceTracker(BundleContext context, Filter filter, ServiceTrackerCustomizer customizer) {
        super(context, filter, customizer);
    }

    @Override
    public Object addingService(ServiceReference reference) {
        Object function = super.addingService(reference);
        if (eventHandler != null) {
            HashMap<String, String> properties = new HashMap();
            properties.put(Function.SERVICE_UID, reference.getProperty(Function.SERVICE_UID).toString());
            EventProperties eventProps = new EventProperties(properties);
            Event event = new Event(DAL_FUNCTION_ADDED, eventProps);
            eventHandler.handleEvent(event);
        }
        return function;
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
        super.removedService(reference, service);
        if (eventHandler != null) {
            HashMap<String, String> properties = new HashMap();
            properties.put(Function.SERVICE_UID, reference.getProperty(Function.SERVICE_UID).toString());
            EventProperties eventProps = new EventProperties(properties);
            Event event = new Event(DAL_FUNCTION_REMOVED, eventProps);
            eventHandler.handleEvent(event);
        }
    }

    public void setEventHandler(EventAdminEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }
}
