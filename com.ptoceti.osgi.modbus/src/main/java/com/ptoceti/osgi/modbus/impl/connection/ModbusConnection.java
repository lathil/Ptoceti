

package com.ptoceti.osgi.modbus.impl.connection;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Modbus
 * FILENAME : ModbusConnection.java
 * 
 * This file is part of the Ptoceti project. More information about
 * this project can be found here: http://www.ptoceti.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2013 - 2015 ptoceti
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

import com.ptoceti.osgi.modbus.impl.message.ModbusMessageResponse;
import com.ptoceti.osgi.modbus.impl.message.ModbusMessageRequest;

/**
 * Interface class. Define minimum methods that subclasses must implement. Mainly method 
 * signatures for sending messages, and read the diagnotic counters.
 *
 * @author  Laurent Thil
 * @version 1.0b
 */
public interface ModbusConnection {
	
	public static long MODBUS_SERIAL_RESPONSE_WAITING_TIMEOUT = 1000; // 1 seconde timeout
	
	public ModbusMessageResponse sendMessage( ModbusMessageRequest message );
	public void waitForRequest();
	
	public void close();
	public String getEncodingType();
	
	public String getPortName();
	public void setDoMonitor(boolean doMonitor);
	public boolean getDoMonitor();
	
	public void incrementFrameSentCounter();
	public int getFrameSentCounter();
	public void incrementFramesReceivedCounter();
	public int getFramesReceivedCounter();
	public void incrementBadFramesSentCounter();
	public int getBadFramesSentCounter();
	public void incrementBadFramesReceivedCounter();
	public int getBadFramesReceivedCounter();
	public void incrementNoRepliesCounter();
	public int getNoRepliesCounter();

}
