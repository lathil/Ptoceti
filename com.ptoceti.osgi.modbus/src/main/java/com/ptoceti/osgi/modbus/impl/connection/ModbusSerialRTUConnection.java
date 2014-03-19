
package com.ptoceti.osgi.modbus.impl.connection;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Modbus
 * FILENAME : ModbusSerialRTUConnection.java
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

import gnu.io.*;

public class ModbusSerialRTUConnection extends ModbusSerialConnection implements SerialPortEventListener {

	// flag to indicate that a new frame has arrived.
	boolean hasNewFrame = false;
	// flag to indicate that a special "start of frame" char has been received.
	boolean foundFrameStart = false;
	
	// create a new frame detector. usefull to detec timeout between frames when waiting for a reply.
	FrameDetector frameDetector = null;
	
	// buffer byte array to receive bytes comming from the serial link input stream.
	ByteArrayOutputStream bytesInRaw = new ByteArrayOutputStream();
	// buffer byte array to get new incoming frame without the two CRC bytes.
	ByteArrayOutputStream bytesIn = new ByteArrayOutputStream();
	// buffer byte array to store bytes to write to the serial link output stream.
	ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
	
	// time delimiting two frames in chars time in the modbus protocol.
	public static double FRAME_SEPERATOR_LENGTH = 3.5;
	
	/**
	 * Constructor. Try to create and open the serial connection to modbus from the given arguments.
	 * The serial port is amso configured to use 8 bits data, default for RTU framing. If the connection
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
	public ModbusSerialRTUConnection( String portName, int baudRate, boolean usesParity, boolean evenParity, boolean echo ) throws Exception {
	
		Enumeration portList;
		CommPortIdentifier portID;
		boolean hasFoundPort = false;
		
		this.portName = portName;
		
		try {
			portList = CommPortIdentifier.getPortIdentifiers();
			while (portList.hasMoreElements()) {
				portID = (CommPortIdentifier) portList.nextElement();
				if( portID.getPortType() == CommPortIdentifier.PORT_SERIAL) {
					// if this is the port we want,
					if( portID.getName().equals( portName )) {
						// try to get hold of this port. If not successfull within 1 second, throws an exception.
						serialPort = (SerialPort) portID.open( this.getClass().getName(), (int)1000 );
						// if the parity bit is not used, the  port is configured to send two stop bits.
						if( usesParity == true )
							serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8,
								SerialPort.STOPBITS_1, ( evenParity ? SerialPort.PARITY_EVEN : SerialPort.PARITY_ODD ));
						else
							serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8,
								SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
						// set itself to listen to data available events.
						serialPort.addEventListener(this);
						serialPort.notifyOnDataAvailable(true);
						// calculate the time between two frames depending on the baudrate setting.
						frameDetector = new FrameDetector((int) Math.ceil( FRAME_SEPERATOR_LENGTH * 10 * 1000 / serialPort.getBaudRate()));
						// get hold of the inputs and outputs streams.
						inStream = serialPort.getInputStream();
						outStream = serialPort.getOutputStream();
						// success ! we got our port !
						hasFoundPort = true;
						// remember whether to expect the echo of broadcasted frames.
						usesEcho = echo;
						break;
					}
				}
			}
			
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
	 * The message is sent with RTU encoding and framing style, meaning that bytes are sent
	 * raw, an CRC is joined, there is no start and end of frame chars, and the end of frame
	 * is caracterized by a silence on the serial link.
	 *
	 *
	 * @param msg :  the message to send to a slave device over the bus.
	 * @return the response message. Null if no reply is sent over.
	 */
	public synchronized ModbusMessageResponse sendMessage( ModbusMessageRequest msg ){
	
		ModbusMessageResponse response = null;
	
		try {
			bytesOut.reset();
			msg.writeTo( bytesOut); // write unitID, functionID + message data
			int crc = ModbusUtils.calculateCRC( bytesOut.toByteArray(), bytesOut.size()); // calculate CRC, and add at end of message.
			bytesOut.write(( crc & 0xFF00 ) >>> 8 ); // send hight byte of the crc
			bytesOut.write(( crc & 0x00FF )); // send low byte of the crc		
			
			Activator.log(LogService.LOG_DEBUG, "Sending frame: " + ModbusUtils.writeHex( bytesOut.toByteArray()));
			
			bytesOut.writeTo( outStream );
			outStream.flush();
			// update statistics counter
			incrementFrameSentCounter();
			try {
				if( usesEcho ) {
					if ( hasNewFrame == false ) wait( 100 );
					if ( hasNewFrame == true ) {
						Activator.log(LogService.LOG_DEBUG, "Received echo frame: " + ModbusUtils.writeHex( bytesIn.toByteArray()));
						hasNewFrame = false;
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
					hasNewFrame = false;
				} else {
					Activator.log(LogService.LOG_DEBUG, "Missing response frame.");
				}
				
			} catch (InterruptedException e) {}

		} catch ( IOException e) {
			// if we could not send the message, update the appropriate statistic counter.
			incrementBadFramesSentCounter();
		}
		
		return response;
	}
	

	public String getEncodingType(){
		return ModbusDriver.RTU_ENCODING;
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
				Activator.log(LogService.LOG_DEBUG, "Received request frame: " + ModbusUtils.writeHex( bytesIn.toByteArray()));
			} catch (IOException e ) {}
		
		} catch (InterruptedException e ) {}
		
