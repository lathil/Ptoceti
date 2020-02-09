package com.ptoceti.osgi.modbusdevice.impl.command;

import com.ptoceti.osgi.modbusdevice.ModbusCommand;
import com.ptoceti.osgi.modbusdevice.ModbusDevice;

public class ForceSingleRegisterCommand extends ModbusCommand {
    public ForceSingleRegisterCommand(ModbusDevice modbusDevice) {
        super(modbusDevice);
    }
}
