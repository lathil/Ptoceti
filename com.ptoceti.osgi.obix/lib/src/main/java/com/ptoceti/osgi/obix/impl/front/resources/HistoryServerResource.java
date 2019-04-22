package com.ptoceti.osgi.obix.impl.front.resources;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : HistoryServerResource.java
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



import com.ptoceti.osgi.obix.cache.HistoryCache;
import com.ptoceti.osgi.obix.contract.History;
import com.ptoceti.osgi.obix.contract.Nil;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.impl.front.exception.HttpNotFoundException;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.resources.ResourceException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;

@Singleton
@Path(HistoryServerResource.uri)
public class HistoryServerResource extends AbstractServerResource {

    public static final String HISTORY_URI = "historyuri";

    public static final String baseuri = "/history";

    public static final String uri = baseuri + "/{" + HISTORY_URI + "}/";

	private HistoryCache cache;
	
	@Inject
	public HistoryServerResource(HistoryCache cache) {
		this.cache = cache;
	}

    @GET
    @Produces({"application/xml", "application/json"})
    public History retrieve(@PathParam(HistoryServerResource.HISTORY_URI) String historyuri) throws ResourceException {

        String historyUri = HistoryServerResource.baseuri.concat("/").concat(historyuri).concat("/");
		History history = null;
		try {
			history = cache.retrieve(historyUri);
            if (history != null) {

                history.getQuery().setHref(new Uri("uri", historyUri + HistoryQueryServerResource.baseuri));
                history.getRollup().setHref(new Uri("uri", historyUri + HistoryRollupServerResource.baseuri));
            } else {
                throw new HttpNotFoundException("history at : " + historyUri + "not found");
            }
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".retrieve", ex);
		}
		
		return history;
		
	}

    @DELETE
    public Nil remove(@PathParam(HistoryServerResource.HISTORY_URI) String historyuri) throws ResourceException {

        String historyUri = HistoryServerResource.baseuri.concat("/").concat(historyuri).concat("/");
		try {
			cache.delete(historyUri);
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".deleteWatch", ex);
		}
	
		return new Nil();
	}

    @Path(HistoryQueryServerResource.baseuri)
    public Class<HistoryQueryServerResource> getHistoryQueryServerResource() {
        return HistoryQueryServerResource.class;
    }

    @Path(HistoryRollupServerResource.baseuri)
    public Class<HistoryRollupServerResource> getHistoryRollupServerResource() {
        return HistoryRollupServerResource.class;
    }
}
