package com.ptoceti.osgi.obix.impl.front.converters;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : ObjDeserializer.java
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
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ptoceti.osgi.obix.constants.ObixNames;
import com.ptoceti.osgi.obix.contract.About;
import com.ptoceti.osgi.obix.contract.BatchIn;
import com.ptoceti.osgi.obix.contract.BatchOut;
import com.ptoceti.osgi.obix.contract.Dimension;
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
import com.ptoceti.osgi.obix.custom.contract.DigitPoint;
import com.ptoceti.osgi.obix.custom.contract.MeasurePoint;
import com.ptoceti.osgi.obix.custom.contract.MonitoredPoint;
import com.ptoceti.osgi.obix.custom.contract.ReferencePoint;
import com.ptoceti.osgi.obix.custom.contract.SwitchPoint;
import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Bool;
import com.ptoceti.osgi.obix.object.Enum;
import com.ptoceti.osgi.obix.object.Err;
import com.ptoceti.osgi.obix.object.Feed;
import com.ptoceti.osgi.obix.object.Int;
import com.ptoceti.osgi.obix.object.List;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Op;
import com.ptoceti.osgi.obix.object.Real;
import com.ptoceti.osgi.obix.object.Ref;
import com.ptoceti.osgi.obix.object.Reltime;
import com.ptoceti.osgi.obix.object.Str;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.object.Val;



public class ObjDeserializer extends StdDeserializer<Obj> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public ObjDeserializer() {
		super( Obj.class);
	}
	

	@Override
	public Obj deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		
		ObjectMapper mapper = (ObjectMapper) jp.getCodec();  
	    ObjectNode root = (ObjectNode) mapper.readTree(jp);  
	    Class<? extends Obj> objClass = null;  
	    Iterator<Entry<String, JsonNode>> elementsIterator =   root.fields();  
	    
	    String isValue = null;
	    String typeValue = null;
	    while (elementsIterator.hasNext())  
	    {  
	      Entry<String, JsonNode> element=elementsIterator.next();  
	      String name = element.getKey();  
	      if( name.equals("type")) typeValue = element.getValue().textValue();
	      if( name.equals("is")) isValue = element.getValue().textValue();
	    }  
	    
	    if( typeValue != ObixNames.OBJ) {
			// native object to create
			if( typeValue == ObixNames.ABSTIME) {
				objClass = Abstime.class;
			} else if( typeValue.equals(ObixNames.BOOL)) {
				objClass = Bool.class;
			} else if( typeValue.equals(ObixNames.ENUM)) {
				objClass = Enum.class;
			} else if( typeValue.equals(ObixNames.ERR)) {
				objClass = Err.class;
			} else if( typeValue.equals(ObixNames.FEED)) {
				objClass = Feed.class;
			} else if( typeValue.equals(ObixNames.INT)) {
				objClass = Int.class;
			} else if( typeValue.equals(ObixNames.LIST)) {
				objClass = List.class;
			} else if( typeValue.equals(ObixNames.OP)) {
				objClass = Op.class;
			} else if( typeValue.equals(ObixNames.REAL)) {
				objClass = Real.class;
			} else if( typeValue.equals(ObixNames.REF)) {
				objClass = Ref.class;
			} else if( typeValue.equals(ObixNames.RELTIME)) {
				objClass = Reltime.class;
			} else if( typeValue.equals(ObixNames.STR)) {
				objClass = Str.class;
			} else if( typeValue.equals(ObixNames.URI)) {
				objClass = Uri.class;
			} else {
				objClass = getObixObjClassFromContract(isValue);
			}
			
		} else {
			objClass = getObixObjClassFromContract(isValue);
			
		}
	    if (objClass == null) return null;  
	   
	    return mapper.convertValue(root, objClass);
	}
	
	private Class<? extends Obj> getObixObjClassFromContract(String contract) {
			
		Obj obixObj = null;
		
		StringTokenizer st = new StringTokenizer(contract);
		if( st.hasMoreTokens()) {
			String contractUri = st.nextToken();
			
			if( contractUri.equals(About.contract.getUris()[0].getPath())) return About.class;
			else if( contractUri.equals(BatchIn.contract.getUris()[0].getPath())) return BatchIn.class;
			else if( contractUri.equals(BatchOut.contract.getUris()[0].getPath())) return BatchOut.class;
			else if( contractUri.equals(Dimension.contract.getUris()[0].getPath())) return Dimension.class;
			else if( contractUri.equals(Lobby.contract.getUris()[0].getPath())) return Lobby.class;
			else if( contractUri.equals(Nil.contract.getUris()[0].getPath())) return Nil.class;
			else if( contractUri.equals(Point.contract.getUris()[0].getPath())) return Point.class;
			else if( contractUri.equals(Unit.contract.getUris()[0].getPath())) return Unit.class;
			else if( contractUri.equals(Watch.contract.getUris()[0].getPath())) return Watch.class;
			else if( contractUri.equals(WatchService.contract.getUris()[0].getPath())) return WatchService.class;
			else if( contractUri.equals(WritePointIn.contract.getUris()[0].getPath())) return WritePointIn.class;
			else if( contractUri.equals(Read.contract.getUris()[0].getPath())) return Read.class;
			else if( contractUri.equals(Write.contract.getUris()[0].getPath())) return Write.class;
			else if( contractUri.equals(WatchIn.contract.getUris()[0].getPath())) return WatchIn.class;
			else if( contractUri.equals(MonitoredPoint.contract.getUris()[0].getPath())) return MonitoredPoint.class;
			
			
		}
		
		
		return null;
	}
	
}
