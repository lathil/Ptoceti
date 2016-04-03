package com.ptoceti.osgi.obix.impl.cache;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : WatchCacheImpl.java
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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.Cache;
import com.google.inject.Inject;
import com.ptoceti.osgi.obix.cache.WatchCache;
import com.ptoceti.osgi.obix.contract.Watch;
import com.ptoceti.osgi.obix.contract.WatchIn;
import com.ptoceti.osgi.obix.contract.WatchOut;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.domain.ObjDomain;
import com.ptoceti.osgi.obix.domain.WatchDomain;
import com.ptoceti.osgi.obix.impl.observer.WatchObserver;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.observable.IObserver;

public class WatchCacheImpl extends ObjCacheImpl implements WatchCache {

	WatchDomain watchDomain;
	
	@Inject
	WatchCacheImpl(WatchDomain watchDomain, ObjDomain objDomain, Cache<String, Obj> cache){
		
		super( objDomain, cache);
		this.watchDomain = watchDomain;
	}
	
	@Override
	public Watch make() throws DomainException {
		Watch watch = watchDomain.make();
		WatchObserver observer = new WatchObserver();
		watch.setObserver(observer);
		cache.put(watch.getHref().getPath(), watch);
		return watch;
	}


	@Override
	public Watch retrieve(final String uri) throws DomainException {
		Watch result = null;
		try {
			result = (Watch) cache.get(uri, new Callable<Watch>(){

				@Override
				public Watch call() throws Exception {
					Watch watch =  watchDomain.retrieve(uri);
					if( watch == null) throw new ObjNotFoundException("Obj at "  + uri + " not found");
					WatchObserver observer = new WatchObserver();
					watch.setObserver(observer);
					List<Obj> childs = watch.getChildrens();
					// add watch observer to all watched resources
					for( Obj nextUri : childs){
						if( nextUri instanceof Uri){
							Obj nextObj = getObixObj((Uri)nextUri);
							if( nextObj != null){
								nextObj.addObserver(observer);
							}
						}
					}
					
					return watch;
				}
				
			});
		} catch (ExecutionException e) {
			
		}
		return result;
	}

	/**
	 * Add a list of references to a watch.
	 * 
	 * 
	 */
	@Override
	public WatchOut addWatch(String uri, WatchIn in) throws DomainException {
		List<Uri> uris = watchDomain.addWatch(uri, in);
		
		WatchOut watchOut = new WatchOut();
		Watch currentWatch = retrieve(uri);
		IObserver<? super Obj> observer = currentWatch.getObserver();
		for( Uri nextUri : uris){
			// get object link to the ref
			Obj nextObj = getObixObj(nextUri);
			if( nextObj != null){
				// add ref to persistent store
				currentWatch.getChildrens().add(nextUri);
				// add watch's observer  to observable resource
				nextObj.addObserver(observer);
				watchOut.getValuesList().addChildren(nextObj);
			}
		}

		return watchOut;
	}
	
	/**
	 * Remove a list of references from a watch
	 * 
	 */
	@Override
	public void removeWatch(String uri, WatchIn in) throws DomainException {
		List<Uri> uris = watchDomain.removeWatch(uri, in);
		Watch currentWatch = retrieve(uri);
		IObserver<? super Obj> observer = currentWatch.getObserver();
		for( Uri nextUri : uris){
			// get object link to the ref
			Obj nextObj = getObixObj(nextUri);
			if( nextObj != null){
				currentWatch.getChildrens().remove(nextUri);
				// remove link to observer
				nextObj.removeObserver(observer);
			}
		}
	}
	
	/**
	 * Delete the watch.
	 * 
	 */
	@Override
	public void deleteWatch(String uri) throws DomainException {
		Watch currentWatch = retrieve(uri);
		IObserver<? super Obj> observer = currentWatch.getObserver();
		List<Obj> childs = currentWatch.getChildrens();
		// remove all links to ref objects
		for( Obj nextUri : childs){
			if( nextUri instanceof Uri){
				Obj nextObj = getObixObj((Uri)nextUri);
				if( nextObj != null){
					// remove link to observer
					nextObj.removeObserver(observer);
				}
			}
		}
		
		watchDomain.deleteWatch(uri);
		cache.invalidate(uri);
		
	}

	@Override
	public WatchOut poolChanges(String uri) throws DomainException {
		Watch currentWatch = retrieve(uri);
		WatchOut watchOut = new WatchOut();
		List<Obj> changedObj = ((WatchObserver)currentWatch.getObserver()).getChangedObj();
		for( Obj obj : changedObj){
			watchOut.getValuesList().addChildren(obj);
		}
		/**
		List<Obj> childs = currentWatch.getChildrens();
		for( Obj child : childs){
			if( child instanceof Uri){
				Obj obj = getObixObj((Uri)child);
				if( child.getUpdateTimeStamp() < obj.getUpdateTimeStamp()){
					watchOut.getValuesList().addChildren(obj);
					child.setUpdateTimeStamp(Calendar.getInstance().getTimeInMillis());
				}
			}
		}
		**/
		return watchOut;
	}

	@Override
	public WatchOut poolRefresh(String uri) throws DomainException {
		
		Watch currentWatch = retrieve(uri);
		WatchOut watchOut = new WatchOut();
		List<Obj> childs = currentWatch.getChildrens();
		for( Obj child : childs){
			if( child instanceof Uri){
				Obj obj = getObixObj((Uri)child);
				watchOut.getValuesList().addChildren(obj);
				child.setUpdateTimeStamp(Calendar.getInstance().getTimeInMillis());
			}
		}
		return watchOut;
		
	}



	@Override
	public List<Obj> getObixWatches() throws DomainException {
		return watchDomain.getObixWatches();
	}

	@Override
	public void update(String uri, Watch watchIn) throws DomainException {
		watchDomain.update(uri, watchIn);
	}

}
