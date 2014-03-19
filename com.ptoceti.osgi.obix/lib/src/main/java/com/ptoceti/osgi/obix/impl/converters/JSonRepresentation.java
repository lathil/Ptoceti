package com.ptoceti.osgi.obix.impl.converters;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : JSonRepresentation.java
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

import org.osgi.service.log.LogService;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.WriterRepresentation;
import org.restlet.resource.UniformResource;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.ptoceti.osgi.obix.contract.WatchInItem;
import com.ptoceti.osgi.obix.contract.WatchOut;
import com.ptoceti.osgi.obix.contract.WatchService;
import com.ptoceti.osgi.obix.contract.WritablePoint;
import com.ptoceti.osgi.obix.contract.Write;
import com.ptoceti.osgi.obix.contract.WritePoint;
import com.ptoceti.osgi.obix.contract.WritePointIn;
import com.ptoceti.osgi.obix.custom.contract.MonitoredPoint;
import com.ptoceti.osgi.obix.impl.Activator;
import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Bool;
import com.ptoceti.osgi.obix.object.Contract;
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

public class JSonRepresentation<T> extends WriterRepresentation  {
	
	  /** The (parsed) object to format. */
    private T object;

    /** The object class to instantiate. */
    private Class<T> objectClass;
    
    /** The JSON representation to parse. */
    private Representation jsonRepresentation;
    
    private UniformResource resource;
    
    private ObjectMapper objectMapper;


    
    public JSonRepresentation(MediaType mediaType, T object, UniformResource resource) {
        super(mediaType);
        this.object = object;
        this.objectClass = (Class<T>) ((object == null) ? null : object.getClass());
        this.jsonRepresentation = null;
        this.resource = resource;
        //this.objectMapper = null;
    }
    
    public JSonRepresentation(Representation representation, Class<T> objectClass, UniformResource resource) {
        super(representation.getMediaType());
        this.object = null;
        this.objectClass = objectClass;
        this.jsonRepresentation = representation;
        this.resource = resource;
        //this.objectMapper = null;
    }
    
	public JSonRepresentation(MediaType mediaType) {
		super(mediaType);
		// TODO Auto-generated constructor stub
	}
	
	protected ObjectMapper getObjectMapper() {
		if( objectMapper == null) { 
			JsonFactory factory = new JsonFactory();
			factory.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
			
			
			objectMapper =  new ObjectMapper(factory);
			
			/**
			objectMapper.configure(MapperFeature.AUTO_DETECT_FIELDS, true);
			objectMapper.configure(MapperFeature.AUTO_DETECT_GETTERS, false);
			objectMapper.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
			objectMapper.configure(MapperFeature.AUTO_DETECT_SETTERS, false);
			
			
			objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC);
			**/
			
			objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
			objectMapper.addMixInAnnotations(Contract.class, ContractMixIn.class);
			objectMapper.addMixInAnnotations(Obj.class, ObjMixIn.class);
			
			
		}
		
		return objectMapper;
	}

	@Override
	public void write(Writer writer) throws IOException {
		if (this.jsonRepresentation != null)
			this.jsonRepresentation.write(writer);
		else if (this.object != null) 
			try {
				getObjectMapper().writeValue(writer, this.object);
			} catch( Exception e){
				Activator.log(LogService.LOG_ERROR, "Erreur serializing json representation: " + e.getMessage());
				e.printStackTrace();
			}
		
	}

	public T getObject() {
		 T result = null;
		 
		 if (this.object != null) {
	            result = this.object;
	        } else if (this.jsonRepresentation != null) {
	            try {
	            	result = getObjectMapper().readValue(this.jsonRepresentation.getStream(), this.objectClass);
	            	
	            } catch (Exception e) {
	                Activator.log(LogService.LOG_ERROR, "Erreur deserializing json representation: " + e.getMessage());
	                e.printStackTrace();
	            }
	        }
		 
		 return result;
	 }
	 
	 public void setObject(T object) {
	        this.object = object;
	 }
	 
	 public Class<T> getObjectClass() {
	        return objectClass;
	 }
	 
	 public void setObjectClass(Class<T> objectClass) {
	        this.objectClass = objectClass;
	 }

	 @JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
	 @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property="type")
	 @JsonSubTypes({
	 	@Type(value=Abstime.class, name="abstime"),
	 	@Type(value=Bool.class, name="bool"),
	 	@Type(value=Enum.class, name="enum"),
	 	@Type(value=Err.class, name="err"),
	 	@Type(value=Feed.class, name="feed"),
	 	@Type(value=Int.class, name="int"),
	 	@Type(value=List.class, name="list"),
	 	@Type(value=Op.class, name="op"),
	 	@Type(value=Real.class, name="real"),
	 	@Type(value=Ref.class, name="ref"),
	 	@Type(value=Reltime.class, name="reltime"),
	 	@Type(value=Str.class, name="str"),
	 	@Type(value=Uri.class, name="uri"),
	 	@Type(value=Obj.class, name="obj"),
	 	
	 	@Type(value=About.class, name="about"),
	 	@Type(value=Batch.class, name="batch"),
	 	@Type(value=BatchIn.class, name="batchin"),
	 	@Type(value=BatchOut.class, name="batchout"),
	 	@Type(value=Dimension.class, name="dimension"),
	 	@Type(value=Lobby.class, name="lobby"),
	 	@Type(value=Nil.class, name="nil"),
	 	@Type(value=Point.class, name="point"),
	 	@Type(value=Read.class, name="read"),
	 	@Type(value=Unit.class, name="unit"),
	 	@Type(value=Watch.class, name="watch"),
	 	@Type(value=WatchIn.class, name="watchin"),
	 	@Type(value=WatchInItem.class, name="watchinitem"),
	 	@Type(value=WatchOut.class, name="watchout"),
	 	@Type(value=WatchService.class, name="watchservice"),
	 	@Type(value=WritablePoint.class, name="writablepoint"),
	 	@Type(value=Write.class, name="write"),
	 	@Type(value=WritePoint.class, name="writepoint"),
	 	@Type(value=WritePointIn.class, name="writepointin"),
	 	@Type(value=History.class,name="history"),
	 	@Type(value=HistoryRecord.class,name="historyrecord"),
	 	@Type(value=HistoryFilter.class,name="historyfilter"),
	 	@Type(value=HistoryQueryOut.class,name="historyqueryout"),
	 	@Type(value=HistoryRollupIn.class,name="historyrollupin"),
	 	@Type(value=HistoryRollupOut.class,name="historyrollupout"),
	 	@Type(value=HistoryRollupRecord.class,name="historyrolluprecord"),
	 	
	 	@Type(value=MonitoredPoint.class, name="monitoredpoint")
	 })
	 
	
	 public abstract class ObjMixIn {

		 @JsonProperty("val") public abstract String encodeVal();

		 @JsonProperty("val") public abstract void decodeVal(String value);
		 
	 }
	 
	 @JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
	 public abstract class ContractMixIn {
		 
	 }
}
