

package com.ptoceti.osgi.modbus.impl.message;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Modbus
 * FILENAME : ModbusMessageImpl.java
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


public abstract class ModbusMessageImpl implements ModbusMessage {

	private byte functionID;
	private byte unitID;
	
	public void setFunctionID( byte functID) {
		functionID = functID;
	}
	
	public byte getFunctionID(){
		return functionID;
	}
	
	public void setUnitID( byte id ) {
		unitID = id;
	}
	
	public byte getUnitID() {
		return unitID;
	}
	
	public abstract int getMessageLength();
	
}
