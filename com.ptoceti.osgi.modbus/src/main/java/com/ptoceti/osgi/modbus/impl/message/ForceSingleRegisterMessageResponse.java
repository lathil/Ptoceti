

package com.ptoceti.osgi.modbus.impl.message;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Modbus
 * FILENAME : ForceSingleRegisterMessageResponse.java
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
import java.io.IOException;

public class ForceSingleRegisterMessageResponse extends ModbusMessageResponse {

	private int registerAdress; // adress of starting input - 2 bytes
	private int registerValue; // number of input - 2 bytes
	
	public ForceSingleRegisterMessageResponse( byte unitID) {
		setFunctionID(ModbusMessage.FORCE_SINGLE_REGISTER);
		setUnitID(unitID);
	}
	
	public ForceSingleRegisterMessageResponse() {
		setFunctionID(ModbusMessage.FORCE_SINGLE_REGISTER);
		setUnitID((byte)0);
	}
	
	public int getMessageLength() {
		int length = 0;
		
		return length;
	}
	
	public int getRegisterAdress() {
		return registerAdress;
	}
	
	public int getValue() {
		return registerValue;
	}
	
	public boolean readData(InputStream in) {
	
		try {
			registerAdress = in.read() << 8;
			registerAdress =+ in.read();
			registerValue = in.read() << 8;
			registerValue =+ in.read();
		} catch ( IOException e ) { return false; }
		
		return true;
	}
}
