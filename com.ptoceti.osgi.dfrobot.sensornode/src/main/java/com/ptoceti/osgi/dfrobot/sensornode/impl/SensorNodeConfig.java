package com.ptoceti.osgi.dfrobot.sensornode.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : SensorNode
 * FILENAME : SensorNodeConfig.java
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


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.kxml2.io.KXmlParser;
import org.osgi.service.log.LogService;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


public class SensorNodeConfig {

	public static final String ContentDatasElement = "ContentDatas";
	public static final String ContentDataElement = "ContentData";
	public static final String DataIdElement = "DataId";
	public static final String IdentificationElement = "Identification";
	public static final String ScopeElement = "Scope";
	public static final String UnitElement = "Unit";
	public static final String ScalingElement = "Scale";
	public static final String OffsetElement = "Offset";
	
	/**
	 * A url to the configFile
	 */
	URL configUrl = null;

	public SensorNodeConfig(URL configFileUrl){
		configUrl = configFileUrl;
	}
	
	public SensorNodeConfig(String configFilePath) {

		if (configFilePath.startsWith("file:")) {
			configFilePath = configFilePath.substring("file:".length());

			File file = new File(configFilePath);
			if (file.exists() && !file.isDirectory()) {
				try {
					configUrl = file.toURI().toURL();
				} catch (MalformedURLException e) {
					Activator.log(LogService.LOG_ERROR,"Error creating url for file path: " + configFilePath);
				}
			} else {
				Activator.log( LogService.LOG_ERROR, "Error reading modbusdevice file at: " + file.getAbsolutePath());
			}
		} else {
			configUrl = Activator.getResourceStream(configFilePath);
		}

	}
	
	public List<SensorData> initialiseDataFromConfigFile() throws XmlPullParserException, IOException{
		List<SensorData> sensorNodes = null;
		
		if (configUrl != null) {
			InputStream configFileStream = configUrl.openStream();
			
			sensorNodes = parse(configFileStream);
			configFileStream.close();
		}
		
		return sensorNodes;
	}
	
	private List<SensorData> parse(InputStream configFileStream) throws XmlPullParserException, IOException{
		
		List<SensorData> sensorNodes = null;
		KXmlParser parser = new KXmlParser();
		
		// We set to null the encoding type. The parser should then dected it from the file stream.
		parser.setInput(configFileStream, null);
		
		int eventType = parser.getEventType();
		while( eventType != XmlPullParser.END_DOCUMENT) {
		
			if( eventType == XmlPullParser.START_TAG){
				if( parser.getName().equals( ContentDatasElement)) {
					// We move to the next element inside the Wires element
					parser.next();
					sensorNodes = parseMeasurementsElement(parser);
					break;
				}
			}
			
			eventType = parser.next();
		}
		
		
		return sensorNodes;
	}
	
	private List<SensorData> parseMeasurementsElement(KXmlParser parser) throws XmlPullParserException, IOException {
		
		List<SensorData> sensorDatas = new ArrayList<SensorData>();
		
		int eventType = parser.getEventType();
		while( eventType != XmlPullParser.END_TAG) {
			
			if(eventType == XmlPullParser.START_TAG) {
				if (parser.getName().equals(ContentDataElement)) {
					sensorDatas.add(this.parseMeasurementElement(parser));
				}
			}
			
			eventType = parser.next();
		}
		
		return sensorDatas;
	}
	
	private SensorData parseMeasurementElement(KXmlParser parser) throws XmlPullParserException, IOException {
		
		SensorData sensorData = new SensorData();
		
		int eventType = parser.getEventType();
		while( eventType != XmlPullParser.END_TAG) {
			
			if(eventType == XmlPullParser.START_TAG) {
				if (parser.getName().equals(DataIdElement)) {
					sensorData.setId(Integer.valueOf(parseGetText(parser)));
				} else if (parser.getName().equals(IdentificationElement)) {
					sensorData.setIdentification(parseGetText(parser));
				} else if (parser.getName().equals(ScopeElement)) {
					sensorData.setScope(parseGetText(parser));
				} else if (parser.getName().equals(UnitElement)) {
					sensorData.setUnit(parseGetText(parser));
				} else if (parser.getName().equals(ScalingElement)) {
					sensorData.setScale(Double.valueOf(parseGetText(parser)));
				} else if (parser.getName().equals(OffsetElement)) {
					sensorData.setOffset(Double.valueOf(parseGetText(parser)));	
				}
			}
			
			eventType = parser.next();
		}
		
		return sensorData;
	}
	
	private String parseGetText(KXmlParser parser) throws XmlPullParserException, IOException {
		
		int eventType = parser.getEventType();
		String text = null;
		
		eventType = parser.next();
		while( eventType != XmlPullParser.END_TAG) {
			
			if( eventType == XmlPullParser.TEXT) {
				text = parser.getText();
				if(text.trim().length() == 0) text = null;
			} else if( eventType == XmlPullParser.START_TAG) {
				// We skip this subtree
				parser.skipSubTree();
				// SkipSubTree position us on the corresponding end tag
			}
			eventType = parser.next();
		}
		
		return text;
	}
}
