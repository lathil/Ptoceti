package com.ptoceti.osgi.rest.impl.application.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dal.Device;
import org.osgi.service.dal.DeviceException;

import com.ptoceti.osgi.rest.impl.Activator;
import org.osgi.service.device.Constants;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "Thing")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ThingWrapper implements Device {

    enum Status {
        STATUS_REMOVED(1),
        STATUS_OFFLINE(2),
        STATUS_ONLINE(3),
        STATUS_PROCESSING(4),
        STATUS_NOT_INITIALIZED(5),
        STATUS_NOT_CONFIGURED(6);

        int code;

        private Status(int code) {
            this.code = code;
        }

        public static Status getByCode(int code) {
            for (Status e : Status.values()) {
                if (code == e.code) return e;
            }
            return null;
        }

    }

    enum StatusDetail {
        STATUS_DETAIL_CONNECTING(1),
        STATUS_DETAIL_INITIALIZING(2),
        STATUS_DETAIL_REMOVING(3),
        STATUS_DETAIL_FIRMWARE_UPDATING(4),
        STATUS_DETAIL_CONFIGURATION_UNAPPLIED(-1),
        STATUS_DETAIL_BROKEN(-2),
        STATUS_DETAIL_COMMUNICATION_ERROR(-3),
        STATUS_DETAIL_DATA_INSUFFICIENT(-4),
        STATUS_DETAIL_INACCESSIBLE(-5),
        STATUS_DETAIL_CONFIGURATION_ERROR(-6),
        STATUS_DETAIL_DUTY_CYCLE(-7);

        int code;

        private StatusDetail(int code) {
            this.code = code;
        }

        public static StatusDetail getByCode(int code) {
            for (StatusDetail e : StatusDetail.values()) {
                if (code == e.code) return e;
            }
            return null;
        }
    }


    private ServiceReference sRef;
    private Device wrappedDevice;

    public ThingWrapper() {
    }

    public ThingWrapper(ServiceReference deviceSref) {
        sRef = deviceSref;
        wrappedDevice = (Device) Activator.getBundleContext().getService(deviceSref);
    }

    @JsonGetter
    @XmlAttribute
    public String getUid() {
        return (String) getServiceProperty(Device.SERVICE_UID);
    }

    @JsonGetter
    @XmlAttribute
    public String getDriver() {
        return (String) getServiceProperty(Device.SERVICE_DRIVER);
    }

    @JsonGetter
    @XmlAttribute
    public String getName() {
        return (String) getServiceProperty(Device.SERVICE_NAME);
    }

    @JsonGetter
    @XmlAttribute
    public Status getStatus() {
        Integer statusCode = (Integer) getServiceProperty(Device.SERVICE_STATUS);
        if (statusCode != null) {
            return Status.getByCode(statusCode.intValue());
        } else {
            return null;
        }
    }

    @JsonGetter
    @XmlAttribute
    public StatusDetail getStatusDetail() {
        Integer statusDetailCode = (Integer) getServiceProperty(Device.SERVICE_STATUS_DETAIL);
        if (statusDetailCode != null) {
            return StatusDetail.getByCode(statusDetailCode.intValue());
        } else {
            return null;
        }
    }

    @JsonGetter
    @XmlAttribute
    public String getHardwareVendor() {
        return (String) getServiceProperty(Device.SERVICE_HARDWARE_VENDOR);
    }

    @JsonGetter
    @XmlAttribute
    public String getHardwareVersion() {
        return (String) getServiceProperty(Device.SERVICE_HARDWARE_VERSION);
    }

    @JsonGetter
    @XmlAttribute
    public String getFirmwareVendor() {
        return (String) getServiceProperty(Device.SERVICE_FIRMWARE_VENDOR);
    }

    @JsonGetter
    @XmlAttribute
    public String getFirmwareVersion() {
        return (String) getServiceProperty(Device.SERVICE_FIRMWARE_VERSION);
    }

    @JsonGetter
    @XmlAttribute
    public String[] getTypes() {
        return (String[]) getServiceProperty(Device.SERVICE_TYPES);
    }

    @JsonGetter
    @XmlAttribute
    public String getModel() {
        return (String) getServiceProperty(Device.SERVICE_MODEL);
    }

    @JsonGetter
    @XmlAttribute
    public String getSerialNumber() {
        return (String) getServiceProperty(Device.SERVICE_SERIAL_NUMBER);
    }

    @JsonGetter
    @XmlAttribute
    public String getDescription() {
        return (String) getServiceProperty(Device.SERVICE_DESCRIPTION);
    }


    @JsonIgnore
    @Override
    public Object getServiceProperty(String s) {
        return wrappedDevice.getServiceProperty(s);
    }

    @JsonIgnore
    @Override
    public String[] getServicePropertyKeys() {
        return wrappedDevice.getServicePropertyKeys();
    }

    @JsonGetter
    @XmlAttribute
    public Map<String, String> getProperties() {
        Map<String, String> result = new HashMap<>();

        List<String> defaultKeys = Arrays.asList(Device.DEVICE_CATEGORY, Device.SERVICE_UID, Device.SERVICE_REFERENCE_UIDS,
                Device.SERVICE_DRIVER,
                Device.SERVICE_NAME,
                Device.SERVICE_STATUS,
                Device.SERVICE_STATUS_DETAIL,
                Device.SERVICE_HARDWARE_VENDOR,
                Device.SERVICE_HARDWARE_VERSION,
                Device.SERVICE_FIRMWARE_VENDOR,
                Device.SERVICE_FIRMWARE_VERSION,
                Device.SERVICE_TYPES,
                Device.SERVICE_MODEL,
                Device.SERVICE_SERIAL_NUMBER,
                Device.SERVICE_DESCRIPTION);

        for (String key : sRef.getPropertyKeys()) {
            if (!defaultKeys.contains(key)) {
                result.put(key, sRef.getProperty(key).toString());
            }
        }
        ;
        return result;
    }

    @Override
    public void remove() throws DeviceException {

    }
}
