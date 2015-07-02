package com.ptoceti.osgi.pi.impl;

import org.osgi.service.wireadmin.Envelope;

import com.pi4j.io.gpio.GpioController;

public interface PiPin {

	void stop(GpioController gpio);
	
	String getIdentification();
	
	String getScope();
	
	boolean directionIn();
	
	Envelope getValue();
	
	void setValue(Envelope envelope);
}
