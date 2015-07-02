package com.ptoceti.osgi.modbusdevice.impl;

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
