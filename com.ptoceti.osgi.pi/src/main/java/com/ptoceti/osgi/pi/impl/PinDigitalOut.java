package com.ptoceti.osgi.pi.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Pi
 * FILENAME : PinDigitalOut.java
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


import org.osgi.service.wireadmin.BasicEnvelope;
import org.osgi.service.wireadmin.Envelope;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;

import com.ptoceti.osgi.control.Switch;

public class PinDigitalOut extends AbstractPin {

	private GpioPinDigitalOutput pin;
	
	public PinDigitalOut(PinConfig config, GpioController gpio){
		this.config = config;
		pin = gpio.provisionDigitalOutputPin(getPinInstance(), PinState.LOW);
		
		
	}
	
	public void stop(GpioController gpio ) {
		gpio.unprovisionPin(pin);
	}

	public boolean directionIn() {
		return false;
	}
	
	public Envelope getValue() {
		Switch state = new Switch(pin.getState().getValue() > 0 ? true : false, pin.getName());
		return new BasicEnvelope(state, getIdentification(), config.getScope());
	}
	
	public void setValue(Envelope value) {
		pin.setState( ((Switch) (value.getValue())).getState() ? true : false);
	}

}
