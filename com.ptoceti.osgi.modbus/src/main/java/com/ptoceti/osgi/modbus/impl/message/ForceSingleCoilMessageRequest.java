

package com.ptoceti.osgi.modbus.impl.message;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Modbus
 * FILENAME : ForceSingleCoilMessageRequest.java
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

public class ForceSingleCoilMessageRequest extends ModbusMessageRequest {

	private int coilAdress; // adress of starting input - 2 bytes
	private int coilValue; // number of input - 2 bytes
	
	public ForceSingleCoilMessageRequest(byte unitID ) {
		super();
		setFunctionID(ModbusMessage.FORCE_SINGLE_COIL );
		setUnitID(unitID);
	}
	
	public ForceSingleCoilMessageRequest(byte unitID, int coil, boolean value ) {
		super();
		setFunctionID(ModbusMessage.FORCE_SINGLE_COIL);
		setUnitID(unitID);
		setCoilAdress(coil);
		setValue(value);
	}
	
	public void setCoilAdress(int coil) {
		coilAdress = coil;
	}
	
	public void setValue(boolean value) {
		if( value == true ) coilValue = 0xFF00;
		else coilValue = 0x0000;
	}

	public int getMessageLength() {
	
		int length = 0;
		
		return length;
	}
	
	public boolean writeData(OutputStream out) {
	
		try {
			out.write(( coilAdress & 0xFF00 ) >>> 8);
			out.write( coilAdress & 0x00FF);
			out.write(( coilValue & 0xFF000 ) >>> 8);
			out.write( coilValue & 0x00FF);
		} catch (IOException e ) {
			return false;
		}
		
		return true;
	}

}
