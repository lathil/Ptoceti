package com.ptoceti.osgi.serialdevice.nrjavaserial.impl;

import org.osgi.service.serial.SerialEvent;

public class SerialEventImpl implements SerialEvent {

    protected int type;
    protected String comPort;

    protected SerialEventImpl(int type, String comPort) {
        this.type = type;
        this.comPort = comPort;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public String getComPort() {
        return comPort;
    }
}
