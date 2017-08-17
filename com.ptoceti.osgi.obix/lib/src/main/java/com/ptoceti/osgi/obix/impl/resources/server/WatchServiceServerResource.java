package com.ptoceti.osgi.obix.impl.resources.server;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : WatchServiceServerResource.java
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


import org.restlet.resource.Get;
import org.restlet.resource.Post;

import com.google.inject.Inject;

import com.ptoceti.osgi.obix.cache.WatchCache;
import com.ptoceti.osgi.obix.contract.Nil;
import com.ptoceti.osgi.obix.contract.Watch;
import com.ptoceti.osgi.obix.contract.WatchService;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.domain.WatchDomain;
import com.ptoceti.osgi.obix.object.Op;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.resources.ResourceException;
import com.ptoceti.osgi.obix.resources.WatchServiceResource;

public class WatchServiceServerResource extends AbstractServerResource implements WatchServiceResource {
	
	private WatchCache cache;
	
	@Inject
	public WatchServiceServerResource(WatchCache cache){
		this.cache = cache;
	}
	
	@Get
	public WatchService retrieve() throws ResourceException{
		
		WatchService serv = new WatchService();
		Op make = serv.getMake();
		make.setHref(new Uri("uri", WatchServiceServerResource.uri));
		return serv;
		
	}
	
	@Post("xml|json")
	public Watch make(Nil nil) throws ResourceException{	
		try {
			Watch watch = cache.make();
			return watch;
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".make", ex);
		}
	}

}
