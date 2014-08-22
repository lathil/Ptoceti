package com.ptoceti.osgi.modbusdevice.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : ModbusDevice
 * FILENAME : ModbusReference.java
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

import com.ptoceti.osgi.control.ExtendedUnit;
import com.ptoceti.osgi.control.Reference;


import java.util.Vector;

public class ModbusReference extends ModbusData {

private String dataExpression = null;
	
	public ModbusReference(String identification, String scope, String expression, int adress, int lenght ){
		
		dataIdentification = identification;
		dataScope = scope;
		dataExpression = expression;
		this.adress = adress;
		this.length = lenght;
		
	}
	
	
	public ModbusReference( Vector  referenceSettings ) throws Exception {

		try {
		
			dataIdentification = (String)(referenceSettings.get(0));
			dataScope = (String)(referenceSettings.get(1));
			dataExpression = (String)(referenceSettings.get(2));
			adress = ((Integer)(referenceSettings.get(3))).intValue();
			length = ((Integer)(referenceSettings.get(4))).intValue();
			
		} catch (ArrayIndexOutOfBoundsException e ) {
			Exception ne = new Exception("Error reading settings parameters.");
			throw(ne);
		}
	}
	
	public Object getValue() {
	
		int data = reader.read( adress, length);
		
		Reference reference = new Reference((double) data, ExtendedUnit.findUnit(dataExpression));
		
		return reference;
		
	}
	
	
}
