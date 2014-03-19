

package com.ptoceti.osgi.modbus.impl.message;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Modbus
 * FILENAME : ReadCoilStatusMessageRequest.java
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
import java.io.IOException;

public class ReadCoilStatusMessageRequest extends ModbusMessageRequest{

	private int firstCoil; // adress of starting coil - 2 bytes
	private int nbCoils; // number of coils - 2 bytes
	
	public ReadCoilStatusMessageRequest(byte unitID ) {
		super();
		setFunctionID(ModbusMessage.READ_COIL_STATUS);
		setUnitID(unitID);
	}
	
	public ReadCoilStatusMessageRequest(byte unitID, int coil, int number ) {
		super();
		setFunctionID(ModbusMessage.READ_COIL_STATUS);
		setUnitID(unitID);
		setFirstCoil(coil);
		setNbCoils(number);
	}
	
	public void setFirstCoil(int coil) {
		firstCoil = coil;
	}
	
	public void setNbCoils(int number) {
		nbCoils = number;
	}

	public int getMessageLength() {
	
		int length = 0;
		
		return length;
	}
	
	public boolean writeData(OutputStream out) {
	
		try {
			out.write(( firstCoil & 0xFF00 ) >>> 8);
			out.write( firstCoil & 0x00FF);
			out.write(( nbCoils & 0xFF000 ) >>> 8);
			out.write( nbCoils & 0x00FF);
		} catch (IOException e ) {
			return false;
		}
		
		return true;
	}

}
