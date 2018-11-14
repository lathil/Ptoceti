package com.ptoceti.osgi.ebus.impl.message;

import com.ptoceti.osgi.ebus.impl.exception.BadCrcException;
import com.ptoceti.osgi.ebus.impl.utils.EbusUtils;

public class EbusResponse {

    protected byte[] data;

    private EbusResponse(byte[] dataIn){
        data = dataIn;
    }

    public static EbusResponse fromBytes(byte[] in, int start, int length, byte crcIn) throws  BadCrcException{
        byte[] tempData = new byte[length];
        for( int i = 0; i< length; i++){
            tempData[i] = in[start + 1];
        }
        byte crc = EbusUtils.calculateCRC(tempData);
        if( crc != crcIn){
            throw new BadCrcException( "Bad crc: calculated: " + EbusUtils.writeHex(crc) + " , in: " + EbusUtils.writeHex(crcIn));
        }

        return new EbusResponse( tempData);
    }
}
