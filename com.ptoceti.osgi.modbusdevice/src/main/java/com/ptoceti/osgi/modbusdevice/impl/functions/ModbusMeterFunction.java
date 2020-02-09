package com.ptoceti.osgi.modbusdevice.impl.functions;

import com.ptoceti.osgi.modbusdevice.ModbusDevice;
import com.ptoceti.osgi.modbusdevice.impl.Activator;
import com.ptoceti.osgi.modbusdevice.impl.command.ReadInputRegistersCommand;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dal.*;
import org.osgi.service.dal.functions.Meter;
import org.osgi.service.dal.functions.data.LevelData;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;

public class ModbusMeterFunction extends AbstractModbusFunction implements Meter {

    ServiceRegistration sReg;

    protected Object currentLock = new Object();
    protected LevelData current;

    protected Object totalLock = new Object();
    protected LevelData total;

    byte modbusDeviceId;
    int modbusCurrentRegisterId;
    String modbusCurrentUnit;
    int modbusTotalRegisterId;
    String modbusTotalUnit;

    ServiceTracker eventAdminTracker;


    Map<String, PropertyMetadata> propertyMetadataMap = new HashMap<String, PropertyMetadata>();

    public ModbusMeterFunction(String functionId, String deviceId, String serviceType, byte modbusId, int currentRegisterId, String currentUnit, int totalRegisterId, String totalUnit) {
        super(functionId, deviceId, serviceType);
        modbusDeviceId = modbusId;
        modbusCurrentRegisterId = currentRegisterId;
        modbusCurrentUnit = currentUnit;
        modbusTotalRegisterId = totalRegisterId;
        modbusTotalUnit = totalUnit;
    }

    @Override
    public void start() {

        String[] clazzes = new String[]{
                ModbusMeterFunction.class.getName(),
                Function.class.getName()
        };

        String[] operationNames = new String[]{};
        String[] propertyNames = new String[]{ModbusMeterFunction.PROPERTY_CURRENT, ModbusMeterFunction.PROPERTY_TOTAL};

        Map<String, Object> currentMetaData = new HashMap<>() {{
            put(PropertyMetadata.UNITS, modbusCurrentUnit);
            put(PropertyMetadata.ACCESS, Integer.valueOf(PropertyMetadata.ACCESS_READABLE));
        }};

        Map<String, Object> totalMetaData = new HashMap<>() {{
            put(PropertyMetadata.UNITS, modbusTotalUnit);
            put(PropertyMetadata.ACCESS, Integer.valueOf(PropertyMetadata.ACCESS_READABLE));
        }};

        propertyMetadataMap.put(ModbusMeterFunction.PROPERTY_CURRENT, new PropertyMetadataImpl(currentMetaData, null, null, null, null));
        propertyMetadataMap.put(ModbusMeterFunction.PROPERTY_TOTAL, new PropertyMetadataImpl(totalMetaData, null, null, null, null));

        Hashtable currentProps = new Hashtable();
        currentProps.put(LevelData.FIELD_LEVEL, BigDecimal.valueOf(0));
        currentProps.put(LevelData.FIELD_UNIT, modbusCurrentUnit);
        currentProps.put(LevelData.FIELD_TIMESTAMP, System.currentTimeMillis());
        current = new LevelData(currentProps);

        Hashtable totalProps = new Hashtable();
        totalProps.put(LevelData.FIELD_LEVEL, BigDecimal.valueOf(0));
        totalProps.put(LevelData.FIELD_UNIT, modbusTotalUnit);
        totalProps.put(LevelData.FIELD_TIMESTAMP, System.currentTimeMillis());
        total = new LevelData(totalProps);

        Dictionary props = new Hashtable();
        props.put(Function.SERVICE_UID, this.deviceId + ":" + this.functionId);
        props.put(Function.SERVICE_TYPE, "");
        props.put(Function.SERVICE_VERSION, Activator.getBc().getBundle().getVersion().toString());
        props.put(Function.SERVICE_DEVICE_UID, this.deviceId);
        props.put(Function.SERVICE_DESCRIPTION, "Modbus Meter DAL function");
        props.put(Function.SERVICE_OPERATION_NAMES, operationNames);
        props.put(Function.SERVICE_PROPERTY_NAMES, propertyNames);

        sReg = Activator.getBc().registerService(clazzes, this, props);

        eventAdminTracker = new ServiceTracker(Activator.getBc(), EventAdmin.class.getName(), null);
        eventAdminTracker.open();

        Activator.getLogger().info("Unregistered " + this.getClass().getName());

        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        eventAdminTracker.close();
        sReg.unregister();
        sReg = null;
        Activator.getLogger().info("Registered " + this.getClass().getName());
    }

