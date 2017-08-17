

package com.ptoceti.osgi.modbusdevice.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : ModbusDevice
 * FILENAME : ModbusMeasurement.java
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

import com.ptoceti.osgi.control.ExtendedUnit;
import com.ptoceti.osgi.control.Measure;

/**
 * ModbusMeasurement class
 *
 *
 *
 * @author Laurent Thil
 * @version 1.0
 */

public class ModbusMeasurement extends ModbusData{

	
	private String dataExpression = null;
	
	public ModbusMeasurement(String identification, String scope, String expression, int adress, int lenght ){
		
		dataIdentification = identification;
		dataScope = scope;
		dataExpression = expression;
		this.adress = adress;
		this.length = lenght;
		
	}
	
	
	public ModbusMeasurement( Vector  measurementSettings ) throws Exception {

		try {
		
			dataIdentification = (String)(measurementSettings.get(0));
			dataScope = (String)(measurementSettings.get(1));
			dataExpression = (String)(measurementSettings.get(2));
			adress = ((Integer)(measurementSettings.get(3))).intValue();
			length = ((Integer)(measurementSettings.get(4))).intValue();
			
		} catch (ArrayIndexOutOfBoundsException e ) {
			Exception ne = new Exception("Error reading settings parameters.");
			throw(ne);
		}
	}
	
	public Object getValue() {
	
		int data = bufferDelegate.read( adress, length);
		Measure measurement = new Measure((double) data, 0.0, ExtendedUnit.findUnit(dataExpression), bufferDelegate.getLastUpdateTime());
		
		return measurement;
		
	}


	@Override
	public void setValue(Object value) {
		bufferDelegate.write(adress, length, (int)((Measure) value).getValue());
		
	}
	
	
}
