package com.ptoceti.ebus.ebusdevice.impl;

import com.ptoceti.ebus.ebusdevice.EbusDevice;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.log.LogService;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

public class EbusDeviceFactory implements org.osgi.service.cm.ManagedServiceFactory {
    // the hashtable contain the references to all EbusDevice instances created.
    Hashtable<String, EbusDevice> ebusDevServices;
    // a reference to the service registration for the EbusDeviceFactory.
    ServiceRegistration ebusDevFactoryReg = null;

    public static String compositeIdentityKey = "com.ptoceti.osgi.ebudevice.compositeIdentity";
    public static String portNameKey = "com.ptoceti.osgi.ebudevice.portName";
    public static String ebusIdKey = "com.ptoceti.osgi.ebusdevice.ebusId";
    public static String poolingRateKey = "com.ptoceti.osgi.ebusdevice.poolingRate";
    public static String configFilePathKey = "com.ptoceti.osgi.ebusdevice.configFilePath";
    public static String mock = "com.ptoceti.osgi.ebusdevice.mock";

    /**
     * Create a new EbusDeviceFactory instance. Register the class instance as a ManagedServiceFactory.
     * The class will be recognised as such by the framework allowing it to pass on configuration data.
     *
     */
    public EbusDeviceFactory() {
        // create a new hastable that will contain references to all the EbusDevice modules.
        ebusDevServices = new Hashtable<String, EbusDevice>();
        // register the class as a service factory.
        Hashtable properties = new Hashtable();
        properties.put( Constants.SERVICE_PID, "com.ptoceti.osgi.ebusdevice.EbusDeviceFactory");
        ebusDevFactoryReg = Activator.bc.registerService(ManagedServiceFactory.class.getName(),
                this, properties );

        Activator.log(LogService.LOG_INFO, "Registered " + EbusDeviceFactory.class.getName()
                + " as " + ManagedServiceFactory.class.getName() + ", Pid = " + properties.get(Constants.SERVICE_PID));
    }

    /**
     * Uregistered the class from the service registration system.
     *
     *
     */
    public void stop() {

        // Unregister the factory first ..
        ebusDevFactoryReg.unregister();
        // .. second, stop all the ModbusDevice services.
        for(Enumeration<EbusDevice> mdbServs = ebusDevServices.elements(); mdbServs.hasMoreElements(); ) {
            EbusDevice mdbDev = mdbServs.nextElement();
            mdbDev.stop();
        }

        Activator.log(LogService.LOG_INFO, "Unregistered " + EbusDeviceFactory.class.getName());
    }

    /**
     * ManagedServiceFactory Interface method
     * Called by the framewok when the configuration manager has fond new configuration for this service.
     *
     * @param pid The persistent identificator of the EbusDevice service to update.
     * @param properties The new properties for this service.
     */
    public void updated(String pid, Dictionary properties) {


        String compositeIdentity = (String) properties.get(compositeIdentityKey);
        String portName = (String) properties.get(portNameKey);
        Object id = properties.get(ebusIdKey);
        Integer ebusId = id instanceof Integer ? (Integer) id : Integer.parseInt(id.toString());
        Object rate = properties.get(poolingRateKey);
        Integer poolingRate = rate instanceof Integer ? (Integer) rate: Integer.parseInt(rate.toString());
        String configFilePath = (String)  properties.get(configFilePathKey);
        Object mck = properties.get(mock);
        Boolean isMock = mck instanceof Boolean ? (Boolean) mck : Boolean.parseBoolean(mck != null ? mck.toString(): "false");


        // We need to check if the servive with the given pid already exist in our collection. This would
        // mean that the configuration has been updated.
        EbusDevice ebusDevSer = ( EbusDevice) ebusDevServices.get( pid );
        if( ebusDevSer != null ) {
            // in which case, the simplest is to get rid of the existing instance, and recreate a brand new one.
            ebusDevSer.stop();
            ebusDevServices.remove(ebusDevSer);
            ebusDevSer = null;
        }

        try {

            URL configUrl = null;
            if( configFilePath.startsWith("file:")){
                configFilePath = configFilePath.substring("file:".length());

                File file = new File(configFilePath);
                if( file.exists() && !file.isDirectory()) {
                    try {
                        configUrl= file.toURI().toURL();
                    } catch (MalformedURLException e) {
                        Activator.log(LogService.LOG_ERROR, "Error creating url for file path: " + configFilePath);
                    }
                } else {
                    Activator.log(LogService.LOG_ERROR, "Error reading EbusDevice file at: " + file.getAbsolutePath());
                }
            } else {
                configUrl = Activator.getResourceStream(configFilePath);
            }

            if (configUrl != null) {
                InputStream configFileStream = configUrl.openStream();
                // Create an xml file EbusDevice configuration reader, and
                // pass it the EbusDeviceFactory as delegate
                //EbusDeviceConfig mdbConfig = new EbusDeviceConfig(this, pid, compositeIdentity, portName, modbusId, poolingRate, isMock);
                // , tell it to read the file. This in turn will call back the
                // EbusDeviceFactory to create the wires.
                //mdbConfig.parse(configFileStream);
                configFileStream.close();
            }
        } catch (java.io.IOException e) {
            Activator.log(LogService.LOG_INFO, "Configuration file: "
                    + configFilePath + " could not be found.");
        }
    }

    /**
     * Add a EbusDevice object to the internal list
     *
     * @param pid : the persistant identifier of the device class.
     * @param device : the EbusDevice object to add.
     */
    protected void add(String pid, EbusDevice device ) {

        // add this instance to the hashtable.
        ebusDevServices.put( pid, device);
        Activator.log(LogService.LOG_INFO,"EbusDeviceFactory: created EbusDevice, pid=" + pid);
    }
    /**
     * ManagedServiceFactory Interface method
     * Called by the framewok when one of the service instance created by
     * the factory is removed.
     *
     * @param pid: the service instance persistant identificator
     */
    public void deleted(String pid ) {

        EbusDevice ebusDevSer = ebusDevServices.get( pid );
        // simple precaution, we first check that we effectively got an instance with this pid
        if ( ebusDevSer != null ) {
            // then we got rid of it.
            ebusDevSer.stop();
            ebusDevServices.remove(pid);
            Activator.log(LogService.LOG_INFO,"Removed EbusDevice type: " + ebusDevSer.getClass().getName()
                    + ", service pid: " + pid);
        }
    }

    /**
     * ManagedServiceFactory Interface method
     *
     * @return the name of this factory.
     */
    public String getName() {

        return( this.getName());
    }
}
