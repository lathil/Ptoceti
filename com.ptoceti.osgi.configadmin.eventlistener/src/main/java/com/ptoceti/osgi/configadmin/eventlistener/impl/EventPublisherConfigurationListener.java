package com.ptoceti.osgi.configadmin.eventlistener.impl;

import com.ptoceti.osgi.configadmin.eventlistener.ConfigurationAdminEvent;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventProperties;
import org.osgi.util.tracker.ServiceTracker;

import java.util.HashMap;
import java.util.Hashtable;

import static com.ptoceti.osgi.configadmin.eventlistener.ConfigurationAdminEvent.*;
import static org.osgi.service.cm.ConfigurationEvent.*;

public class EventPublisherConfigurationListener implements ConfigurationListener {

    ServiceRegistration sReg;
    ServiceTracker eventAdminTracker;

    public EventPublisherConfigurationListener() {

        String[] clazzes = new String[]{ConfigurationListener.class.getName()};
        // register the class as a managed service.
        Hashtable<String, String> properties = new Hashtable<String, String>();
        properties.put(Constants.SERVICE_PID, EventPublisherConfigurationListener.class.getName());
        sReg = Activator.bc.registerService(clazzes, this, properties);

        Activator.getLogger().info("Registered " + this.getClass().getName() + ", Pid = "
                + (String) properties.get(Constants.SERVICE_PID));

        eventAdminTracker = new ServiceTracker(Activator.bc, EventAdmin.class.getName(), null);
        eventAdminTracker.open();
    }

    protected void stop() {

        eventAdminTracker.close();
        sReg.unregister();
    }

    @Override
    public void configurationEvent(ConfigurationEvent configurationEvent) {

        EventAdmin eventAdmin = (EventAdmin) eventAdminTracker.getService();
        if (eventAdmin != null) {
            HashMap<String, Object> properties = new HashMap();

            if (configurationEvent.getFactoryPid() != null) {
                properties.put("cm.factoryPid", configurationEvent.getFactoryPid());
            }
            if (configurationEvent.getPid() != null) {
                properties.put("cm.pid", configurationEvent.getPid());
            }
            properties.put("service", configurationEvent.getReference());
            properties.put(Constants.SERVICE_ID, configurationEvent.getReference().getProperty(Constants.SERVICE_ID).toString());
            properties.put(Constants.SERVICE_PID, configurationEvent.getReference().getProperty(Constants.SERVICE_PID).toString());
            EventProperties eventProps = new EventProperties(properties);
            switch (configurationEvent.getType()) {
                case CM_UPDATED:
                    eventAdmin.postEvent(new ConfigurationAdminEvent(CM_UPDATED_TOPIC, eventProps));
                    break;
                case CM_DELETED:
                    eventAdmin.postEvent(new ConfigurationAdminEvent(CM_DELETED_TOPIC, eventProps));
                    break;
                case CM_LOCATION_CHANGED:
                    eventAdmin.postEvent(new ConfigurationAdminEvent(CM_LOCATION_CHANGED_TOPIC, eventProps));
                    break;
            }

        }
    }
}
