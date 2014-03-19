

package com.ptoceti.osgi.modbus.impl.message;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Modbus
 * FILENAME : ModbusMessageResponse.java
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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public abstract class ModbusMessageResponse extends ModbusMessageImpl {

	public static ModbusMessageResponse createResponse( byte unitID, byte functionID ){
	
		ModbusMessageResponse response = null;
		
		switch(functionID) {
			case ModbusMessage.READ_COIL_STATUS:
				response = new ReadCoilStatusMessageResponse(unitID);
				break;
			case ModbusMessage.READ_INPUT_STATUS:
				response = new ReadInputStatusMessageResponse(unitID);
				break;
			case ModbusMessage.READ_HOLDING_REGISTERS:
				response = new ReadHoldingRegistersMessageResponse(unitID);
				break;
			case ModbusMessage.READ_INPUT_REGISTERS:
				response = new ReadInputRegistersMessageResponse(unitID);
				break;
			case ModbusMessage.FORCE_SINGLE_COIL:
				response = new ForceSingleCoilMessageResponse(unitID);
				break;
			case ModbusMessage.FORCE_SINGLE_REGISTER:
				response = new ForceSingleRegisterMessageResponse(unitID);
				break;
		}
		
		return response;
	}
			
	public boolean readFrom(InputStream in ) {
		try {
			if( in.read() != getUnitID()) return false;
			if( in.read() != getFunctionID()) return false;
			return( readData(in));
		} catch (IOException e) {
			return false;
		}
	}
	
	public abstract boolean readData(InputStream in);
	
	
	public boolean writeTo(OutputStream out) {
		return false;
	}
}
