package com.ptoceti.osgi.obix.impl.entity;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : ObjEntity.java
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


import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.ptoceti.osgi.data.ResultSetGeneratedKeysHandler;
import com.ptoceti.osgi.data.ResultSetMultipleHandler;
import com.ptoceti.osgi.data.ResultSetSingleHandler;
import com.ptoceti.osgi.obix.constants.ObixNames;
import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Bool;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Enum;
import com.ptoceti.osgi.obix.object.Err;
import com.ptoceti.osgi.obix.object.Feed;
import com.ptoceti.osgi.obix.object.Int;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Op;
import com.ptoceti.osgi.obix.object.Real;
import com.ptoceti.osgi.obix.object.Ref;
import com.ptoceti.osgi.obix.object.Reltime;
import com.ptoceti.osgi.obix.object.Status;
import com.ptoceti.osgi.obix.object.Str;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.impl.ObixDataHandler;

public class ObjEntity extends AbstractEntity {

	private static final String CREATE_OBJ = "insert into object (name, uri_id, contract_id, isnullable, displayname, display, writable, status_id, type_id, parent_id, created_ts ) values (?,?,?,?,?,?,?,?,?,?,?)";
	private static final String SEARCH_OBJ_BY_HREF = "select object.* from object, uri where object.uri_id = uri.id and uri.hash=?";
	private static final String SEARCH_OBJ_BY_ID = "select object.* from object where object.id=?";
	private static final String SEARCH_OBJ_BY_PARENT_ID = "select object.* from object where object.parent_id=?";
	private static final String SEARCH_OBJ_BY_PARENT_ID_AND_TIMESTAMP = "select object.* from object where object.parent_id=? and created_ts > ? and created_ts <= ?";
	private static final String SEARCH_OBJ_BY_CONTRACT_ID = "select object.* from object where object.contract_id = ?";

	private static final String DELETE_OBJ = "delete from object where object.id=?";

	private static final String UPDATE_OBJ = "update object set name = ?, isnullable = ?, displayname = ?, display = ?,writable = ?, status_id = ?, modified_ts = ? where id = ? ";

	private static final String COL_OBJ_ID = "id";
	private static final String COL_OBJ_NAME = "name";
	private static final String COL_OBJ_URI_ID = "uri_id";
	private static final String COL_OBJ_CONTRACT_ID = "contract_id";
	private static final String COL_OBJ_ISNULL = "isnullable";
	private static final String COL_OBJ_ICON_ID = "icon_id";
	private static final String COL_OBJ_DISPLAYNAME = "displayname";
	private static final String COL_OBJ_DISPLAY = "display";
	private static final String COL_OBJ_WRITABLE = "writable";
	private static final String COL_OBJ_STATUS_ID = "status_id";
	private static final String COL_OBJ_TYPE_ID = "type_id";
	private static final String COL_OBJ_PARENT_ID = "parent_id";
	private static final String COL_CREATE_TS = "created_ts";
	private static final String COL_MODIFIED_TS = "modified_ts";

	private Obj obixObject;

	private Integer objid;

	private Integer obj_contract_id;
	private Integer obj_uri_id;
	private Integer parent_id;
	
	private Date creationDate;
	private Date modificationDate;

	private List childs;

	private EntityType objtype = EntityType.Obj;

	private boolean fetched = false;
	
	// indicate if val specific details have been fetched
	private boolean detailsfetched = false;

	public ObjEntity( EntityType type) {
		objtype = type;
	}
	
	public ObjEntity(ObjEntity entObj) {
		
		setObixObject(subClassObjToType(entObj));
		setId(entObj.getId());
		setObjtype(entObj.getObjtype());

		setObj_contract_id(entObj.getObj_contract_id());
		setObj_uri_id(entObj.getObj_uri_id());
		setParent_id(entObj.getParent_id());
		
		setCreationDate(entObj.getCreationDate());
		setModificationDate(entObj.getModificationDate());
		
		fetched = entObj.fetched;

		setChilds(entObj.getChilds());
	}

	public ObjEntity(Obj obixObj) {
		setObixObject(obixObj);
	}

