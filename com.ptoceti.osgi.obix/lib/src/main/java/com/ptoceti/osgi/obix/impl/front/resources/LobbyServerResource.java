package com.ptoceti.osgi.obix.impl.front.resources;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : LobbyServerResource.java
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



import com.ptoceti.osgi.obix.contract.Lobby;
import com.ptoceti.osgi.obix.domain.ObjDomain;

import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.resources.ResourceException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Singleton
@Path(LobbyServerResource.uri)
public class LobbyServerResource extends AbstractServerResource {

    public static final String uri = "/";

    private ObjDomain objDomain;

    @Inject
    public LobbyServerResource(ObjDomain pointDomain) {
        this.objDomain = pointDomain;
    }

    @GET
    @Produces({"application/xml", "application/json"})
    public Lobby retrieve() throws ResourceException{
        Lobby lobby = new Lobby();

        lobby.setHref(new Uri("uri", LobbyServerResource.uri));

        lobby.setAbout(new Uri("uri", AboutServerResource.uri));
        lobby.setWatchService(new Uri("uri", WatchServiceServerResource.uri));

        lobby.setHistoryService(new Uri("uri", HistoryServiceServerResource.uri));

        lobby.setAlarmService(new Uri("uri", AlarmServiceServerResource.uri));

        lobby.getBatch().setHref(new Uri("uri",BatchServerResource.uri));

        lobby.getSearch().setHref(new Uri("uri", SearchServerResource.uri));


        return lobby;
    }
}
