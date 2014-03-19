

package com.ptoceti.osgi.modbus.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Modbus
 * FILENAME : ModbusSlave.java
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
import com.ptoceti.osgi.modbus.impl.connection.ModbusSerialASCIIConnection;
import com.ptoceti.osgi.modbus.impl.connection.ModbusSerialRTUConnection;

/**
 * 
 */
 
public class ModbusSlave extends ModbusDriverImpl {

	public FrameListener listener;
	
	public ModbusSlave( int id, String portName, String encoding, int baudRate, boolean usesParity, boolean evenParity  ) throws Exception {
		
		this.setID((byte)id);

		if( encoding.equals(ModbusDriver.RTU_ENCODING))
			mdbConnection = new ModbusSerialRTUConnection( portName, baudRate, usesParity, evenParity, false );
		else if (encoding.equals(ModbusDriver.ASCII_ENCODING ))
			mdbConnection = new ModbusSerialASCIIConnection( portName, baudRate, usesParity, evenParity, false );
		
		listener = new FrameListener();
	}
	
	
	public boolean isMaster() {
		return false;
	}
	
	public boolean isSlave() {
		return true;
	}
	
	/**
	 * Return the port's name that is used for the modbus connection.
	 */
	public String getPortName() {
		
		String portName = null;
		
		if( mdbConnection != null){
			portName = mdbConnection.getPortName();
		}
		
		return portName;
	}
	
	public byte[] readCoilsStatus( byte unitID, int coilID, int nbCoils ) {
		return null;
	}
	
	public byte[] readInputStatus( byte unitID, int inputStatID, int nbInputsStats ) {
		return null;
	}
	
	public int[] readHoldingRegisters( byte unitID, int holdingRegID, int nbHoldingReg ){
		return null;
	}
	
	public int[] readInputRegisters( byte unitID, int inputRegID, int bnInputReg ) {
		return null;
	}
	
	public boolean forceSingleCoil( byte unitID, int coilID, boolean value ) {
		return false;
	}
	
	public boolean forceSingleRegister( byte unitID, int registerID, int value ) {
		return false;
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
	public class FrameListener implements Runnable {
	
		
		// the thread that run this
		Thread myThread;
		
		public FrameListener() {
			myThread = new Thread(this);
			myThread.start();
		}
				
				
		/**
		 * Method implementing the Runnable interface. Continuously invoque the checkForFrame private methode.
		 * 
		 */
		public void run() {
			while(true) {
				// this object's monitors can only be blocked from inside a synchronized method. Call this one.
				mdbConnection.waitForRequest();
			}
		}
	}

}
