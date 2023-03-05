

package com.ptoceti.osgi.modbus;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Modbus
 * FILENAME : ModbusDriver.java
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

public interface ModbusDriver {

	public static final String RTU_ENCODING = "rtu_encoding";
	public static final String ASCII_ENCODING = "ascii_encoding";
	public static final String MASTER = "master";
	public static final String SLAVE = "slave";

    public static final String MODBUS_ENCODING = "com.ptoceti.osgi.modbusdriver.encoding";
    public static final String MODBUS_PORT = "com.ptoceti.osgi.modbusdriver.port";
    public static final String MODBUS_SLAVE_MASTER = "com.ptoceti.osgi.modbusdriver.slave.master";
    public static final String MODBUS_ID = "com.ptoceti.osgi.modbusdriver.id";
    public static final String MODBUS_BAUDRATE = "com.ptoceti.osgi.modbusdriver.baudrate";
    public static final String MODBUS_USESPARITY = "com.ptoceti.osgi.modbusdriver.usesparity";
    public static final String MODBUS_EVENPARITY = "com.ptoceti.osgi.modbusdriver.evenparity";
    public static final String MODBUS_DRIVER_SERVICE_PORT = "com.ptoceti.osgi.modbusdriver.port";
    public static final String MODBUS_ECHO = "com.ptoceti.osgi.modbusdriver.echo";

    public static final String MODBUS_DRIVER_NAME = "Modbus";

    public boolean isMaster();

    public boolean isSlave();

    public byte getID();

    public String getPortName();

    public void addListener(ModbusDriverListener listener);

    public void removeListener(ModbusDriverListener listener);

    public byte[] readCoilsStatus(byte unitID, int coilID, int nbCoils);

    public byte[] readInputStatus(byte unitID, int inputStatID, int nbInputsStats);

    public int[] readHoldingRegisters(byte unitID, int holdingRegID, int nbHoldingReg);

    public int[] readInputRegisters(byte unitID, int inputRegID, int bnInputReg);

    public boolean forceSingleCoil(byte unitID, int coilID, boolean value);

    public boolean forceSingleRegister(byte unitID, int registerID, int value);

}
