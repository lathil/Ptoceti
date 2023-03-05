package com.ptoceti.osgi.ebusdevice.impl;

import com.ptoceti.osgi.ebus.EbusDriver;
import com.ptoceti.osgi.ebus.EbusDriverListener;
import com.ptoceti.osgi.ebusdevice.EbusDevice;
import org.osgi.framework.*;
import org.osgi.service.dal.Device;
import org.osgi.service.dal.DeviceException;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import java.util.Dictionary;
import java.util.Hashtable;

public class EbusDeviceImpl implements EbusDevice, EbusDriverListener, Device {


    String pid;
    String name;
    String serialPort;
    Integer ebusId;
    Integer ebusPoolingRate;

    Integer deviceStatus = Device.STATUS_PROCESSING;
    Integer deviceStatusDetails = Device.STATUS_DETAIL_INITIALIZING;

    ServiceRegistration sReg;
    ServiceTracker ebusDriverTracker;

    public EbusDeviceImpl(String pid, String name, String serialPort, Integer ebusId, int ebusPoolingRate) {
        this.pid = pid;
        this.name = name;
        this.serialPort = serialPort;
        this.ebusId = ebusId;
        this.ebusId = ebusPoolingRate;
    }

    public void start() {
        String[] clazzes = new String[]{
                EbusDevice.class.getName(),
                Device.class.getName()
        };

        Dictionary props = new Hashtable();
        props.put(Device.SERVICE_DRIVER, EbusDriver.EBUS_DRIVER_NAME);
        props.put(Device.SERVICE_UID, EbusDriver.EBUS_DRIVER_NAME + ":" + ebusId);
        props.put(Device.SERVICE_NAME, this.name);
        props.put(Device.SERVICE_DESCRIPTION, "Ebus DAL device");
        props.put(Device.SERVICE_STATUS, deviceStatus);
        props.put(Device.SERVICE_STATUS_DETAIL, deviceStatusDetails);
        props.put(Constants.SERVICE_PID, pid);
        props.put(org.osgi.service.device.Constants.DEVICE_CATEGORY, new String[]{Device.DEVICE_CATEGORY});


        sReg = Activator.bc.registerService(clazzes, this, props);

        Activator.getLogger().info("Registered " + this.getClass().getName());

        String modbusDeviceFactoryFilterSpec = "(objectClass=" + EbusDriver.class.getName() + ")";
        try {
            EbusDriverListener ebusDriverListener = this;
            Filter deviceFilter = Activator.bc.createFilter(modbusDeviceFactoryFilterSpec);
            ebusDriverTracker = new ServiceTracker(Activator.bc, deviceFilter, null) {
                @Override
                public Object addingService(ServiceReference reference) {
                    Object device = super.addingService(reference);
                    ((EbusDriver) device).addListener(ebusDriverListener);
                    return device;
                }

                @Override
                public void removedService(ServiceReference reference, Object service) {
                    ((EbusDriver) service).removeListener(ebusDriverListener);
                    super.removedService(reference, service);

                }
            };
            ebusDriverTracker.open();
        } catch (InvalidSyntaxException ex) {
            Activator.getLogger().error("Error creating UsbDeviceFactory tracker: " + ex.toString());
        }
    }

    public int getId() {
        return 0;
    }

    public String getPortName() {
        return "";
    }

    public void stop() {
        ebusDriverTracker.close();
        sReg = null;
    }

    @Override
    public void ebusDriverConnected() {
        deviceStatus = Device.STATUS_ONLINE;
        deviceStatusDetails = null;
        updateServiceProperties();
    }

    @Override
    public void ebusDriverDisconnected() {
        deviceStatus = Device.STATUS_OFFLINE;
        deviceStatusDetails = Device.STATUS_DETAIL_BROKEN;
        updateServiceProperties();
    }

    @Override
    public Object getServiceProperty(String s) {
        return sReg.getReference().getProperty(s);
    }

    @Override
    public String[] getServicePropertyKeys() {
        return sReg.getReference().getPropertyKeys();
    }

    @Override
    public void remove() throws DeviceException {
        deviceStatus = Device.STATUS_REMOVED;
        deviceStatusDetails = null;
        updateServiceProperties();
        sReg.unregister();
        stop();
    }

    protected void updateServiceProperties() {

        if (sReg != null) {
            Dictionary props = new Hashtable();
            props.put(Device.SERVICE_DRIVER, EbusDriver.EBUS_DRIVER_NAME);
            props.put(Device.SERVICE_UID, EbusDriver.EBUS_DRIVER_NAME + ":" + pid);
            props.put(Device.SERVICE_NAME, EbusDriver.EBUS_DRIVER_NAME + ":" + pid);
            props.put(Device.SERVICE_DESCRIPTION, "Modbus DAL device");
            props.put(Device.SERVICE_STATUS, deviceStatus);
            props.put(Constants.SERVICE_PID, pid);
            if (deviceStatusDetails != null) {
                props.put(Device.SERVICE_STATUS_DETAIL, deviceStatusDetails);
            }
            props.put(org.osgi.service.device.Constants.DEVICE_CATEGORY, new String[]{Device.DEVICE_CATEGORY});

            sReg.setProperties(props);
        }
    }

}
