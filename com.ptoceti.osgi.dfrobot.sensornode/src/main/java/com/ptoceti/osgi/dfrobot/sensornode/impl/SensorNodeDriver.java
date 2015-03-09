package com.ptoceti.osgi.dfrobot.sensornode.impl;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.TooManyListenersException;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;


/**
 * Driver class to handle communication with a set of DfRobot Sensor Node from a serial port.
 * 
 * @author LATHIL
 *
 */
public class SensorNodeDriver implements SerialPortEventListener {

	/**
	 * configuration key for node id
	 */
	public static final String SENSORNODE_ID = "com.ptoceti.osgi.sensornodedriver.id";
	/**
	 * configuration key for port to be used
	 */
	public static final String SENSORNODE_PORT = "com.ptoceti.osgi.sensornodedriver.port";
	/*
	 * configuration key for port baud rate
	 */
	public static final String SENSORNODE_BAUDRATE = "com.ptoceti.osgi.sensornodedriver.baudrate";
	/**
	 * configuration key for port parity setting
	 */
	public static final String SENSORNODE_USESPARITY = "com.ptoceti.osgi.sensornodedriver.usesparity";
	/**
	 * configuration key for port parity setting
	 */
	public static final String SENSORNODE_EVENPARITY = "com.ptoceti.osgi.sensornodedriver.evenparity";
	
	/**
	 * the Sensor nod driver service registration
	 */
	ServiceRegistration sReg;
	
	/**
	 *  driver id received from the configuration.
	 */
	Integer id;
	
	/**
	 * the name of the port used by this driver
	 */
	String port;
	
	/**
	 * Serial port used to communicate with the sensor nodes ( they can be chained)
	 */
	protected SerialPort serialPort;
	
	/**
	 * Serial port output stream
	 */
	protected OutputStream outStream;
	/**
	 * Serial port input stream
	 */
	protected InputStream inStream;
	
	/**
	 * First start byte in a frame to be sent to the node
	 */
	static final int FRAME_START_1 = 0x55; 
	/**
	 * Second start byte in a frame to be sent to the node
	 */
	static final int FRAME_START_2 = 0xAA;
	
	/**
	 * Command identification for the node for a get all value frame
	 */
	static final byte CHECK_ALL_COMMAND = 0x21;
	
	/**
	 * this buffer contains the raw bytes of the next message to send.
	 */
	ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
	/**
	 * this buffer contains the last message received from the serial link, converted back into binary.
	 */
	ByteArrayOutputStream bytesIn = new ByteArrayOutputStream();
	/**
	 * flag to indicate that a special "start of frame" first byte has been received.
	 */
	boolean foundFrameStart1 = false;
	/**
	 * flag to indicate that a special "start of frame" second byte has been received.
	 */
	boolean foundFrameStart2 = false;
	
	/**
	 * contents buffer of the sensor node response for any command.
	 */
	
