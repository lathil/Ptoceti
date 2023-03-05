package com.ptoceti.osgi.rest.impl;

import org.osgi.framework.*;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.dal.Device;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;

import java.util.*;

import static com.ptoceti.osgi.configadmin.eventlistener.ConfigurationAdminEvent.*;
import static com.ptoceti.osgi.deviceaccess.DeviceEvent.*;
import static com.ptoceti.osgi.rest.impl.FunctionServiceTracker.DAL_FUNCTION_ADDED;
import static com.ptoceti.osgi.rest.impl.FunctionServiceTracker.DAL_FUNCTION_REMOVED;
import static org.osgi.service.dal.FunctionEvent.TOPIC_PROPERTY_CHANGED;

public class EventAdminEventHandler implements org.osgi.service.event.EventHandler {

    static final String SERVICE_EVENT_MODIFIED = "org/osgi/framework/ServiceEvent/MODIFIED";
    static final String DAL_DEVICE_MODIFIED = "com/ptoceti/osgi/rest/DAL_DEVICE_MODIFIED";

    String dalDeviceFilterSpec = "(|(service.objectClass=" + Device.class.getName() + ")(DEVICE_CATEGORY=DAL))";
    Filter dalDeviceFilter = null;

    ServiceRegistration sReg = null;
    List<EventListener> listeners = new ArrayList<>();

    public EventAdminEventHandler() {

        try {
            dalDeviceFilter = Activator.bc.createFilter(dalDeviceFilterSpec);
        } catch (InvalidSyntaxException ex) {
            Activator.getLogger().error("Error creating dalDeviceFilter: " + ex.toString());
        }

        String[] topics = new String[]{SERVICE_EVENT_MODIFIED, DEVICE_ADDED_TOPIC, DEVICE_REMOVED_TOPIC, DRIVER_ADDED_TOPIC, DRIVER_REMOVED_TOPIC, DRIVER_ATTACHED_TOPIC, DAL_ADDED_TOPIC, DAL_REMOVED_TOPIC, DAL_FUNCTION_ADDED, DAL_FUNCTION_REMOVED, CM_DELETED_TOPIC, CM_UPDATED_TOPIC, CM_LOCATION_CHANGED_TOPIC, TOPIC_PROPERTY_CHANGED};

        String[] clazzes = new String[]{
                org.osgi.service.event.EventHandler.class.getName(),
        };

        Hashtable<String, Object> properties = new Hashtable<String, Object>();
        properties.put(EventConstants.EVENT_TOPIC, topics);
        sReg = Activator.bc.registerService(clazzes, this, properties);

    }

    @Override
    public void handleEvent(Event event) {

        Event nextEvent = event;
        if (event.getTopic().equals(SERVICE_EVENT_MODIFIED)) {
            if (dalDeviceFilter != null && event.matches(dalDeviceFilter)) {
                Map<String, Object> props = new HashMap<String, Object>();
                for (String name : event.getPropertyNames()) {
                    props.put(name, event.getProperty(name));
                }
                nextEvent = new Event(DAL_DEVICE_MODIFIED, props);
            } else {
                nextEvent = null;
            }
        }
        if (nextEvent != null) {
            for (EventListener listener : listeners) {
                listener.postEvent(nextEvent);
            }
        }
    }

    public void addListener(EventListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    public interface EventListener {
        void postEvent(Event event);
    }
}
