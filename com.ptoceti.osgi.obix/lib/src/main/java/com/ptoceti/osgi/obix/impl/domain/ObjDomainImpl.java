package com.ptoceti.osgi.obix.impl.domain;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : ObjDomainImpl.java
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


import java.util.ArrayList;
import java.util.List;


import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.domain.ObjDomain;
import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Bool;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Enum;
import com.ptoceti.osgi.obix.object.Feed;
import com.ptoceti.osgi.obix.object.Int;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Real;
import com.ptoceti.osgi.obix.object.Ref;
import com.ptoceti.osgi.obix.object.Reltime;
import com.ptoceti.osgi.obix.object.Str;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.object.Val;
import com.ptoceti.osgi.obix.impl.entity.AbsTimeEntity;
import com.ptoceti.osgi.obix.impl.entity.BoolEntity;
import com.ptoceti.osgi.obix.impl.entity.EntityException;
import com.ptoceti.osgi.obix.impl.entity.EntityType;
import com.ptoceti.osgi.obix.impl.entity.EnumEntity;
import com.ptoceti.osgi.obix.impl.entity.FeedEntity;
import com.ptoceti.osgi.obix.impl.entity.IntEntity;
import com.ptoceti.osgi.obix.impl.entity.ListEntity;
import com.ptoceti.osgi.obix.impl.entity.ObjEntity;
import com.ptoceti.osgi.obix.impl.entity.RealEntity;
import com.ptoceti.osgi.obix.impl.entity.RefEntity;
import com.ptoceti.osgi.obix.impl.entity.RelTimeEntity;
import com.ptoceti.osgi.obix.impl.entity.StrEntity;

public class ObjDomainImpl extends AbstractDomain implements ObjDomain {

	
	public Obj getObixObjWithRefTo(Uri href) throws DomainException {
		
		Ref ref = new Ref("history", href);
		RefEntity refEnt = new RefEntity(ref);
		try {
			if( refEnt.fetchByHref()){
				
				Integer parentId = refEnt.getParent_id();
				
				ObjEntity objEnt = new ObjEntity(new Obj());
				objEnt.setId(parentId);
				if( objEnt.fetchByObjectId()){
					return objEnt.getObixObject();
				}
				
			}
		} catch(EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".getObixObj", ex);
		}
		