    void submitCommand(ModbusDevice modbusDevice) {
        ReadInputRegistersCommand readCurrentCommand = new ReadInputRegistersCommand(modbusDevice, modbusDeviceId, modbusCurrentRegisterId, 1);
        Future<List<Integer>> currentFuture = modbusDevice.submitCommand(readCurrentCommand);
        try {
            List<Integer> results = currentFuture.get();
            if (results != null && results.size() > 0) {
                Integer updated = results.get(0);
                synchronized (currentLock) {
                    BigDecimal next = BigDecimal.valueOf(updated.longValue());
                    Map currentMap = current.getMetadata();
                    BigDecimal previous = (BigDecimal) currentMap.get(LevelData.FIELD_LEVEL);
                    currentMap.put(LevelData.FIELD_LEVEL, next);
                    currentMap.put(LevelData.FIELD_TIMESTAMP, System.currentTimeMillis());

                    if (previous.compareTo(next) != 0) {
                        EventAdmin eventAdmin = (EventAdmin) eventAdminTracker.getService();
                        if (eventAdmin != null) {
                            FunctionEvent event = new FunctionEvent(FunctionEvent.TOPIC_PROPERTY_CHANGED, this.functionId, ModbusMeterFunction.PROPERTY_CURRENT, current);
                            eventAdmin.postEvent(event);
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        }

        ReadInputRegistersCommand readTotalCommand = new ReadInputRegistersCommand(modbusDevice, modbusDeviceId, modbusTotalRegisterId, 1);
        Future<List<Integer>> totalFuture = modbusDevice.submitCommand(readTotalCommand);
        try {
            List<Integer> results = totalFuture.get();
            if (results != null && results.size() > 0) {
                Integer updated = results.get(0);
                synchronized (totalLock) {
                    BigDecimal next = BigDecimal.valueOf(updated.longValue());
                    Map currentMap = total.getMetadata();
                    BigDecimal previous = (BigDecimal) currentMap.get(LevelData.FIELD_LEVEL);
                    currentMap.put(LevelData.FIELD_LEVEL, next);
                    currentMap.put(LevelData.FIELD_TIMESTAMP, System.currentTimeMillis());
                    if (previous.compareTo(next) != 0) {
                        EventAdmin eventAdmin = (EventAdmin) eventAdminTracker.getService();
                        if (eventAdmin != null) {
                            FunctionEvent event = new FunctionEvent(FunctionEvent.TOPIC_PROPERTY_CHANGED, this.functionId, ModbusMeterFunction.PROPERTY_TOTAL, total);
                            eventAdmin.postEvent(event);
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        }
    }


    @Override
    public LevelData getCurrent() throws DeviceException {
        synchronized (currentLock) {
            return current;
        }
    }

    @Override
    public LevelData getTotal() throws DeviceException {
        synchronized (totalLock) {
            return total;
        }
    }

    @Override
    public PropertyMetadata getPropertyMetadata(String s) {
        return propertyMetadataMap.get(s);
    }

    @Override
    public OperationMetadata getOperationMetadata(String s) {
        return null;
    }

    @Override
    public Object getServiceProperty(String s) {
        return sReg.getReference().getProperty(s);
    }

    @Override
    public String[] getServicePropertyKeys() {
        return sReg.getReference().getPropertyKeys();
    }
}
