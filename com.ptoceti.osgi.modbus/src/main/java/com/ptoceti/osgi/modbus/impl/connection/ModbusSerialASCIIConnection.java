

package com.ptoceti.osgi.modbus.impl.connection;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Modbus
 * FILENAME : ModbusSerialASCIIConnection.java
 * 
 * This file is part of the Ptoceti project. More information about
 * this project can be found here: http://www.ptoceti.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2013 - 2014 ptoceti
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.ptoceti.osgi.modbus.ModbusDriver;
import com.ptoceti.osgi.modbus.impl.Activator;
import com.ptoceti.osgi.modbus.impl.message.ModbusMessageResponse;
import com.ptoceti.osgi.modbus.impl.message.ModbusMessageRequest;
import com.ptoceti.osgi.modbus.impl.utils.ModbusUtils;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import org.osgi.service.log.LogService;

import gnu.io.CommPortIdentifier;
import gnu.io.RXTXCommDriver;
import gnu.io.RXTXVersion;
import gnu.io.SerialPortEventListener;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPort;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

/**
 * Handle a connection with framing ASCII framing style. In this style, every byte is sent as 2 hex digits,
 * and there iss defined chars for start and end of frame.
 *
 * @author Laurent Thil
 * @version 1.0b
 */
 
public class ModbusSerialASCIIConnection extends ModbusSerialConnection implements SerialPortEventListener {

	static final byte FRAME_START = 0x3A; // :
	static final byte FRAME_END_1 = 0x0D; //CR
	static final byte FRAME_END_2 = 0x0A; //LF
	
	// this buffer is used to recived the hex chqrs from the serial link
	ByteArrayOutputStream bytesInHex = new ByteArrayOutputStream();
	// this buffer contains the last message received from the serial link, converted back into binary.
	ByteArrayOutputStream bytesIn = new ByteArrayOutputStream();
	// this buffer contains the next message in hex to send over the link.
	ByteArrayOutputStream bytesOutHex = new ByteArrayOutputStream();
	// this buffer contains the raw bytes of the next message to send.
	ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
	
	// flag to indicate that a new frame has arrived.
	boolean hasNewFrame = false;
	// flag to indicate that a special "start of frame" char has been received.
	boolean foundFrameStart = false;
	// flag to indicate that a special "end of frame" char has been received.
	boolean foundFrameEnd1 = false;
	
	/**
	 * Constructor. Try to create and open the serial connection to modbus from the given arguments.
	 * The serial port is amso configured to use 7 bits data, default for Ascii framing. If the connection
	 * is open successfully, the class registers itself as an listener to even from the serial port and
	 * get hold of the input and pitput streams.
	 *
	 *
	 * @param porName: name of the serial port to use to access the serial port, eg "/dev/tty01"
	 * @param baudrate: baud rate to used on the serial link: 9600 to 19200.
	 * @param usesParity: state whether to send the parity bit on the serial link.
	 * @param evenParity: state whether to use even or odd parity.
	 * @param echo: state whether to expect receivng the echo of a broadcasted message back on the receiver channel.
	 *
	 * @exception Exception is thrown if probmems creating the serial connection.
	 */
	 
