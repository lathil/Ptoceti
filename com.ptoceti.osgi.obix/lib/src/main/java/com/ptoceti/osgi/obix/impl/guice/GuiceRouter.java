package com.ptoceti.osgi.obix.impl.guice;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : GuiceRouter.java
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


import org.restlet.Context;
import org.restlet.resource.Finder;
import org.restlet.routing.Router;

public class GuiceRouter extends Router {

	private GuiceFinderFactory finderFactory;
	
	public GuiceRouter() {
		super();
	}
	
	public GuiceRouter(Context context) {
		super(context);
	}
	
	@Override
	public Finder createFinder(Class<? extends org.restlet.resource.ServerResource> targetClass) {
		
		Finder finder = finderFactory.getFinder(targetClass, getContext(), getLogger());
		
		return finder;
	}

	public void setFinderFactory(GuiceFinderFactory finderFactory) {
		this.finderFactory = finderFactory;
	}

	public GuiceFinderFactory getFinderFactory() {
		return finderFactory;
	}

}
