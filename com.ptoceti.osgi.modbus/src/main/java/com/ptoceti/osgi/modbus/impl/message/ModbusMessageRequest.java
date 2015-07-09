

package com.ptoceti.osgi.modbus.impl.message;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Modbus
 * FILENAME : ModbusMessageRequest.java
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

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;

public abstract class ModbusMessageRequest extends ModbusMessageImpl {
	
	public ModbusMessageResponse response = null;
	
	public static ModbusMessageRequest createMessage(byte unitID, byte functionID ) {
	
		ModbusMessageRequest request = null;
		
		switch(functionID) {
			case ModbusMessage.READ_COIL_STATUS:
				request = new ReadCoilStatusMessageRequest(unitID);
				request.setResponseMessage( ModbusMessageResponse.createResponse(unitID, ModbusMessage.READ_COIL_STATUS));
				break;
			case ModbusMessage.READ_INPUT_STATUS:
				request = new ReadInputStatusMessageRequest(unitID);
				request.setResponseMessage( ModbusMessageResponse.createResponse(unitID, ModbusMessage.READ_INPUT_STATUS));
				break;
			case ModbusMessage.READ_HOLDING_REGISTERS:
				request = new ReadHoldingRegistersMessageRequest(unitID);
				request.setResponseMessage( ModbusMessageResponse.createResponse(unitID, ModbusMessage.READ_HOLDING_REGISTERS));
				break;
			case ModbusMessage.READ_INPUT_REGISTERS:
				request = new ReadInputRegistersMessageRequest(unitID);
				request.setResponseMessage( ModbusMessageResponse.createResponse(unitID, ModbusMessage.READ_INPUT_REGISTERS));
				break;
			case ModbusMessage.FORCE_SINGLE_COIL:
				request = new ForceSingleCoilMessageRequest(unitID);
				request.setResponseMessage( ModbusMessageResponse.createResponse(unitID, ModbusMessage.FORCE_SINGLE_COIL));
				break;
			case ModbusMessage.FORCE_SINGLE_REGISTER:
				request = new ForceSingleRegisterMessageRequest(unitID);
				request.setResponseMessage( ModbusMessageResponse.createResponse(unitID, ModbusMessage.FORCE_SINGLE_REGISTER));
				break;
		}
		
		return request;
	}
	
	public boolean writeTo(OutputStream out) {
	
		try {
			out.write( getUnitID());
			out.write( getFunctionID());
			return( writeData(out));
		} catch (IOException e) {
			return false;
		}
	}
	
	public abstract boolean writeData(OutputStream out);
	
	public boolean readFrom(InputStream in) {
		return false;
	}
	
	private void setResponseMessage( ModbusMessageResponse responseMessage ) {
		response = responseMessage;
	}
	
	public ModbusMessageResponse getResponseMessage() {
		return response;
	}

}
