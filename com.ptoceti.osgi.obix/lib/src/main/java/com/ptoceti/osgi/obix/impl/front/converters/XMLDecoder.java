package com.ptoceti.osgi.obix.impl.front.converters;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : XMLDecoder.java
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
import java.io.InputStream;
import java.util.Date;
import java.util.StringTokenizer;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import com.ptoceti.osgi.obix.constants.ObixNames;
import com.ptoceti.osgi.obix.contract.About;
import com.ptoceti.osgi.obix.contract.Batch;
import com.ptoceti.osgi.obix.contract.BatchIn;
import com.ptoceti.osgi.obix.contract.BatchOut;
import com.ptoceti.osgi.obix.contract.Dimension;
import com.ptoceti.osgi.obix.contract.History;
import com.ptoceti.osgi.obix.contract.HistoryFilter;
import com.ptoceti.osgi.obix.contract.HistoryQueryOut;
import com.ptoceti.osgi.obix.contract.HistoryRecord;
import com.ptoceti.osgi.obix.contract.HistoryRollupIn;
import com.ptoceti.osgi.obix.contract.HistoryRollupOut;
import com.ptoceti.osgi.obix.contract.HistoryRollupRecord;
import com.ptoceti.osgi.obix.contract.Lobby;
import com.ptoceti.osgi.obix.contract.Nil;
import com.ptoceti.osgi.obix.contract.Point;
import com.ptoceti.osgi.obix.contract.Read;
import com.ptoceti.osgi.obix.contract.Unit;
import com.ptoceti.osgi.obix.contract.Watch;
import com.ptoceti.osgi.obix.contract.WatchIn;
import com.ptoceti.osgi.obix.contract.WatchService;
import com.ptoceti.osgi.obix.contract.Write;
import com.ptoceti.osgi.obix.contract.WritePointIn;
import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Bool;
import com.ptoceti.osgi.obix.object.Err;
import com.ptoceti.osgi.obix.object.Feed;
import com.ptoceti.osgi.obix.object.Int;
import com.ptoceti.osgi.obix.object.Enum;
import com.ptoceti.osgi.obix.object.List;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Op;
import com.ptoceti.osgi.obix.object.Real;
import com.ptoceti.osgi.obix.object.Ref;
import com.ptoceti.osgi.obix.object.Reltime;
import com.ptoceti.osgi.obix.object.Status;
import com.ptoceti.osgi.obix.object.Str;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.object.Val;

public class XMLDecoder {

