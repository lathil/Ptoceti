package com.ptoceti.osgi.usbdevice;

public interface UsbDeviceFactory {

    public void addUsbDeviceTracker(UsbDeviceTracker tracker, String udbDeviceInfoFilter);

    public void removeUsbDeviceTracker(UsbDeviceTracker tracker);
}