	public void create() throws EntityException{

		ArrayList params = new ArrayList();
		params.add(getObixObject().getName());

		Uri hrefUri = getObixObject().getHref();
		if (hrefUri != null) {
			UriEntity uriEntity = new UriEntity(hrefUri);
			uriEntity.create();
			setObj_uri_id(uriEntity.getUriId());
		}

		params.add(getObj_uri_id());

		Contract isContract = getObixObject().getIs();
		if (isContract != null) {
			ContractEntity contractEntity = new ContractEntity(isContract);
			contractEntity.create();
			setObj_contract_id(contractEntity.getContractid());
		}

		params.add(getObj_contract_id());

		params.add(getObixObject().getIsNull());
		params.add(getObixObject().getDisplayName());
		params.add(getObixObject().getDisplay());
		params.add(getObixObject().getWritable());
		
		if( getObixObject().getStatus() != null ){
			params.add(getObixObject().getStatus().getId());
		} else params.add(null);
		
		params.add(getObjtype().getIdent());
		params.add(getParent_id());
		params.add(Calendar.getInstance().getTime());

		update(CREATE_OBJ, params.toArray(),
				new ObjResultSetGeneratedKeysHandler(this));

		Iterator objIter = obixObject.getChildrens().iterator();
		while (objIter.hasNext()) {
			Obj obixObj = (Obj) objIter.next();
			if( !(obixObj.getIs()!= null && obixObj.getIs().containsContract(Op.contract)))
				addChildren(obixObj);
		}
	}

	public void delete() throws EntityException {

		if (getObj_uri_id() != null) {
			UriEntity uriEntity = new UriEntity(new Uri());
			uriEntity.setId(this.getObj_uri_id());
			uriEntity.delete();
		}

		if (getObj_contract_id() != null) {
			ContractEntity contractEntity = new ContractEntity(new Contract());
			contractEntity.setContractid(this.getObj_contract_id());
			contractEntity.delete();
		}

		List childs = this.getChilds();
		Iterator childIter = childs.iterator();
		while (childIter.hasNext()) {
			ObjEntity rootentity = (ObjEntity) childIter.next();

			ObjEntity descentity = subClassToType( rootentity);
			
			if (descentity != null)
				descentity.delete();
		}

		ArrayList params = new ArrayList();
		params.add(this.getId());
		update(DELETE_OBJ, params.toArray(), null);

	}

	public boolean fetchByHref() throws EntityException {

		ArrayList params = new ArrayList();
		params.add(getObixObject().getHref().getPath().hashCode());

		query(SEARCH_OBJ_BY_HREF, params.toArray(),
				new ObjResultSetHandler(this));

		if (this.isFetched()) {
			if( getObj_uri_id() != null){
				UriEntity uriEntity = new UriEntity(new Uri());
				uriEntity.setUriId(getObj_uri_id());
				uriEntity.fetchById();
				getObixObject().setHref(((Uri)uriEntity.getObixObject()));
			}
			
			if (getObj_contract_id() != null) {
				ContractEntity contractEntity = new ContractEntity(getObj_contract_id());
				contractEntity.fetch();
				getObixObject().setIs(contractEntity.getObixContract());
			}
			return true;
		}

		return false;
	}
	
	public static ObjEntity fetchByHref(ObjEntity objEnt) throws EntityException {
		
		ObjEntity resultObjEntity = null;
		
		if( objEnt.fetchByHref()){
		
			// Obj could be of any obix type.
			if(!objEnt.getObjtype().equals(EntityType.Obj)) {
				// re-map it according to it type to the right obj (Int, Real, ... )
				resultObjEntity = objEnt.subClassToType( objEnt);
				if( resultObjEntity instanceof ValEntity) {
					((ValEntity)resultObjEntity).fetchDetailsById();
				}
			} else {
				resultObjEntity = objEnt;
			}
		}
		
		return resultObjEntity;
	}

	public boolean fetchByObjectId() throws EntityException {

		boolean found = false;

		ArrayList params = new ArrayList();
		params.add(getId());

		query(SEARCH_OBJ_BY_ID, params.toArray(),
				new ObjResultSetHandler(this));

		if (isFetched()) {
			if( getObj_uri_id() != null){
				UriEntity uriEntity = new UriEntity(new Uri());
				uriEntity.setUriId(getObj_uri_id());
				uriEntity.fetchById();
				getObixObject().setHref(((Uri)uriEntity.getObixObject()));
			}
			
			if (getObj_contract_id() != null) {
				ContractEntity contractEntity = new ContractEntity(getObj_contract_id());
				contractEntity.fetch();
				getObixObject().setIs(contractEntity.getObixContract());
			}
			return true;
		}

		return false;
	}
	
