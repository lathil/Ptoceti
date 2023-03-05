package com.ptoceti.osgi.rest.impl.application.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.ptoceti.osgi.rest.impl.Activator;
import com.sun.xml.bind.v2.runtime.reflect.opt.Const;
import org.osgi.framework.ServiceReference;
import org.osgi.service.device.Constants;
import org.osgi.service.device.Device;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class DeviceWrapper implements Device {

    private ServiceReference sRef;

    public DeviceWrapper() {
    }

    public DeviceWrapper(ServiceReference deviceSref) {

        this.sRef = deviceSref;
    }

    @JsonGetter
    @XmlAttribute
    public String[] getDeviceCategory() {
        return (String[]) (sRef.getProperty(Constants.DEVICE_CATEGORY));
    }

    @JsonGetter
    @XmlAttribute
    public String getDeviceSerial() {
        return (String) (sRef.getProperty(Constants.DEVICE_SERIAL));
    }

    @JsonGetter
    @XmlAttribute
    public String getDeviceDescription() {
        return (String) (sRef.getProperty(Constants.DEVICE_DESCRIPTION));
    }

    @JsonGetter
    @XmlAttribute
    public Map<String, String> getProperties() {
        Map<String, String> result = new HashMap<>();

        List<String> defaultKeys = Arrays.asList(Constants.DRIVER_ID, Constants.DEVICE_CATEGORY, Constants.DEVICE_SERIAL, Constants.DEVICE_DESCRIPTION);

        for (String key : sRef.getPropertyKeys()) {
            if (!defaultKeys.contains(key)) {
                result.put(key, sRef.getProperty(key).toString());
            }
        }
        return result;
    }

    @Override
    public void noDriverFound() {

    }
}
