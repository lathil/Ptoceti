package com.ptoceti.osgi.pi.impl;

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
