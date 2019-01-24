

package com.ptoceti.osgi.modbus.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Modbus
 * FILENAME : ModbusMaster.java
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

import com.ptoceti.osgi.modbus.ModbusDriver;
import com.ptoceti.osgi.modbus.impl.connection.ModbusSerialASCIIConnection;
import com.ptoceti.osgi.modbus.impl.connection.ModbusSerialRTUConnection;
import com.ptoceti.osgi.modbus.impl.message.*;

/**
 * The ModbusMaster class embodied a Modbus device that is a master on the serial bus. A master device act as a client
 * as it send request messages to slave devices ( server like ) that must respond. This master class provides ways to
 * send six different types of request messages to other slave devices on the bus. Theses messages are about getting
 * information on coils and inputs digital states, as well as register values. They also be used for forcing coils on
 * and off, or writing register parameters values. No diagnostic or file record access functions ar implemented at the
 * moment.
 *
 * @author Laurent Thil
 * @version 1.0b
 */
 
public class ModbusMaster extends ModbusDriverImpl {

	/**
	 * Initialise the class by setting the serial connection to the Modbus serial bus. If setting up the connection encounter
	 * a problem, an exception is issued.
	 *
	 * @param id: ID of the device on the modbus bus.
	 * @param portName: name of the serial port to use to access the serial port, eg "/dev/tty01"
	 * @param encoding: style of encoding to use on the bus: either ascii or rtu.
	 * @param baudRate: baud rate to used on the serial link: 9600 to 19200.
	 * @param usesParity: state whether to send the parity bit on the serial link.
	 * @param evenParity: state whether to use even or odd parity.
	 * @param echo: true if need to check for an echo
	 * @exception Exception is thrown if probmems creating the serial connection.
	 */
	public ModbusMaster( int id, String portName, String encoding, int baudRate, boolean usesParity, boolean evenParity, boolean echo ) throws Exception {
	
		this.setID((byte)id);

		if( encoding.equals(ModbusDriver.RTU_ENCODING))
			mdbConnection = new ModbusSerialRTUConnection( portName, baudRate, usesParity, evenParity, echo );
		else if (encoding.equals(ModbusDriver.ASCII_ENCODING ))
			mdbConnection = new ModbusSerialASCIIConnection( portName, baudRate, usesParity, evenParity, echo );
	}
		
	/**
	 * Return whether this ModbusDriver is master or not.
	 *
	 * @return true if master, false otherwise.
	 */
	public boolean isMaster() {
		return true;
	}
	
	/**
	 * Return whether this ModbusDriver is slave or not.
	 *
	 * @return true if slave, false otherwise.
	 */
	public boolean isSlave() {
		return false;
	}
	
	/**
	 * Return the port's name that is used for the modbus connection.
	 */
	public String getPortName() {
		
		String portName = null;
		
		if( mdbConnection != null){
			portName = mdbConnection.getPortName();
		}
		
		return portName;
	}
	
	public byte[] readCoilsStatus( byte unitID, int coilID, int nbCoils ) {
	
		ReadCoilStatusMessageRequest request =
			(ReadCoilStatusMessageRequest) ModbusMessageRequest.createMessage( unitID, ModbusMessage.READ_COIL_STATUS );
		
		request.setFirstCoil( coilID );
		request.setNbCoils( nbCoils );
		
		ReadCoilStatusMessageResponse response = (ReadCoilStatusMessageResponse) sendMessage( request );
		if( response != null ) {
			return response.getValues();
		}
		else return null;
	}
	
	public byte[] readInputStatus( byte unitID, int inputStatID, int nbInputsStats ) {
	
		ReadInputStatusMessageRequest request =
			( ReadInputStatusMessageRequest ) ModbusMessageRequest.createMessage(unitID, ModbusMessage.READ_INPUT_STATUS );
		
		request.setFirstInput( inputStatID );
		request.setNbInputs( nbInputsStats );
		
		ReadInputStatusMessageResponse response = (ReadInputStatusMessageResponse) sendMessage( request );
		if( response != null ) {
			return response.getValues();
		}
		else return null;
	}
	
	public int[] readHoldingRegisters( byte unitID, int holdingRegID, int nbHoldingReg ) {
	
		ReadHoldingRegistersMessageRequest request =
			( ReadHoldingRegistersMessageRequest ) ModbusMessageRequest.createMessage(unitID, ModbusMessage.READ_HOLDING_REGISTERS );
		
		request.setFirstRegister(holdingRegID);
		request.setNbRegisters(nbHoldingReg);
		
		ReadHoldingRegistersMessageResponse response = (ReadHoldingRegistersMessageResponse) sendMessage( request );
		if( response != null ) {
			return response.getValues();
		}
		else return null;
	}
	
	public int[] readInputRegisters( byte unitID, int inputRegID, int nbInputReg ) {
	
		ReadInputRegistersMessageRequest request =
			( ReadInputRegistersMessageRequest ) ModbusMessageRequest.createMessage(unitID, ModbusMessage.READ_INPUT_REGISTERS );
		
		request.setFirstRegister(inputRegID);
		request.setNbRegisters(nbInputReg);
		
		ReadInputRegistersMessageResponse response = (ReadInputRegistersMessageResponse) sendMessage( request );
		if( response != null ) {
			return response.getValues();
		}
		else return null;
	}
	
	public boolean forceSingleCoil( byte unitID, int coilID, boolean value ) {
	
		ForceSingleCoilMessageRequest request =
			( ForceSingleCoilMessageRequest ) ModbusMessageRequest.createMessage(unitID, ModbusMessage.FORCE_SINGLE_COIL );
		
		request.setCoilAdress( coilID);
		request.setValue( value);
		
		ForceSingleCoilMessageResponse response = (ForceSingleCoilMessageResponse) sendMessage( request );
		if( response != null ) {
			if((response.getCoilAdress() == coilID ) && (response.getValue() == value ))
				return true;
			else
				return false;
		}
		else
			return false;
			
	}
	
	public boolean forceSingleRegister( byte unitID, int registerID, int value ) {
	
		ForceSingleRegisterMessageRequest request =
			( ForceSingleRegisterMessageRequest ) ModbusMessageRequest.createMessage(unitID, ModbusMessage.FORCE_SINGLE_REGISTER );
		
		request.setRegisterAdress( registerID);
		request.setValue( value);
		
		ForceSingleRegisterMessageResponse response = (ForceSingleRegisterMessageResponse) sendMessage( request );
		if( response != null ) {
			if((response.getRegisterAdress() == registerID ) && (response.getValue() == value ))
				return true;
			else
				return false;
		}
		else
			return false;
			
	}
}

