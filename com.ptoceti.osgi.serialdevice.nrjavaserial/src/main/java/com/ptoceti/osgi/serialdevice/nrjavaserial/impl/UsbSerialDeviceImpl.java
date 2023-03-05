package com.ptoceti.osgi.serialdevice.nrjavaserial.impl;

import com.ptoceti.osgi.usbdevice.UsbDeviceFactory;
import com.ptoceti.osgi.usbdevice.UsbDeviceTracker;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.device.Constants;
import org.osgi.service.serial.SerialDevice;
import org.osgi.service.serial.SerialDeviceException;
import org.osgi.service.usbinfo.USBInfoDevice;
import org.osgi.util.tracker.ServiceTracker;

import javax.usb.UsbDevice;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

public class UsbSerialDeviceImpl extends SerialDeviceImpl implements USBInfoDevice, UsbDeviceTracker {

    private String usbDeviceFilter;
    private Map<String, Object> usbDeviceInfo;

    ServiceTracker usbDeviceFactoryTracker;
    UsbDeviceFactory usbDeviceFactory;

    UsbDevice usbDevice;

    public UsbSerialDeviceImpl(String pid, String portName, String usbDeviceFilter) {
        super(pid, portName);
        this.usbDeviceFilter = usbDeviceFilter;
    }

    @Override
    public void start() throws SerialDeviceException {
        String usDeviceFactoryFilterSpec = "(objectClass=" + UsbDeviceFactory.class.getName() + ")";
        try {
            Filter deviceFilter = Activator.bc.createFilter(usDeviceFactoryFilterSpec);
            UsbDeviceTracker usbDeviceTracker = this;
            usbDeviceFactoryTracker = new ServiceTracker(Activator.bc, deviceFilter, null) {
                @Override
                public Object addingService(ServiceReference reference) {
                    Object device = super.addingService(reference);
                    usbDeviceFactory = (UsbDeviceFactory) device;
                    usbDeviceFactory.addUsbDeviceTracker(usbDeviceTracker, usbDeviceFilter);
                    return device;
                }

                @Override
                public void removedService(ServiceReference reference, Object service) {
                    super.removedService(reference, service);
                    usbDeviceFactory = null;

                }
            };
            usbDeviceFactoryTracker.open();
        } catch (InvalidSyntaxException ex) {
            Activator.getLogger().error("Error creating UsbDeviceFactory tracker: " + ex.toString());
        }
    }

    @Override
    public void stop() {

        if (usbDeviceFactoryTracker != null) {
            usbDeviceFactoryTracker.close();
        }
        super.stop();
    }

    @Override
    protected void register() {
        String[] clazzes = new String[]{
                SerialDevice.class.getName(), USBInfoDevice.class.getName()
        };

        Dictionary props = new Hashtable();
        props.put(Constants.DEVICE_CATEGORY, new String[]{SerialDevice.DEVICE_CATEGORY, USBInfoDevice.DEVICE_CATEGORY});
        props.put(SerialDevice.SERIAL_COMPORT, serialPortName);
        props.put(Constants.DEVICE_DESCRIPTION, "Serial device");
        props.put(Constants.DEVICE_SERIAL, usbDeviceInfo.get(USBInfoDevice.USB_SERIALNUMBER));
        props.put(org.osgi.framework.Constants.SERVICE_PID, pid);

        usbDeviceInfo.forEach((key, value) -> {
            props.put(key, value);
        });
        sReg = Activator.bc.registerService(clazzes, this, props);

        Activator.getLogger().info("Registered " + this.getClass().getName() + " as " + SerialDevice.class.getName() + ", " + USBInfoDevice.class.getName() + " on port : " + props.get(SerialDevice.SERIAL_COMPORT));
    }

    /**
     * Notified by UsbdeviceFactory that a device matching selection filter has been found
     *
     * @param usbDevice
     * @param usbDeviceInfo
     */
    @Override
    public void deviceAdded(UsbDevice usbDevice, Map<String, Object> usbDeviceInfo) {
        if (usbDevice != null) {
            this.usbDeviceInfo = usbDeviceInfo;
            try {
                super.start();
                this.usbDevice = usbDevice;
            } catch (SerialDeviceException ex) {

            }
        }
    }

    /**
     * notified by usebDeviceFactory that the device matching selection filter has been removed
     *
     * @param usbDevice
     */
    @Override
    public void deviceRemoved(UsbDevice usbDevice) {
        super.stop();
        this.usbDevice = null;
        this.usbDeviceInfo = null;
    }
}
