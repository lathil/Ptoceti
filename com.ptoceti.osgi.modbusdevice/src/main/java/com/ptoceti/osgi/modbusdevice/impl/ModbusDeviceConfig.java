package com.ptoceti.osgi.modbusdevice.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : ModbusDevice
 * FILENAME : ModbusDeviceConfig.java
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


import org.kxml2.io.KXmlParser;
import org.osgi.service.log.LogService;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParser;

import com.ptoceti.osgi.modbusdevice.impl.Activator;
import com.ptoceti.osgi.modbusdevice.impl.ModbusDeviceFactory;
import com.ptoceti.osgi.modbusdevice.impl.ModbusDeviceImpl;
import com.ptoceti.osgi.modbusdevice.impl.ModbusMeasurement;
import com.ptoceti.osgi.modbusdevice.ModbusDevice;

import java.io.InputStream;
import java.io.IOException;

import java.util.ArrayList;

public class ModbusDeviceConfig {

	public static final String ModbusDeviceElement = "ModbusDevice";
	public static final String CompositeIdentityElement = "CompositeIdentity";
	public static final String PortElement = "Port";
	public static final String IdElement = "Id";
	public static final String PoolingRateElement = "PoolingRate";
	public static final String MeasurementsElement = "Measurements";
	public static final String MeasurementElement = "Measurement";
	public static final String ReferencesElement = "References";
	public static final String ReferenceElement = "Reference";
	public static final String StatesElement = "States";
	public static final String StateElement = "State";
	public static final String IdentificationElement = "Identification";
	public static final String ScopeElement = "Scope";
	public static final String ExpressionElement = "Expression";
	public static final String AdressElement = "Adress";
	public static final String LengthElement = "Length";
	
	
	private ModbusDeviceFactory factory;
	private String pid;
	private Boolean isMock;
	
	private String compositeIdentity = null;
	private String port = null;
	private Integer id = null;
	private Integer poolingRate = null;
	private ArrayList references = new ArrayList();
	private ArrayList measurements = new ArrayList();
	private ArrayList states = new ArrayList();
	
	public ModbusDeviceConfig(ModbusDeviceFactory factory, String pid, String compositeIdentity, String portName, Integer modbusId, Integer poolingRate, Boolean mock) {
		this.factory = factory;
		this.pid = pid;
		this.compositeIdentity = compositeIdentity;
		this.port = portName;
		this.id = modbusId;
		this.poolingRate = poolingRate;
		this.isMock = mock;
	}
	
