package com.ptoceti.osgi.usbdevice.impl;


import jdk.jshell.execution.Util;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.device.Device;
import org.osgi.service.usbinfo.USBInfoDevice;

import javax.usb.UsbDevice;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

public class USBInfoDeviceImpl implements USBInfoDevice, Device {

    ServiceRegistration reg;

    public USBInfoDeviceImpl(UsbDevice usbDevice, Map<String, Object> usbDeviceInfo) {
        String[] clazzes = new String[]{
                USBInfoDevice.class.getName(),
        };

        Dictionary props = new Hashtable();
        props.put(org.osgi.service.device.Constants.DEVICE_CATEGORY, new String[]{USBInfoDevice.DEVICE_CATEGORY});
        props.put(org.osgi.service.device.Constants.DEVICE_DESCRIPTION, "USBInfo device");
        props.put(org.osgi.service.device.Constants.DEVICE_SERIAL, usbDeviceInfo.get(USBInfoDevice.USB_SERIALNUMBER));
        props.put(org.osgi.framework.Constants.SERVICE_PID, USBInfoDeviceImpl.class.getName() + "." + usbDeviceInfo.get(USBInfoDevice.USB_SERIALNUMBER));
        usbDeviceInfo.forEach((key, value) -> {
            props.put(key, value);
        });

        reg = Activator.bc.registerService(clazzes, this, props);
        Activator.getLogger().info("Registered " + this.getClass().getName() + " as " + USBInfoDevice.class.getName() + " for device: " + Utils.getUsbDeviceStringInfo(usbDevice));
    }

    public void stop() {

        reg.unregister();
        Activator.getLogger().info("Unegistered " + this.getClass().getName() + " as " + USBInfoDevice.class.getName());
    }

    @Override
    public void noDriverFound() {

    }
}
