

package com.ptoceti.osgi.modbusdevice.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : ModbusDevice
 * FILENAME : ModbusData.java
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



/**
 * ModbusData class
 *
 *
 *
 * @author Laurent Thil
 * @version 1.0
 */
 
public abstract class ModbusData {

	
		
	protected ModbusDataBufferDelegate bufferDelegate;
	
	protected int adress = 0;
	protected int length = 0;
	
	protected String dataScope = null;
	protected String dataIdentification = null;
	
	public void setDelegate( ModbusDataBufferDelegate mdbDelegate ){
		bufferDelegate = mdbDelegate;
	}
	
	public abstract Object getValue();
	
	public abstract void setValue(Object value);
	
	public String getScope() {
		return dataScope;
	}
	
	public Object getIdentification() {
		return (Object) dataIdentification;
	}
	
	public int getAdress() {
		return adress;
	}
	
	
}
