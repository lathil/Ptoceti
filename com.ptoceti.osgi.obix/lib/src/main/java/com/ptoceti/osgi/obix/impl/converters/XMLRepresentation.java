package com.ptoceti.osgi.obix.impl.converters;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : XMLRepresentation.java
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

import com.ptoceti.osgi.obix.impl.Activator;
import com.ptoceti.osgi.obix.object.Obj;

public class XMLRepresentation<T> extends WriterRepresentation {

	  /** The (parsed) object to format. */
    private T object;

    /** The object class to instantiate. */
    private Class<T> objectClass;
    
    /** The JSON representation to parse. */
    private Representation obixRepresentation;
    
    private UniformResource resource;

    
    public XMLRepresentation(MediaType mediaType, T object, UniformResource resource) {
        super(mediaType);
        this.object = object;
        this.objectClass = (Class<T>) ((object == null) ? null : object
                .getClass());
        this.obixRepresentation = null;
        this.resource = resource;
        //this.objectMapper = null;
    }
    
    public XMLRepresentation(Representation representation,
            Class<T> objectClass, UniformResource resource) {
        super(representation.getMediaType());
        this.object = null;
        this.objectClass = objectClass;
        this.obixRepresentation = representation;
        this.resource = resource;
        //this.objectMapper = null;
    }
    
	public XMLRepresentation(MediaType mediaType) {
		super(mediaType);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void write(Writer writer) throws IOException {
		if (obixRepresentation != null) {
			obixRepresentation.write(writer);
        } else if (object != null) {
        	
        	XMLEncoder encoder = new XMLEncoder();
        	encoder.encode((Obj)object, writer, resource.getRootRef().toString());
        }
		
	}
	
	 public T getObject() {
		 T result = null;
		 
		 if (this.object != null) {
	            result = this.object;
	        } else if (this.obixRepresentation != null) {
	            try {
	            	XMLDecoder decoder = new XMLDecoder();
	            	result = (T) decoder.parse(this.obixRepresentation.getStream());
	            	
	            } catch (Exception e) {
	                Activator.log(LogService.LOG_ERROR, "Erreur deserializing xml representation: " + e.getMessage());
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
