package com.ptoceti.osgi.obix.impl.resources.server;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : AboutServerResource.java
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


import com.google.inject.Inject;
import com.ptoceti.osgi.obix.contract.About;
import com.ptoceti.osgi.obix.domain.AboutDomain;

import org.restlet.resource.Get;

import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.resources.AboutResource;

public class AboutServerResource extends AbstractServerResource implements AboutResource  {

	private AboutDomain domain;
	
	@Inject
	public AboutServerResource(AboutDomain domain) {
		this.domain = domain;
	}
	
	@Get
	public About retrieve() {		
		
		About about = domain.getAbout();
		
		about.setHref(new Uri("uri", AboutResource.uri));
		
		return about;
	}
}
