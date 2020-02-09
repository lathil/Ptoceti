package com.ptoceti.osgi.deviceaccess;

import org.osgi.service.event.Event;

import java.util.Dictionary;
import java.util.Map;

public class DeviceEvent extends Event {

    public static final String DEVICE_ADDED_TOPIC = "com/ptoceti/osgi/deviceaccess/DEVICE_ADDED";
    public static final String DEVICE_REMOVED_TOPIC = "com/ptoceti/osgi/deviceaccess/DEVICE_REMOVED";
    public static final String DRIVER_ADDED_TOPIC = "com/ptoceti/osgi/deviceaccess/DRIVER_ADDED";
    public static final String DRIVER_REMOVED_TOPIC = "com/ptoceti/osgi/deviceaccess/DRIVER_REMOVED";
    public static final String DRIVER_ATTACHED_TOPIC = "com/ptoceti/osgi/deviceaccess/DRIVER_ATTACHED";
    public static final String DAL_ADDED_TOPIC = "com/ptoceti/osgi/deviceaccess/DAL_ADDED";
    public static final String DAL_REMOVED_TOPIC = "com/ptoceti/osgi/deviceaccess/DAL_REMOVED";

    public DeviceEvent(String topic, Dictionary properties) {
        super(topic, properties);
    }

    public DeviceEvent(String topic, Map<String, ?> properties) {
        super(topic, properties);
    }
}
