
package com.ptoceti.osgi.modbus.impl.message;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Modbus
 * FILENAME : ReadInputStatusMessageResponse.java
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

public class ReadInputStatusMessageResponse extends ModbusMessageResponse {

	private byte[] arrayList = null;
		
	public ReadInputStatusMessageResponse( byte unitID) {
		setFunctionID(ModbusMessage.READ_INPUT_STATUS);
		setUnitID(unitID);
	}
	
	public ReadInputStatusMessageResponse() {
		setFunctionID(ModbusMessage.READ_INPUT_STATUS);
		setUnitID((byte)0);
	}
	
	
	public int getMessageLength() {
		int length = 0;
		
		return length;
	}
	
	public byte[] getValues() {
	
		byte[] result = new byte[ arrayList.length ];
		for(int i = 0; i < result.length; i++ ) {
			result[i] = arrayList[i];
		}
		
		return result;
	}

	
	public boolean readData(InputStream in) {

		try {
			int byteCount = in.read();
			arrayList = new byte[ byteCount];
			for(int i = 0 ; i < byteCount; i ++ ) {
				arrayList[i] = (byte)in.read();
			}
		
		} catch ( IOException e ) {
			arrayList = null;
			return false;
		}
		
		return true;
	}

}
