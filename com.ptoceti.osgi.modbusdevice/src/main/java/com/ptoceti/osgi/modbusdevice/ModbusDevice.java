package com.ptoceti.osgi.modbusdevice;

import com.ptoceti.osgi.modbus.ModbusDriver;

import java.util.concurrent.Future;

public interface ModbusDevice {
    Object getServiceProperty(String serviceStatus);

    public String[] getServicePropertyKeys();

    public Future submitCommand(ModbusCommand command);

    public ModbusDriver getModbusDriver();

}
