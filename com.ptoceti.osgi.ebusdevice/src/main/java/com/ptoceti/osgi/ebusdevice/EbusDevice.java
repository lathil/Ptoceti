package com.ptoceti.osgi.ebusdevice;

import org.osgi.service.wireadmin.Consumer;
import org.osgi.service.wireadmin.Producer;

public interface EbusDevice extends Producer, Consumer {
    /**
     * Return the id of the device in the Modbus bus link
     * @return the id of this device
     */
    public int getId();

    /**
     * Return the serial link port used by the device.
     * @return String the name of the port for this device
     */
    public String getPortName();

    public void stop();
}
