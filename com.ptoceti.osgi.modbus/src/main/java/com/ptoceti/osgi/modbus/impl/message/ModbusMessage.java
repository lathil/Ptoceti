

package com.ptoceti.osgi.modbus.impl.message;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Modbus
 * FILENAME : ModbusMessage.java
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

import java.io.OutputStream;
import java.io.InputStream;

public interface ModbusMessage {

	public static final byte READ_COIL_STATUS = 1;
	public static final byte READ_INPUT_STATUS = 2;
	public static final byte READ_HOLDING_REGISTERS = 3;
	public static final byte READ_INPUT_REGISTERS = 4;
	public static final byte FORCE_SINGLE_COIL = 5;
	public static final byte FORCE_SINGLE_REGISTER = 6;
	public static final byte READ_EXCEPTION_STATUS = 7;
	
	public static final byte  FECTH_COMM_EVENT_COUNTER = 11;
	public static final byte  FETCH_COMM_EVENT_LOG = 12;
	public static final byte  FORCE_MULTIPLE_COILS = 15;
	public static final byte  PRESET_MULTIPLE_FLAGS = 16;
	public static final byte  REPORT_SLAVE_ID = 17;
	public static final byte  READ_GENERAL_REFERENCE = 20;
	public static final byte  WRITE_GENERAL_REFERENCE = 21;
	public static final byte  MASK_WRITE_4X_REGISTER = 22;
	public static final byte  READ_WRITE_4X_REGISTERS= 23;
	public static final byte  READ_FIFO_QUEUE = 24;
	
	
	public byte getFunctionID();
	public byte getUnitID();
	public int getMessageLength();
	
	public boolean writeTo(OutputStream out);
	public boolean readFrom(InputStream in);
 
}
