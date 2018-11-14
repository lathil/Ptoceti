package com.ptoceti.osgi.ebus.impl.connection;

import org.omg.Messaging.SYNC_WITH_TRANSPORT;

public enum EbusConnectionState implements EbusEventListener{
    /**
     * Connection is standby, nothing to send
     */
    STANDBY {
        public EbusConnectionState onReadyToSend (EbusSerialConnection connection){
            return EbusConnectionState.READYSTOSEND;
        }
    },
    /**
     * Connection has sometinhg to send, wait for sync
     */
    READYSTOSEND {

        @Override
        public EbusConnectionState onSyncReceived(EbusSerialConnection connection) {
            // default behaviour, end of message, reset input buffer
            connection.bytesIn.reset();
            connection.notifySync();

            return this;
        }

    },
    /**
     * Connection is transmitting it message
     */
    TRANSMITING,
    /**
     * Connection is waiting for reply, ack + response if master-slave message
     */
    WAITFORREPLY,
    /**
     * Read response from slave
     */
    READRESPONSE,
    /**
     * End of message send SYNc
     */
    FINALIZING;

    @Override
    public EbusConnectionState onReadyToSend (EbusSerialConnection connection){
        return this;
    }

    @Override
    public EbusConnectionState onSyncReceived(EbusSerialConnection connection) {
        // default behaviour, end of message, reset input buffer
        connection.bytesIn.reset();
        return this;
    }

    @Override
    public EbusConnectionState onAckReceived(EbusSerialConnection connection) {
        return this;
    }

    @Override
    public EbusConnectionState onNAckReceived(EbusSerialConnection connection) {
        return this;
    }

    @Override
    public EbusConnectionState onByteReceived(EbusSerialConnection connection, byte receivedByte) {
        connection.bytesIn.put(receivedByte);
        connection.notifyByte();
        return this;
    }
}
