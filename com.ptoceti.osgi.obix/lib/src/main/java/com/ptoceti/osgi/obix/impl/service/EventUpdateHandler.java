package com.ptoceti.osgi.obix.impl.service;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : EventUpdateHandler.java
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
import java.util.regex.PatternSyntaxException;

import org.osgi.service.log.LogService;
import org.osgi.service.wireadmin.BasicEnvelope;
import org.osgi.service.wireadmin.Envelope;
import org.osgi.service.wireadmin.Wire;

import com.google.inject.Inject;
import com.ptoceti.osgi.obix.cache.AlarmCache;
import com.ptoceti.osgi.obix.cache.HistoryCache;
import com.ptoceti.osgi.obix.cache.ObjCache;
import com.ptoceti.osgi.obix.contract.History;
import com.ptoceti.osgi.obix.contract.Point;
import com.ptoceti.osgi.obix.custom.contract.MeasurePoint;
import com.ptoceti.osgi.obix.custom.contract.MonitoredPoint;
import com.ptoceti.osgi.obix.custom.contract.DigitPoint;
import com.ptoceti.osgi.obix.custom.contract.ReferencePoint;
import com.ptoceti.osgi.obix.custom.contract.SwitchPoint;
import com.ptoceti.osgi.obix.impl.back.converters.OsgiConverterFactory;
import com.ptoceti.osgi.obix.impl.observer.AlarmObserver;
import com.ptoceti.osgi.obix.impl.observer.HistoryObserver;
import com.ptoceti.osgi.obix.impl.service.CommandHandler.AsyncCommand;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Ref;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.object.Val;
import com.ptoceti.osgi.obix.observable.IObserver;
import com.ptoceti.osgi.obix.resources.ObjResource;

public class EventUpdateHandler {

	private ObjCache objCache;
	
	private HistoryCache historyCache;
	
	private AlarmCache alarmCache;
	
	@Inject
	public EventUpdateHandler(ObjCache cache, HistoryCache historyCache, AlarmCache alarmCache ) {
		this.objCache = cache;
		this.historyCache = historyCache;
		this.alarmCache = alarmCache;
	}
	
	/*
	 * Update or create the obix objects corresponding to an osgi envelope.
	 * 
	 * @param Envelope The envelope containing the object to map to obix.
	 * 
	 * @param Wire The wire that provided the envelope.
	 */
	public void update(Envelope env, Wire wire) {

		Object val = env.getValue();
		String name = env.getIdentification().toString();
		String scope = env.getScope();
		
		Val obj  = OsgiConverterFactory.getInstance().getConverterFromOsgi(val).toBaseObix(val);
		obj.setName(name);
		obj.setDisplayName(name);

		// We look for an obix oject that already exists with the same scope
		String href = this.mapScopeToHref(scope + "." + name);
		// The href is then translated as a relative URI (this because the
		// server adress
		// can change. ). This rel Uri will be normalise to an absolute Url on
		// requests by the
		// client.

		href = ObjResource.uri + href;
		// obj.setHref(new Uri("", href));

		updateWith(obj, href);

	}
	
	public void updateWith(Val obj, String href) {
		Activator.getObixService().getExecutorService().submit(new AsyncUpdateCommand(obj, href));
	}
	
	protected class AsyncUpdateCommand implements Callable {

		Val obj;
		String href;
		
		public AsyncUpdateCommand(Val obj, String href){
			this.obj = obj;
			this.href = href;
		}
		
		@Override
		public Object call() throws Exception {
			updateObject(obj, href);
			
			return true;
		}
		
	}
	
	private void updateObject(Val obj, String href) {
		try {
			// Preload object to fetch in cache
			Obj cachedObj = objCache.getObixObj(new Uri("", href ));
			if( cachedObj != null ){
				// If object exists, check if we have a reference to an history object
				Obj historyRef = cachedObj.getChildren("history");
				if( historyRef != null && historyRef instanceof Ref){
					// check that if there is an history linked to this object, the observer exists.
					List<IObserver<? super Obj>> observers = cachedObj.getObservers();
					boolean found = false;
					for( IObserver<? super Obj> observer : observers){
						if( observer instanceof HistoryObserver){
							found = true;
							break;
						}
					}
					if( !found){
						historyCache.addHistoryObserver(historyRef.getHref().getPath(), cachedObj);
					}
				}
				Obj alarmRef = cachedObj.getChildren("alarm");
				if( alarmRef != null && alarmRef instanceof Ref){
					// check that if there is an alarm linked to this object, the observer exists.
					List<IObserver<? super Obj>> observers = cachedObj.getObservers();
					boolean found = false;
					for( IObserver<? super Obj> observer : observers){
						if( observer instanceof AlarmObserver){
							found = true;
							break;
						}
					}
					if( !found){
						alarmCache.addAlarmObserver(alarmRef.getHref().getPath(), cachedObj);
					}
				}
			}
			if( obj.getIs().containsContract(DigitPoint.contract)){
				obj.setHref(new Uri("", href ));
				objCache.createUpdateObixObj(obj);
			} else if (obj.getIs().containsContract(SwitchPoint.contract)) {
				obj.setHref(new Uri("", href ));
				objCache.createUpdateObixObj(obj);
			} else if (obj.getIs().containsContract(ReferencePoint.contract)) {
				obj.setHref(new Uri("", href ));
				objCache.createUpdateObixObj(obj);
			} else if( obj.getIs().containsContract(MeasurePoint.contract)){
				obj.setHref(new Uri("", href ));
				objCache.createUpdateObixObj(obj);
				
			} else {
	
				objCache.createUpdateObixObj(obj);
			}
			
		} catch (Exception ex ) {
			Activator.log(LogService.LOG_ERROR, "Error while handling wire input: " + ex.toString());
		}
	}
	
	protected String mapScopeToHref(String scope) {

		String result = null;
		try {
			// Remove white space
			String resultNoWhites = scope.replaceAll("\t\n\f\r", "");
			// Replace any '.' by '/'.
			String resultNoDots = resultNoWhites.replaceAll("[.]", "/");
			result = resultNoDots;
		} catch (PatternSyntaxException e) {
			Activator.log(LogService.LOG_DEBUG, "Error while mapping Pid to Href: " + e.toString());
		} catch (Exception e) {
			Activator.log(LogService.LOG_DEBUG, "Error while mapping Pid to Href: " + e.toString());
		}

		return result;
	}
	
	
}
