package com.ptoceti.osgi.obix.impl.front.converters;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : JSonConverter.java
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


import java.io.IOException;
import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.engine.converter.ConverterHelper;
import org.restlet.engine.resource.VariantInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Resource;

public class JSonConverter extends ConverterHelper{

	private static final VariantInfo VARIANT_JSON = new VariantInfo(
	            MediaType.APPLICATION_JSON);
	    
	@Override
	public List<Class<?>> getObjectClasses(Variant source) {
		List<Class<?>> result = null;

        if (VARIANT_JSON.isCompatible(source)) {

        	result = addObjectClass(result, Object.class);
            result = addObjectClass(result, JSonRepresentation.class);
        }

        return result;
	}

	@Override
	public List<VariantInfo> getVariants(Class<?> source) {
		List<VariantInfo> result = null;

        if ( source != null) {
            result = addVariant(result, VARIANT_JSON);
        }

        return result;
	}

	@Override
	public float score(Object source, Variant target, Resource arg2) {
		float result = -1.0F;

        if (source instanceof JSonRepresentation<?>) {
            result = 1.0F;
        } else {
            if (target == null) {
                result = 0.5F;
            } else if (VARIANT_JSON.isCompatible(target)) {
                result = 1.0F;
            } else {
                result = 0.5F;
            }
        }

        return result;
	}

	@Override
	public <T> float score(Representation source, Class<T> target,
			Resource resource) {
		 float result = -1.0F;

	        if (source instanceof JSonRepresentation<?>) {
	            result = 1.0F;
	        } else if ((target != null)
	                && JSonRepresentation.class.isAssignableFrom(target)) {
	            result = 1.0F;
	        } else if (VARIANT_JSON.isCompatible(source)) {
	            result = 1.0F;
	        }

	        return result;
	}

	@Override
	public <T> T toObject(Representation source, Class<T> target,
			Resource resource) throws IOException {
		 Object result = null;

	        // The source for the Jackson conversion
	        JSonRepresentation<?> obixSource = null;

	        if (source instanceof JSonRepresentation) {
	        	obixSource = (JSonRepresentation<?>) source;
	        } else if (VARIANT_JSON.isCompatible(source)) {
	        	obixSource = create(source, target, resource);
	        }

	        if (obixSource != null) {
	            // Handle the conversion
	            if ((target != null) && JSonRepresentation.class.isAssignableFrom(target)) {
	                result = obixSource;
	            } else {
	                result = obixSource.getObject();
	            }
	        }

	        return (T) result;
	}

	@Override
	public Representation toRepresentation(Object source, Variant target,
			Resource resource) throws IOException {
		Representation result = null;

        if (source instanceof JSonRepresentation) {
            result = (JSonRepresentation<?>) source;
        } else {
            if (target.getMediaType() == null) {
                target.setMediaType(MediaType.APPLICATION_JSON);
            }

            if (VARIANT_JSON.isCompatible(target) ) {
            	
            	JSonRepresentation<Object> obixRepresentation = create(
                        target.getMediaType(), source, resource);
                result = obixRepresentation;
            }
        }
        
        return result;
	}

	 protected <T> JSonRepresentation<T> create(MediaType mediaType, T source, Resource resource) {
	        return new JSonRepresentation<T>(mediaType, source, resource);
	 }
	        
    protected <T> JSonRepresentation<T> create(Representation source,
            Class<T> objectClass, Resource resource) {
        return new JSonRepresentation<T>(source, objectClass, resource);
	}

}