	public List fetchByContract () throws EntityException {
		
		boolean found = false;
		
		ArrayList objs = new ArrayList();
		
		ContractEntity contractEntity = new ContractEntity(obixObject.getIs());
		List contractEntityList = contractEntity.searchContractByUri(contractEntity.getObixContract().getUris()[0].getPath());

		if( contractEntityList != null && contractEntityList.size() > 0) {
			
			ArrayList fetchedObj = new ArrayList();
			
			Iterator iter = contractEntityList.iterator();
			while( iter.hasNext()){
				
				ObjEntity objEnt = new ObjEntity(new Obj());
				ContractEntity contractEnt = (ContractEntity)iter.next();
				
				
				ArrayList params = new ArrayList();
				params.add(contractEnt.getContractid());
				query(SEARCH_OBJ_BY_CONTRACT_ID, params.toArray(),
						new ObjResultSetHandler(objEnt));
				
				// Obj could be of any obix type.
				if(!objEnt.getObjtype().equals(EntityType.Obj)) {
					// re-map it according to it type to the right obj (Int, Real, ... )
					ObjEntity subentity = subClassToType( objEnt);
					if( subentity != null) objEnt = subentity;
					if( subentity instanceof ValEntity) {
						((ValEntity)subentity).fetchDetailsById();
					}
				}
				
				if (objEnt.isFetched()) {
					
					if( objEnt.getObj_uri_id() != null){
						UriEntity nextUriEntity = new UriEntity(new Uri());
						nextUriEntity.setUriId(objEnt.getObj_uri_id());
						nextUriEntity.fetchById();
						objEnt.getObixObject().setHref(((Uri)nextUriEntity.getObixObject()));
					}
					
					if (objEnt.getObj_contract_id() != null) {
						ContractEntity nextContractEntity = new ContractEntity(objEnt.getObj_contract_id());
						nextContractEntity.fetch();
						objEnt.getObixObject().setIs(nextContractEntity.getObixContract());
					}
	
				}
				
				objs.add(objEnt);
			}
		}

		return objs;
	}

	public void fetchChildrens() throws EntityException {

		ArrayList params = new ArrayList();
		params.add(getId());

		ArrayList childsList = new ArrayList();

		queryMultiple(SEARCH_OBJ_BY_PARENT_ID, params.toArray(),
				new ObjResultSetMultipleHandler(childsList));

		
		for( int i = 0; i < childsList.size();i++){
			ObjEntity objEnt = (ObjEntity)childsList.get(i);
			
			if( objEnt.getObj_uri_id() != null){
				UriEntity uriEntity = new UriEntity(new Uri());
				uriEntity.setUriId(objEnt.getObj_uri_id());
				uriEntity.fetchById();
				objEnt.getObixObject().setHref(((Uri)uriEntity.getObixObject()));
			}
			
			if (objEnt.getObj_contract_id() != null) {
				ContractEntity contractEntity = new ContractEntity(objEnt.getObj_contract_id());
				contractEntity.fetch();
				objEnt.getObixObject().setIs(contractEntity.getObixContract());
			}
			
			if(!objEnt.getObjtype().equals(EntityType.Obj)) {
				// re-map it according to it type to the right obj (Int, Real, ... )
				ObjEntity resultObjEntity = objEnt.subClassToType( objEnt);
				if( resultObjEntity instanceof ValEntity) {
					((ValEntity)resultObjEntity).fetchDetailsById();
				}
				childsList.set(i, resultObjEntity);
			}
		}
		
		setChilds(childsList);
	}
	
