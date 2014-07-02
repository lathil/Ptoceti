package com.ptoceti.osgi.obix.impl.resources.server;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : WatchPoolChangesServerResource.java
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


import java.util.Calendar;

import org.osgi.service.log.LogService;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.google.inject.Inject;
import com.ptoceti.osgi.obix.contract.Nil;
import com.ptoceti.osgi.obix.contract.WatchOut;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.domain.WatchDomain;
import com.ptoceti.osgi.obix.impl.Activator;
import com.ptoceti.osgi.obix.resources.ResourceException;
import com.ptoceti.osgi.obix.resources.WatchAddResource;
import com.ptoceti.osgi.obix.resources.WatchPoolChangesResource;
import com.ptoceti.osgi.obix.resources.WatchResource;

public class WatchPoolChangesServerResource extends AbstractServerResource implements WatchPoolChangesResource {

	private WatchDomain watchDomain;
	
	@Inject
	public WatchPoolChangesServerResource(WatchDomain domain) {
		watchDomain = domain;
	}
	
	@Post("xml|json")
	public WatchOut poolChanges(Nil nil) throws ResourceException {
		String watchUri = WatchResource.baseuri.concat("/").concat((String)getRequest().getAttributes().get(WatchResource.WATCH_URI)).concat("/");
		try {
			
			Long start = Calendar.getInstance().getTimeInMillis();
			WatchOut result =  watchDomain.poolChanges(watchUri);
			
			Long end = Calendar.getInstance().getTimeInMillis();
			
			Activator.log(LogService.LOG_DEBUG, "WatchPoolChangesServerResource poolChanges time: " +  Long.valueOf(end - start) + " ms ");
			
			return result;
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".poolChanges", ex);
		}
	}

}
