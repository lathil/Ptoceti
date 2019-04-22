package com.ptoceti.osgi.obix.impl.front.resources;

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



import com.ptoceti.osgi.obix.cache.AlarmCache;
import com.ptoceti.osgi.obix.cache.WatchCache;
import com.ptoceti.osgi.obix.contract.Alarm;
import com.ptoceti.osgi.obix.contract.Nil;
import com.ptoceti.osgi.obix.contract.WatchOut;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Ref;
import com.ptoceti.osgi.obix.object.Status;
import com.ptoceti.osgi.obix.resources.ResourceException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;

@Singleton
public class WatchPoolChangesServerResource extends AbstractServerResource {

    public static final String baseuri = "poolchanges/";

    public static final String uri = WatchServerResource.uri.concat(baseuri);

	private WatchCache cache;
	private AlarmCache alarmCache;
	
	@Inject
	public WatchPoolChangesServerResource(WatchCache cache, AlarmCache alarmCache) {
		this.cache = cache;
		this.alarmCache = alarmCache;
	}

    @POST
    @Consumes({"application/xml", "application/json"})
    public WatchOut poolChanges(@PathParam(WatchServerResource.WATCH_URI) String watchuri, Nil nil) throws ResourceException {
        String watchUri = WatchServerResource.baseuri.concat("/").concat(watchuri).concat("/");
		try {
			
			WatchOut result =  cache.poolChanges(watchUri);
			List<Obj> objList = result.getValuesList().getChildrens();
			for( Obj obj: objList){
				// check if there is an alarm associated with this object
				Ref alarmRef = (Ref)obj.getChildren("alarm");
				if( alarmRef != null){
					Alarm alarm = alarmCache.retrieve(alarmRef.getHref().getPath());
					if( alarm != null){
						// if yes, then check status
						if( obj.getStatus().compareTo(alarm.getStatus()) != 0){
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
			throw new ResourceException("Exception in " + this.getClass().getName() + ".poolChanges", ex);
		}
	}

}
