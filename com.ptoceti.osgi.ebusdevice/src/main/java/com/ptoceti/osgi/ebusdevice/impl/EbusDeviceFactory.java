package com.ptoceti.osgi.ebusdevice.impl;

import com.ptoceti.osgi.ebusdevice.EbusDevice;
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

    public static String name = "com.ptoceti.osgi.ebudevice.name";
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

        Activator.getLogger().info("Registered " + EbusDeviceFactory.class.getName()
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

        Activator.getLogger().info("Unregistered " + EbusDeviceFactory.class.getName());
    }

    /**
     * ManagedServiceFactory Interface method
     * Called by the framewok when the configuration manager has fond new configuration for this service.
     *
     * @param pid The persistent identificator of the EbusDevice service to update.
     * @param properties The new properties for this service.
     */
    public void updated(String pid, Dictionary properties) {

        String portName = (String) properties.get(portNameKey);
        String name = (String) properties.get(portNameKey);
        Object id = properties.get(ebusIdKey);
        Integer ebusId = id instanceof Integer ? (Integer) id : Integer.parseInt(id.toString());
        Object rate = properties.get(poolingRateKey);
        Integer poolingRate = rate instanceof Integer ? (Integer) rate: Integer.parseInt(rate.toString());
        String configFilePath = (String)  properties.get(configFilePathKey);
        Object mck = properties.get(mock);
        Boolean isMock = mck instanceof Boolean ? (Boolean) mck : Boolean.parseBoolean(mck != null ? mck.toString(): "false");


        // We need to check if the servive with the given pid already exist in our collection. This would
        // mean that the configuration has been updated.
        EbusDeviceImpl ebusDevSer = (EbusDeviceImpl) ebusDevServices.get(pid);
        if (ebusDevSer != null) {
            // in which case, the simplest is to get rid of the existing instance, and recreate a brand new one.
            ebusDevSer.stop();
            ebusDevServices.remove(ebusDevSer);
            ebusDevSer = null;
        }

        ebusDevSer = new EbusDeviceImpl(pid, name, portName, ebusId, poolingRate);
        add(pid, ebusDevSer);
        ebusDevSer.start();

    }

    /**
     * Add a EbusDevice object to the internal list
     *
     * @param pid : the persistant identifier of the device class.
     * @param device : the EbusDevice object to add.
     */
    protected void add(String pid, EbusDevice device ) {

        // add this instance to the hashtable.
        ebusDevServices.put(pid, device);
        Activator.getLogger().info("EbusDeviceFactory: created EbusDevice, pid=" + pid);
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
            Activator.getLogger().info("Removed EbusDevice type: " + ebusDevSer.getClass().getName() + ", service pid: " + pid);
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