	public void parse(InputStream configFileStream) throws IOException {
		
		KXmlParser parser = new KXmlParser();
		try {
			// We set to null the encoding type. The parser should then dected it from the file stream.
			parser.setInput(configFileStream, null);
			
			int eventType = parser.getEventType();
			while( eventType != XmlPullParser.END_DOCUMENT) {
			
				if( eventType == XmlPullParser.START_TAG){
					if( parser.getName().equals( ModbusDeviceElement)) {
						// We move to the next element inside the Wires element
						parser.next();
						parseModbusDeviceElement(parser);
						break;
					}
				}
				
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			Activator.log(LogService.LOG_INFO, "ModbusDeviceConfig reader, problem while reading the configuration: " + e.toString());
		}
	}
		
	public void parseModbusDeviceElement(KXmlParser parser) throws XmlPullParserException, IOException {
			
		int eventType = parser.getEventType();
		while( eventType != XmlPullParser.END_TAG) {
			
			if(eventType == XmlPullParser.START_TAG) {
				if (parser.getName().equals(ReferencesElement)) {
					this.parseReferencesElement(parser);
				} else if (parser.getName().equals(MeasurementsElement)) {
					this.parseMeasurementsElement(parser);
				} else if (parser.getName().equals(StatesElement)) {
					this.parseStatesElement(parser);
				}
			}
			
			eventType = parser.next();
		}
		
		// Finaly, if we have found all properties necessary, 
		if(( port != null) & (id != null) & (compositeIdentity != null) & (poolingRate != null)) {
			
			try {
				ModbusDevice newDevice = null;
				if( isMock.booleanValue()) {
					newDevice = new ModbusDeviceMockImpl(this.pid, compositeIdentity, port, id.intValue(), poolingRate.intValue(), references,measurements, states);
				} else {
				//... then whe can create a new modbus device
					newDevice = new ModbusDeviceImpl(this.pid, compositeIdentity, port, id.intValue(), poolingRate.intValue(), references,measurements, states);
				}
				// .. and add it to the factory (id creation went ok.
				factory.add(pid, newDevice);
			} catch ( Exception ex) {
				
			}
		}
	}
	
private void parseReferencesElement(KXmlParser parser) throws XmlPullParserException, IOException {

	int eventType = parser.next();
	while( eventType != XmlPullParser.END_TAG) {
		
		if(eventType == XmlPullParser.START_TAG) {
			if(parser.getName().equals( ReferenceElement)) {
				ModbusReference mdbRef= parseReferenceElement(parser);
				if( mdbRef != null) {
					references.add(mdbRef);
				}
			}
		}
		eventType = parser.next();
	}

}

private ModbusReference parseReferenceElement(KXmlParser parser ) throws XmlPullParserException, IOException {
		
		ModbusReference mdbReference = null;
		
		String identification = null;
		String scope = null;
		String expression = null;
		int adress = 0;
		int length = 0;
		
		int eventType = parser.getEventType();
		while( eventType != XmlPullParser.END_TAG) {
			
			if(eventType == XmlPullParser.START_TAG) {
				if(parser.getName().equals( IdentificationElement)) {
					identification = parseGetText(parser);
				} else if (parser.getName().equals(ScopeElement)) {
					scope = parseGetText(parser);
				} else if (parser.getName().equals(ExpressionElement)) {
					expression = parseGetText(parser);
				} else if (parser.getName().equals(AdressElement)) {
					adress = (new Integer(parseGetText(parser))).intValue();
				} else if (parser.getName().equals(LengthElement)) {
					length = (new Integer(parseGetText(parser))).intValue();
				}
			}
		
			eventType = parser.next();
		}
		
		if(( identification != null) && ( scope != null) && (length > 0)){
			mdbReference = new ModbusReference(identification, scope, expression, adress, length);
		}
		return mdbReference;
	}


	private void parseMeasurementsElement(KXmlParser parser) throws XmlPullParserException, IOException {
	
		int eventType = parser.next();
		while( eventType != XmlPullParser.END_TAG) {
			
			if(eventType == XmlPullParser.START_TAG) {
				if(parser.getName().equals( MeasurementElement)) {
					ModbusMeasurement mdbMsr= parseMeasurementElement(parser);
					if( mdbMsr != null) {
						measurements.add(mdbMsr);
					}
				}
			}
			eventType = parser.next();
		}
	}
	
	private ModbusMeasurement parseMeasurementElement(KXmlParser parser ) throws XmlPullParserException, IOException {
		
		ModbusMeasurement mdbMeasurement = null;
		
		String identification = null;
		String scope = null;
		String expression = null;
		int adress = 0;
		int length = 0;
		
		int eventType = parser.getEventType();
		while( eventType != XmlPullParser.END_TAG) {
			
			if(eventType == XmlPullParser.START_TAG) {
				if(parser.getName().equals( IdentificationElement)) {
					identification = parseGetText(parser);
				} else if (parser.getName().equals(ScopeElement)) {
					scope = parseGetText(parser);
				} else if (parser.getName().equals(ExpressionElement)) {
					expression = parseGetText(parser);
				} else if (parser.getName().equals(AdressElement)) {
					adress = (new Integer(parseGetText(parser))).intValue();
				} else if (parser.getName().equals(LengthElement)) {
					length = (new Integer(parseGetText(parser))).intValue();
				}
			}
		
			eventType = parser.next();
		}
		
		if(( identification != null) && ( scope != null) && (length > 0)){
			mdbMeasurement = new ModbusMeasurement(identification, scope, expression, adress, length);
		}
		return mdbMeasurement;
	}

	private void parseStatesElement(KXmlParser parser) throws XmlPullParserException, IOException {
		
		int eventType = parser.next();
		while( eventType != XmlPullParser.END_TAG) {
			
			if(eventType == XmlPullParser.START_TAG) {
				if(parser.getName().equals( StateElement)) {
					ModbusState mdbSt= parseStateElement(parser);
					if( mdbSt != null) {
						states.add(mdbSt);
					}
				}
			}
			eventType = parser.next();
		}
	}
	
	private ModbusState parseStateElement(KXmlParser parser ) throws XmlPullParserException, IOException {
		
		ModbusState mdbState = null;
		
		String identification = null;
		String scope = null;
		int adress = 0;
		int length = 0;
		
		int eventType = parser.getEventType();
		while( eventType != XmlPullParser.END_TAG) {
			
			if(eventType == XmlPullParser.START_TAG) {
				if(parser.getName().equals( IdentificationElement)) {
					identification = parseGetText(parser);
				} else if (parser.getName().equals(ScopeElement)) {
					scope = parseGetText(parser);
				} else if (parser.getName().equals(AdressElement)) {
					adress = (new Integer(parseGetText(parser))).intValue();
				} else if (parser.getName().equals(LengthElement)) {
					length = (new Integer(parseGetText(parser))).intValue();
				}
			}
		
			eventType = parser.next();
		}
		
		if(( identification != null) && ( scope != null) && (length > 0)){
			mdbState = new ModbusState(identification, scope, adress, length);
		}
		
		return mdbState;
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