		hasNewFrame = false;

	}

	/**
	 * Check the new incoming frame validity that we have that just received. This requires
	 * recalculating the CRC and checking it with the one in the frame.
	 * Also notify any waiting thread on this object's lock that we have received a frame.
	 *
	 */
	private synchronized void checkNewIncomingMessage() {
	
		// stored the raw incoming frame in a tempory buffer.
		byte[] buff = bytesInRaw.toByteArray();
		// reset the bytesInRaw buffer. this in fact clears it of any previous content.
		bytesInRaw.reset();
		// reset the internal char buffer.
		bytesIn.reset();
		// recalculate the CRC on the whole received hex message - less the last 2 bytes that are the sent LRC
		int calculatedCRC = ModbusUtils.calculateCRC( buff, buff.length - 2 );
		// extract the CRC sent over with the frame. It is sent over as bytes
		int receivedCRC = ( buff[buff.length - 2] << 8 ) + ((buff[buff.length - 1]) & 0x00FF);
		
		// if the received crc equals the newly calculated, we consider that the frame is intact.
		if( calculatedCRC == receivedCRC ) {
			// extract the frame without the two crc chars in the byte array buffer
			bytesIn.write( buff, 0, buff.length - 2);
			try {
				bytesIn.flush();
			} catch (IOException e ) {}
			// flag that in.
			hasNewFrame = true;
			// update the statistic counter
			incrementFramesReceivedCounter();
			// and notify any waiting thread that we got a new frame
			notify();
		}
		else {
			hasNewFrame = false;
			// update the statistic counter.
			incrementBadFramesReceivedCounter();
			String messageString = new String();
			try {
				messageString = ModbusUtils.writeHex( buff );
			} catch (IOException e ) { messageString = "Error converting binary message to Hex."; }
			Activator.log(LogService.LOG_DEBUG, "Bad CRC on received frame: " + messageString );
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
	
		int nbAvailableBytes = 0;
		byte[] newBytes = new byte[255];
		
		if( event.getEventType() == SerialPortEvent.DATA_AVAILABLE ) {
			try {
				nbAvailableBytes = inStream.available();
				inStream.read( newBytes, 0, nbAvailableBytes);
				bytesInRaw.write( newBytes, 0, nbAvailableBytes);
			} catch (IOException e ) {}
			frameDetector.gotNewBytes();
		}
	}
	
	/**
	 * This class act as a synchronisation object around a monitor ( the object's monitor ).
	 * It allows detecting whether a new frame has been received by implementing a timeout. This
	 * class create a thread that is waiting for new coming bytes. If, after it got notification that
	 * that some bytes have arrived, it does not receive anything else for a certain time, it considers
	 * this was a frame.
	 * The outer class (ModbusSerialRTUConnection) method serialEvent notify this class through
	 * its gotNewBytes() method that it got a new stream of bytes coming from the input stream.
	 * The class notify the outer class that new bytes have arrived
	 * 
	 */
	public class FrameDetector implements Runnable {
	
		// time delimiting two frames in milliseconds;
		int frameBreakTime = 1;
		// the time we received the latest bytes
		long newBytesReceivedTime;
		// the state machine node
		int state;
		// the 3 possible states the state machine can have
		public static final int HASNOTDETECTEDFRAME = 0;
		public static final int HASDETECTEDFRAMESTART = 1;
		public static final int HASDETECTEDFRAMEEND = 2;
		// the thread that run this
		Thread myThread;
		
		public FrameDetector(int timeout ) {
			// note the timeout value to detect a silence minimum time between two frames.
			frameBreakTime = timeout;
			// set the state to not yet received a frame
			state = HASNOTDETECTEDFRAME;
			newBytesReceivedTime = System.currentTimeMillis();
			myThread = new Thread(this);
			myThread.start();
		}
		
		/**
		 * Notify the object that new bytes have arrived. Update the state machine and release
		 * the threads waiting on the object lock.
		 */
		public synchronized void gotNewBytes() {
		
			// the input stream has received new bytes. We need to note when theses bytes were received.
			newBytesReceivedTime = System.currentTimeMillis();
			// if we have not yet detected a frame new beginning, note this fact.
			if(( state == HASNOTDETECTEDFRAME ) || ( state == HASDETECTEDFRAMEEND)) {
				state = HASDETECTEDFRAMESTART;
			}
			// notify to other thread waiting for notice that new bytes have arrived in
			notify();
			
		}
		
		/**
		 * Method executed by the class's thread. Wait for the object monitor to be released, meaning
		 * that we got notified that some bytes have been received. If the timeout is reached, that mean
		 * that we have completed receiving a new frame. In this case, we notify the outer class.
		 *
		 */
		private synchronized void checkForFrame() {
			try {
				// if we have not yet detected a start of frame, or we just saw a end of frame
				if(( state == HASNOTDETECTEDFRAME ) || ( state == HASDETECTEDFRAMEEND))
					// .. then we just wait indefinitively for the start of the new one
					wait();
				// if we already detected a new start of frame
				else if( state == HASDETECTEDFRAMESTART ) {
					// wait a for a maximum time the next bytes of this frame
					wait(frameBreakTime);
					// check if we have waited for the timeout ..
					long thisTimeNow = System.currentTimeMillis();
					if(( thisTimeNow - newBytesReceivedTime ) >= frameBreakTime ) {
						// ... if yes this means that we have detected the end of frame.
						state = HASDETECTEDFRAMEEND;
						// finally check that the frame is complete.
						checkNewIncomingMessage();
					}
				}
			} catch (InterruptedException e ) {}
		}
		
		/**
		 * Method implementing the Runnable interface. Continuously invoque the checkForFrame private methode.
		 * 
		 */
		public void run() {
			while(true) {
				// this object's monitors can only be blocked from inside a synchronized method. Call this one.
				checkForFrame();
			}
		}
	}
	
}
