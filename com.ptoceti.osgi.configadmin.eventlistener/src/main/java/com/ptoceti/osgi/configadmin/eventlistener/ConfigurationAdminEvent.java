package com.ptoceti.osgi.configadmin.eventlistener;

import org.osgi.service.event.Event;

import java.util.Map;

public class ConfigurationAdminEvent extends Event {

    public static final String CM_DELETED_TOPIC = "org/osgi/service/cm/ConfigurationEvent/CM_DELETED";
    public static final String CM_UPDATED_TOPIC = "org/osgi/service/cm/ConfigurationEvent/CM_UPDATED";
    public static final String CM_LOCATION_CHANGED_TOPIC = "org/osgi/service/cm/ConfigurationEvent/CM_LOCATION_CHANGED";

    public ConfigurationAdminEvent(String topic, Map<String, ?> properties) {
        super(topic, properties);
    }
}
