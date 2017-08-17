

package com.ptoceti.osgi.modbus.impl.message;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Modbus
 * FILENAME : ReadHoldingRegistersMessageRequest.java
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
import java.io.IOException;


public class ReadHoldingRegistersMessageRequest extends ModbusMessageRequest {

	private int firstRegister; // adress of starting input - 2 bytes
	private int nbRegisters; // number of input - 2 bytes
	
	public ReadHoldingRegistersMessageRequest(byte unitID ) {
		super();
		setFunctionID(ModbusMessage.READ_HOLDING_REGISTERS );
		setUnitID(unitID);
	}
	
	public ReadHoldingRegistersMessageRequest(byte unitID, int register, int number ) {
		super();
		setFunctionID(ModbusMessage.READ_HOLDING_REGISTERS);
		setUnitID(unitID);
		setFirstRegister(register);
		setNbRegisters(number);
	}
	
	public void setFirstRegister(int register) {
		firstRegister = register;
	}
	
	public void setNbRegisters(int number) {
		nbRegisters = number;
	}

	public int getMessageLength() {
	
		int length = 0;
		
		return length;
	}
	
	public boolean writeData(OutputStream out) {
	
		try {
			out.write(( firstRegister & 0xFF00 ) >>> 8);
			out.write( firstRegister & 0x00FF);
			out.write(( nbRegisters & 0xFF000 ) >>> 8);
			out.write( nbRegisters & 0x00FF);
		} catch (IOException e ) {
			return false;
		}
		
		return true;
	}

}
