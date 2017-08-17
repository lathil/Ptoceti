package com.ptoceti.osgi.obix.impl.resources.server;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : WatchPoolRefreshServerResource.java
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


import java.util.List;

import org.restlet.resource.Post;

import com.google.inject.Inject;
import com.ptoceti.osgi.obix.cache.AlarmCache;
import com.ptoceti.osgi.obix.cache.WatchCache;
import com.ptoceti.osgi.obix.contract.Alarm;
import com.ptoceti.osgi.obix.contract.Nil;
import com.ptoceti.osgi.obix.contract.WatchOut;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.domain.WatchDomain;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Ref;
import com.ptoceti.osgi.obix.object.Status;
import com.ptoceti.osgi.obix.resources.ResourceException;
import com.ptoceti.osgi.obix.resources.WatchPoolRefreshResource;
import com.ptoceti.osgi.obix.resources.WatchResource;

public class WatchPoolRefreshServerResource extends AbstractServerResource implements WatchPoolRefreshResource {

	private WatchCache cache;
	private AlarmCache alarmCache;
	
	@Inject
	public WatchPoolRefreshServerResource(WatchCache cache, AlarmCache alarmCache) {
		this.cache = cache;
		this.alarmCache = alarmCache;
	}
	
	@Post("xml|json")
	public WatchOut poolRefresh(Nil nil) throws ResourceException {
		String watchUri = WatchResource.baseuri.concat("/").concat((String)getRequest().getAttributes().get(WatchResource.WATCH_URI)).concat("/");
		try {
			WatchOut result = cache.poolRefresh(watchUri);
			List<Obj> objList = result.getValuesList().getChildrens();
			for( Obj obj: objList){
				// check if there is an alarm associated with this object
				Ref alarmRef = (Ref)obj.getChildren("alarm");
				if( alarmRef != null){
					Alarm alarm = alarmCache.retrieve(alarmRef.getHref().getPath());
					if( alarm != null){
						// if yes, then check status
						if( obj.getStatus() != null && alarm.getStatus() != null && obj.getStatus().compareTo(alarm.getStatus()) != 0){
							if( alarm.getStatus().compareTo(Status.UNACKED) == 0 || alarm.getStatus().compareTo(Status.UNACKEDALARM) == 0 || alarm.getStatus().compareTo(Status.ALARM) == 0){
								obj.setStatus(alarm.getStatus());
							} else if(obj.getStatus().compareTo(Status.UNACKED) == 0 || obj.getStatus().compareTo(Status.UNACKEDALARM) == 0 || obj.getStatus().compareTo(Status.ALARM) == 0) {
								obj.setStatus(alarm.getStatus());
							}
						}
					}
				}
			}
			return result;
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".poolRefresh", ex);
		}
	}

}
