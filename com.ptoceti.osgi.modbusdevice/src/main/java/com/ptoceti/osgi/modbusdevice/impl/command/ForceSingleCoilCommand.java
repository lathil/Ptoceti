package com.ptoceti.osgi.modbusdevice.impl.command;

import com.ptoceti.osgi.modbus.ModbusDriver;
import com.ptoceti.osgi.modbusdevice.ModbusCommand;
import com.ptoceti.osgi.modbusdevice.ModbusDevice;

public class ForceSingleCoilCommand extends ModbusCommand {

    byte unitId;
    int coilId;
    boolean value;

    public ForceSingleCoilCommand(ModbusDevice modbusDevice, byte unitId, int coilId, boolean value) {
        super(modbusDevice);
        this.unitId = unitId;
        this.coilId = coilId;
        this.value = value;
    }

    @Override
    public Boolean call() throws Exception {
        ModbusDriver driver = getModbusDevice().getModbusDriver();
        boolean result = false;
        if (driver != null) {
            result = driver.forceSingleCoil(unitId, coilId, value);
        }

        return Boolean.valueOf(result);
    }
}
