package com.ptoceti.osgi.obix.impl.domain;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : WatchDomainImpl.java
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


import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.ptoceti.osgi.obix.contract.Point;
import com.ptoceti.osgi.obix.contract.Watch;
import com.ptoceti.osgi.obix.contract.WatchIn;
import com.ptoceti.osgi.obix.contract.WatchOut;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.domain.WatchDomain;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Err;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Reltime;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.resources.WatchResource;
import com.ptoceti.osgi.obix.impl.entity.EntityException;
import com.ptoceti.osgi.obix.impl.entity.ObjEntity;
import com.ptoceti.osgi.obix.impl.entity.UriEntity;

public class WatchDomainImpl extends AbstractDomain implements WatchDomain {

	public WatchOut addWatch(String uri, WatchIn in) throws DomainException {
		
		WatchOut watchOut = new WatchOut();
		
		ObjEntity watch = getWatch(uri);
		if( watch != null){
			try {
				for( Obj objUri :  in.getHrefsList().getChildrens()){
					if( objUri instanceof Uri && ((Uri)objUri).getPath().toString() != null ){
						String uriToAdd = ((Uri)objUri).getPath().toString();
						
						// try to fetch the object to check if it exists.
						Obj obixObj = new Obj();
						obixObj.setHref((Uri)objUri);
						ObjEntity objEnt = new ObjEntity(obixObj);
						objEnt = ObjEntity.fetchByHref(objEnt);
						
						if( containsUri( watch, uriToAdd) == null && objEnt != null){
							// uri not already recorded and object exists, add to list.
							watch.addChildren(objUri);
						}
						
						if( objEnt != null){
							watchOut.getValuesList().addChildren(objEnt.getObixObject());
						} else {
							Err errorObj = new Err();
							errorObj.setHref((Uri)objUri);
						}
					}
				}
			} catch(EntityException ex) {
				throw new DomainException("Exception in " + this.getClass().getName() + ".addWatch", ex);
			}
		}
		
		return watchOut;
	}
	
	
	public void removeWatch(String uri, WatchIn in) throws DomainException {
		
		ObjEntity watch = getWatch(uri);
		try {
			if( watch != null){
				for( Obj objUri : in.getHrefsList().getChildrens()){
					if( objUri instanceof Uri && ((Uri)objUri).getPath().toString() != null ){
						String uriTorRemove = ((Uri)objUri).getPath().toString();
						
						ObjEntity uriEntity = containsUri( watch, uriTorRemove);
						if(uriEntity != null) uriEntity.delete();
					}
				}
			}
		} catch(EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".removeWatch", ex);
		}
		
	}

	public void deleteWatch(String uri) throws DomainException {
		
		ObjEntity watch = getWatch(uri);
		try {
			if( watch != null) watch.delete();
		} catch(EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".deleteWatch", ex);
		}
	}

	
	public Watch make() throws DomainException {
		
		String timeStamp = (new Long (Calendar.getInstance().getTimeInMillis())).toString();
		
		Watch watch = new Watch(timeStamp);
		Reltime lease = watch.getLease();
		// initiate lease time to 7 days minutes
		lease.setVal(new Long(7*24*60*60*1000));
		
		watch.setHref(new Uri("uri",WatchResource.baseuri.concat("/").concat(timeStamp).concat("/")));
		ObjEntity objEnt = new ObjEntity(watch);
		try {
			objEnt.create();
		} catch(EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".make", ex);
		}
		
		return (Watch) objEnt.getObixObject();
	}

	public WatchOut poolChanges(String uri) throws DomainException {

		WatchOut watchOut = new WatchOut();
		
		try {
			ObjEntity watch = getWatch(uri);
			if( watch != null){
				// parse each child of the watch
				List<ObjEntity> childs = watch.getChilds();
				
				List<Uri> uris = new ArrayList<Uri>();
				for( ObjEntity uriEnt : childs){
					// filter for uri, as there is also the lease object
					if( uriEnt instanceof UriEntity){
						uris.add((Uri)uriEnt.getObixObject());
					}
				}
				
				List<ObjEntity> monitoredObjs = null;
				// get all objects identified by the watch's href uris
				if( uris.size() > 0){
					ObjEntity fetchObj = new ObjEntity(new Obj());
					monitoredObjs = fetchObj.fetchByHrefs(uris);
				}
				
				List<ObjEntity> updatedUris = new ArrayList<ObjEntity>();
				
				for( ObjEntity uriEnt : childs){
					// filter for uri, as there is also the lease object
					if( uriEnt instanceof UriEntity){
						// uri represent an object to watch for
						Uri watchedUri = (Uri)uriEnt.getObixObject();
						
						Date timestamp = uriEnt.getModificationDate() == null ? uriEnt.getCreationDate() : uriEnt.getModificationDate();
						boolean isModified = false;
						
						Obj watchedObj = new Obj();
						watchedObj.setHref(watchedUri);
						
						for( ObjEntity watchObjEnt : monitoredObjs ){
							if( watchObjEnt.getObj_uri().equals(watchedUri.getPath())){
	
								watchObjEnt.fetchChildrens();
								for( ObjEntity childEntity : (List<ObjEntity>) watchObjEnt.getChilds()){
									// check if one of the child object has been modified ( only check one )
									if( childEntity.getModificationDate() != null && childEntity.getModificationDate().after(timestamp) && !isModified ){
										// add the updated object to the list
										watchOut.getValuesList().addChildren(watchObjEnt.getObixObject());
										// update the uri to mark the last modification time;
										updatedUris.add(uriEnt);
										// yes
										isModified = true;
									}
									watchObjEnt.getObixObject().addChildren(childEntity.getObixObject());
								}
								// if the object exists and it has been modified since last update of the watched uri .. ( and none of the childs has been modified)
								if( watchObjEnt.getModificationDate() != null &&  watchObjEnt.getModificationDate().after(timestamp) && !isModified){
									// add the updated object to the list
									watchOut.getValuesList().addChildren(watchObjEnt.getObixObject());
									// update the uri to mark the last modification time;
									updatedUris.add(uriEnt);
								}
								
								break;
							}
						}
					}
				}
				
				if(updatedUris.size() > 0){
					ObjEntity obj = new ObjEntity(new Obj());
					obj.updateModTimeStamp(updatedUris);
				}
			}
		} catch(EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".poolRefresh", ex);
		}
		
		return watchOut;
	}

	public WatchOut poolRefresh(String uri) throws DomainException {
		WatchOut watchOut = new WatchOut();
		
		try {
			ObjEntity watch = getWatch(uri);
			if( watch != null){
				// parse each child of the watch
				List<ObjEntity> childs = watch.getChilds();
				
				List<Uri> uris = new ArrayList<Uri>();
				for( ObjEntity uriEnt : childs){
					// filter for uri, as there is also the lease object
					if( uriEnt instanceof UriEntity){
						uris.add((Uri)uriEnt.getObixObject());
					}
				}
				
				List<ObjEntity> monitoredObjs = null;
				// get all objects identified by the watch's href uris
				if( uris.size() > 0){
					ObjEntity fetchObj = new ObjEntity(new Obj());
					monitoredObjs = fetchObj.fetchByHrefs(uris);
				}
				
				List<ObjEntity> updatedUris = new ArrayList<ObjEntity>();
				
				for( ObjEntity uriEnt : childs){
					// filter for uri, as there is also the lease object
					if( uriEnt instanceof UriEntity){
						// uri represent an object to watch for
						Uri watchedUri = (Uri)uriEnt.getObixObject();
						
						for( ObjEntity watchObjEnt : monitoredObjs ){
							if( watchObjEnt.getObj_uri().equals(watchedUri.getPath())){
								watchObjEnt.fetchChildrens();
								for( ObjEntity entity : (List<ObjEntity>) watchObjEnt.getChilds()){
									watchObjEnt.getObixObject().addChildren(entity.getObixObject());
								}
								// add the updated object to the list
								watchOut.getValuesList().addChildren(watchObjEnt.getObixObject());
								// update the uri to mark the last modification time;
								updatedUris.add(uriEnt);
								break;
							}
						}
					}
				}
				
				if(updatedUris.size() > 0){
					ObjEntity obj = new ObjEntity(new Obj());
					obj.updateModTimeStamp(updatedUris);
				}
			}
		} catch(EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".poolChanges", ex);
		}
		
		return watchOut;
	}

	public Watch retrieve(String uri) throws DomainException {

		ObjEntity watchEntity = getWatch(uri);
		if( watchEntity != null) return (Watch)getWatch(uri).getObixObject();
		else return null;
	}
	
	
	public void update(String uri, Watch watchIn) throws DomainException {
		Watch obixObj = new Watch();
		obixObj.setHref(new Uri("href", uri));
		ObjEntity objEnt = new ObjEntity(obixObj);
		
		try {
			if( objEnt.fetchByHref()) {
				if( objEnt.getObixObject().containsContract(Watch.contract)) {
					objEnt.getObixObject().setDisplayName(watchIn.getDisplayName());
					objEnt.update();
				}
			}
		} catch(EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".getWatch", ex);
		}
	}
	
	
	private ObjEntity getWatch( String uri) throws DomainException {

		Watch obixObj = new Watch();
		obixObj.setHref(new Uri("href", uri));
		ObjEntity objEnt = new ObjEntity(obixObj);
		
		try {
			if( objEnt.fetchByHref()) {
				if( objEnt.getObixObject().containsContract(Watch.contract)) {
					objEnt.fetchChildrens();
					for( ObjEntity entity : (List<ObjEntity>) objEnt.getChilds()){
						objEnt.getObixObject().addChildren(entity.getObixObject());
					}
					return objEnt;
				}
			}
		} catch(EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".getWatch", ex);
		}
		
		return null;
	}
	
	private ObjEntity containsUri(ObjEntity watch, String uri){
		
		List<ObjEntity> childs = watch.getChilds();
		for( ObjEntity objEnt : childs){
			if( objEnt instanceof UriEntity){
				Uri uriObj = (Uri)objEnt.getObixObject();
				if( uriObj.getPath().toString().endsWith(uri)) return objEnt;
			}
		}
		
		return null;
	}


	@Override
	public List<Obj> getObixWatches() throws DomainException {
		Obj obixObj = new Obj();
		obixObj.setIs(Watch.contract);
		ObjEntity objEnt = new ObjEntity(obixObj);
	
		List<Obj> objs = new ArrayList<Obj>();
		
		try {
			List<ObjEntity> watchList = (List<ObjEntity>)objEnt.fetchByContract();
			
			
			for( ObjEntity entity : watchList) {
				entity.fetchChildrens();
				Obj obj = entity.getObixObject();
				
				List<ObjEntity> childEntities = (List<ObjEntity>)entity.getChilds();
				for( ObjEntity childEntity : childEntities){
					obj.addChildren(childEntity.getObixObject());
				}
				objs.add(entity.getObixObject());
			}
		} catch(EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".getObixWatches", ex);
		}
		
		return objs;
	}
	
}
