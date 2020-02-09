package com.ptoceti.osgi.rest.impl.application.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.ptoceti.osgi.rest.impl.Activator;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dal.Device;
import org.osgi.service.device.Constants;
import org.osgi.service.device.Driver;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class DriverWrapper implements Driver {

    private Driver wrappedDriver;
    private ServiceReference sRef;

    private String deviceSerial;

    public DriverWrapper() {
    }

    public DriverWrapper(ServiceReference deviceSref, String deviceSerial) {

        wrappedDriver = (org.osgi.service.device.Driver) Activator.getBundleContext().getService(deviceSref);
        this.sRef = deviceSref;
        this.deviceSerial = deviceSerial;
    }

    @JsonGetter
    @XmlAttribute
    public String getDriverId() {
        return (String) (sRef.getProperty(Constants.DRIVER_ID));
    }


    @JsonGetter
    @XmlAttribute
    public String getDeviceSerial() {
        return (this.deviceSerial);
    }

    @Override
    public int match(ServiceReference serviceReference) throws Exception {
        return 0;
    }

    @Override
    public String attach(ServiceReference serviceReference) throws Exception {
        return null;
    }

    @JsonGetter
    @XmlAttribute
    public Map<String, String> getProperties() {
        Map<String, String> result = new HashMap<>();

        List<String> defaultKeys = Arrays.asList(Constants.DRIVER_ID);

        for (String key : sRef.getPropertyKeys()) {
            if (!defaultKeys.contains(key)) {
                result.put(key, sRef.getProperty(key).toString());
            }
        }
        ;
        return result;
    }
}