		return null;
	}
	
	
	public Obj getObixObj(Uri href) throws DomainException {
		
		Obj obixObj = new Obj();
		obixObj.setHref(href);
		ObjEntity objEnt = new ObjEntity(obixObj);
		
		try {
			objEnt = ObjEntity.fetchByHref(objEnt);
			if(objEnt != null){
				
				objEnt.fetchChildrens();
				Obj obj = objEnt.getObixObject();
				
				List<ObjEntity> childEntities = (List<ObjEntity>)objEnt.getChilds();
				for( ObjEntity childEntity : childEntities){
					obj.addChildren(childEntity.getObixObject());
				}
				return objEnt.getObixObject();
			}
		} catch(EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".getObixObj", ex);
		}
		
		return null;
	}

	public List<Obj> getObixObjsByContract(Contract contract) throws DomainException {
		
		Obj obixObj = new Obj();
		obixObj.setIs(contract);
		ObjEntity objEnt = new ObjEntity(obixObj);
	
		List<Obj> objs = new ArrayList<Obj>();
		try {
			List<ObjEntity> pointList = (List<ObjEntity>)objEnt.fetchByContract();
			
			for( ObjEntity entity : pointList) {
				objs.add(entity.getObixObject());
			}
		} catch(EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".getObixObjsByContract", ex);
		}
		
		return objs;
	}
	
	public Obj updateObixObjAt(Uri href, Obj updateObj) throws DomainException{
	
		ObjEntity objEnt = mapEntityToObj(updateObj);
		objEnt.getObixObject().setHref(href);
		
		try {
			if( objEnt.fetchByHref())
			{
				objEnt.setObixObject(updateObj);
				objEnt.update();
			}
		} catch(EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".updateObixObjAt", ex);
		}
		
		return objEnt.getObixObject();
	}
	
	public boolean deleteChildObject(Uri href,String childName) throws DomainException{
		Obj obixObj = new Obj();
		obixObj.setHref(href);
		ObjEntity objEnt = new ObjEntity(obixObj);
		
		try {
			if( objEnt.fetchByHref())
			{
				objEnt.fetchChildrens();
				
				for( ObjEntity child: objEnt.getChilds()){
					if( child.getObixObject().getName().equals(childName)){
						child.delete();
						break;
					}
				}
				
				return true;
			}
		} catch(EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".updateObixObjAt", ex);
		}
		
		return false;
	}
	
	public boolean addChildObject(Uri href, Obj childObj) throws DomainException{
		
		Obj obixObj = new Obj();
		obixObj.setHref(href);
		ObjEntity objEnt = new ObjEntity(obixObj);
		
		try {
			if( objEnt.fetchByHref())
			{
				objEnt.addChildren(childObj);
				return true;
			}
		} catch(EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".updateObixObjAt", ex);
		}
		
		return false;
	}
	
	public Obj createUpdateObixObj(Obj updateObj) throws DomainException{
		
		ObjEntity objEnt = mapEntityToObj(updateObj);
		objEnt.getObixObject().setHref(updateObj.getHref());
		
		try {
			if( objEnt.fetchByHref()){
				
				Obj storedObj = objEnt.getObixObject();
				boolean hasChanged = false;
				
				if( storedObj.getStatus() != updateObj.getStatus()) hasChanged = true;
				if( (storedObj instanceof Val && updateObj instanceof Val) 
						&& !(((Val)storedObj).getVal().toString().equals(((Val)updateObj).getVal().toString()))) hasChanged = true;
				
				if(hasChanged){
					objEnt.setObixObject(updateObj);
					objEnt.update();
				}
				
			} else {
				objEnt.setObixObject(updateObj);
				objEnt.create();
			}
		} catch(EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".createUpdateObixObj", ex);
		}
		
		return objEnt.getObixObject();
	}
	
	public Obj createObixObj(Obj newObj) throws DomainException{
		
		ObjEntity objEnt = mapEntityToObj(newObj);
		objEnt.setObixObject(newObj);
		try {
			objEnt.create();
		} catch(EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".createObixObj", ex);
		}
		
		return objEnt.getObixObject();
		
	}
	
	
	private ObjEntity mapEntityToObj(Obj obixObj ){
		
		ObjEntity entObj = null;
		
		if ( obixObj instanceof Abstime ){
			entObj = new AbsTimeEntity(new Abstime());
		} else if ( obixObj instanceof Bool ) {
			entObj = new BoolEntity(new Bool());
		} else if(obixObj instanceof Enum){
			entObj = new EnumEntity(new Enum());
		}  else if(obixObj instanceof Feed){
			entObj = new FeedEntity(new Feed());
		} else if( obixObj instanceof Int){
			entObj = new IntEntity(new Int());
		} else if( obixObj instanceof Feed){
			entObj = new FeedEntity(new Feed());
		} else if( obixObj instanceof com.ptoceti.osgi.obix.object.List){
			entObj = new ListEntity(new com.ptoceti.osgi.obix.object.List());
		} else if( obixObj instanceof Real){
			entObj = new RealEntity(new Real());
			
		} else if( obixObj instanceof Ref){
			entObj = new RefEntity(new Ref());
			
		} else if ( obixObj instanceof Reltime ){
			entObj = new RelTimeEntity(new Reltime());
		} else if ( obixObj instanceof Str ) {
			entObj = new StrEntity(new Str());
		} else {
			entObj = new ObjEntity(new Obj());
		}
		
		return entObj;
		
	}

}
