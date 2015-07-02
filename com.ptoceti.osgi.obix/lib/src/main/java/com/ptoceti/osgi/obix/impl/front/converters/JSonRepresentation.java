package com.ptoceti.osgi.obix.impl.front.converters;

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
import org.restlet.resource.Resource;

import com.ptoceti.osgi.obix.impl.Activator;


public class JSonRepresentation<T> extends WriterRepresentation  {
	
	  /** The (parsed) object to format. */
    private T object;

    /** The object class to instantiate. */
    private Class<T> objectClass;
    
    /** The JSON representation to parse. */
    private Representation jsonRepresentation;
    
    private Resource resource;
    
    
    public JSonRepresentation(MediaType mediaType, T object, Resource resource) {
        super(mediaType);
        this.object = object;
        this.objectClass = (Class<T>) ((object == null) ? null : object.getClass());
        this.jsonRepresentation = null;
        this.resource = resource;
        //this.objectMapper = null;
    }
    
    public JSonRepresentation(Representation representation, Class<T> objectClass, Resource resource) {
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
	
	

	@Override
	public void write(Writer writer) throws IOException {
		if (this.jsonRepresentation != null)
			this.jsonRepresentation.write(writer);
		else if (this.object != null) 
			try {
				ObjectMapperFactory.configure().writeValue(writer, this.object);
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
	            	result = ObjectMapperFactory.configure().readValue(this.jsonRepresentation.getStream(), this.objectClass);
	            	
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

	
}
