package com.ptoceti.osgi.modbusdevice.impl.functions;

import com.ptoceti.osgi.modbusdevice.impl.Activator;
import com.ptoceti.osgi.modbusdevice.impl.ModbusDeviceFactory;
import com.ptoceti.osgi.modbusdevice.impl.ModbusDeviceImpl;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

public class ModbusMeterFactory implements org.osgi.service.cm.ManagedServiceFactory {

    public static String FUNCTION_ID = "com.ptoceti.osgi.modbusdevice.function.meter.functionId";
    public static String DEVICE_ID = "com.ptoceti.osgi.modbusdevice.function.meter.deviceId";
    public static String SERVICE_TYPE = "com.ptoceti.osgi.modbusdevice.function.meter.serviceType";
    public static String MODBUS_ID = "com.ptoceti.osgi.modbusdevice.function.meter.modbusId";
    public static String CURRENT_REGISTER_ID = "com.ptoceti.osgi.modbusdevice.function.meter.current.registerId";
    public static String CURRENT_UNIT = "com.ptoceti.osgi.modbusdevice.function.meter.current.unit";
    public static String TOTAL_REGISTER_ID = "com.ptoceti.osgi.modbusdevice.function.meter.total.registerId";
    public static String TOTAL_UNIT = "com.ptoceti.osgi.modbusdevice.function.meter.total.unit";

    // the hashtable contain the references to all function instances created.
    Hashtable<String, ModbusMeterFunction> modbusFuncServices;
    // a reference to the service registration for the ModbusFunctionFactory.
    ServiceRegistration modbusFuncFactoryReg = null;

    public ModbusMeterFactory() {
        // create a new hastable that will contain references to all the ModbusDevice modules.
        modbusFuncServices = new Hashtable<String, ModbusMeterFunction>();
        // register the class as a service factory.
        Hashtable properties = new Hashtable();
        properties.put(Constants.SERVICE_PID, "com.ptoceti.osgi.modbusdevice.functions.ModbusMeterFactory");
        modbusFuncFactoryReg = Activator.getBc().registerService(ManagedServiceFactory.class.getName(),
                this, properties);

        Activator.getLogger().info("Registered " + ModbusMeterFactory.class.getName()
                + " as " + ManagedServiceFactory.class.getName() + ", Pid = " + (String) properties.get(Constants.SERVICE_PID));
    }

    /**
     * Uregistered the class from the service registration system.
     */
    public void stop() {

        // Unregister the factory first ..
        modbusFuncFactoryReg.unregister();
        // .. second, stop all the ModbusDevice services.
        for (Enumeration<ModbusMeterFunction> mdbFunct = modbusFuncServices.elements(); mdbFunct.hasMoreElements(); ) {
            ModbusMeterFunction mdbDev = mdbFunct.nextElement();
            mdbDev.stop();
        }

        Activator.getLogger().info("Unregistered " + ModbusDeviceFactory.class.getName());
    }

    /**
     * Add a ModbusMeterFunction object to the internal list
     *
     * @param pid      : the persistant identifier of the function class.
     * @param function : the Modbus function object to add.
     */
    protected void add(String pid, ModbusMeterFunction function) {

        // add this instance to the hashtable.
        modbusFuncServices.put(pid, function);
        Activator.getLogger().info("ModbusMeterFactory: created ModbusMeterFunction, pid=" + pid);
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
        id = dictionary.get(CURRENT_REGISTER_ID);
        Integer currentRegisterId = id instanceof Integer ? (Integer) id : Integer.parseInt(id.toString());
        String currentUnit = (String) dictionary.get(CURRENT_UNIT);
        id = dictionary.get(TOTAL_REGISTER_ID);
        Integer totalRegisterId = id instanceof Integer ? (Integer) id : Integer.parseInt(id.toString());
        String totalUnit = (String) dictionary.get(TOTAL_UNIT);


        // We need to check if the service with the given pid already exist in our collection. This would
        // mean that the configuration has been updated.
        ModbusMeterFunction modbusFunctSer = (ModbusMeterFunction) modbusFuncServices.get(functionId);
        if (modbusFunctSer != null) {
            // in which case, the simplest is to get rid of the existing instance, and recreate a brand new one.
            modbusFunctSer.stop();
            modbusFuncServices.remove(modbusFunctSer);
        }

        modbusFunctSer = new ModbusMeterFunction(functionId, deviceId, type, modbusId.byteValue(), currentRegisterId.intValue(), currentUnit, totalRegisterId, totalUnit);
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
        ModbusMeterFunction modbusFunctSer = modbusFuncServices.get(pid);
        // simple precaution, we first check that we effectively got an instance with this pid
        if (modbusFunctSer != null) {
            // then we got rid of it.
            modbusFunctSer.stop();
            modbusFuncServices.remove(pid);
            Activator.getLogger().info("Removed ModbusMeterFunction type: " + modbusFunctSer.getClass().getName() + ", service pid: " + pid);
        }
    }
}
