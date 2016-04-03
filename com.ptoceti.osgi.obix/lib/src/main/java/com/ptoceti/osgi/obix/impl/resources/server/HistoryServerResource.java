package com.ptoceti.osgi.obix.impl.resources.server;

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


import org.restlet.resource.Delete;
import org.restlet.resource.Get;

import com.google.inject.Inject;
import com.ptoceti.osgi.obix.cache.HistoryCache;
import com.ptoceti.osgi.obix.contract.History;
import com.ptoceti.osgi.obix.contract.Nil;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.resources.HistoryResource;
import com.ptoceti.osgi.obix.resources.ResourceException;

public class HistoryServerResource extends AbstractServerResource implements HistoryResource{

	private HistoryCache cache;
	
	@Inject
	public HistoryServerResource(HistoryCache cache) {
		this.cache = cache;
	}
	
	@Get
	public History retrieve() throws ResourceException {
		
		String historyUri = HistoryResource.baseuri.concat("/").concat((String)getRequest().getAttributes().get(HistoryResource.HISTORY_URI)).concat("/");
		History history = null;
		try {
			history = cache.retrieve(historyUri);
			
			history.getQuery().setHref(new Uri("uri", historyUri + HistoryQueryServerResource.baseuri));
			history.getRollup().setHref(new Uri("uri", historyUri + HistoryRollupServerResource.baseuri));
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".retrieve", ex);
		}
		
		return history;
		
	}
	
	@Delete
	public Nil remove() throws ResourceException {
		
		String historyUri = HistoryResource.baseuri.concat("/").concat((String)getRequest().getAttributes().get(HistoryResource.HISTORY_URI)).concat("/");
		try {
			cache.delete(historyUri);
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".deleteWatch", ex);
		}
	
		return new Nil();
	}
}
