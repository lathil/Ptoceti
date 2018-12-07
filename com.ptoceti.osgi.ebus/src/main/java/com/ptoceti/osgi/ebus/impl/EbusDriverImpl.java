package com.ptoceti.osgi.ebus.impl;

import com.ptoceti.osgi.ebus.EbusDriver;
import com.ptoceti.osgi.ebus.impl.connection.EbusResponseListener;
import com.ptoceti.osgi.ebus.impl.connection.EbusSerialConnection;
import com.ptoceti.osgi.ebus.impl.message.EbusMessage;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.Future;

public class EbusDriverImpl implements EbusDriver {

    ServiceRegistration sReg;
    byte id;
    String portName;
    int lockCounter;

    EbusSerialConnection ebusConnection;

    EbusDriverImpl(int id, String portName, int lockCounter) {

        this.id = ((byte) id);
        this.portName = portName;
        this.lockCounter = lockCounter;
    }

    public void start() throws Exception{

        String[] clazzes = new String[] {
                EbusDriver.class.getName()
        };

        Dictionary props = new Hashtable();
        props.put( org.osgi.framework.Constants.SERVICE_PID, EbusDriver.class.getName());
        props.put( org.osgi.framework.Constants.SERVICE_DESCRIPTION, "Ebus service implements a Device interface.");
        props.put( EbusDriver.EBUS_PORT, ebusConnection.getPortName());

        ebusConnection = new EbusSerialConnection(portName);
        // if serial connection start ok ..
        ebusConnection.start(getID(), lockCounter);
        sReg = Activator.bc.registerService( clazzes, this, props );
        Activator.log(LogService.LOG_INFO, "Registered " + this.getClass().getName());

    }

    public void stop() {

        if( ebusConnection != null ) ebusConnection.close();
        if( sReg != null ) sReg.unregister();

        Activator.log(LogService.LOG_INFO, "Unregistered " + this.getClass().getName());
    }

    /**
     * Return this ebus driver's identificator
     *
     * @return the ebus identificator.
     */
    public byte getID() {
        return this.id;
    }

    public Future sendMasterMasterMessage(int destAddress, int primaryCommand, int secondaryCommand, byte[] payload){
        EbusMessage message = new EbusMessage( (byte)( destAddress & 0x00FF), (byte)( primaryCommand & 0x00FF), (byte)( secondaryCommand & 0x00FF), payload);
        EbusResponseListener listener = ebusConnection.addMessageToSend(message);
    }

    public Future<byte[]> sendMasterSlaveMessage(int destAddress, int primaryCommand, int secondaryCommand, byte[] payload ){
        EbusMessage message = new EbusMessage( (byte)( destAddress & 0x00FF), (byte)( primaryCommand & 0x00FF), (byte)( secondaryCommand & 0x00FF), payload);
        EbusResponseListener listener = ebusConnection.addMessageToSend(message);
    }

    public Future sendBroadcastMessage( int primaryCommand, int secondaryCommand, byte[] payload){
        EbusMessage message = new EbusMessage( (byte)( EbusMessage.BROADCAST_ADD), (byte)( primaryCommand & 0x00FF), (byte)( secondaryCommand & 0x00FF), payload);
        EbusResponseListener listener = ebusConnection.addMessageToSend(message);
    }

}
