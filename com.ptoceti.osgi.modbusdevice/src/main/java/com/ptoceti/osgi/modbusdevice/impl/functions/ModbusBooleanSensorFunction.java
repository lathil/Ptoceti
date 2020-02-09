package com.ptoceti.osgi.modbusdevice.impl.functions;

import com.ptoceti.osgi.modbusdevice.ModbusDevice;
import com.ptoceti.osgi.modbusdevice.impl.Activator;
import com.ptoceti.osgi.modbusdevice.impl.command.ReadInputStatusCommand;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dal.*;
import org.osgi.service.dal.functions.BooleanSensor;
import org.osgi.service.dal.functions.data.BooleanData;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ModbusBooleanSensorFunction extends AbstractModbusFunction implements BooleanSensor {


    ServiceRegistration sReg;

    protected ModbusDevice modbusDevice;

    protected Object dataLock = new Object();
    BooleanData data;

    byte modbusDeviceId;
    int modbusInputId;

    ServiceTracker eventAdminTracker;

    Map<String, PropertyMetadata> propertyMetadataMap = new HashMap<String, PropertyMetadata>();

    public ModbusBooleanSensorFunction(String functionId, String deviceId, String serviceType, byte modbusId, int inputId) {
        super(functionId, deviceId, serviceType);
        modbusDeviceId = modbusId;
        modbusInputId = inputId;
    }

    public void start() {

        Map<String, Object> metaData = new HashMap<>() {{
            put(PropertyMetadata.ACCESS, Integer.valueOf(PropertyMetadata.ACCESS_READABLE));
        }};

        propertyMetadataMap.put(ModbusBooleanSensorFunction.PROPERTY_DATA, new PropertyMetadataImpl(metaData, null, null, null, null));

        Hashtable dataProps = new Hashtable();
        dataProps.put(BooleanData.FIELD_VALUE, false);
        dataProps.put(BooleanData.FIELD_TIMESTAMP, System.currentTimeMillis());
        data = new BooleanData(dataProps);

        String[] clazzes = new String[]{
                BooleanSensor.class.getName(),
                Function.class.getName()
        };

        String[] operationNames = new String[]{};
        String[] propertyNames = new String[]{BooleanSensor.PROPERTY_DATA};

        Dictionary props = new Hashtable();
        props.put(Function.SERVICE_UID, this.deviceId + ":" + this.functionId);
        props.put(Function.SERVICE_TYPE, "");
        props.put(Function.SERVICE_VERSION, Activator.getBc().getBundle().getVersion().toString());
        props.put(Function.SERVICE_DEVICE_UID, this.deviceId);
        props.put(Function.SERVICE_DESCRIPTION, "Modbus BooleanSensor DAL function");
        props.put(Function.SERVICE_OPERATION_NAMES, operationNames);
        props.put(Function.SERVICE_PROPERTY_NAMES, propertyNames);

        sReg = Activator.getBc().registerService(clazzes, this, props);

        eventAdminTracker = new ServiceTracker(Activator.getBc(), EventAdmin.class.getName(), null);
        eventAdminTracker.open();

        Activator.getLogger().info("Registered " + this.getClass().getName());

        super.start();
    }

    public void stop() {
        super.stop();
        eventAdminTracker.close();
        sReg.unregister();
        sReg = null;
        Activator.getLogger().info("Registered " + this.getClass().getName());
    }

    @Override
    void submitCommand(ModbusDevice modbusDevice) {
        ReadInputStatusCommand command = new ReadInputStatusCommand(modbusDevice, modbusDeviceId, modbusInputId, 1);
        Future<List<Boolean>> future = modbusDevice.submitCommand(command);
        try {
            List<Boolean> results = future.get();
            if (results != null && results.size() > 0) {
                Boolean updated = results.get(0);
                synchronized (dataLock) {
                    Map currentMap = data.getMetadata();
                    Boolean previous = (Boolean) currentMap.get(BooleanData.FIELD_VALUE);
                    currentMap.put(BooleanData.FIELD_VALUE, updated);
                    currentMap.put(BooleanData.FIELD_TIMESTAMP, System.currentTimeMillis());
                    if (previous.compareTo(updated) != 0) {
                        EventAdmin eventAdmin = (EventAdmin) eventAdminTracker.getService();
                        if (eventAdmin != null) {
                            FunctionEvent event = new FunctionEvent(FunctionEvent.TOPIC_PROPERTY_CHANGED, this.functionId, ModbusBooleanSensorFunction.PROPERTY_DATA, data);
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
    public BooleanData getData() throws DeviceException {
        synchronized (dataLock) {
            return data;
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
