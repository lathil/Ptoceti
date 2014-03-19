package com.ptoceti.osgi.obix.impl.resources.server;

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
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.google.inject.Inject;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.domain.ObjDomain;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.resources.ObjResource;
import com.ptoceti.osgi.obix.resources.ResourceException;

public class ObjServerResource extends AbstractServerResource implements ObjResource {

	private ObjDomain domain;
	
	@Inject
	public ObjServerResource( ObjDomain domain) {
		this.domain = domain;
	}
	
	@Get
	public Obj retrieve() throws ResourceException {
		String href = (String)getRequestAttributes().get("href");
		
		Uri hrefUri = new Uri("href", uri + href);
		Obj obj = null;
		try {
			obj = domain.getObixObj(hrefUri);
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".retrieve", ex);
		}
		
		return obj;
	}
	
	/**
	 * Resource creation
	 * @param objIn
	 * @throws ResourceException
	 */
	@Post
	public void create (Obj objIn) throws ResourceException{
		String href = (String)getRequestAttributes().get("href");
		Uri hrefUri = new Uri("href", uri + href);
		
		try {
			domain.updateObixObjAt(hrefUri, objIn);
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".update", ex);
		}
		
	}
	
	/**
	 * Resource update
	 * @param objIn
	 * @throws ResourceException
	 */
	@Put
	public void update(Obj objIn) throws ResourceException{
		String href = (String)getRequestAttributes().get("href");
		Uri hrefUri = new Uri("href", uri + href);
		
		try {
			domain.updateObixObjAt(hrefUri, objIn);
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".update", ex);
		}
		
	}

}
