
package com.ptoceti.osgi.modbusdevice.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : ModbusDevice
 * FILENAME : ModbusState.java
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


import java.util.Vector;
import java.util.Date;

import com.ptoceti.osgi.control.Digit;

/**
 * ModbusState class
 *
 *
 *
 * @author Laurent Thil
 * @version 1.0
 */

public class ModbusState extends ModbusData{

	public ModbusState(String identification, String scope, int adress, int lenght ){
		
		dataIdentification = identification;
		dataScope = scope;
		this.adress = adress;
		this.length = lenght;
		
	}

	public ModbusState(Vector stateSettings) throws Exception {
	
		try {
		
			dataIdentification = (String)(stateSettings.get(0));
			dataScope = (String)(stateSettings.get(1));
			adress = ((Integer)(stateSettings.get(2))).intValue();
			length = ((Integer)(stateSettings.get(3))).intValue();
			
		} catch (ArrayIndexOutOfBoundsException e ) {
			Exception ne = new Exception("Error reading settings parameters.");
			throw(ne);
		}
	}
	
	public Object getValue() {
	
		int data = bufferDelegate.read( adress, length);
		Digit state = new Digit(data > 0 ? true : false, "");
		
		return state;
	}

	@Override
	public void setValue(Object value) {
		bufferDelegate.write(adress, length, ((Digit) value).getState() == true ? 1 : 0);
	}
}
