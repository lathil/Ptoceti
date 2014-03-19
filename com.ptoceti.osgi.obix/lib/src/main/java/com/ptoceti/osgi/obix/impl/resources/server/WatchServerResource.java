package com.ptoceti.osgi.obix.impl.resources.server;

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
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.google.inject.Inject;
import com.ptoceti.osgi.obix.contract.Nil;
import com.ptoceti.osgi.obix.contract.Watch;
import com.ptoceti.osgi.obix.contract.WatchIn;
import com.ptoceti.osgi.obix.contract.WatchOut;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.domain.WatchDomain;
import com.ptoceti.osgi.obix.object.Op;
import com.ptoceti.osgi.obix.object.Reltime;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.resources.ResourceException;
import com.ptoceti.osgi.obix.resources.WatchResource;

public class WatchServerResource extends AbstractServerResource implements WatchResource{

	private WatchDomain watchDomain;
	
	@Inject
	public WatchServerResource(WatchDomain domain) {
		watchDomain= domain;
	}
	
	@Get
	public Watch retrieve() throws ResourceException {
		
		String watchUri = WatchResource.baseuri.concat("/").concat((String)getRequest().getAttributes().get(WatchResource.WATCH_URI)).concat("/");
		Watch watch = null;
		try {
			watch = watchDomain.retrieve(watchUri);
			
			watch.getAdd().setHref(new Uri("uri", watchUri + WatchAddServerResource.baseuri));
			watch.getRemove().setHref(new Uri("uri", watchUri + WatchRemoveServerResource.baseuri));
			watch.getDelete().setHref(new Uri("uri", watchUri +WatchDeleteServerResource.baseuri));
			watch.getPoolChanges().setHref(new Uri("uri", watchUri + WatchPoolChangesServerResource.baseuri));
			watch.getPoolRefresh().setHref(new Uri("uri", watchUri + WatchPoolRefreshServerResource.baseuri));
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".retrieve", ex);
		}
		
		return watch;
	}
	
	@Put
	public void update(Watch watchIn) throws ResourceException {
		String watchUri = WatchResource.baseuri.concat("/").concat((String)getRequest().getAttributes().get(WatchResource.WATCH_URI)).concat("/");
		
		try {
			watchDomain.update(watchUri, watchIn);
		} catch (DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".update", ex);
		}
		
	}

}
