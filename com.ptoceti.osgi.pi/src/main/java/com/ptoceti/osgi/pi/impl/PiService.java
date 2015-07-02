package com.ptoceti.osgi.pi.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Pi
 * FILENAME : PiService.java
 * 
 * This file is part of the Ptoceti project. More information about
 * this project can be found here: http://www.ptoceti.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2013 - 2014 ptoceti
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


import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.log.LogService;
import org.xmlpull.v1.XmlPullParserException;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
//import com.ptoceti.osgi.control.Command;
import com.ptoceti.osgi.pi.impl.Activator;

/**
 * Simple service that expose Pi's pin ans system infos to the wire handler.
 * Configuration for the pins to read or write to is taken from an external file.
 * 
 * The service is exposed as a managed service so that it can take configuration from the configuration admin.
 * 
 * @author lor
 *
 */
public class PiService implements ManagedService {

	/**
	 * refresh rate at which the system infos is push to the wire handler.
	 */
	public static final String CONFIGURATION_KEY_REFRESHRATE= "com.ptoceti.osgi.pi.refreshRate";
	/**
	 * configuration that indicate the location of the ins configuration file.
	 */
	public static final String CONFIGURATION_KEY_PINSCONFIGFILE = "com.ptoceti.osgi.pi.pinsConfigFile";
	
	/**
	 * The list of pins, when builded and configured.
	 */
	private List<PiPin> pins = new ArrayList<PiPin>();
	
	/**
	 * The instance of the gpio controller for this service.
	 */
	private GpioController gpioController;
	/**
	 * The wire handler instance that manage pushing and updating the wires.
	 */
	private WireHandler wireHandler = null;
		
	/**
	 * PiService creator. Record itself as a managed service.
	 * 
	 */
	public PiService() {
		
		gpioController = GpioFactory.getInstance();

		String[] clazzes = new String[] { ManagedService.class.getName() };
		// register the class as a managed service.
		Hashtable<String, Object> properties = new Hashtable<String,Object>();
		properties.put(Constants.SERVICE_PID, this.getClass().getName());

		Activator.bc.registerService(clazzes, this, properties);
		
		Activator.log(LogService.LOG_INFO, "Registered "
				+ this.getClass().getName() +  ", Pid = "
				+ (String) properties.get(Constants.SERVICE_PID));
	}
	
	/**
	 * Stop the service. Stop the wire handler, release the pins and the gpio controller.
	 */
	public void stop() {
		if( wireHandler != null) wireHandler.stop();
		releasePins();
		gpioController.shutdown();
	}
	
	
	/**
	 * From ManagedService interface. Expect service configuration to be passed here.
	 */
	@Override
	public void updated(Dictionary properties) throws ConfigurationException {
		
		Activator.log(LogService.LOG_DEBUG, "Configuration update.");
		
		if( properties != null ) {
			if( wireHandler != null){
				wireHandler.stop();
				wireHandler = null;
			}
			
			Integer newRefreshRate = null;
			
			if( properties.get(CONFIGURATION_KEY_REFRESHRATE) != null ) {
				Object rate = properties.get(CONFIGURATION_KEY_REFRESHRATE);
				newRefreshRate = rate instanceof Integer ? (Integer) rate : Integer.parseInt(rate.toString());

				Activator.log(LogService.LOG_DEBUG, "Refresh Thread configured at intervals of : "+ newRefreshRate.toString() + " ms.");
			}
			
			if( properties.get(CONFIGURATION_KEY_PINSCONFIGFILE) != null) {
				
				releasePins();
	
				ConfigReader configReader = new ConfigReader((String)properties.get(CONFIGURATION_KEY_PINSCONFIGFILE));
				try {
					List<PinConfig> pinsConfig = configReader.initialiseDataFromConfigFile();
					pins = buildPins(pinsConfig);
				} catch (XmlPullParserException e) {
					Activator.log(LogService.LOG_ERROR, "Error parsing xml config file: " + e.toString());
				} catch (IOException e) {
					Activator.log(LogService.LOG_ERROR, "Error reading xml config file: " + e.toString());
				}
			}
			
			wireHandler = new WireHandler(newRefreshRate, pins);
		}
	}
	
	
	/**
	 * Build a list of pins object that matches the configuration
	 * @param pinsConfig
	 * @return
	 */
	List<PiPin> buildPins(List<PinConfig> pinsConfig ){
		List<PiPin> result = new ArrayList<PiPin>();
		for( PinConfig config: pinsConfig){
			if( config.isDigital()){
				if( config.isDirectionIn()){
					PiPin pin = new PinDigitalIn(config,gpioController,this);
					result.add(pin);
				} else {
					PiPin pin = new PinDigitalOut(config, gpioController);
					result.add(pin);
				}
			}
		}
		return result;
	}
	
	
	/**
	 * Release pins resources taken by each.
	 */
	protected void releasePins(){
		for(PiPin pin : pins){
			pin.stop(gpioController);
		}
	}
	
	/**
	 * Redirect wire value update from pins to wirehandler.
	 * 
	 * @param pin
	 */
	protected void pushPinValues(PiPin pin) {
		if( wireHandler != null){
			wireHandler.pushPinValues(pin);
		}
	}
	
	
	
}
