package com.ptoceti.osgi.obix.impl.front.resources;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : WatchServerResource.java
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
import com.ptoceti.osgi.obix.contract.Watch;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.impl.front.exception.HttpNotFoundException;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.resources.ResourceException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;

@Singleton
@Path(WatchServerResource.uri)
public class WatchServerResource extends AbstractServerResource {

    public static final String WATCH_URI = "watchuri";

    public static final String baseuri = "/watch";

    public static final String uri = baseuri + "/{" + WATCH_URI + "}/";

	private WatchCache cache;

	
	@Inject
	public WatchServerResource(WatchCache cache) {
		this.cache= cache;
	}

    @GET
    @Produces({"application/xml", "application/json"})
    public Watch retrieve(@PathParam(WatchServerResource.WATCH_URI) String watchuri) throws ResourceException {

        String watchUri = WatchServerResource.baseuri.concat("/").concat(watchuri).concat("/");
		Watch watch = null;
		try {
			watch = cache.retrieve(watchUri);
			if( watch != null ) {
				watch.getAdd().setHref(new Uri("uri", watchUri + WatchAddServerResource.baseuri));
				watch.getRemove().setHref(new Uri("uri", watchUri + WatchRemoveServerResource.baseuri));
				watch.getDelete().setHref(new Uri("uri", watchUri +WatchDeleteServerResource.baseuri));
				watch.getPoolChanges().setHref(new Uri("uri", watchUri + WatchPoolChangesServerResource.baseuri));
				watch.getPoolRefresh().setHref(new Uri("uri", watchUri + WatchPoolRefreshServerResource.baseuri));
			} else {
                throw new HttpNotFoundException("watch at : " + watchUri + "not found");
			}
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".retrieve", ex);
		}
		
		return watch;
	}

    @PUT
    @Consumes({"application/xml", "application/json"})
    public void update(@PathParam(WatchServerResource.WATCH_URI) String watchuri, Watch watchIn) throws ResourceException {
        String watchUri = WatchServerResource.baseuri.concat("/").concat(watchuri).concat("/");
		
		try {
			cache.update(watchUri, watchIn);
		} catch (DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".update", ex);
		}
		
	}


    @Path(WatchAddServerResource.baseuri)
    public Class<WatchAddServerResource> getWatchAddServerResource() {
        return WatchAddServerResource.class;
    }

    @Path(WatchDeleteServerResource.baseuri)
    public Class<WatchDeleteServerResource> getWatchDeleteServerResource() {
        return WatchDeleteServerResource.class;
    }

    @Path(WatchRemoveServerResource.baseuri)
    public Class<WatchRemoveServerResource> getWatchRemoveServerResource() {
        return WatchRemoveServerResource.class;
    }

    @Path(WatchPoolChangesServerResource.baseuri)
    public Class<WatchPoolChangesServerResource> getWatchPoolChangesServerResource() {
        return WatchPoolChangesServerResource.class;
    }

    @Path(WatchPoolRefreshServerResource.baseuri)
    public Class<WatchPoolRefreshServerResource> getWatchPoolRefreshServerResource() {
        return WatchPoolRefreshServerResource.class;
    }
}
