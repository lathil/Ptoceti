package com.ptoceti.osgi.obix.impl.resources.server;

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


import java.util.List;

import com.google.inject.Inject;
import com.ptoceti.osgi.obix.contract.Lobby;
import com.ptoceti.osgi.obix.custom.contract.MonitoredPoint;
import com.ptoceti.osgi.obix.custom.contract.DigitPoint;
import com.ptoceti.osgi.obix.custom.contract.ReferencePoint;
import com.ptoceti.osgi.obix.custom.contract.SwitchPoint;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.domain.ObjDomain;

import org.restlet.resource.Get;


import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Ref;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.resources.AboutResource;
import com.ptoceti.osgi.obix.resources.LobbyResource;
import com.ptoceti.osgi.obix.resources.ResourceException;
import com.ptoceti.osgi.obix.resources.WatchServiceResource;

public class LobbyServerResource extends AbstractServerResource implements LobbyResource{


	private ObjDomain objDomain;
	
	@Inject
	public LobbyServerResource(ObjDomain pointDomain) {
		this.objDomain = pointDomain;
	}
	
	@Get
	public Lobby retrieve() throws ResourceException{		
		Lobby lobby = new Lobby();
		
		lobby.setHref(new Uri("uri",LobbyResource.uri));
		
		lobby.setAbout(new Uri("uri", AboutResource.uri));
		lobby.setWatchService(new Uri("uri", WatchServiceResource.uri));
		
		lobby.getBatch().setHref(new Uri("uri",BatchServerResource.uri));
		
		try {
			//Add all the points we could find.
			
			List<Obj> lobbyPointList = objDomain.getObixObjsByContract(MonitoredPoint.contract);
			lobbyPointList.addAll(objDomain.getObixObjsByContract(SwitchPoint.contract));
			lobbyPointList.addAll(objDomain.getObixObjsByContract(ReferencePoint.contract));
			lobbyPointList.addAll(objDomain.getObixObjsByContract(DigitPoint.contract));
			for(Obj lobbyPoint : lobbyPointList ){
				Ref ref = new Ref();
				ref.setHref(lobbyPoint.getHref());
				ref.setIs(lobbyPoint.getIs());
				lobby.addChildren(ref);
			}
			
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".retrieve", ex);
		}
		
		return lobby;
	}
}
