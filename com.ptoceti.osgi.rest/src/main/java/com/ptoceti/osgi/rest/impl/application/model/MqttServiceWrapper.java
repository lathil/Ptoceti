package com.ptoceti.osgi.rest.impl.application.model;

import com.ptoceti.osgi.rest.impl.Activator;

import com.ptoceti.osgi.mqtt.MqttService;
import org.osgi.framework.ServiceReference;

import javax.xml.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@XmlRootElement(name = "MqttService")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class MqttServiceWrapper implements MqttService {

    private MqttService wrappedMqttService;
    ServiceReference sRef;

    public MqttServiceWrapper() {
    }

    public MqttServiceWrapper(ServiceReference sRef) {
        this.sRef = sRef;
        wrappedMqttService = (MqttService) Activator.getBundleContext().getService(sRef);
    }

    @XmlAttribute
    public boolean isConnected() {
        return wrappedMqttService.isConnected();
    }

    @XmlElementWrapper(name = "properties")
    public Map<String, String> getProperties() {
        String[] keys = sRef.getPropertyKeys();
        HashMap<String, String> result = new HashMap<>();
        for (String key : keys) {
            result.put(key, sRef.getProperty(key).toString());
        }
        ;
        return result;
    }
}
