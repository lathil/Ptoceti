package com.ptoceti.osgi.obix.cache;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : ObjCache.java
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

import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Uri;

public interface ObjCache {

	/**
	 * Get Obix oject specified by given uri
	 * 
	 * @param href ref to the object
	 * @return Obj an obix Obj
	 * @throws DomainException on accessing the object
	 */
	public Obj getObixObj(Uri href) throws DomainException;
	
	/**
	 * Get all Obix ojects that responds to specified contract
	 * 
	 * @param contract describe the type of objects to be returned
	 * @return List an list of obix Obj
	 * @throws DomainException on accessing the object
	 */
	public List<Obj> getObixObjsByContract(Contract contract) throws DomainException;
	
	/**
	 * Update an Obix oject at the uri specified
	 * 
	 * @param href ref to the object
	 * @param updatePbj  value to take as an update
	 * @return Obj the updated object
	 * @throws DomainException on accessing the object
	 */
	public Obj updateObixObjAt(Uri href, Obj updatePbj) throws DomainException;
	
	/**
	 * Update an existing Obix object or if it does not exists, create it.
	 * 
	 * @param updateObj object to take as an update
	 * @return Obj updated object
	 * @throws DomainException on accessing the object
	 */
	public Obj createUpdateObixObj(Obj updateObj) throws DomainException;
	
	/**
	 * Create a new Obix object
	 * 
	 * @param newObj object to be taken as input
	 * @return Obj object persisted
	 * @throws DomainException on accessing the object
	 */
	public Obj createObixObj(Obj newObj) throws DomainException;
}
