package com.ptoceti.osgi.modbusdevice.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : ModbusDevice
 * FILENAME : ModbusSwitch.java
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

import java.util.Date;
import java.util.Vector;

import org.osgi.util.measurement.State;

import com.ptoceti.osgi.control.Switch;

public class ModbusSwitch extends ModbusData {


	public ModbusSwitch(String identification, String scope, int adress, int lenght ){
		
		dataIdentification = identification;
		dataScope = scope;
		this.adress = adress;
		this.length = lenght;
		
	}

	public ModbusSwitch(Vector switchSettings) throws Exception {
	
		try {
		
			dataIdentification = (String)(switchSettings.get(0));
			dataScope = (String)(switchSettings.get(1));
			adress = ((Integer)(switchSettings.get(2))).intValue();
			length = ((Integer)(switchSettings.get(3))).intValue();
			
		} catch (ArrayIndexOutOfBoundsException e ) {
			Exception ne = new Exception("Error reading settings parameters.");
			throw(ne);
		}
	}
	
	public Object getValue() {
	
		int data = bufferDelegate.read( adress, length);
		
		Switch switchData = new Switch(data > 0 ? true : false);
		
		return switchData;
	}

	@Override
	public void setValue(Object value) {
		bufferDelegate.write(adress, length, ((Switch) value).getState() == true ? 1 : 0);
	}
}
