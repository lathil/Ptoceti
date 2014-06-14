package com.ptoceti.osgi.dfrobot.sensornode.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SensorNodeDriverUtils {

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
	
	
	public static final byte calculateLRC(byte[] buff, int count) {
	
		int lrc = 0x00;
		
		for( int i = 0; i < buff.length && i < count; i++ ) {
			lrc += buff[i];
		}
		
		return (byte)lrc;
	}
	
	public static final int calculateCRC( byte[] buff, int count) {
	
		int crc = 0xFFFF;
		int shifts = 0;
		int flag = 0x0000;
		
		for( int i = 0; i < buff.length && i < count ; i++) {
			// exclusive OR the first 8 bit byte of the message with the low order byte of the 16 bit crc register,
			// putting the result in the crc register.
			crc = ( crc & 0xFF00 ) | (( crc ^ buff[i] ) & 0x00FF );
			do {
				// shift the crc register one bit to the right, zero-filling the msb. If the LSB was zero,
				// repeat another shift. If the LSB was 1, exclusive XOR the CRC with the value 0xA001;
				flag = crc & 0x0001;
				crc = crc >>> 1;
				if( flag == 0x0001 ) crc = crc ^ 0xA001;
				shifts++;
			// repeat till 8 shifts have been performed.
			} while ( shifts < 8 );
		}
		// result is in crc. must swap low and high bytes.
		return (( 0x00FF & crc) << 8 ) | (( 0xFF00 & crc ) >>> 8 );
	}
}
