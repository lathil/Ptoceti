package com.ptoceti.osgi.ebus.impl;

import com.ptoceti.osgi.ebus.EbusDriver;
import com.ptoceti.osgi.ebus.impl.connection.EbusSerialConnection;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.Future;

public class EbusDriverImpl implements EbusDriver {

    ServiceRegistration sReg;
    byte id;
    String portName;

    EbusSerialConnection ebusConnection;

    EbusDriverImpl(int id, String portName) throws Exception{

        this.setID((byte) id);
        this.portName = portName;
    }

    public void start() {

        String[] clazzes = new String[] {
                EbusDriver.class.getName()
        };

        Dictionary props = new Hashtable();
        props.put( org.osgi.framework.Constants.SERVICE_PID, EbusDriver.class.getName());
        props.put( org.osgi.framework.Constants.SERVICE_DESCRIPTION, "Ebus service implements a Device interface.");
        props.put( EbusDriver.EBUS_PORT, ebusConnection.getPortName());

        sReg = Activator.bc.registerService( clazzes, this, props );

        ebusConnection = new EbusSerialConnection(portName);

        Activator.log(LogService.LOG_INFO, "Registered " + this.getClass().getName());

    }

    public void stop() {

        if( ebusConnection != null ) ebusConnection.close();
        sReg.unregister();

        Activator.log(LogService.LOG_INFO, "Unregistered " + this.getClass().getName());
    }

    /**
     * Set the modbus identificator for this lodbus driver. The identificator is used when communicating
     * on the bus.
     *
     * @param id: the ebus identificator
     */
    public void setID(byte id ) {
        this.id = id;
    }

    /**
     * Return this ebus driver's identificator
     *
     * @return the ebus identificator.
     */
    public byte getID() {
        return this.id;
    }

    Future sendMasterMasterMessage(int destAddress, int primaryCommand, int secondaryCommand, byte[] payload){

    }

    Future<byte[]> sendMasterSlaveMessage(int destAddress, int primaryCommand, int secondaryCommand, byte[] payload ){

    }

    Future sendBraodcastMessage(int destAddress, int primaryCommand, int secondaryCommand, byte[] payload){

    }

}
