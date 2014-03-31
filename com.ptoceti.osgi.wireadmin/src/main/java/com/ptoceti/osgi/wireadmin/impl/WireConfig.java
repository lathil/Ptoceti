package com.ptoceti.osgi.wireadmin.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : WireAdmin
 * FILENAME : WireConfig.java
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

import org.osgi.service.wireadmin.WireAdmin;
import org.osgi.service.wireadmin.WireConstants;
import org.osgi.service.wireadmin.Wire;
import org.osgi.service.log.LogService;

import org.kxml2.io.KXmlParser;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.io.IOException;

public class WireConfig {

	public static final String WireAdminElement = WireAdmin.class.getName();
	public static final String WireElement = Wire.class.getName();
	public static final String ConsumerElement = WireConstants.WIREADMIN_CONSUMER_PID;
	public static final String ProducerElement = WireConstants.WIREADMIN_PRODUCER_PID;
	public static final String ConsumerFilter = "wireadmin.consumer.consumer.filter";
	public static final String ProducerFilter = "wireadmin.consumer.producer.filter";
	
	WireAdminImpl wireAdmin;
	
	public WireConfig(WireAdminImpl wireAdminListener){
		
		wireAdmin = wireAdminListener;
	}
	
	public void parse(InputStream configFileStream) throws IOException {
		
		KXmlParser parser = new KXmlParser();
		try {
			// We set to null the encoding type. The parser should then dected it from the file stream.
			parser.setInput(configFileStream, null);
			
			int eventType = parser.getEventType();
			while( eventType != XmlPullParser.END_DOCUMENT) {
			
				if( eventType == XmlPullParser.START_TAG){
					if( parser.getName().equals( WireAdminElement)) {
						// We move to the next element inside the Wires element
						parser.next();
						parseWires(parser);
					}
				}
				
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			Activator.log(LogService.LOG_INFO, "WireConfig reader, problem while reading the configuration: " + e.toString());
		}
	}
	
	private void parseWires( KXmlParser parser) throws XmlPullParserException, IOException{
		
		int eventType = parser.getEventType();
		while( eventType != XmlPullParser.END_TAG) {
			
			if(eventType == XmlPullParser.START_TAG) {
				if(parser.getName().equals( WireElement)) {
					// We move to the next elemeent inside the Wire elemente
					parser.next();
					parseWire(parser);
				}
			}
			
			eventType = parser.next();
		}
	}
	
	private void parseWire( KXmlParser parser) throws XmlPullParserException, IOException {
		
		int eventType = parser.getEventType();
		
		String consumer = null;
		String producer = null;
		String consumerFilter = null;
		String producerFilter = null;
		
		while( eventType != XmlPullParser.END_TAG) {
			
			if(eventType == XmlPullParser.START_TAG ) {
				if(parser.getName().equals(ConsumerElement)){
					consumer = parseElement(parser);
				} else if(parser.getName().equals(ProducerElement)){
					producer = parseElement(parser);
				} else if ( parser.getName().equals(ConsumerFilter)) {
					consumerFilter = parseElement(parser);
				} else if ( parser.getName().equals(ProducerFilter)) {
					producerFilter = parseElement(parser);
				}
			}
			
			eventType = parser.next();
		}
		
		if( consumer != null & producer != null ){
			wireAdmin.createWire(producer,consumer,null);
			producer = null;
			consumer = null;
		} else {
			wireAdmin.createWire(producer, producerFilter, consumer, consumerFilter, null);
		}
	}
	
	private String parseElement(KXmlParser parser) throws XmlPullParserException, IOException {
		
		int eventType = parser.getEventType();
		String element = null;
		
		eventType = parser.next();
		while( eventType != XmlPullParser.END_TAG) {
			
			if( eventType == XmlPullParser.TEXT) {
				element = parser.getText();
				if(element.trim().length() == 0) element = null;
			} else if( eventType == XmlPullParser.START_TAG) {
				// We skip this subtree
				parser.skipSubTree();
				// SkipSubTree position us on the corresponding end tag
			}
			eventType = parser.next();
		}
		
		return element;
	}
	
	
}
