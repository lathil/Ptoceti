package com.ptoceti.osgi.dfrobot.sensornode.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : SensorNode
 * FILENAME : SensorNodeFactory.java
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


import java.io.IOException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.log.LogService;
import org.xmlpull.v1.XmlPullParserException;



/**
 * A factory responsible to build all sensor node based on configuration
 * 
 * @author LATHIL
 *
 */
public class SensorNodeFactory implements org.osgi.service.cm.ManagedServiceFactory {

	/**
	 * the hashtable contain the references to all SensorNode instances created.
	 */
	Hashtable<String, SensorNode> sensorNodes;
	/**
	 *  a reference to the service registration for the SensorNodeFactory.
	 */
	ServiceRegistration sensorNodesFactoryReg = null;
	/**
	 * The default config for data entries that are not configurable/
	 */
	public static final String STANDARD_CONFIG_FILE = "/config/sensordevice.xml";
	

	/**
	 * Register the class instance as a ManagedServiceFactory.
	 */
	public SensorNodeFactory() {
		// create a new hastable that will contain references to all the sensorNode services.
		sensorNodes = new Hashtable<String, SensorNode>();
		// register the class as a service factory.
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put( Constants.SERVICE_PID, this.getClass().getName());
		sensorNodesFactoryReg = Activator.bc.registerService(ManagedServiceFactory.class.getName(),this, properties );
		
		Activator.log(LogService.LOG_INFO, "Registered " + SensorNodeFactory.class.getName()
			+ " as " + ManagedServiceFactory.class.getName());
	}
	
	/**
	 * Called when the main bundles is stopped
	 */
	public void stop(){
		
		for(SensorNode node : sensorNodes.values()){
			node.stop();
			sensorNodes.remove(node);
		}
		
	}
	
	/**
	 * Called by the framework configuration admin with configuration required to build a new SensorNoder.
	 * 
	 * @param pid of the configuration under which to create the node
	 * @param properties the properties from the configuration store.
	 * 
	 */
	public void updated(String pid, Dictionary properties)
			throws ConfigurationException {

		Integer sensorNodeId = Integer.valueOf((String)properties.get(SensorNode.SENSORNODE_ID));
		String sensorPort = (String)properties.get(SensorNode.SENSORNODE_PORT);
		String sensorCompositIdentity = (String)properties.get(SensorNode.SENSORNODE_COMPOSITE_IDENTITY);
		Object rate = properties.get(SensorNode.SENSORNODE_POOLING_RATE);
		Integer sensorPoolinRate = rate instanceof Integer ? (Integer) rate : Integer.parseInt(rate.toString());
		String sensorNodeConfigFile = (String)properties.get(SensorNode. SENSORNODE_MEASUREMENT_CONFIGFILE);
		
		// configuration is necessary ..
		if( sensorNodeId != null && sensorPort != null && sensorCompositIdentity != null) {
		
			SensorNode sensorNode = sensorNodes.get(pid);
			if( sensorNode != null){
				sensorNode.stop();
				sensorNodes.remove(pid);
			}
			
			try {
				// Load defaut config for inputs that are not optionals
				URL defaultConfigurl = Activator.bc.getBundle().getEntry(STANDARD_CONFIG_FILE);
				List<SensorData> defaultSensorDatas = ( new SensorNodeConfig(defaultConfigurl)).initialiseDataFromConfigFile();
				// add user defined configurations
				if( sensorNodeConfigFile != null && sensorNodeConfigFile.length() > 0){
					List<SensorData> extraSensorDatas = ( new SensorNodeConfig(sensorNodeConfigFile)).initialiseDataFromConfigFile();
					for( SensorData data : extraSensorDatas){
						if( data.getId() > 3 && data.getId() < 10){
							defaultSensorDatas.add(data);
						}
					}
				}
				
				sensorNode = new SensorNode( pid, sensorNodeId, sensorPort, sensorPoolinRate, sensorCompositIdentity, defaultSensorDatas.toArray(new SensorData[defaultSensorDatas.size()]));
				sensorNodes.put(pid, sensorNode);
			
			} catch (XmlPullParserException e) {
				Activator.log(LogService.LOG_ERROR, "Error parsing xml config file: " + e.toString());
			} catch (IOException e) {
				Activator.log(LogService.LOG_ERROR, "Error reading xml config file: " + e.toString());
			}
			
		}
		
	}

	/**
	 * ManagedServiceFactory Interface method
	 * Called by the framewok when one of the service instance created by
	 * the factory is removed.
	 *
	 * @param pid: the service instance persistant identificator
	 */
	public void deleted(String pid) {
		SensorNode node = sensorNodes.get(pid);
		if( node != null){
			node.stop();
			sensorNodes.remove(pid);
			Activator.log(LogService.LOG_INFO,"Removed SensorNode: " + node.getClass().getName() + ", service pid: " + pid);
		}
	}
	
	/**
	 * ManagedServiceFactory Interface method
	 *
	 * @return the name of this factory.
	 */
	public String getName() {
		
		return( this.getName());
	}

}
