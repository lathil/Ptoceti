package com.ptoceti.osgi.usbdevice;

import javax.usb.UsbDevice;
import java.util.Map;

public interface UsbDeviceTracker {

    void deviceAdded(UsbDevice usbDevice, Map<String, Object> usbDeviceInfo);

    void deviceRemoved(UsbDevice usbDevice);
}
