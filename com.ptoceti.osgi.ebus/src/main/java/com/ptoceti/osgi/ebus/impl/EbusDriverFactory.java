package com.ptoceti.osgi.ebus.impl;

import com.ptoceti.osgi.ebus.EbusDriver;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.log.LogService;

import java.util.Hashtable;
import java.util.Dictionary;
import java.util.Enumeration;

/**
 * EbusDriverFactory obey the factory design pattern. It is a factory for creating ModusDriver 
 * implementations classes ( the EbusDriver classes are the real device services used by other services ).
 * The class also implement the ManagedServiceFactory, which means it can receive configuration from
 * ConfigurationManager service of the framework for each object it has to create. This information is
 * passed on through the updated(..) method.
 * At initialisation, the class does not create any object, it only registers itself as a ManagedServiceFactory
 * for receiving the configuration. It then create the objects as the configuration is loaded.
 * It is slightly different from a normal factory. The class will create the objects as the configuration
 * manager indicate. The objects ( instances of EbusDriver ) will then be registerd in the framework for
 * use by other services.
 *
 * @author Laurent Thil
 * @version 1.0b
 */
public class EbusDriverFactory implements org.osgi.service.cm.ManagedServiceFactory {

    // the hashtable contain the references to all EbusDrivers instances created.
    Hashtable EbusDrivers;
    // a reference to the service registration for the EbusDriverFactory.
    ServiceRegistration EbusDriverFactoryReg = null;

    /**
     * Create a new EbusDriverFactory instance. Register the class instance as a ManagedServiceFactory.
     * The class will be recognised as such by the framework allowing it to pass on configuration data.
     */
    public EbusDriverFactory() {
        // create a new hastable that will contain references to all the Ebusdriver services.
        EbusDrivers = new Hashtable();
        // register the class as a service factory.
        Hashtable properties = new Hashtable();
        properties.put(Constants.SERVICE_PID, "com.ptoceti.osgi.ebus.EbusDriverFactory");
        EbusDriverFactoryReg = Activator.bc.registerService(ManagedServiceFactory.class.getName(),
                this, properties);

        Activator.getLogger().info("Registered " + EbusDriverFactory.class.getName()
                + " as " + ManagedServiceFactory.class.getName());
    }

    /**
     * Uregistered the class from the service registration system.
     *
     *
     */
    public void stop() {
        // Unregister the factory first ..
        EbusDriverFactoryReg.unregister();
        // .. second, stop all the EbusDriver services.
        for( Enumeration mdbDrivs = EbusDrivers.elements(); mdbDrivs.hasMoreElements(); ) {
            EbusDriverImpl mdbDrvImpl= (EbusDriverImpl) mdbDrivs.nextElement();
            mdbDrvImpl.stop();
        }

        Activator.getLogger().info("Unregistered " + EbusDriverFactory.class.getName());
    }

    /**
     * ManagedServiceFactory Interface method
     * Called by the framewok when the configuration manager has fond new configuration for this service.
     * Four properties are expected from each configuration set: Ebus_port, Ebus_slave_master, 
     * Ebus_id and Ebus_encoding ( theses values are described in the EbusDriver interface ). For
     * each configuration set, an EbusDriver instance is created with the above properties. The instance
     * will register itself as an available service to the framework.
     *
     * @param pid The persistent identificator of the service to update.
     * @param properties The new properties collection for this service.
     */
    public void updated(String pid, Dictionary properties) {

        String port = (String) properties.get(EbusDriver.EBUS_PORT);
        Object id = properties.get(EbusDriver.EBUS_ID);
        Integer ebusID = id instanceof Integer ? (Integer) id: Integer.parseInt(id.toString());

        Object lockCounter = properties.get(EbusDriver.EBUS_LOCKCOUNTER_MAX);
        Integer ebusLockCounter = id instanceof Integer ? (Integer) lockCounter: Integer.parseInt(lockCounter.toString());

        // First check that we have all the configuration data necessary.
        if((port != null ) && (ebusID != null)) {

            int EbusIDInt = ( ebusID ).intValue();

            // Then check that the configuration data is valid.
            if((EbusIDInt > 0) && (EbusIDInt <= 254 )) {

                // check if we have already created a EbusDriver with this particular configuration pid.
                EbusDriverImpl mdbDriver = (EbusDriverImpl) EbusDrivers.get(pid);
                // if we already got it, we need to stop it. This will unregister the service from the framework.
                if( mdbDriver != null ) {
                    mdbDriver.stop();
                    // remove the old driver from the list
                    EbusDrivers.remove(pid);
                    // and clear the reference
                    mdbDriver = null;
                }
                // then we need to create an new instance of a EbusDriver, either master or slave kind. There could be errors when 
                // opening the serial port.
                try {
                    mdbDriver = new EbusDriverImpl(pid, EbusIDInt, port, ebusLockCounter);
                    // try to start
                    mdbDriver.start();
                    // keep track of this instance.
                    EbusDrivers.put(pid, mdbDriver);
                    Activator.getLogger().info("Created EbusDriver type: " + mdbDriver.getClass().getName()
                            + ", port: " + port + ", id: " + ebusID + ", service factory pid: " + pid);
                } catch ( Exception e ) {
                    Activator.getLogger().info("Could not create EbusDriver port. Reason: " + e.toString());
                    mdbDriver = null;
                }

            }
            else {
                Activator.getLogger().info("Cannot create EbusDriver service: bad configuration data.");
            }

        }
        else {
            String missingParam = "";
            if(port == null) missingParam = EbusDriver.EBUS_PORT;
            else if (ebusID == null) missingParam = EbusDriver.EBUS_ID;
            Activator.getLogger().info("Cannot create EbusDriver service: configuration data missing: " + missingParam);
        }
    }

    /**
     * ManagedServiceFactory Interface method
     * Called by the framewok when one of the service instance created by
     * the factory is removed.
     *
     * @param pid: the service instance persistant identificator
     */
    public void deleted(String pid ) {
        EbusDriverImpl mdbDriver = (EbusDriverImpl) EbusDrivers.get(pid);
        // simple precaution, we first check that we effectively got an instance with this pid
        if( mdbDriver != null ) {
            // then we got rid of it.
            EbusDrivers.remove(pid);
            mdbDriver.stop();
            Activator.getLogger().info("Removed EbusDriver type: " + mdbDriver.getClass().getName()
                    + ", service factory pid: " + pid);
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
