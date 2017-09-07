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
import com.ptoceti.osgi.obix.impl.command.AddHistoryRecordCommand;
import com.ptoceti.osgi.obix.impl.guice.GuiceContext;
import com.ptoceti.osgi.obix.impl.observer.HistoryObserver;
import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Int;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Real;
import com.ptoceti.osgi.obix.object.Ref;
import com.ptoceti.osgi.obix.object.Reltime;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.object.Val;

public class HistoryCacheImpl extends ObjCacheImpl implements HistoryCache {

	HistoryDomain historyDomain;
	

	@Inject
	HistoryCacheImpl(HistoryDomain historyDomain, ObjDomain objDomain, Cache<String, Obj> cache){
		
		super(objDomain, cache);
		this.historyDomain = historyDomain;
	}
	
	@Override
	public History make(Ref ref) throws DomainException {

		History history;
		// get the object for which the history will be created
		Obj obj = getObixObj(ref.getHref());
		// check if there is already a history for this object
		Obj historyRef = obj.getChildren("history");
		
		if( historyRef == null ){
			// create a history object of type same as referenced object
			history = make(obj.getContract(), obj.getDisplayName() + ".history");
			// create a observer for the history object .. and add a comman object for injecting history records
			HistoryObserver observer = new HistoryObserver( history.getHref().getPath(), GuiceContext.Instance.getInjector().getInstance(AddHistoryRecordCommand.class));
			// .. and set it
			history.setObserver(observer);
			// create a reference to history object ..
			historyRef = new Ref("history", history.getHref());
			// .. and add it to the object
			historyRef.setIs(History.contract);
			objDomain.addChildObject(ref.getHref(), historyRef);
			obj.addChildren(historyRef);
			// finally add history observer to observable object
			obj.addObserver(observer);
		} else {
			history = retrieve(ref.getHref().getPath());
		}
		
		return history;
	}
	
	public void addHistoryObserver(String uri, Obj observable) throws DomainException {
		
		History history = retrieve(uri);
		if( history != null){
			// create a observer for the history object .. and add a comman object for injecting history records
			HistoryObserver observer = new HistoryObserver( history.getHref().getPath(), GuiceContext.Instance.getInjector().getInstance(AddHistoryRecordCommand.class));
			// .. and set it
			history.setObserver(observer);
			// finally add history observer to observable object
			observable.addObserver(observer);
		}
	}

	@Override
	public History make(Contract of, String displayName) throws DomainException {
		History result =  historyDomain.make(of, displayName);
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
	
	public boolean delete(String uri) throws DomainException {
		// get hold of history ...
		History history = retrieve(uri);
		// ... and object that has a reference to it
		Obj refObj = objDomain.getObixObjWithRefTo(new Uri("ref", uri));
		if( refObj != null && history != null)	{
			// if both found, get counterpart in cache ...
			Obj observableObj = getObixObj(refObj.getHref());
			// ... and remove reference to observer
			observableObj.removeObserver(history.getObserver());
			// remove reference to ref object in persistant store ..
			objDomain.deleteChildObject(observableObj.getHref(), "history");
			// .. and reference in cache
			observableObj.removeChildren("history");
			
			// remove from persistent storage ...
			historyDomain.remove(uri);
			// .. and from cache
			cache.invalidate(uri);
			return true;
		}
		
		return false;
	}

	@Override
	public void addRecord(String uri, Val value) throws DomainException {
		historyDomain.addRecord(uri, value);
	}

	@Override
	public List<HistoryRecord> getRecords(String uri, Int limit, Abstime start, Abstime end) throws DomainException {
		return historyDomain.getRecords(uri, limit, start, end);
	}

	@Override
	public List<HistoryRollupRecord> getRollUprecords(String uri, Int limit, Abstime start, Abstime end, Reltime roolUpDuration) throws DomainException {
		// get hold of history ...
		History history = retrieve(uri);
		// ... and object that has a reference to it
		Obj refObj = objDomain.getObixObjWithRefTo(new Uri("ref", uri));
		List<HistoryRollupRecord> rollUps = null;
		rollUps = historyDomain.getRollUprecords(uri, limit, start, end, roolUpDuration);
		
		if( refObj != null && rollUps != null)	{
			// if both found, get counterpart in cache ...
			Obj observableObj = getObixObj(refObj.getHref());
			
			for( HistoryRollupRecord rollup : rollUps){
				if( observableObj instanceof Real) {
					rollup.getAvg().setUnit(((Real)observableObj ).getUnit());
					rollup.getMax().setUnit(((Real)observableObj ).getUnit());
					rollup.getMin().setUnit(((Real)observableObj ).getUnit());
					rollup.getSum().setUnit(((Real)observableObj ).getUnit());
				} else if( observableObj instanceof Int) {
					rollup.getAvg().setUnit(((Int)observableObj ).getUnit());
					rollup.getMax().setUnit(((Int)observableObj ).getUnit());
					rollup.getMin().setUnit(((Int)observableObj ).getUnit());
					rollup.getSum().setUnit(((Int)observableObj ).getUnit());
				} 
			}
		}
		
		return rollUps;
	}


}
