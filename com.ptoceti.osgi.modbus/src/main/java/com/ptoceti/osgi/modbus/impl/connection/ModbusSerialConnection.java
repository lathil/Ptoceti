
package com.ptoceti.osgi.modbus.impl.connection;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Modbus
 * FILENAME : ModbusSerialConnection.java
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

import java.io.OutputStream;
import java.io.InputStream;

import gnu.io.SerialPort;


/**
 * Abstract class for implementing an ModbusConnection. Contain common functionality to ModbusSerialASCIIConnection
 * and ModbusSerialRTUConnection class. Mainly deal with containning the modbus diagnostic counters; These record
 * some statistic about the number of frames sent and received, the number of dropped frames, etc ..
 *
 * @author Laurent Thil
 * @version 1.0b
 */
public abstract class ModbusSerialConnection implements ModbusConnection {
		
	protected OutputStream outStream;
	protected InputStream inStream;
	
	protected int framesSentCounter;
	protected int framesReceivedCounter;
	protected int badFramesSentCounter;
	protected int badFramesReceivedCounter;
	protected int noRepliesCounter;
	
	boolean doMonitorFlag;
	boolean usesEcho;
	
	protected SerialPort serialPort;
	protected String portName;
	
	public String getPortName() {
		return portName;
	}
	public void setDoMonitor(boolean doMonitor) {
		doMonitorFlag = doMonitor;
	}
	
	public boolean getDoMonitor() {
		return doMonitorFlag;
	}
	
	public void incrementFrameSentCounter(){
		framesSentCounter++;
	}
	
	public int getFrameSentCounter() {
		return framesSentCounter;
	}
	
	public void incrementFramesReceivedCounter() {
		framesReceivedCounter++;
	}
	
	public int getFramesReceivedCounter() {
		return framesReceivedCounter;
	}
	
	public void incrementBadFramesSentCounter() {
		badFramesSentCounter++;
	}
	
	public int getBadFramesSentCounter() {
		return badFramesSentCounter;
	}
	
	public void incrementBadFramesReceivedCounter() {
		badFramesReceivedCounter++;
	}
	
	public int getBadFramesReceivedCounter() {
		return badFramesReceivedCounter;
	}
	
	public void incrementNoRepliesCounter() {
		noRepliesCounter++;
	}
	
	public int getNoRepliesCounter() {
		return noRepliesCounter;
	}
}
