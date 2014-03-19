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


import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.inject.Inject;
import com.ptoceti.osgi.obix.contract.History;
import com.ptoceti.osgi.obix.contract.Watch;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.domain.HistoryDomain;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.resources.HistoryResource;
import com.ptoceti.osgi.obix.resources.ResourceException;
import com.ptoceti.osgi.obix.resources.WatchResource;

public class HistoryServerResource extends AbstractServerResource implements HistoryResource{

	private HistoryDomain historyDomain;
	
	@Inject
	public HistoryServerResource(HistoryDomain domain) {
		this.historyDomain = domain;
	}
	
	@Get
	public History retrieve() throws ResourceException {
		
		String historyUri = HistoryResource.baseuri.concat("/").concat((String)getRequest().getAttributes().get(HistoryResource.HISTORY_URI)).concat("/");
		History history = null;
		try {
			history = historyDomain.retrieve(historyUri);
			
			history.getQuery().setHref(new Uri("uri", historyUri + HistoryQueryServerResource.baseuri));
			history.getRollup().setHref(new Uri("uri", historyUri + HistoryRollupServerResource.baseuri));
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".retrieve", ex);
		}
		
		return history;
		
	}

}
