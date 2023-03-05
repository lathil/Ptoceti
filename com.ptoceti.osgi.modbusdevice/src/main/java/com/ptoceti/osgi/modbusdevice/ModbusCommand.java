package com.ptoceti.osgi.modbusdevice;

import java.util.concurrent.Callable;

public class ModbusCommand<T> implements Callable<T> {
    private int priority = 0;
    private ModbusDevice modbusdevice;

    public ModbusCommand(ModbusDevice modbusDevice) {
        this.modbusdevice = modbusDevice;
    }

    @Override
    public T call() throws Exception {
        return null;
    }


    public ModbusDevice getModbusDevice() {
        return modbusdevice;
    }

    ;

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
