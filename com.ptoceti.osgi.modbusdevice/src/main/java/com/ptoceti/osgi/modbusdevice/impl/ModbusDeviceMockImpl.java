package com.ptoceti.osgi.modbusdevice.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : ModbusDevice
 * FILENAME : ModbusDeviceMockImpl.java
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


import java.util.ArrayList;
import java.util.List;

import org.osgi.service.log.LogService;

public class ModbusDeviceMockImpl extends ModbusDeviceAbstractImpl {

	MockThread thread;
	
	public ModbusDeviceMockImpl( String pid, String compositeIdentity, String modbusPort, int modbusId, int  modbusPoolingRateS, List<ModbusData> mdbReferenceList, List<ModbusData> mdbMeasurementList, List<ModbusData> mdbStateList, List<ModbusData> mdbSwitchList  ) throws Exception {
		
		// Force the list of wires to null. It will get initialiwed by the wire admin.
		consumerWires = null;
		// Initialise the list of ModbusData to an empty list. We'll feed it latter.
		modbusData = new ArrayList<ModbusData>();
		
		// Crate the reference data buffer ..
		modbusRDataBuffer = new MockReferenceDataBuffer();
		// Create the measurement data buffer ..
		modbusMDataBuffer = new MockMeasurementDataBuffer();
		// and the state data buffer now. We'll need to give them as delegates to the modbus data objects.
		modbusSDataBuffer = new MockStateDataBuffer();
		// a state buffer for the coils as well
		modbusSwDataBuffer = new MockSwitchDataBuffer();
		
		init(pid, compositeIdentity, mdbReferenceList, mdbMeasurementList, mdbStateList, mdbSwitchList);
		
		thread = new MockThread(modbusPoolingRateS);
		
	}
	
	public void stop() {
		thread.disconnect();
		sReg.unregister();
		Activator.log(LogService.LOG_INFO, "Unregistered " + this.getClass().getName());
		
	}
		
		
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getPortName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	private class MockReferenceDataBuffer implements ModbusDataBufferDelegate{
	
		private int offset = 0;
		private int count = 0;
		private int registers[];
		private long updateTime = 0;
	
		/**
		 * Setup the size and starting offset of the buffer array.
		 *
		 * @param count The number of bytes to read.
		 * @param offset The adress of the first byte to read in the device adress space.
		 */
		public void init(int offset, int count){
	
			this.count = count;
			this.offset = offset;
			this.registers = new int[count];
		}
		
		/**
		 * Read a value from the buffered array.
		 *
		 * @param adress The adress of the calue to read
		 * @param size The size of the value to read in bits
		 * @return The value.
		 */
		public synchronized int read( int adress, int size ) {
		
			if( registers != null ) {
				if((adress >= offset ) && ( adress <= ( offset + count ))) {
					return registers[adress - offset];
				} else
					return 0;
			} else
				return 0;
		}
		
		public synchronized void write(int adress, int size, int value){
			if( registers != null ) {
				if((adress >= offset ) && ( adress <= ( offset + count ))) {
					registers[adress - offset] = value;
				} 
			} 
		}
		
		public long getLastUpdateTime() {
			return updateTime;
		}
	}
	
	
	private class MockMeasurementDataBuffer implements ModbusDataBufferDelegate{
	
		private int offset = 0;
		private int count = 0;
		private int registers[];
		private long updateTime = 0;
		
		private int fakeValue;
	
		/**
		 * Setup the size and starting offset of the buffer array.
		 *
		 * @param count The number of bytes to read.
		 * @param offset The adress of the first byte to read in the device adress space.
		 */
		public void init(int offset, int count){
	
			this.count = count;
			this.offset = offset;
			
			fakeValue = 0;
			
		}
		
		/**
		 * Read a value from the buffered array.
		 *
		 * @param adress The adress of the calue to read
		 * @param size The size of the value to read in bits
		 * @return The value.
		 */
		public synchronized int read( int adress, int size ) {
		
			if( registers != null ) {
				if((adress >= offset ) && ( adress <= ( offset + count ))) {
					return registers[adress - offset];
				} else
					return 0;
			} else
				
				fakeValue++;
				if( fakeValue > 100) fakeValue = -100;
				return fakeValue;
		}
		
		public synchronized void write(int adress, int size, int value){
			
		}
		
		public long getLastUpdateTime() {
			return updateTime;
		}
	}
	

	private class MockStateDataBuffer implements ModbusDataBufferDelegate{
	
		
		private int offset = 0;
		private int count = 0;
		private int registers[];
		private long updateTime = 0;
		
		/**
		 * Setup the size and starting offset of the buffer array.
		 *
		 * @param count The number of bytes to read.
		 * @param offset The adress of the first byte to read in the device adress space.
		 */
		public void init(int offset, int count){
			
			this.count = count;
			this.offset = offset;
			registers = new int[count];
		}
		
		/**
		 * Read a value from the buffered array.
		 *
		 * @param adress The adress of the calue to read
		 * @param size The size of the value to read in bits
		 * @return The value.
		 */
		public synchronized int read( int adress, int size ) {
			
			if( registers != null ) {
				if((adress >= offset ) && ( adress <= ( offset + count ))) {
					return registers[adress - offset];
				} else
					return 0;
			} else
				return 0;
		}
		
		public synchronized void write(int adress, int size, int value){
			
		}
		
		public long getLastUpdateTime() {
			return updateTime;
		}
	}
	
	private class MockSwitchDataBuffer implements ModbusDataBufferDelegate{
	
		
		private int offset = 0;
		private int count = 0;
		private byte coils[];
		private long updateTime = 0;
		
		/**
		 * Setup the size and starting offset of the buffer array.
		 *
		 * @param count The number of bytes to read.
		 * @param offset The adress of the first byte to read in the device adress space.
		 */
		public void init(int offset, int count){
			
			this.count = count;
			this.offset = offset;
			this.coils = new byte[count];
		}
		
		/**
		 * Read a value from the buffered array.
		 *
		 * @param adress The adress of the calue to read
		 * @param size The size of the value to read in bits
		 * @return The value.
		 */
		public synchronized int read( int adress, int size ) {
			
			if( coils != null ) {
				if((adress >= offset ) && ( adress <= ( offset + count ))) {
					return coils[adress - offset];
				} else
					return 0;
			} else
				return 0;
		}
		
		public synchronized void write(int adress, int size, int value){
			if( coils != null ) {
				if((adress >= offset ) && ( adress <= ( offset + count ))) {
					coils[adress - offset] = (byte) value;
				} 
			} 
		}

		public long getLastUpdateTime() {
			return updateTime;
		}
	}
	
	protected class MockThread implements Runnable {
		
		// a flag asking to suspend communicating with the device.
		private boolean disconnect = false;
		// pooling time between successive communications with the modbus device.
		private long poolingTimeMill = 1000;
		// the thread that manage the communucation work with the device.
		Thread myThread = null;
		
		
		protected MockThread(long modbusPoolingRateS) {
			
			poolingTimeMill = modbusPoolingRateS;
			myThread = new Thread(this);
			myThread.start();
		}

		synchronized public void disconnect(){
			disconnect = true;
		}
		
		public void run() {
			while(!disconnect){
					refreshWires();
				try {
					Thread.sleep( poolingTimeMill );
				} catch (InterruptedException e) {
					break;
				}
			}
		}
		
		synchronized public boolean getDisconnect(){
			return disconnect;
		}
	}
}
