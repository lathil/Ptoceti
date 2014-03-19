package com.ptoceti.osgi.obix.impl.converters;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : XMLEncoder.java
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
import java.io.Writer;
import java.util.Date;
import java.util.Iterator;

import com.ptoceti.osgi.obix.constants.ObixNames;
import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Bool;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Err;
import com.ptoceti.osgi.obix.object.Feed;
import com.ptoceti.osgi.obix.object.Int;
import com.ptoceti.osgi.obix.object.List;
import com.ptoceti.osgi.obix.object.Op;
import com.ptoceti.osgi.obix.object.Status;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.object.Real;
import com.ptoceti.osgi.obix.object.Ref;
import com.ptoceti.osgi.obix.object.Reltime;
import com.ptoceti.osgi.obix.object.Str;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.object.Enum;
import com.ptoceti.osgi.obix.object.Val;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import com.ptoceti.osgi.obix.object.Obj;

public class XMLEncoder {

	private XmlSerializer serializer;
	

	private static final String obixNs = "http://obix.org/ns/schema/1.0";
	private static final String obixPrefix = "obix";
	
	private String rootUrl;
	private int objectStack = -1;

	public XMLEncoder( ) {
		
		try {
			XmlPullParserFactory factory;
			factory = XmlPullParserFactory.newInstance();
			serializer = factory.newSerializer();
			serializer.setPrefix(obixPrefix, obixNs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void encode(Obj obixObj, Writer writer, String rootUrl) {

		try {
			serializer.setOutput(writer);
			serializer.startDocument("utf-8", null);
			
			this.rootUrl = rootUrl;

			encode(obixObj);

			serializer.endDocument();

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void encode(Obj obixObj) {

		String elemName = getElementName(obixObj);
		
		try {
			objectStack ++;
			
			serializer.startTag(null, elemName);

			if (obixObj.getName() != null) serializer.attribute(null, ObixNames.NAME, obixObj.getName());

			if (obixObj.getHref() != null) {
				if( objectStack == 0)
					serializer.attribute(null, ObixNames.HREF, rootUrl.concat(obixObj.getHref().getVal().toString()));
				else
				serializer.attribute(null, ObixNames.HREF, obixObj.getHref().getVal().toString());
			}

			if (obixObj.getDisplay() != null) serializer.attribute(null, ObixNames.DISPLAY, obixObj.getDisplay().toString());
			if (obixObj.getDisplayName() != null) serializer.attribute(null, ObixNames.DISPLAYNAME, obixObj.getDisplayName().toString());
			if (obixObj.getIcon() != null) serializer.attribute(null, ObixNames.ICON, obixObj.getIcon().getVal().toString());
			if (obixObj.getStatus() != null ) {
				serializer.attribute(null, ObixNames.STATUS, obixObj.getStatus().getName());
			}
			if (obixObj.getIsNull().booleanValue()) serializer.attribute(null, ObixNames.NULL, "true");
			if (obixObj.getWritable().booleanValue()) serializer.attribute(null, ObixNames.WRITABLE, "true");

			Contract contract = obixObj.getIs();
			if( contract != null ) {
				serializer.attribute(null, ObixNames.IS, encodeUris(contract.getUris()));
			}
			
			
			if( obixObj instanceof Abstime ) {
				Abstime abstime = (Abstime) obixObj;
				if(abstime.getMin() != null) serializer.attribute(null, ObixNames.MIN, (String) abstime.getMin().encodeVal());
				if(abstime.getMax() != null) serializer.attribute(null,ObixNames.MAX, (String) abstime.getMax().encodeVal());
				
			} else if (obixObj instanceof Bool ) {
				
			} else if (obixObj instanceof Enum ) {
				Enum en = (Enum) obixObj;
				if( en.getRange() != null) serializer.attribute(null, ObixNames.RANGE, en.getRange().getPath());
				
			} else if (obixObj instanceof Err) {
				
			} else if (obixObj instanceof Feed ) {
				Feed feed = (Feed) obixObj;
				if( feed.getIn()!= null) serializer.attribute(null, ObixNames.IN, encodeUris(feed.getIn().getUris()));
				if( feed.getOf()!= null) serializer.attribute(null, ObixNames.OF, encodeUris(feed.getOf().getUris()));
				
			} else if (obixObj instanceof Int ) {
				Int in = (Int) obixObj;
				if( in.getMax()!= null) serializer.attribute(null, ObixNames.MAX, in.getMax().toString());
				if( in.getMin()!= null) serializer.attribute(null, ObixNames.MIN, in.getMin().toString());
				if( in.getUnit()!= null) serializer.attribute(null, ObixNames.UNIT, in.getUnit().getVal().toString());
				
			} else if (obixObj instanceof List ) {
				List list = (List) obixObj;
				if( list.getOf() != null) serializer.attribute(null, ObixNames.OF, encodeUris(list.getOf().getUris()));
				
			} else if (obixObj instanceof Op ) {
				Op op = (Op)obixObj;
				if(op.getIn() != null) serializer.attribute(null, ObixNames.IN, encodeUris(op.getIn().getUris()));
				if(op.getOut() != null) serializer.attribute(null, ObixNames.OUT, encodeUris(op.getOut().getUris()));
				
			} else if (obixObj instanceof Real ) {
				Real real = (Real) obixObj;
				if( real.getMax()!= null) serializer.attribute(null, ObixNames.MAX, real.getMax().toString());
				if( real.getMin()!= null) serializer.attribute(null, ObixNames.MIN, real.getMin().toString());
				if( real.getUnit()!= null) serializer.attribute(null, ObixNames.UNIT, real.getUnit().getVal().toString());
				if( real.getPrecision()!= null) serializer.attribute(null, ObixNames.PRECISION, real.getPrecision().toString());
				
			} else if (obixObj instanceof Ref ) {
				
			} else if (obixObj instanceof Reltime ) {
				Reltime reltime = (Reltime) obixObj;
				if(reltime.getMin() != null) serializer.attribute(null, ObixNames.MIN, (String) reltime.getMin().encodeVal());
				if(reltime.getMax() != null) serializer.attribute(null, ObixNames.MAX, (String) reltime.getMax().encodeVal());
				
			} else if (obixObj instanceof Str ) {
				Str str = (Str) obixObj;
				if( str.getMax()!= null) serializer.attribute(null, ObixNames.MAX, str.getMax().toString());
				if( str.getMin()!= null) serializer.attribute(null, ObixNames.MIN, str.getMin().toString());
				
			} else if (obixObj instanceof Uri ) {
				
			} 
				
			if( obixObj instanceof Val) {
				serializer.attribute(null, ObixNames.VAL,((Val)obixObj).encodeVal());
			}
			
			if( obixObj.getChildrens() != null){
				Iterator objIter = obixObj.getChildrens().iterator();
				while (objIter.hasNext()) {
					encode((Obj) objIter.next());
				}
			}

			serializer.endTag(null, elemName);
			objectStack--;

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public String encodeUris(Uri[] uris) {
	
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < uris.length; i++){
			sb.append(uris[i].getVal().toString());
		}
		
		return sb.toString();
	}
	
	
	public String getElementName( Obj obixObj) {
	
		if( obixObj instanceof Abstime )  return ObixNames.ABSTIME;
		else if (obixObj instanceof Bool ) return ObixNames.BOOL;
		else if (obixObj instanceof Enum ) return ObixNames.ENUM;
		else if (obixObj instanceof Err) return ObixNames.ERR;
		else if (obixObj instanceof Feed ) return ObixNames.FEED;
		else if (obixObj instanceof Int ) return ObixNames.INT;
		else if (obixObj instanceof List ) return ObixNames.LIST;
		else if (obixObj instanceof Op ) return ObixNames.OP;
		else if (obixObj instanceof Real ) return ObixNames.REAL;
		else if (obixObj instanceof Ref ) return ObixNames.REF;
		else if (obixObj instanceof Reltime ) return ObixNames.RELTIME;
		else if (obixObj instanceof Str )return ObixNames.STR;
		else if (obixObj instanceof Uri ) return ObixNames.URI;
		else if (obixObj instanceof Obj ) return ObixNames.OBJ;
		else return "";
			
	}
}
