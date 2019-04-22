package com.ptoceti.osgi.obix.impl.front.resources;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : WatchDeleteServerResource.java
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

import com.ptoceti.osgi.obix.cache.WatchCache;
import com.ptoceti.osgi.obix.contract.Nil;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.resources.ResourceException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;

@Singleton
public class WatchDeleteServerResource extends AbstractServerResource {

    public static final String baseuri = "delete/";

    public static final String uri = WatchServerResource.uri.concat(baseuri);

	private WatchCache cache;
	
	@Inject
	public WatchDeleteServerResource(WatchCache cache) {
		this.cache = cache;
	}

    @POST
    @Consumes({"application/xml", "application/json"})
    public Nil deleteWatch(@PathParam(WatchServerResource.WATCH_URI) String watchuri, Nil nil) throws ResourceException {
        String watchUri = WatchServerResource.baseuri.concat("/").concat((watchuri).concat("/"));
		try {
			cache.deleteWatch(watchUri);
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".deleteWatch", ex);
		}
		
		return new Nil();
	}
}
