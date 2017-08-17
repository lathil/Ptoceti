package com.ptoceti.osgi.pi.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Pi
 * FILENAME : PinDigitalIn.java
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

import java.util.Calendar;

import org.osgi.service.wireadmin.BasicEnvelope;
import org.osgi.service.wireadmin.Envelope;
import org.osgi.util.measurement.State;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;


public class PinDigitalIn extends AbstractPin implements GpioPinListenerDigital{

	private GpioPinDigitalInput pin;
	
	private PiService service;
	
	public PinDigitalIn(PinConfig config, GpioController gpio, PiService service){
		this.config = config;
		pin = gpio.provisionDigitalInputPin(getPinInstance());
		pin.addListener(this);
		this.service = service;
	}
	
	public void stop(GpioController gpio ) {
		pin.removeListener(this);
		gpio.unprovisionPin(pin);
	}

	@Override
	public void handleGpioPinDigitalStateChangeEvent(
			GpioPinDigitalStateChangeEvent event) {
		service.pushPinValues(this);
	}

	public boolean directionIn() {
		return true;
	}
	
	public Envelope getValue() {
		State state = new State(pin.getState().getValue(), pin.getName(), Calendar.getInstance().getTimeInMillis());
		return new BasicEnvelope(state, getIdentification(), config.getScope());
	}

	@Override
	public void setValue(Envelope envelope) {
		
	}

}
