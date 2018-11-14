package com.ptoceti.osgi.ebus.impl.connection;

public interface EbusEventListener {

    EbusConnectionState onReadyToSend (EbusSerialConnection connection);

    EbusConnectionState onSyncReceived(EbusSerialConnection connection);

    EbusConnectionState onAckReceived(EbusSerialConnection connection);

    EbusConnectionState onNAckReceived(EbusSerialConnection connection);

    EbusConnectionState onByteReceived(EbusSerialConnection connection, byte receivedByte);

}
