package com.ptoceti.osgi.obix.impl.guice;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : GuiceFinder.java
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


import java.util.logging.Logger;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.Finder;
import org.restlet.resource.ServerResource;

import com.google.inject.Injector;

/**
 * A Finder to create ServerResource instances with injectables dependancies resolved.
 * 
 * @author LATHIL
 *
 */
public class GuiceFinder extends Finder {

	private Injector injector;

	public static GuiceFinder createGuiceFinder(Class<? extends ServerResource> targetClass, Context context, Logger logger) {
		return (GuiceFinder)GuiceFinder.createFinder( targetClass, GuiceFinder.class, context, logger);
	}
	
	 /**
     * Constructor.
     */
    public GuiceFinder() {
        this(null);
    }

    /**
     * Constructor.
     * 
     * @param context The context.
     */
    public GuiceFinder(Context context) {
        super(context);
    }

    /**
     * Constructor.
     * 
     * @param context The context.
     * @param targetClass The target handler class. It must be either a subclass of {@link Handler} or of {@link ServerResource}.
     */
    public GuiceFinder(Context context, Class<? extends ServerResource> targetClass) {
        super(context, targetClass);
    }
    
    /**
     * Create an instance of the desired ServerResource, and inject dependencies.
     * 
     * @param targetClass the desired class for the ServerResource
     * @param request the current restlet request
     * @param response the current restlet response
     */
	@Override
	public ServerResource create(Class<? extends ServerResource> targetClass,
			Request request, Response response) {
		ServerResource result = null;

		if (targetClass != null) {
			result = injector.getInstance(targetClass);
		}
		
		return result;
	}

	/**
	 * Setter
	 * @param injector the guice injector.
	 */
	public void setInjector(Injector injector) {
		this.injector = injector;
	}
	/**
	 * Getter
	 * @return the guice inector.
	 */
	public Injector getInjector() {
		return injector;
	}

}
