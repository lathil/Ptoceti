package com.ptoceti.osgi.pi.impl;


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