	public ModbusSerialASCIIConnection( String portName, int baudRate, boolean usesParity, boolean evenParity, boolean echo ) throws Exception {
		
		Enumeration portList;
		CommPortIdentifier portID;
		boolean hasFoundPort = false;
		
		this.portName = portName;
		
		try {
				
				portID = CommPortIdentifier.getPortIdentifier(portName);
				if( portID.getPortType() == CommPortIdentifier.PORT_SERIAL) {
					// if this is the port we want,
					if( portID.getName().equals( portName )) { 
						// try to get hold of this port. If not successfull within 1 second, throws an exception.
						serialPort = (SerialPort) portID.open( this.getClass().getName(), (int)1000 );
						// if the parity bit is not used, the  port is configured to send two stop bits.
						if( usesParity == true )
							serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_7,
								SerialPort.STOPBITS_1, ( evenParity ? SerialPort.PARITY_EVEN : SerialPort.PARITY_ODD ));
						else
							serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_7,
								SerialPort.STOPBITS_2, SerialPort.PARITY_NONE);
						// set itself to listen to data available events.
						serialPort.addEventListener(this);
						serialPort.notifyOnDataAvailable(true);
						// get hold of the inputs and outputs streams.
						inStream = serialPort.getInputStream();
						outStream = serialPort.getOutputStream();
						// success ! we got our port !
						hasFoundPort = true;
						// remember whether to expect the echo of broadcasted frames.
						usesEcho = echo;
					}
				}
			// We did not found the port in the list of ports availables. throw an exception.
			if( hasFoundPort == false ) throw new Exception("Could not find port with name: " + portName + ".");
			
		} catch ( PortInUseException e) { throw new Exception("Port " + portName + " is already in use."); }
			catch (UnsupportedCommOperationException e) { throw new Exception("Port does not support this operation."); }
			catch (TooManyListenersException e) { throw new Exception("Could not create listener on port: " + portName + "."); }
			catch (IOException e) { throw new Exception("Could not open input or output streams on port: " + portName + "."); }
		
	}
	
	public void close() {
	
		serialPort.close();
		inStream = null;
		outStream = null;
	}
	
	/**
	 * This method send a ModbusMessageRequest to a slave device over the modbus serial bu.
	 * The method handle the conversion of the bytes to two hex chars, and the framing with
	 * start and end chars.
	 *
	 *
	 * @param msg :  the message to send to a slave device over the bus.
	 * @return the response message. Null if no reply is sent over.
	 */
	public synchronized ModbusMessageResponse sendMessage( ModbusMessageRequest msg ) {
		
		ModbusMessageResponse response = null;
		
		try {
			bytesOut.reset();
			bytesOutHex.reset();
			msg.writeTo( bytesOut); // write unitID, functionID + message data
			ModbusUtils.writeHex(bytesOut.toByteArray(), 0, bytesOut.size(), bytesOutHex); // convert the message to hexadecimal.
			// now; calculate the lrc on the asciii message, and add the result byte as two asci hex chars.
			byte lrc  = ModbusUtils.calculateLRC(bytesOutHex.toByteArray(), bytesOutHex.size());
			ModbusUtils.writeHex(lrc, bytesOutHex);
			
			Activator.log(LogService.LOG_DEBUG, "Sending frame: " + bytesOutHex.toString());

			outStream.write( FRAME_START); // write start of frame byte in hex
			outStream.write( bytesOutHex.toByteArray());
			outStream.write( FRAME_END_1 ); // add CR
			outStream.write( FRAME_END_2 ); // add LF
			outStream.flush(); // be sure everything in the buffer is send.
			// update statistics counter
			incrementFrameSentCounter();
			// wait for incoming response.
			try {
				if( usesEcho ) {
					if ( hasNewFrame == false ) wait( 10 );
					if ( hasNewFrame == true ) {
						Activator.log(LogService.LOG_DEBUG, "Received echo frame:" + ModbusUtils.writeHex( bytesIn.toByteArray()));
					}
					else { 
						Activator.log(LogService.LOG_DEBUG, "Missing echo frame.");
						return response;
					}
				}
				if ( hasNewFrame == false ) wait( 1000 );
				if ( hasNewFrame == true ) {
					// and ask the embedded response message to parse the raw response to a ModbusMessageResponse.
					ModbusMessageResponse respMessage = msg.getResponseMessage();
					// if parsing was successfull, we can return the response message.
					if( respMessage.readFrom( new ByteArrayInputStream(bytesIn.toByteArray())) == true ) response = respMessage;
				}
			} catch (InterruptedException e) {}
			
		} catch (IOException e) {
			// if we could not send the message, update the appropriate statistic counter.
			incrementBadFramesSentCounter();
		}
		
		return response;
	}
	
	public String getEncodingType(){
		return ModbusDriver.ASCII_ENCODING;
	}
	
	/**
	 * This	method wait till the FrameDetector has recognised a frame on the incoming channel.
	 * This method should be used by the ModbusSlave object. Upon receiving the frame, a new
	 * ModbusRequestMessage is build around it and returned. The method block till a incoming
	 * frame is received.
	 *
	 * @return the request message. 
	 */
	public synchronized void waitForRequest(){
	
		try {
			if( hasNewFrame == false ) wait();
		
			try {
				Activator.log(LogService.LOG_DEBUG, "Received requestframe: " + ModbusUtils.writeHex( bytesIn.toByteArray()));
			} catch (IOException e ) {}
		
		} catch (InterruptedException e ) {}
		
		hasNewFrame = false;

	}
	
	/**
	 * Convert the frame in hex contained in BytesInHex to a binary frame and
	 * saves it in bytesIn. Also check the LRC validity.
	 * Also notify any waiting thread on this object's lock that we have received a frame.
	 *
	 */
	private synchronized void checkNewIncomingMessage() {
	
		byte[] buff = bytesInHex.toByteArray();
		// recalculate the LRC on the whole received hex message - less the last 2 bytes that are the sent LRC
		byte calculatedLrc = ModbusUtils.calculateLRC( buff, buff.length - 2 );
		// extract the LRC sent over with the frame. It is ent over as two hex char. So we need to convert it back to a single byte
		int hexWord = ( buff[buff.length - 2] << 8 ) + buff[buff.length - 1];
		byte receivedLrc = ModbusUtils.hexWordToByte( hexWord );
		
		// if the received lrc equals the newly calculated, we consider that the frame is intact.
		if( calculatedLrc == receivedLrc ) {
			try {
				// reset the bytesIn buffer. this in fact clears it of any previous content.
				bytesIn.reset();
				// convert the hex char to plain bytes. Stores result in bytesIn
				ModbusUtils.readHex(buff, 0, buff.length - 2, bytesIn );
				// flag that in.
				hasNewFrame = true;
				// update the statistic counter
				incrementFramesReceivedCounter();
				// and notify any waiting thread that we got a new frame
				notify();
			} catch (IOException e ) {}
		}
		else {
			hasNewFrame = false;
			// update the statistic counter.
			incrementBadFramesReceivedCounter();
			Activator.log(LogService.LOG_DEBUG, "Bad LRC on received frame: " + bytesInHex.toString());
		}
	}
	
	/**
	 * Serial port's SerialPortEventistener. When a event is produced by the SerialPort class,
	 * is is sent to this method. This mechanism is used to listen to incoming bytes on the serial bus.
	 * Once the start and end special chars are received, the incoming bytes are packed as a proper frame
	 * and passed on to be converted back to binary ( from the hex bytes ).
	 *
	 * @param event : the SerialPortEvent event.
	 */
	public void serialEvent( SerialPortEvent event ) {
	
		int inByte = 0;
		// at present we are only listening to DATA_AVAILABLE events, which that new incoming bytes
		// have received on the serial port.
		if( event.getEventType() == SerialPortEvent.DATA_AVAILABLE ) {
			try {
			while (inStream.available() > 0 ) {
					// we are working on every received byte ( there may be more than one byte on the input
					// stream ). We also do no want to block the current thread by attempting a read() with
					// no available bytes.
					
					inByte = inStream.read();
					// if we receive a start of frame byte, flag it and reset the hex buffer.
					if( inByte == FRAME_START ) {
						foundFrameStart = true;
						bytesInHex.reset();
					}
					// if we received the first end frame byte (there is two in succession), then ..
					else if( inByte == FRAME_END_1 ) {
						// if we already found the frame start byte, the frame is complete, so flag it.
						if( foundFrameStart == true ) {
							foundFrameEnd1 = true;
						}
						else {
						// we did not received the frame start byte, so the frame is incomplete, reset everything.
							foundFrameStart = false;
							bytesInHex.reset();
						}
					}
					// if we eceived the second end of frame byte, ..
					else if( inByte == FRAME_END_2 ) {
						// and if we received the start and first end of frame chars, the frame is complete.
						if(( foundFrameEnd1 == true ) && ( foundFrameStart == true )){
							// we can convert the whole frame from hex to binary and save it.
							checkNewIncomingMessage();
						}
						foundFrameStart = false;
						foundFrameEnd1 = false;
					}
					// this is just a norma byte, so if we received the start of frame, but not yet the end
					// of frame char, we just record it.
					else if(( foundFrameStart == true ) && ( foundFrameEnd1 == false )) {
							// write the hex byte into the hex buffer.
							bytesInHex.write(inByte);
						}
					// we gave not received the start of frame, then we just discard the byte and reset everything.
					else {
							foundFrameStart = false;
							foundFrameEnd1 = false;
							bytesInHex.reset();
					}
						
				}
				
			} catch (IOException e ) {}
		}
		
	}
	
	
}
