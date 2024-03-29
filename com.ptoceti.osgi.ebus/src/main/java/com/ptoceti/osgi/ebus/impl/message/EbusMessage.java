package com.ptoceti.osgi.ebus.impl.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class EbusMessage {

    public static int SYNC = 0xAA;
    public static int SYNC_ESCAPE = 0xA9;
    public static int ACK = 0x00;
    public static int NACK = 0xFF;

    public static int BROADCAST_ADD = 0xFE;

    private byte destId;
    private byte command;
    private byte subCommmand;
    private byte[] data;

    public EbusMessage(byte destId, byte command, byte subCommand, byte[] data){
        this.destId = destId;
        this.command = command;
        this.subCommmand = subCommand;
        this.data = data;
    }

    public byte getDestId() {
        return destId;
    }

    public byte getCommand() {
        return command;
    }

    public byte getSubCommmand() {
        return subCommmand;
    }

    public byte[] getData() {
        return data;
    }

    /**
     * test if message is type broadcast. Destination address is broadcast adress
     *
     * @return boolean tru if message is a broadcast message
     */
    public boolean isBroadcastMessage(){
        return getDestId() == (byte) ( BROADCAST_ADD & 0x00FF);
    }

    /**
     * test if message is for a master address
     * @return boolean true if message is master to master message
     */
    public boolean isMasterToMasterMessage(){
        int subAddress = (getDestId() & 0xF0 );
        int priority = (getDestId() & 0x0F);

        if( ((subAddress == 0x00) || (subAddress == 0x10) || (subAddress == 0x30) || (subAddress == 0x70) || (subAddress == 0xF0)) &&
                ((subAddress == 0x00) || (subAddress == 0x01) || (subAddress == 0x03) || (subAddress == 0x07) || (subAddress == 0x0F))) {
            return true;
        }
        return false;
    }

    public byte[] toBytes(){
        ByteArrayOutputStream byteBuff = new ByteArrayOutputStream();

        try {
            byteBuff.write(getDestId());
            byteBuff.write(getCommand());
            byteBuff.write(getSubCommmand());
            byteBuff.write((byte) getData().length);
            byteBuff.write(getData());

        } catch (IOException ex){

        }

        return byteBuff.toByteArray();
    }

}
