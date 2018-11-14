package com.ptoceti.osgi.ebus.impl.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.ByteBuffer;

public class EbusUtils {

    static int SYNC_ESCAPE =  0xA901;
    static int A9_ESCAPE = 0xA900;


    public static final int byteToHexWord(byte in ) {

        byte[] table = { 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39,
                0x41, 0x42, 0x43, 0x44, 0x45, 0x46 };
        int result = 0x0000;

        result = ( table[( in & 0xF0) >>> 4]) << 8;
        result = ( result & 0xFF00 ) | ( table[ in & 0x0F]);

        return result;
    }

    public static final byte hexWordToByte(int in ) {

        int highByte = ((in & 0xFF00) >>> 8 );
        int lowByte = (in & 0x00FF);

        if(( highByte >= 30 ) || ( highByte <= 39 )) highByte =- 0x30;
        else if(( highByte >= 0x41 ) || ( highByte <= 0x46 )) highByte =- ( 0x41 - 0x0A );
        else highByte = 0x00;

        if(( lowByte >= 30 ) || ( lowByte <= 39 )) lowByte =- 0x30;
        else if(( lowByte >= 0x41 ) || ( lowByte <= 0x46 )) lowByte =- ( 0x41 - 0x0A );
        else lowByte = 0x00;

        return (byte) (( highByte * 0x10 ) + lowByte );
    }


    public static final String writeHex(byte[] buff) throws IOException {

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        writeHex( buff, 0, buff.length, bout );
        return bout.toString();
    }

    public static final void writeHex(byte[] buff, int off, int len,  OutputStream out) throws IOException {

        for(int i = off; i < ( off + len ); i++ ){
            writeHex( buff[i], out );
        }

    }

    public static final void writeHex(byte theByte, OutputStream out ) throws IOException {

        int theWord = byteToHexWord( theByte );
        out.write((theWord & 0xFF00) >>> 8 );
        out.write(theWord & 0x00FF);
    }

    public static final String writeHex(byte in){
        int theWord = byteToHexWord( in );
        StringWriter writer = new StringWriter();
        writer.write((theWord & 0xFF00) >>> 8);
        writer.write(theWord & 0x00FF);
        return writer.toString();
    }

    public static final void readHex(byte[] buff, int off, int len, OutputStream out ) throws IOException {

        for(int i = off; i < (off + len); i = i + 2 ) {

            byte highByte = buff[i];
            byte lowByte = buff[i+1];
            int theWord = (((int) highByte ) << 8 ) | ((int) lowByte );

            readHex( theWord, out );
        }
    }

    public static final void readHex(int theWord, OutputStream out) throws IOException {

        byte thebyte = hexWordToByte( theWord );
        out.write( thebyte );
    }

    public static byte[] encodeStream(byte[] in) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for( int i = 0; i < in.length; i ++) {
            if( in[i] == SYNC) {
                out.write( (byte)((SYNC_ESCAPE & 0xFF00) >>> 8));
                out.write( (byte)((SYNC_ESCAPE & 0x00FF)));
            } else if ( in[i] == ((SYNC_ESCAPE & 0xFF00) >>> 8)){
                out.write( (byte)((A9_ESCAPE & 0xFF00) >>> 8));
                out.write( (byte)((A9_ESCAPE & 0x00FF)));
            } else {
                out.write(in[i]);
            }
        }

        return out.toByteArray();
    }

    public static final byte calculateCRC(byte[] in ) {

        int poly = 0x00B9; // x8 + x7 + x4 + x3 + x + 1
        int crc =  0x0000; /* start with 0 so first byte can be 'xored' in */
        for( int i = 0; i < in.length ; i++) {
            crc = (crc ^ in[i]) & 0x00FF;
            for (int shifts = 0; shifts < 8; shifts++) {
                if(( crc & 0x0080) != 0) {
                    crc = (crc << 1) ^ poly;
                } else {
                    crc = crc << 1;
                }
            }
        }

        return (byte)(crc & 0x00FF);
    }

}
