package com.ptoceti.osgi.rest.impl.application.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.osgi.service.event.Event;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.HashMap;
import java.util.Map;


public class EventWrapper {

    public enum EventType {

        NULL_EVENT("com/ptoceti/osgi/rest/event/NULL_EVENT"),
        DEVICE_ADDED_EVENT("com/ptoceti/osgi/deviceaccess/DEVICE_ADDED"),
        DEVICE_REMOVED_EVENT("com/ptoceti/osgi/deviceaccess/DEVICE_REMOVED"),
        DRIVER_ADDED_EVENT("com/ptoceti/osgi/deviceaccess/DRIVER_ADDED"),
        DRIVER_REMOVED_EVENT("com/ptoceti/osgi/deviceaccess/DRIVER_REMOVED"),
        DRIVER_ATTACHED_EVENT("com/ptoceti/osgi/deviceaccess/DRIVER_ATTACHED"),
        DAL_ADDED_EVENT("com/ptoceti/osgi/deviceaccess/DAL_ADDED"),
        DAL_REMOVED_EVENT("com/ptoceti/osgi/deviceaccess/DAL_REMOVED"),
        CM_DELETED_EVENT("org/osgi/service/cm/ConfigurationEvent/CM_DELETED"),
        CM_UPDATED_EVENT("org/osgi/service/cm/ConfigurationEvent/CM_UPDATED"),
        CM_LOCATION_CHANGED_EVENT("org/osgi/service/cm/ConfigurationEvent/CM_LOCATION_CHANGED"),
        DAL_DEVICE_MODIFIED("com/ptoceti/osgi/rest/DAL_DEVICE_MODIFIED"),
        DAL_DEVICE_ADDED("com/ptoceti/osgi/rest/DAL_DEVICE_ADDED"),
        DAL_DEVICE_REMOVED("com/ptoceti/osgi/rest/DAL_DEVICE_REMOVED"),
        DAL_FUNCTION_ADDED("com/ptoceti/osgi/rest/DAL_FUNCTION_ADDED"),
        DAL_FUNCTION_REMOVED("com/ptoceti/osgi/rest/DAL_FUNCTION_REMOVED"),
        DAL_FUNCTION_PROPERTY_CHANGED("org/osgi/service/dal/FunctionEvent/PROPERTY_CHANGED");

        String topic;

        EventType(String topic) {
            this.topic = topic;
        }

        public static EventType getByTopic(String topic) {
            for (EventWrapper.EventType e : EventWrapper.EventType.values()) {
                if (topic.equals(e.topic)) return e;
            }
            return EventType.NULL_EVENT;
        }
    }

    private EventType eventType;
    private Map<String, String> props;

    public EventWrapper() {
        this.eventType = EventType.NULL_EVENT;
        this.props = new HashMap<>();
    }

    public EventWrapper(Event event) {
        this.eventType = EventType.getByTopic(event.getTopic());
        this.props = new HashMap<>();
        for (String key : event.getPropertyNames()) {
            props.put(key, event.getProperty(key).toString());
        }
    }

    @JsonGetter
    @XmlAttribute
    public EventType getEvent() {
        return eventType;
    }

    @JsonGetter
    @XmlAttribute
    public Map<String, String> getProperties() {
        return props;
    }

}
