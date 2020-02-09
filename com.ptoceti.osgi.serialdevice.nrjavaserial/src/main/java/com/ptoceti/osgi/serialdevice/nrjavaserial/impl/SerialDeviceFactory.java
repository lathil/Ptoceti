package com.ptoceti.osgi.serialdevice.nrjavaserial.impl;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.serial.SerialDeviceException;

import java.util.Dictionary;
import java.util.Hashtable;

public class SerialDeviceFactory implements org.osgi.service.cm.ManagedServiceFactory {

    // the hashtable contain the references to all SerialDeviceImpl instances created.
    Hashtable<String, SerialDeviceImpl> serialDevServices;
    // a reference to the service registration for the SerialDeviceFactory.
    ServiceRegistration serialDevFactoryReg = null;


    public static String compositeIdentityKey = "com.ptoceti.osgi.serialdevice.nrjavaserial.compositeIdentity";
    public static String portName = "com.ptoceti.osgi.serialdevice.nrjavaserial.serialport";
    public static String usbDeviceFiler = "com.ptoceti.osgi.serialdevice.nrjavaserial.usbdevicefilter";


    /**
     * Create a new SerialDeviceFactory instance. Register the class instance as a ManagedServiceFactory.
     * The class will be recognised as such by the framework allowing it to pass on configuration data.
     */
    public SerialDeviceFactory() {
        // create a new hastable that will contain references to all the SerialDevice modules.
        serialDevServices = new Hashtable<String, SerialDeviceImpl>();
        // register the class as a service factory.
        Hashtable properties = new Hashtable();
        properties.put(Constants.SERVICE_PID, "com.ptoceti.osgi.serialdevice.nrjavaserial.SerialDeviceFactory");
        serialDevFactoryReg = Activator.bc.registerService(ManagedServiceFactory.class.getName(),
                this, properties);

        Activator.getLogger().info("Registered " + SerialDeviceFactory.class.getName()
                + " as " + ManagedServiceFactory.class.getName() + ", Pid = " + (String) properties.get(Constants.SERVICE_PID));
    }

    /**
     * Uregistered the class from the service registration system.
     */
    public void stop() {

        // Unregister the factory first ..
        serialDevFactoryReg.unregister();

        serialDevServices.forEach((pid, serialDevice) -> serialDevice.stop());


        Activator.getLogger().info("Unregistered " + SerialDeviceFactory.class.getName());
    }

    /**
     * ManagedServiceFactory Interface method
     * Called by the framewok when the configuration manager has fond new configuration for this service.
     *
     * @param pid        The persistent identificator of the Serial device service to update.
     * @param properties The new properties for this service.
     */
    public void updated(String pid, Dictionary properties) {

        String port = (String) properties.get(portName);
        String usbDeviceFilter = (String) properties.get(usbDeviceFiler);

        // remove client with same id if it exists.
        deleted(pid);

        SerialDeviceImpl device = null;
        if (usbDeviceFilter != null && !usbDeviceFilter.isBlank()) {
            device = new UsbSerialDeviceImpl(pid, port, usbDeviceFilter);
        } else {
            device = new SerialDeviceImpl(pid, port);
        }

        try {
            device.start();
            add(pid, device);
        } catch (SerialDeviceException ex) {

        }


    }

    /**
     * Add a Serialdevice object to the internal list
     *
     * @param pid    : the persistant identifier of the device class.
     * @param device : the TeleinfoDevice object to add.
     */
    protected void add(String pid, SerialDeviceImpl device) {

        // add this instance to the hashtable.
        serialDevServices.put(pid, device);
        Activator.getLogger().info("SerialDeviceFactory: created SerialDevice, pid=" + pid);
    }

    /**
     * ManagedServiceFactory Interface method
     * Called by the framewok when one of the service instance created by
     * the factory is removed.
     *
     * @param pid: the service instance persistant identificator
     */
    public void deleted(String pid) {

        SerialDeviceImpl teleinfoDevSer = serialDevServices.get(pid);
        // simple precaution, we first check that we effectively got an instance with this pid
        if (teleinfoDevSer != null) {
            // then we got rid of it.
            teleinfoDevSer.stop();
            serialDevServices.remove(pid);
            Activator.getLogger().info("Removed TeleinfoDevice type: " + teleinfoDevSer.getClass().getName()
                    + ", service pid: " + pid);
        }
    }

    /**
     * ManagedServiceFactory Interface method
     *
     * @return the name of this factory.
     */
    public String getName() {

        return (this.getName());
    }
}
