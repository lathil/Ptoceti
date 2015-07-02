package com.ptoceti.osgi.pi.impl;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

public abstract class AbstractPin implements PiPin {

	PinConfig config;

	Pin getPinInstance(){
		Pin result = null;
		switch(config.getPinNumber().intValue()){
			case 0:
				result = RaspiPin.GPIO_00;
				break;
			case 1:
				result = RaspiPin.GPIO_01;
				break;
			case 2:
				result = RaspiPin.GPIO_02;
				break;
			case 3:
				result = RaspiPin.GPIO_03;
				break;
			case 4:
				result = RaspiPin.GPIO_04;
				break;
			case 5:
				result = RaspiPin.GPIO_05;
				break;
			case 6:
				result = RaspiPin.GPIO_06;
				break;
			case 7:
				result = RaspiPin.GPIO_07;
				break;
			case 8:
				result = RaspiPin.GPIO_08;
				break;
			case 9:
				result = RaspiPin.GPIO_09;
				break;
			case 10:
				result = RaspiPin.GPIO_10;
				break;
			case 11:
				result = RaspiPin.GPIO_11;
				break;
			case 12:
				result = RaspiPin.GPIO_12;
				break;
			case 13:
				result = RaspiPin.GPIO_13;
				break;
			case 14:
				result = RaspiPin.GPIO_14;
				break;
			case 15:
				result = RaspiPin.GPIO_15;
				break;
			case 16:
				result = RaspiPin.GPIO_16;
				break;
			case 17:
				result = RaspiPin.GPIO_17;
				break;
			case 18:
				result = RaspiPin.GPIO_18;
				break;
			case 19:
				result = RaspiPin.GPIO_19;
				break;
			case 20:
				result = RaspiPin.GPIO_20;
				break;
		}
		
		return result;
	}
	
	public String getIdentification() {
		return config.getIdentification();
	}
	
	public String getScope(){
		return config.getScope();
	}
}
