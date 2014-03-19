package com.ptoceti.osgi.obix.domain;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : ObjDomain.java
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


import java.util.List;

import com.ptoceti.osgi.obix.contract.Point;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Uri;

public interface ObjDomain {

	public Obj getObixObj(Uri href) throws DomainException;
	
	public List<Obj> getObixObjsByContract(Contract contract) throws DomainException;
	
	public Obj updateObixObjAt(Uri href, Obj updatePbj) throws DomainException;
	
	public Obj createUpdateObixObj(Obj updateObj) throws DomainException;
	
	public Obj createObixObj(Obj newObj) throws DomainException;
}
