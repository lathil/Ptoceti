package com.ptoceti.osgi.obix.impl.cache;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : ObjCacheImpl.java
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.Cache;
import com.google.inject.Inject;
import com.ptoceti.osgi.obix.cache.ObjCache;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.domain.ObjDomain;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Uri;

public class ObjCacheImpl extends AbstractCache implements ObjCache{

	ObjDomain objDomain;
	
	@Inject
	ObjCacheImpl(ObjDomain objDomain, Cache<String, Obj> cache){
		
		super( cache);
		this.objDomain = objDomain;
	}
	
	@Override
	public Obj getObixObj(final Uri href) throws DomainException {
		// TODO Auto-generated method stub
	
		Obj result = null;
		try {
			result = cache.get(href.getPath(), new Callable<Obj>(){

				@Override
				public Obj call() throws Exception {
					Obj obj =  objDomain.getObixObj(href);
					if( obj == null) throw new ObjNotFoundException("Obj at "  + href.getPath() + " not found");
					return obj;
				}
				
			});
		} catch (ExecutionException e) {
			
		}
		return result;
	}

	@Override
	public List<Obj> getObixObjsByContract(Contract contract)
			throws DomainException {
		return objDomain.getObixObjsByContract(contract);
	}

	@Override
	public Obj updateObixObjAt(Uri href, Obj updateObj) throws DomainException {
		Obj obj = getObixObj(href);
		if( obj != null){
			if (obj.updateWith(updateObj)){
				objDomain.updateObixObjAt(href, updateObj);
			}
		}
		return obj;
	}

	@Override
	public Obj createUpdateObixObj(Obj updateObj) throws DomainException {
		Obj obj = getObixObj(updateObj.getHref());
		if( obj == null){
			obj = objDomain.createUpdateObixObj(updateObj);
			cache.put(obj.getHref().getPath(), obj);
		} else {
			if (obj.updateWith(updateObj)){
				objDomain.updateObixObjAt(updateObj.getHref(), updateObj);
			}
		}
		return obj;
	}

	
	@Override
	public Obj createObixObj(Obj newObj) throws DomainException {
		Obj result = objDomain.createObixObj(newObj);
		cache.put(result.getHref().getPath(), result);
		return result;
	}

}
