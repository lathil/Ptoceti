package com.ptoceti.osgi.dfrobot.sensornode.impl;

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

	public static final String MeasurementsElement = "Measurements";
	public static final String MeasurementElement = "Measurement";
	public static final String MeasurementIdElement = "MeasurementId";
	public static final String IdentificationElement = "Identification";
	public static final String ScopeElement = "Scope";
	public static final String UnitElement = "Unit";
	public static final String ScalingElement = "Scale";
	public static final String OffsetElement = "Offset";
	
	/**
	 * A url to the configFile
	 */
	URL configUrl = null;

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
	
	public SensorData[] initialiseDataFromConfigFile() throws XmlPullParserException, IOException{
		SensorData[] sensorNodes = null;
		
		if (configUrl != null) {
			InputStream configFileStream = configUrl.openStream();
			
			sensorNodes = parse(configFileStream);
			configFileStream.close();
		}
		
		return sensorNodes;
	}
	
	private SensorData[] parse(InputStream configFileStream) throws XmlPullParserException, IOException{
		
		SensorData[] sensorNodes = null;
		KXmlParser parser = new KXmlParser();
		
		// We set to null the encoding type. The parser should then dected it from the file stream.
		parser.setInput(configFileStream, null);
		
		int eventType = parser.getEventType();
		while( eventType != XmlPullParser.END_DOCUMENT) {
		
			if( eventType == XmlPullParser.START_TAG){
				if( parser.getName().equals( MeasurementsElement)) {
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
	
	private SensorData[] parseMeasurementsElement(KXmlParser parser) throws XmlPullParserException, IOException {
		
		List<SensorData> sensorDatas = new ArrayList<SensorData>();
		
		int eventType = parser.getEventType();
		while( eventType != XmlPullParser.END_TAG) {
			
			if(eventType == XmlPullParser.START_TAG) {
				if (parser.getName().equals(MeasurementElement)) {
					sensorDatas.add(this.parseMeasurementElement(parser));
				}
			}
			
			eventType = parser.next();
		}
		
		return sensorDatas.toArray(new SensorData[sensorDatas.size()]);
	}
	
	private SensorData parseMeasurementElement(KXmlParser parser) throws XmlPullParserException, IOException {
		
		SensorData sensorData = new SensorData();
		
		int eventType = parser.getEventType();
		while( eventType != XmlPullParser.END_TAG) {
			
			if(eventType == XmlPullParser.START_TAG) {
				if (parser.getName().equals(MeasurementIdElement)) {
					sensorData.setId(Integer.valueOf(parseGetText(parser)));
				} else if (parser.getName().equals(IdentificationElement)) {
					sensorData.setIdentification(parseGetText(parser));
				} else if (parser.getName().equals(ScopeElement)) {
					sensorData.setScope(parseGetText(parser));
				} else if (parser.getName().equals(UnitElement)) {
					sensorData.setUnit(parseGetText(parser));
				} else if (parser.getName().equals(ScalingElement)) {
					sensorData.setScale(Integer.valueOf(parseGetText(parser)));
				} else if (parser.getName().equals(OffsetElement)) {
					sensorData.setOffset(Integer.valueOf(parseGetText(parser)));	
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
