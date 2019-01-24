

package com.ptoceti.osgi.modbusdevice;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : ModbusDevice
 * FILENAME : ModbusDevice.java
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

import org.osgi.service.wireadmin.Producer;
import org.osgi.service.wireadmin.Consumer;

public interface ModbusDevice extends Producer, Consumer{
	
	/**
	 * Return the id of the device in the Modbus bus link
	 * @return the id of this device
	 */
	public int getId();
	
	/**
	 * Return the serial link port used by the device.
	 * @return String the name of the port for this device
	 */
	public String getPortName();
	
	public void stop();

}
