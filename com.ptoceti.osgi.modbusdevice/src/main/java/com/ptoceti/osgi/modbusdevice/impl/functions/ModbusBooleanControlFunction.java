package com.ptoceti.osgi.modbusdevice.impl.functions;

import com.ptoceti.osgi.modbusdevice.ModbusDevice;
import com.ptoceti.osgi.modbusdevice.impl.Activator;
import com.ptoceti.osgi.modbusdevice.impl.command.ForceSingleCoilCommand;
import com.ptoceti.osgi.modbusdevice.impl.command.ReadCoilsStatusCommand;

import jdk.dynalink.Operation;
import org.osgi.framework.*;
import org.osgi.service.dal.*;
import org.osgi.service.dal.functions.BooleanControl;
import org.osgi.service.dal.functions.data.BooleanData;


import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ModbusBooleanControlFunction extends AbstractModbusFunction implements BooleanControl {

    ServiceRegistration sReg;

    protected ModbusDevice modbusDevice;

    protected Object dataLock = new Object();
    BooleanData data;

    byte modbusDeviceId;
    int modbusInputId;

    Map<String, OperationMetadata> operationMetadataMap = new HashMap<String, OperationMetadata>();
    Map<String, PropertyMetadata> propertyMetadataMap = new HashMap<String, PropertyMetadata>();

    public ModbusBooleanControlFunction(String functionId, String deviceId, String serviceType, byte modbusId, int inputId) {
        super(functionId, deviceId, serviceType);
        modbusDeviceId = modbusId;
        modbusInputId = inputId;
    }

    public void start() {

        Map<String, Object> metaData = new HashMap<>() {{
            put(PropertyMetadata.ACCESS, Integer.valueOf(PropertyMetadata.ACCESS_READABLE | PropertyMetadata.ACCESS_WRITABLE));
        }};
        propertyMetadataMap.put(ModbusBooleanControlFunction.PROPERTY_DATA, new PropertyMetadataImpl(metaData, null, null, null, null));

        operationMetadataMap.put(BooleanControl.OPERATION_INVERSE, new OperationMetaDataImpl(new HashMap<>() {{
            put(OperationMetadata.DESCRIPTION, "Inverse");
        }}));
        operationMetadataMap.put(BooleanControl.OPERATION_SET_TRUE, new OperationMetaDataImpl(new HashMap<>() {{
            put(OperationMetadata.DESCRIPTION, "Set true");
        }}));
        operationMetadataMap.put(BooleanControl.OPERATION_SET_FALSE, new OperationMetaDataImpl(new HashMap<>() {{
            put(OperationMetadata.DESCRIPTION, "Set false");
        }}));

        Hashtable dataProps = new Hashtable();
        dataProps.put(BooleanData.FIELD_VALUE, false);
        dataProps.put(BooleanData.FIELD_TIMESTAMP, System.currentTimeMillis());
        data = new BooleanData(dataProps);

        String[] clazzes = new String[]{
                BooleanControl.class.getName(),
                Function.class.getName()
        };

        String[] operationNames = new String[]{BooleanControl.OPERATION_INVERSE, BooleanControl.OPERATION_SET_FALSE, BooleanControl.OPERATION_SET_TRUE};
        String[] propertyNames = new String[]{BooleanControl.PROPERTY_DATA};

        Dictionary props = new Hashtable();
        props.put(Function.SERVICE_UID, this.deviceId + ":" + this.functionId);
        props.put(Function.SERVICE_TYPE, "");
        props.put(Function.SERVICE_VERSION, Activator.getBc().getBundle().getVersion().toString());
        props.put(Function.SERVICE_DEVICE_UID, this.deviceId);
        props.put(Function.SERVICE_DESCRIPTION, "Modbus BooleanControl DAL function");
        props.put(Function.SERVICE_OPERATION_NAMES, operationNames);
        props.put(Function.SERVICE_PROPERTY_NAMES, propertyNames);

        sReg = Activator.getBc().registerService(clazzes, this, props);

        Activator.getLogger().info("Registered " + this.getClass().getName());

        super.start();
    }

    public void stop() {
        super.stop();
        sReg.unregister();
        sReg = null;
        Activator.getLogger().info("Unregistered " + this.getClass().getName());
    }

    @Override
    void submitCommand(ModbusDevice modbusDevice) {
        ReadCoilsStatusCommand command = new ReadCoilsStatusCommand(modbusDevice, modbusDeviceId, modbusInputId, 1);
        Future<List<Boolean>> future = modbusDevice.submitCommand(command);
        try {
            List<Boolean> results = future.get();
            if (results != null && results.size() > 0) {
                Boolean updated = results.get(0);
                synchronized (dataLock) {
                    Map currentMap = data.getMetadata();
                    currentMap.put(BooleanData.FIELD_VALUE, updated);
                    currentMap.put(BooleanData.FIELD_TIMESTAMP, System.currentTimeMillis());
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
    public void setData(boolean b) throws DeviceException {
        ForceSingleCoilCommand command = new ForceSingleCoilCommand(modbusDevice, modbusDeviceId, modbusInputId, b);
        Future<Boolean> future = modbusDevice.submitCommand(command);
        try {
            Boolean updated = future.get();
            synchronized (dataLock) {
                Map currentMap = data.getMetadata();
                currentMap.put(BooleanData.FIELD_VALUE, updated);
                currentMap.put(BooleanData.FIELD_TIMESTAMP, System.currentTimeMillis());
            }
        } catch (ExecutionException e) {
        } catch (InterruptedException e) {
        }
    }

    @Override
    public void inverse() throws DeviceException {
        if (data.getValue()) {
            setData(false);
        } else {
            setData(true);
        }
    }

    @Override
    public void setTrue() throws DeviceException {
        setData(false);
    }

    @Override
    public void setFalse() throws DeviceException {
        setData(true);
    }

    @Override
    public PropertyMetadata getPropertyMetadata(String s) {
        return propertyMetadataMap.get(s);
    }

    @Override
    public OperationMetadata getOperationMetadata(String s) {
        return operationMetadataMap.get(s);
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