	public void fetchChildrensFilterByTimeStamp(long millisFrom, long millisTo) throws EntityException {

		List childsList = new ArrayList();

		boolean queried = false;
		// if in cas of a list, of attribute should indicated what we are suppose to get
		if(getObjtype().equals(EntityType.List) && ((com.ptoceti.osgi.obix.object.List)getObixObject()).getOf() != null){
			if( ((com.ptoceti.osgi.obix.object.List)getObixObject()).getOf().containsContract((new Int()).getContract())) {
				IntEntity intEntity = new IntEntity();
				childsList = intEntity.fetchByParentIdFilterByTimestamsp(getId(), millisFrom, millisTo);
				queried = true;
			} else if( ((com.ptoceti.osgi.obix.object.List)getObixObject()).getOf().containsContract((new Real()).getContract())) {
				RealEntity realEntity = new RealEntity();
				childsList = realEntity.fetchByParentIdFilterByTimestamsp(getId(), millisFrom, millisTo);
				queried = true;
			}
			
		}
		if( !queried){

			ArrayList params = new ArrayList();
			params.add(getId());
			params.add(millisFrom);
			params.add(millisTo);
			
			queryMultiple(SEARCH_OBJ_BY_PARENT_ID_AND_TIMESTAMP, params.toArray(), new ObjResultSetMultipleHandler(childsList));
		}

		
		for( int i = 0; i < childsList.size();i++){
			ObjEntity objEnt = (ObjEntity)childsList.get(i);
			
			if( objEnt.getObj_uri_id() != null){
				UriEntity uriEntity = new UriEntity(new Uri());
				uriEntity.setUriId(objEnt.getObj_uri_id());
				uriEntity.fetchById();
				objEnt.getObixObject().setHref(((Uri)uriEntity.getObixObject()));
			}
			
			if (objEnt.getObj_contract_id() != null) {
				ContractEntity contractEntity = new ContractEntity(objEnt.getObj_contract_id());
				contractEntity.fetch();
				objEnt.getObixObject().setIs(contractEntity.getObixContract());
			}
			
			if(!objEnt.getObjtype().equals(EntityType.Obj) && !objEnt.isDetailsfetched()) {
				// re-map it according to it type to the right obj (Int, Real, ... )
				ObjEntity resultObjEntity = objEnt.subClassToType( objEnt);
				if( resultObjEntity instanceof ValEntity) {
					((ValEntity)resultObjEntity).fetchDetailsById();
				}
				childsList.set(i, resultObjEntity);
			}
		}
		
		setChilds(childsList);
	}

	public void update() throws EntityException {

		ArrayList params = new ArrayList();
		params.add(getObixObject().getName());
		params.add(getObixObject().getIsNull());
		params.add(getObixObject().getDisplayName());
		params.add(getObixObject().getDisplay());
		params.add(getObixObject().getWritable());
		
		if( getObixObject().getStatus() != null ){
			params.add(getObixObject().getStatus().getId());
		} else params.add(null);
		
		params.add(Calendar.getInstance().getTime());
		
		params.add(getId());
		

		update(UPDATE_OBJ, params.toArray(), null);

	}

	public void setId(Integer id) {
		this.objid = id;
	}

	public Integer getId() {
		return objid;
	}

	public void setObj_contract_id(Integer obj_contract_id) {
		this.obj_contract_id = obj_contract_id;
	}

	public Integer getObj_contract_id() {
		return obj_contract_id;
	}

	public void setObj_uri_id(Integer obj_uri_id) {
		this.obj_uri_id = obj_uri_id;
	}

	public Integer getObj_uri_id() {
		return obj_uri_id;
	}

	public void setObixObject(Obj obixObject) {
		this.obixObject = obixObject;
	}

	public Obj getObixObject() {
		return obixObject;
	}

	public void addChildren(Obj obixObj) throws EntityException {

		ObjEntity child = null;

		if (obixObj instanceof Abstime) {
			child = new AbsTimeEntity((Abstime) obixObj);
		} else if (obixObj instanceof Bool) {
			child = new BoolEntity((Bool) obixObj);
		} else if (obixObj instanceof Enum) {
			child = new EnumEntity((Enum) obixObj);
		} else if (obixObj instanceof Err) {

		} else if (obixObj instanceof Feed) {
			child = new FeedEntity((Feed) obixObj);
		} else if (obixObj instanceof Int) {
			child = new IntEntity((Int) obixObj);
		} else if (obixObj instanceof com.ptoceti.osgi.obix.object.List) {
			child = new ListEntity((com.ptoceti.osgi.obix.object.List) obixObj);
		} else if (obixObj instanceof Op) {

		} else if (obixObj instanceof Real) {
			child = new RealEntity((Real) obixObj);
		} else if (obixObj instanceof Ref) {
			child = new RefEntity((Ref)obixObj);
		} else if (obixObj instanceof Reltime) {
			child = new RelTimeEntity((Reltime) obixObj);
		} else if (obixObj instanceof Str) {
			child = new StrEntity((Str) obixObj);
		} else if (obixObj instanceof Uri) {
			child = new UriEntity((Uri) obixObj);
		}

		if (child != null) {
			child.setParent_id(this.objid);
			child.create();

			getChilds().add(child);
		}

	}

