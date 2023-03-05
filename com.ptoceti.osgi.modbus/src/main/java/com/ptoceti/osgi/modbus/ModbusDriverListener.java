package com.ptoceti.osgi.modbus;

public interface ModbusDriverListener {

    public void modbusDriverConnected();

    public void modbusDriverDisconnected();
}
