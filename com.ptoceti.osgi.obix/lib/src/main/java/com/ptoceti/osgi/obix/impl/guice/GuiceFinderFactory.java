package com.ptoceti.osgi.obix.impl.guice;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : GuiceFinderFactory.java
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
import org.restlet.resource.Finder;
import org.restlet.resource.ServerResource;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;

public class GuiceFinderFactory  {
	
	public GuiceFinderFactory() {
		
	}
	
	public Finder getFinder(Class<?> targetClass, Context context, Logger logger){
		
		GuiceFinder finder = GuiceFinder.createGuiceFinder(targetClass, context, logger);
		finder.setInjector(GuiceContext.Instance.getInjector());
		return finder;
	}

}