	public void setParent_id(Integer parent_id) {
		this.parent_id = parent_id;
	}

	public Integer getParent_id() {
		return parent_id;
	}

	public void setObjtype(EntityType objtype) {
		this.objtype = objtype;
	}

	public EntityType getObjtype() {
		return objtype;
	}

	public void setChilds(List childs) {
		this.childs = childs;
	}

	public List getChilds() {
		if (childs == null)
			childs = new ArrayList();
		return childs;
	}

	public ObjEntity getChildByName( String name){
		
		List childList = getChilds();
		
		for(int i = 0; i < childList.size(); i++){
			ObjEntity child = (ObjEntity)childList.get(i);
			if( child.getObixObject().getName().equals(name)){
				return child;
			}
		}
		
		return null;
	}
	
	
	protected void setFetched(boolean fetched) {
		this.fetched = fetched;
	}

	protected boolean isFetched() {
		return fetched;
	}
	
	public Obj subClassObjToType(ObjEntity rootentity) {
		
		Obj subObj = null;
		
		EntityType entType = rootentity.getObjtype();
		if (entType.equals(EntityType.AbsTime))
			subObj = new Abstime(rootentity.getObixObject());
		else if (entType.equals(EntityType.Bool))
			subObj = new Bool(rootentity.getObixObject());
		else if (entType.equals(EntityType.Enum))
			subObj = new Enum(rootentity.getObixObject());
		else if (entType.equals(EntityType.Feed))
			subObj = new Feed(rootentity.getObixObject());
		else if (entType.equals(EntityType.Int))
			subObj = new Int(rootentity.getObixObject());
		else if (entType.equals(EntityType.List))
			subObj = new com.ptoceti.osgi.obix.object.List(rootentity.getObixObject());
		else if (entType.equals(EntityType.Real))
			subObj = new Real(rootentity.getObixObject());
		
		else if (entType.equals(EntityType.Ref))
			subObj = new Ref(rootentity.getObixObject());
		
		else if (entType.equals(EntityType.RelTime))
			subObj = new Reltime(rootentity.getObixObject());
		else if (entType.equals(EntityType.Str))
			subObj = new Str(rootentity.getObixObject());
		else if (entType.equals(EntityType.Uri))
			subObj = new Uri(rootentity.getObixObject());
		
		return subObj;
	}

	public ObjEntity subClassToType(ObjEntity rootentity) throws EntityException  {
		
		ObjEntity subObjEntity = null;
		
		EntityType entType = rootentity.getObjtype();
		if (entType.equals(EntityType.AbsTime))
			subObjEntity = new AbsTimeEntity(rootentity);
		else if (entType.equals(EntityType.Bool))
			subObjEntity = new BoolEntity(rootentity);
		else if (entType.equals(EntityType.Enum))
			subObjEntity = new EnumEntity(rootentity);
		else if (entType.equals(EntityType.Feed))
			subObjEntity = new FeedEntity(rootentity);
		else if (entType.equals(EntityType.Int))
			subObjEntity = new IntEntity(rootentity);
		else if (entType.equals(EntityType.List))
			subObjEntity = new ListEntity(rootentity);
		else if (entType.equals(EntityType.Real))
			subObjEntity = new RealEntity(rootentity);
		
		else if (entType.equals(EntityType.Ref))
			subObjEntity = new RefEntity(rootentity);
		
		else if (entType.equals(EntityType.RelTime))
			subObjEntity = new RelTimeEntity(rootentity);
		else if (entType.equals(EntityType.Str))
			subObjEntity = new StrEntity(rootentity);
		else if (entType.equals(EntityType.Uri))
			subObjEntity = new UriEntity(rootentity);
		
		return subObjEntity;
	}

	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param modificationDate the modificationDate to set
	 */
	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	/**
	 * @return the modificationDate
	 */
	public Date getModificationDate() {
		return modificationDate;
	}

