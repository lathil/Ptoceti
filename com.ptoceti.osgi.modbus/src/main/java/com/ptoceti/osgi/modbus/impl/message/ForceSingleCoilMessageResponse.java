

package com.ptoceti.osgi.modbus.impl.message;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Modbus
 * FILENAME : ForceSingleCoilMessageResponse.java
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

import java.io.InputStream;
import java.io.IOException;

public class ForceSingleCoilMessageResponse extends ModbusMessageResponse {

	private int coilAddress; // adress of starting input - 2 bytes
	private int coilValue; // number of input - 2 bytes

	public ForceSingleCoilMessageResponse( byte unitID) {
		setFunctionID(ModbusMessage.FORCE_SINGLE_COIL);
		setUnitID(unitID);
	}
	
	public ForceSingleCoilMessageResponse() {
		setFunctionID(ModbusMessage.FORCE_SINGLE_COIL);
		setUnitID((byte)0);
	}
	
	public int getMessageLength() {
		int length = 0;
		
		return length;
	}
	
	public int getCoilAdress() {
		return coilAddress;
	}
	
	public boolean getValue() {
		if( coilValue == 0xFF00 ) return true;
		else return false;
	}
	
	public boolean readData(InputStream in) {
	
		try {
			coilAddress = in.read() << 8;
			coilAddress =+ in.read();
			coilValue = in.read() << 8;
			coilValue =+ in.read();
		} catch ( IOException e ) { return false; }
		
		return true;
	}
}