	ResponseSynchronizer responseSync;
	
	
	/**
	 * Create a sensor node driver with setting of the serial port to utilise to communicate on a link.
	 * 
	 * @param id driver id
	 * @param port the port name
	 * @param baudRate the port baud rate
	 * @param usesParity whether to use parity or not
	 * @param evenParity use even or odd parity
	 * @throws Exception in case the serial port could'nt  be open
	 */
	public SensorNodeDriver(final Integer id, final String port, final Integer baudRate, final Boolean usesParity, final Boolean evenParity) throws Exception {
		
		this.id = id;
		this.port = port;
		
		responseSync = new ResponseSynchronizer();
		
		try {
			final CommPortIdentifier portID = CommPortIdentifier.getPortIdentifier(port);
			if( portID != null && ( portID.getPortType() == CommPortIdentifier.PORT_SERIAL)) {
				// if this is the port we want,
				if( portID.getName().equals( port )) { 
					// try to get hold of this port. If not successfull within 1 second, throws an exception.
					serialPort = (SerialPort) portID.open( this.getClass().getName(), (int)1000 );
					// if the parity bit is not used, the  port is configured to send two stop bits.
					if( usesParity == true )
						serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8,
							SerialPort.STOPBITS_1, ( evenParity ? SerialPort.PARITY_EVEN : SerialPort.PARITY_ODD ));
					else
						serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8,
							SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
					serialPort.setInputBufferSize(64);
					serialPort.disableReceiveThreshold();
					// set itself to listen to data available events.
					serialPort.addEventListener(this);
					serialPort.notifyOnDataAvailable(true);
					serialPort.enableReceiveTimeout(1000);
					// get hold of the inputs and outputs streams.
					inStream = serialPort.getInputStream();
					outStream = serialPort.getOutputStream();
				} else {
					throw new Exception("Port obtained not same as port requested: " + port + " / " + portID.getName());
				}
			} else {
				throw new Exception("Port not a serial port: " + portID.toString());
			}
		} catch ( PortInUseException e) { throw new Exception("Port " + port + " is already in use."); }
		catch (UnsupportedCommOperationException e) { throw new Exception("Port does not support this operation."); }
		catch (TooManyListenersException e) { throw new Exception("Could not create listener on port: " + port + "."); }
		catch (IOException e) { throw new Exception("Could not open input or output streams on port: " + port + "."); }
		catch (NoSuchPortException e ){throw new Exception("Port unknow: " + port + ". ");}
	}
	
	/**
	 * Register the sensor nde driver in the osgi framework so that it can be use by SensorNode devices.
	 */
	public void start(){
		String[] clazzes = new String[] {
				SensorNodeDriver.class.getName()
		};
			
		Dictionary<String, Object> props = new Hashtable<String, Object>();
		props.put( org.osgi.framework.Constants.SERVICE_PID, SensorNodeDriver.class.getName());
		props.put( org.osgi.framework.Constants.SERVICE_DESCRIPTION, "SensorNodeDriver service implements a Device interface.");
		props.put( SensorNodeDriver.SENSORNODE_PORT, this.port);
		
		sReg = Activator.bc.registerService( clazzes, this, props );
		
		Activator.log(LogService.LOG_INFO, "SensorNodeDriver started on port:  " + this.port);
		
	}
	
	/**
	 * Stop serial port, unregister driver
	 */
	public void stop(){
		sReg.unregister();
		serialPort.removeEventListener();
		serialPort.close();
		inStream = null;
		outStream = null;
		
		Activator.log(LogService.LOG_INFO, "Unregistered " + this.getClass().getName());
	}
	
	
	/**
	 * Send a get al value requets frame to sensor node to retrievd all of it s data.
	 * 
	 * @param nodeId the idnet of the node
	 * @return Integer[] a array of 10  integer words that represent all of the node value data.
	 */
	public synchronized Integer[] getAllValues(final Integer nodeId){
		
		Integer[] result = new Integer[0];
		
		try {
		
			foundFrameStart1 = false;
			foundFrameStart2 = false;
			
			bytesOut.reset();
			bytesOut.write(FRAME_START_1);
			bytesOut.write(FRAME_START_2);
			bytesOut.write(nodeId.byteValue()); // write node adress
			bytesOut.write(0x00); // frame lenght
			bytesOut.write(CHECK_ALL_COMMAND);
			bytesOut.write(SensorNodeDriverUtils.calculateLRC(bytesOut.toByteArray(), bytesOut.size())); // CRC
			
			Activator.log(LogService.LOG_DEBUG, "Sending frame to sensor node: " + SensorNodeDriverUtils.writeHex(bytesOut.toByteArray()));
			
			outStream.write( bytesOut.toByteArray());
			outStream.flush();
			
			result = responseSync.getresponse();
			
		} catch (IOException ex) {
			Activator.log(LogService.LOG_DEBUG, "Error sending frame");
		} 
		
		return result;
	}
	
	/**
	 * Check an incoming data frame from a node and return its values as an array of integers.
	 * @return an array of integers that represents the retuend data
	 */
	private void checkNewIncomingMessage() {
		byte[] buff = bytesIn.toByteArray();
		
		try {
			Activator.log(LogService.LOG_DEBUG, "Receiced message from sensor node: " + SensorNodeDriverUtils.writeHex(buff));
		} catch (IOException e) {
		}
		
		byte calculatedLrc = SensorNodeDriverUtils.calculateLRC(buff, buff.length - 1);
		
		List<Integer> result = new ArrayList<Integer>();
		// Check if check sum are equals
		if( calculatedLrc == buff[buff.length -1]) {
			
			int nbBytes = buff[3];
			
			for(int i = 0 ; i < nbBytes; i = i+2){
				ByteBuffer bbuff = ByteBuffer.allocate(2);
				bbuff.order(ByteOrder.BIG_ENDIAN);
				bbuff.put(0, buff[5 + i ]);
				bbuff.put(1, buff[5 + i + 1]);
				//bbuff.flip();
				result.add((int)bbuff.getShort());
				//int wordValue = ( (((int)buff[5 + i ]) << 8 ) + ((int)buff[5 + i + 1]));
				//result.add(Integer.valueOf(wordValue));
			}
		} 
		
		responseSync.setResponse(result.toArray(new Integer[result.size()]));
	}

	/**
	 * Serial port's SerialPortEventistener. When a event is produced by the SerialPort class,
	 * is is sent to this method. This mechanism is used to listen to incoming bytes on the serial bus.
	 * Once the start and end special chars are received, the incoming bytes are packed as a proper frame
	 * and passed on to be converted back to binary ( from the hex bytes ).
	 *
	 * @param event : the SerialPortEvent event.
	 */
	public void serialEvent(SerialPortEvent event) {
		int inByte = 0;
		// at present we are only listening to DATA_AVAILABLE events, which that new incoming bytes
		// have received on the serial port.
		if( event.getEventType() == SerialPortEvent.DATA_AVAILABLE ) {
			try {

			if (inStream.available() > 0 ) {
					// we are working on every received byte ( there may be more than one byte on the input
					// stream ). We also do no want to block the current thread by attempting a read() with
					// no available bytes.
					
					while((inByte = inStream.read()) > -1) {
						// if we receive a start of frame byte, flag it and reset the hex buffer.
						if( !foundFrameStart1 && inByte == FRAME_START_1 ) {
							foundFrameStart1 = true;
						}
						else if(!foundFrameStart2 && inByte == FRAME_START_2 && foundFrameStart1) {
							foundFrameStart2 = true;
							bytesIn.reset();
							// need to write start frame bytes in buffer as checksum is calculated with them
							bytesIn.write(FRAME_START_1);
							bytesIn.write(FRAME_START_2);
						}
						// this is just a norma byte, so if we received the start of frame, but not yet the end
						// of frame char, we just record it.
						else if(( foundFrameStart2 == true )) {
							if( bytesIn.size() < 5) {
								// first thre bytes contains device adress, frame lenght, command word
								bytesIn.write(inByte);
							} else {
								if( bytesIn.size() < (bytesIn.toByteArray())[3] + 5) {
									bytesIn.write(inByte);
								} else {
									// we are on the end of frame. last byte is SUM
									bytesIn.write(inByte);
									checkNewIncomingMessage();
									bytesIn.reset();
									foundFrameStart1 = false;
									foundFrameStart2 = false;
									break;
								}
							}
						}
					}
				}
				
			} catch (IOException e ) {
				Activator.log(LogService.LOG_ERROR, "Error reading char from serial input stream: " + e); 
			}
		}
		
	}
	
	/**
	 * Class to synchronize access to the response of the sensor node, if there was one. 
	 * @author LATHIL
	 *
	 */
	protected class ResponseSynchronizer {
		 boolean hasNewResponse;
		 Integer[] newResponse;
		 
		 ResponseSynchronizer(){
			 hasNewResponse = false;
		 }
		 
		 /**
		  * Consumer access to response. Wait a maximum time for the response of the sensor node ( currently 2 sec ).
		  * 
		  * @return an array of int if the response has been received, null otherwise
		  */
		 synchronized Integer[] getresponse(){
			 
			try {
				if( !hasNewResponse){
					// wait max 2sec
					wait(2000);
				}
				
				if( hasNewResponse){
					hasNewResponse= false;
					return newResponse;
				}
				
			} catch (InterruptedException e) {
				Activator.log(LogService.LOG_DEBUG, "No response received from sensor node. Waited max time elapse.");
			}
			
			return null;
		 }
		 
		 /**
		  * Producer method. Set the response and notify waiting consumer that response has arived.
		  * @param response
		  */
		 synchronized void setResponse(Integer[] response){
			 newResponse = response.clone();
			 hasNewResponse = true;
			 notifyAll();
		 }
		
	}
}
