package com.ptoceti.osgi.usbdevice.impl;

import org.osgi.service.usbinfo.USBInfoDevice;

import javax.usb.UsbDevice;
import javax.usb.UsbInterfaceDescriptor;
import java.util.Hashtable;
import java.util.Map;

public class Utils {

    public static String getUsbDeviceStringInfo(UsbDevice usbDevice) {
        StringBuffer buffer = new StringBuffer();
        try {
            buffer.append("Manufacter: " + usbDevice.getManufacturerString());
            buffer.append(", Product: " + usbDevice.getProductString());
            buffer.append(", Serial number: " + usbDevice.getSerialNumberString());
        } catch (Exception ex) {

        }

        return buffer.toString();
    }

    /**
     * Extract information from UsbDeviceDescriptor required by UsbInfoDevice
     *
     * @param usbDevice
     * @return
     */
    public static Map<String, Object> getUsbDeviceDescriptorInfo(UsbDevice usbDevice) {
        Hashtable<String, Object> properties = new Hashtable<String, Object>();
        properties.put(USBInfoDevice.USB_BCDUSB, usbDevice.getUsbDeviceDescriptor().bcdUSB());
        properties.put(USBInfoDevice.USB_BDEVICECLASS, usbDevice.getUsbDeviceDescriptor().bDeviceClass());
        properties.put(USBInfoDevice.USB_BDEVICESUBCLASS, usbDevice.getUsbDeviceDescriptor().bDeviceSubClass());
        properties.put(USBInfoDevice.USB_BDEVICEPROTOCOL, usbDevice.getUsbDeviceDescriptor().bDeviceProtocol());
        properties.put(USBInfoDevice.USB_BMAXPACKETSIZE0, usbDevice.getUsbDeviceDescriptor().bMaxPacketSize0());
        properties.put(USBInfoDevice.USB_IDVENDOR, usbDevice.getUsbDeviceDescriptor().idVendor());
        properties.put(USBInfoDevice.USB_IDPRODUCT, usbDevice.getUsbDeviceDescriptor().idProduct());
        properties.put(USBInfoDevice.USB_BCDDEVICE, usbDevice.getUsbDeviceDescriptor().bcdDevice());
        try {
            properties.put(USBInfoDevice.USB_MANUFACTURER, usbDevice.getManufacturerString());
        } catch (Exception ex) {
        }
        try {
            properties.put(USBInfoDevice.USB_PRODUCT, usbDevice.getProductString());
        } catch (Exception ex) {
        }
        try {
            properties.put(USBInfoDevice.USB_SERIALNUMBER, usbDevice.getSerialNumberString());
        } catch (Exception ex) {
        }
        properties.put(USBInfoDevice.USB_BNUMCONFIGURATIONS, usbDevice.getUsbDeviceDescriptor().bNumConfigurations());

        return properties;
    }

    /**
     * Extract information from UsbInterfaceDescriptor required by UsbinfoDevice
     *
     * @param usbInterface
     * @return
     */
    public static Map<String, Object> getUsbInterfaceDescriptor(UsbInterfaceDescriptor usbInterface) {
        Hashtable<String, Object> properties = new Hashtable<String, Object>();
        properties.put(USBInfoDevice.USB_BINTERFACENUMBER, usbInterface.bInterfaceNumber());
        properties.put(USBInfoDevice.USB_BALTERNATESETTING, usbInterface.bAlternateSetting());
        properties.put(USBInfoDevice.USB_BNUMENDPOINTS, usbInterface.bNumEndpoints());
        properties.put(USBInfoDevice.USB_BINTERFACECLASS, usbInterface.bInterfaceClass());
        properties.put(USBInfoDevice.USB_BINTERFACESUBCLASS, usbInterface.bInterfaceSubClass());
        properties.put(USBInfoDevice.USB_BINTERFACEPROTOCOL, usbInterface.bInterfaceProtocol());
        properties.put(USBInfoDevice.USB_INTERFACE, usbInterface.iInterface());
        return properties;
    }

}
