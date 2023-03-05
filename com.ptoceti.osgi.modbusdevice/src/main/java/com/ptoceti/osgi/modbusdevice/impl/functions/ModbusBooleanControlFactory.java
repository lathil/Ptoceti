package com.ptoceti.osgi.modbusdevice.impl.functions;

import com.ptoceti.osgi.modbusdevice.impl.Activator;
import com.ptoceti.osgi.modbusdevice.impl.ModbusDeviceFactory;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

public class ModbusBooleanControlFactory implements org.osgi.service.cm.ManagedServiceFactory {

    public static String FUNCTION_ID = "com.ptoceti.osgi.modbusdevice.function.booleancontrol.functionId";
    public static String DEVICE_ID = "com.ptoceti.osgi.modbusdevice.function.booleancontrol.deviceId";
    public static String SERVICE_TYPE = "com.ptoceti.osgi.modbusdevice.function.booleancontrol.serviceType";
    public static String MODBUS_ID = "com.ptoceti.osgi.modbusdevice.function.booleancontrol.modbusId";
    public static String COIL_ID = "com.ptoceti.osgi.modbusdevice.function.booleancontrol.coilId";

    // the hashtable contain the references to all function instances created.
    Hashtable<String, ModbusBooleanControlFunction> modbusFuncServices;
    // a reference to the service registration for the ModbusFunctionFactory.
    ServiceRegistration modbusFuncFactoryReg = null;

    public ModbusBooleanControlFactory() {
        // create a new hastable that will contain references to all the ModbusDevice modules.
        modbusFuncServices = new Hashtable<String, ModbusBooleanControlFunction>();
        // register the class as a service factory.
        Hashtable properties = new Hashtable();
        properties.put(Constants.SERVICE_PID, "com.ptoceti.osgi.modbusdevice.functions.ModbusBooleanControlFactory");
        modbusFuncFactoryReg = Activator.getBc().registerService(ManagedServiceFactory.class.getName(),
                this, properties);

        Activator.getLogger().info("Registered " + ModbusBooleanControlFactory.class.getName()
                + " as " + ManagedServiceFactory.class.getName() + ", Pid = " + (String) properties.get(Constants.SERVICE_PID));
    }

    /**
     * Uregistered the class from the service registration system.
     */
    public void stop() {

        // Unregister the factory first ..
        modbusFuncFactoryReg.unregister();
        // .. second, stop all the ModbusDevice services.
        for (Enumeration<ModbusBooleanControlFunction> mdbFunct = modbusFuncServices.elements(); mdbFunct.hasMoreElements(); ) {
            ModbusBooleanControlFunction mdbDev = mdbFunct.nextElement();
            mdbDev.stop();
        }

        Activator.getLogger().info("Unregistered " + ModbusDeviceFactory.class.getName());
    }

    /**
     * Add a ModbusBooleanSensorFunction object to the internal list
     *
     * @param pid      : the persistant identifier of the function class.
     * @param function : the Modbus function object to add.
     */
    protected void add(String pid, ModbusBooleanControlFunction function) {

        // add this instance to the hashtable.
        modbusFuncServices.put(pid, function);
        Activator.getLogger().info("ModbusBooleanControlFactory: created ModbusBooleanControlFunction, pid=" + pid);
    }

    /**
     * ManagedServiceFactory Interface method
     *
     * @return the name of this factory.
     */
    @Override
    public String getName() {

        return (this.getName());
    }

    @Override
    public void updated(String s, Dictionary<String, ?> dictionary) throws ConfigurationException {
        String functionId = (String) dictionary.get(FUNCTION_ID);
        String deviceId = (String) dictionary.get(DEVICE_ID);
        String type = (String) dictionary.get(SERVICE_TYPE);
        Object id = dictionary.get(MODBUS_ID);
        Integer modbusId = id instanceof Integer ? (Integer) id : Integer.parseInt(id.toString());
        id = dictionary.get(COIL_ID);
        Integer inputId = id instanceof Integer ? (Integer) id : Integer.parseInt(id.toString());


        // We need to check if the service with the given pid already exist in our collection. This would
        // mean that the configuration has been updated.
        ModbusBooleanControlFunction modbusFunctSer = (ModbusBooleanControlFunction) modbusFuncServices.get(functionId);
        if (modbusFunctSer != null) {
            // in which case, the simplest is to get rid of the existing instance, and recreate a brand new one.
            modbusFunctSer.stop();
            modbusFuncServices.remove(modbusFunctSer);
        }

        modbusFunctSer = new ModbusBooleanControlFunction(functionId, deviceId, type, modbusId.byteValue(), inputId.intValue());
        add(functionId, modbusFunctSer);
        modbusFunctSer.start();
    }

    /**
     * ManagedServiceFactory Interface method
     * Called by the framewok when one of the service instance created by
     * the factory is removed.
     *
     * @param pid: the service instance persistant identificator
     */
    @Override
    public void deleted(String pid) {
        ModbusBooleanControlFunction modbusFunctSer = modbusFuncServices.get(pid);
        // simple precaution, we first check that we effectively got an instance with this pid
        if (modbusFunctSer != null) {
            // then we got rid of it.
            modbusFunctSer.stop();
            modbusFuncServices.remove(pid);
            Activator.getLogger().info("Removed ModbusBooleanControlFunction type: " + modbusFunctSer.getClass().getName() + ", service pid: " + pid);
        }
    }
}
