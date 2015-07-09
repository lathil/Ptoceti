package com.ptoceti.osgi.obix.impl.cache;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : HistoryCacheImpl.java
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
import com.ptoceti.osgi.obix.cache.HistoryCache;
import com.ptoceti.osgi.obix.contract.History;
import com.ptoceti.osgi.obix.contract.HistoryRecord;
import com.ptoceti.osgi.obix.contract.HistoryRollupRecord;
import com.ptoceti.osgi.obix.contract.Watch;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.domain.HistoryDomain;
import com.ptoceti.osgi.obix.domain.ObjDomain;
import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Int;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Reltime;
import com.ptoceti.osgi.obix.object.Val;

public class HistoryCacheImpl extends ObjCacheImpl implements HistoryCache {

	HistoryDomain historyDomain;
	
	@Inject
	HistoryCacheImpl(HistoryDomain historyDomain, ObjDomain objDomain, Cache<String, Obj> cache){
		
		super(objDomain, cache);
		this.historyDomain = historyDomain;
	}
	
	@Override
	public History make(Contract of) throws DomainException {
		History result =  historyDomain.make(of);
		cache.put(result.getHref().getPath(), result);
		return result;
	}

	@Override
	public History retrieve(final String uri) throws DomainException {
		History result = null;
		try {
			result = (History) cache.get(uri, new Callable<History>(){

				@Override
				public History call() throws Exception {
					History obj =  historyDomain.retrieve(uri);
					if( obj == null) throw new ObjNotFoundException("Obj at "  + uri + " not found");
					return obj;
				}
				
			});
		} catch (ExecutionException e) {
			
		}
		return result;
	}

	@Override
	public void addRecord(String uri, Val value) throws DomainException {
		historyDomain.addRecord(uri, value);
	}

	@Override
	public List<HistoryRecord> getRecords(String uri, Int limit, Abstime from, Abstime to) throws DomainException {
		return historyDomain.getRecords(uri, limit, from, to);
	}

	@Override
	public List<HistoryRollupRecord> getRollUprecords(String uri, Int limit, Abstime from, Abstime to, Reltime roolUpDuration) throws DomainException {
		return historyDomain.getRollUprecords(uri, limit, from, to, roolUpDuration);
	}

}