	private XmlPullParser parser;
	
	
	public XMLDecoder() {
		
		try {
			XmlPullParserFactory factory;
			factory = XmlPullParserFactory.newInstance();
			parser = factory.newPullParser();
		
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Obj parse(InputStream stream) {
		
		Obj root = null;
		
		try {
			parser.setInput(stream, null);
			root = parseDocument();
			
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return root;
	}
	
	public Obj parseDocument() {
		
		Obj obixObj = null;
		int eventType;
		try {
			eventType = parser.getEventType();
			
			while( eventType != XmlPullParser.END_DOCUMENT) {
				
				if( eventType == XmlPullParser.START_TAG){
					obixObj = parseElement();
				}
				
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return obixObj;
	}
	
	public Obj parseElement() {
		
		String elemName = parser.getName();
		Obj obixObj = getObixObj(elemName);
		
		int eventType;
		try {
			eventType = parser.getEventType();
			
			while( eventType != XmlPullParser.END_TAG) {
				eventType = parser.next();
				
				// child element
				if( eventType == XmlPullParser.START_TAG){
					obixObj.addChildren(parseElement());
				}
				
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return obixObj;
	}
	
	private Obj getObixObj(String objName) {
		
		Obj obixObj = null;
		
		String contract = parser.getAttributeValue(null, ObixNames.IS);
		String name = parser.getAttributeValue(null, ObixNames.NAME);
		
		String hrefUri = parser.getAttributeValue(null, ObixNames.HREF);
		Uri href = hrefUri == null ? null : new Uri(ObixNames.HREF, hrefUri);
			
		String display = parser.getAttributeValue(null, ObixNames.DISPLAY);
		String displayName = parser.getAttributeValue(null, ObixNames.DISPLAYNAME);
		
		String iconUri = parser.getAttributeValue(null, ObixNames.ICON);
		Uri icon = iconUri == null ? null : new Uri(ObixNames.ICON, iconUri);
		
		Status status = Status.getStatusFromName(parser.getAttributeValue(null, ObixNames.STATUS));
		Boolean isNull = Boolean.valueOf(parser.getAttributeValue(null, ObixNames.NULL));
		Boolean writable = Boolean.valueOf(parser.getAttributeValue(null, ObixNames.WRITABLE));
		
		String max = parser.getAttributeValue(null, ObixNames.MAX);
		String min = parser.getAttributeValue(null, ObixNames.MIN);
		
		String val = parser.getAttributeValue(null, ObixNames.VAL);
		
		
		
		if( objName != ObixNames.OBJ) {
			// native object to create
			if( objName == ObixNames.ABSTIME) {
				Abstime abstime = new Abstime( name);
				abstime.decodeVal(val);
				
				if( max != null) {
					Abstime maxAbs = new Abstime(ObixNames.MAX);
					maxAbs.decodeVal(max);
					abstime.setMax( maxAbs);
				}
				if( min != null) {
					Abstime minAbs = new Abstime(ObixNames.MIN);
					minAbs.decodeVal(min);
					abstime.setMin( minAbs);
				}
				
				obixObj =  abstime;
			} else if( objName.equals(ObixNames.BOOL)) {
				Bool bool = new Bool( name );
				obixObj = bool;
			} else if( objName.equals(ObixNames.ENUM)) {
				Enum enu = new Enum(name);
				obixObj =  enu;
			} else if( objName.equals(ObixNames.ERR)) {
				Err err = new Err(name);
				obixObj = err;
			} else if( objName.equals(ObixNames.FEED)) {
				Feed feed = new Feed(name);
				obixObj = feed;
			} else if( objName.equals(ObixNames.INT)) {
				Int intg = new Int(name);
				obixObj = intg;
			} else if( objName.equals(ObixNames.LIST)) {
				List list = new List(name);
				obixObj = list;
			} else if( objName.equals(ObixNames.OP)) {
				Op op = new Op(name);
				obixObj = op;
			} else if( objName.equals(ObixNames.REAL)) {
				Real real = new Real(name);
				obixObj = real;
			} else if( objName.equals(ObixNames.REF)) {
				Ref ref = new Ref(name);
				obixObj = ref;
			} else if( objName.equals(ObixNames.RELTIME)) {
				Reltime reltime = new Reltime(name);
				reltime.decodeVal(val);
				
				if( max != null) {
					Reltime maxRel = new Reltime(ObixNames.MAX);
					maxRel.decodeVal(max);
					reltime.setMax( maxRel);
				}
				if( min != null) {
					Reltime minRel = new Reltime(ObixNames.MIN);
					minRel.decodeVal(min);
					reltime.setMin( minRel);
				}
				
				obixObj =  reltime;
			} else if( objName.equals(ObixNames.STR)) {
				Str str = new Str(name);
				obixObj = str;
			} else if( objName.equals(ObixNames.URI)) {
				Uri uri = new Uri(name);
				obixObj = uri;
			} else {
				obixObj = getObixObjFromContract(name, contract);
			}
			
			if( val != null && obixObj != null ) {
				if( !objName.equals(ObixNames.ABSTIME )) {
					((Val)obixObj).decodeVal(val);
				}
				else if( !objName.equals(ObixNames.RELTIME )) {
					((Val)obixObj).decodeVal(val);
				}
			}
			
		} else {
			obixObj = getObixObjFromContract(name, contract);
			
		}
		
		if( obixObj != null) {
			if( href != null) obixObj.setHref(href);
			
			if( display != null) obixObj.setDisplay(display);
			if( displayName != null) obixObj.setDisplayName(displayName);
			if( icon != null) obixObj.setIcon(icon);
			if( status != null) obixObj.setStatus(status);
			if( isNull != null) obixObj.setIsNull(isNull);
			if( writable != null) obixObj.setWritable(writable);
		}
		
		
		return obixObj;
	}
	
	private Obj getObixObjFromContract(String name, String contract) {
		
		Obj obixObj = null;
		
		StringTokenizer st = new StringTokenizer(contract);
		if( st.hasMoreTokens()) {
			String contractUri = st.nextToken();
			
			if( contractUri.equals(About.contract.getUris()[0].getPath())) obixObj = new About(name);
			else if( contractUri.equals(BatchIn.contract.getUris()[0].getPath())) obixObj = new BatchIn();
			else if( contractUri.equals(BatchOut.contract.getUris()[0].getPath())) obixObj = new BatchOut();
			else if( contractUri.equals(Dimension.contract.getUris()[0].getPath())) obixObj = new Dimension(name);
			else if( contractUri.equals(Lobby.contract.getUris()[0].getPath())) obixObj = new Lobby(name);
			else if( contractUri.equals(Nil.contract.getUris()[0].getPath())) obixObj = new Nil(name);
			else if( contractUri.equals(Point.contract.getUris()[0].getPath())) obixObj = new Point(name);
			else if( contractUri.equals(Unit.contract.getUris()[0].getPath())) obixObj = new Unit(name);
			else if( contractUri.equals(Watch.contract.getUris()[0].getPath())) obixObj = new Watch(name);
			else if( contractUri.equals(WatchService.contract.getUris()[0].getPath())) obixObj = new WatchService(name);
			else if( contractUri.equals(WritePointIn.contract.getUris()[0].getPath())) obixObj = new WritePointIn(name);
			else if( contractUri.equals(Read.contract.getUris()[0].getPath())) obixObj = new Read(name);
			else if( contractUri.equals(Write.contract.getUris()[0].getPath())) obixObj = new Write(name);
			else if( contractUri.equals(WatchIn.contract.getUris()[0].getPath())) obixObj = new WatchIn();
			else if( contractUri.equals(History.contract.getUris()[0].getPath())) obixObj = new History();
			else if( contractUri.equals(HistoryRecord.contract.getUris()[0].getPath())) obixObj = new HistoryRecord();
			else if( contractUri.equals(HistoryFilter.contract.getUris()[0].getPath())) obixObj = new HistoryFilter();
			else if( contractUri.equals(HistoryQueryOut.contract.getUris()[0].getPath())) obixObj = new HistoryQueryOut();
			else if( contractUri.equals(HistoryRollupIn.contract.getUris()[0].getPath())) obixObj = new HistoryRollupIn();
			else if( contractUri.equals(HistoryRollupOut.contract.getUris()[0].getPath())) obixObj = new HistoryRollupOut();
			else if( contractUri.equals(HistoryRollupRecord.contract.getUris()[0].getPath())) obixObj = new HistoryRollupRecord();
			
		}
		
		
		return obixObj;
	}
	
}
