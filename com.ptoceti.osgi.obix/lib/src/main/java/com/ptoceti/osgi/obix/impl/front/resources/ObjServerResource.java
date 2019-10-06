package com.ptoceti.osgi.obix.impl.front.resources;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : ObjServerResource.java
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



import com.ptoceti.osgi.obix.cache.AlarmCache;
import com.ptoceti.osgi.obix.cache.ObjCache;
import com.ptoceti.osgi.obix.contract.Alarm;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.impl.front.exception.HttpNotFoundException;
import com.ptoceti.osgi.obix.impl.service.CommandHandler;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Ref;
import com.ptoceti.osgi.obix.object.Status;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.object.Val;
import com.ptoceti.osgi.obix.resources.ResourceException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;

@Singleton
@Path(ObjServerResource.uri)
public class ObjServerResource extends AbstractServerResource {

    public static final String OBJ_URI = "objuri";
    public static final String baseuri = "/";
    public static final String uri = baseuri + "/{" + OBJ_URI + "}/";

	private ObjCache cache;
	private AlarmCache alarmCache;
	
	@Inject
	public ObjServerResource( ObjCache cache, AlarmCache alarmCache) {
		this.cache = cache;
		this.alarmCache = alarmCache;
	}

    @GET
    @Produces({"application/xml", "application/json"})
    public Obj retrieve(@PathParam(ObjServerResource.OBJ_URI) String href) throws ResourceException {

		Uri hrefUri = new Uri("href", uri + href);
		Obj obj = null;
		try {
			obj = cache.getObixObj(hrefUri);
            if (obj != null) {
                // check if there is an alarm associated with this object
                Ref alarmRef = (Ref) obj.getChildren("alarm");
                if (alarmRef != null) {
                    Alarm alarm = alarmCache.retrieve(alarmRef.getHref().getPath());
                    if (alarm != null) {
                        // if yes, then check status
                        if (obj.getStatus().compareTo(alarm.getStatus()) != 0) {
                            if (alarm.getStatus().compareTo(Status.UNACKED) == 0 || alarm.getStatus().compareTo(Status.UNACKEDALARM) == 0 || alarm.getStatus().compareTo(Status.ALARM) == 0) {
                                obj.setStatus(alarm.getStatus());
                            } else if (obj.getStatus().compareTo(Status.UNACKED) == 0 || obj.getStatus().compareTo(Status.UNACKEDALARM) == 0 || obj.getStatus().compareTo(Status.ALARM) == 0) {
                                obj.setStatus(alarm.getStatus());
                            }
                        }
                    }
                }
            } else {
                throw new HttpNotFoundException("obj at : " + hrefUri + "not found");
			}
			
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".retrieve", ex);
		}
		
		return obj;
	}
	
	/**
	 * Resource creation
	 * @param objIn the object to create
	 * @throws ResourceException on error creating the obj
	 */
    @POST
    @Consumes({"application/xml", "application/json"})
    public void create(@PathParam("href") String href, Obj objIn) throws ResourceException {

		Uri hrefUri = new Uri("href", uri + href);
		
		try {
			cache.updateObixObjAt(hrefUri, objIn);
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".update", ex);
		}
		
	}
	
	/**
	 * Resource update
	 * @param objIn obj to update
	 * @throws ResourceException on error updating the obj
	 */
    @PUT
    @Consumes({"application/xml", "application/json"})
    public void update(@PathParam("href") String href, Obj objIn) throws ResourceException {

		Uri hrefUri = new Uri("href", uri + href);
		
		try {
			if( objIn instanceof Val){
				CommandHandler commandHandler = new CommandHandler();
				commandHandler.sendCommand((Val)objIn);
			}
			
			cache.updateObixObjAt(hrefUri, objIn);
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".update", ex);
		}
		
	}

}