	public boolean isDetailsfetched() {
		return detailsfetched;
	}

	public void setDetailsfetched(boolean detailsfetched) {
		this.detailsfetched = detailsfetched;
	}

	public class ObjResultSetMultipleHandler extends ResultSetMultipleHandler {

		private List entityList;
		protected ObjEntity entity;

		public ObjResultSetMultipleHandler(List entityList) {
			this.entityList = entityList;
		}

		public void getRowAsBean(ResultSet rs) throws Exception {

			Integer id = getInteger(rs, COL_OBJ_ID);
			if (id != null)
				entity.setId(id);

			entity.getObixObject().setName(getString(rs, COL_OBJ_NAME));
			entity.getObixObject().setDisplayName(
					getString(rs, COL_OBJ_DISPLAYNAME));
			entity.getObixObject().setDisplay(getString(rs, COL_OBJ_DISPLAY));
			entity.getObixObject().setIsNull(getBoolean(rs, COL_OBJ_ISNULL));
			entity.getObixObject()
					.setWritable(getBoolean(rs, COL_OBJ_WRITABLE));
			
			Integer statusId = getInteger(rs, COL_OBJ_STATUS_ID);
			if( statusId != null) {
				entity.getObixObject().setStatus(Status.getStatusFromId(statusId.intValue()));
			}

			entity.setObj_contract_id(getInteger(rs, COL_OBJ_CONTRACT_ID));
			entity.setObj_uri_id(getInteger(rs, COL_OBJ_URI_ID));
			entity.setObjtype(EntityType
					.getEnum(getInteger(rs, COL_OBJ_TYPE_ID)));
			entity.setParent_id(getInteger(rs, COL_OBJ_PARENT_ID));
			
			entity.setCreationDate(getDate(rs, COL_CREATE_TS));
			entity.setModificationDate(getDate(rs, COL_MODIFIED_TS));

			entity.setFetched(true);
		}

		public void getNextRowAsBean(ResultSet rs) throws Exception {

			getNextBean();
			getRowAsBean(rs);
			entityList.add(entity);

		}

		public void getNextBean() {
			Obj obixObj = new Obj();
			entity = new ObjEntity(obixObj);
		}
	}

	public class ObjResultSetHandler extends ResultSetSingleHandler {

		private ObjEntity entity;

		public ObjResultSetHandler(ObjEntity entity) {
			this.entity = entity;
		}

		public void getRowAsBean(ResultSet rs) throws Exception {

			Integer id = getInteger(rs, COL_OBJ_ID);
			if (id != null)
				entity.setId(id);

			entity.getObixObject().setName(getString(rs, COL_OBJ_NAME));
			entity.getObixObject().setDisplayName(
					getString(rs, COL_OBJ_DISPLAYNAME));
			entity.getObixObject().setDisplay(getString(rs, COL_OBJ_DISPLAY));
			entity.getObixObject().setIsNull(getBoolean(rs, COL_OBJ_ISNULL));
			entity.getObixObject()
					.setWritable(getBoolean(rs, COL_OBJ_WRITABLE));
			
			Integer statusId = getInteger(rs, COL_OBJ_STATUS_ID);
			if( statusId != null) {
				entity.getObixObject().setStatus(Status.getStatusFromId(statusId.intValue()));
			}

			entity.setObj_contract_id(getInteger(rs, COL_OBJ_CONTRACT_ID));
			entity.setObj_uri_id(getInteger(rs, COL_OBJ_URI_ID));
			entity.setObjtype(EntityType
					.getEnum(getInteger(rs, COL_OBJ_TYPE_ID)));
			entity.setParent_id(getInteger(rs, COL_OBJ_PARENT_ID));
			
			entity.setCreationDate(getDate(rs, COL_CREATE_TS));
			entity.setModificationDate(getDate(rs, COL_MODIFIED_TS));

			entity.setFetched(true);
		}
	}

	public class ObjResultSetGeneratedKeysHandler extends
			ResultSetGeneratedKeysHandler {

		private ObjEntity entity;

		public ObjResultSetGeneratedKeysHandler(ObjEntity entity) {
			this.entity = entity;
		}

		public void getRowsKey(ResultSet rs) throws Exception {
			entity.setId(getRowID(rs));
		}
	}
}
