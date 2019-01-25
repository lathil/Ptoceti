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


import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.ptoceti.osgi.data.ResultSetGeneratedKeysHandler;
import com.ptoceti.osgi.data.ResultSetMultipleHandler;
import com.ptoceti.osgi.data.ResultSetSingleHandler;
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

public class ObjEntity extends AbstractEntity {

	private static final String CREATE_OBJ = "insert into object (name, uri, uri_hash, contract_id, isnullable, displayname, display, writable, status_id, type_id, parent_id, created_ts ) values (?,?,?,?,?,?,?,?,?,?,?,?)";
	protected static final String SEARCH_OBJ_BY_HREF = "select object.* from object where object.uri_hash=?";
	protected static final String SEARCH_OBJS_BY_HREFS = "select object.* from object where object.uri_hash in (?)";
	protected static final String SEARCH_OBJ_BY_ID = "select object.* from object where object.id=?";
	private static final String SEARCH_OBJ_BY_PARENT_ID = "select object.* from object where object.parent_id=?";
	private static final String SEARCH_OBJ_BY_PARENT_ID_AND_TIMESTAMP = "select object.* from object where object.parent_id=? and created_ts > ? and created_ts <= ?";
	private static final String SEARCH_OBJ_BY_CONTRACT_ID = "select object.* from object where object.contract_id = ?";
	
	private static final String SEARCH_OBJ_BY_DISPLAYNAME = "select object.* from object where uri_hash not null and uri like '/%' and displayname like (?)";

	private static final String DELETE_OBJ = "delete from object where object.id=?";
	private static final String DELETE_CHILD_OBJ = "delete from object where object.parent_id=?";

	private static final String UPDATE_OBJ = "update object set name = ?, isnullable = ?, displayname = ?, display = ?,writable = ?, status_id = ?, modified_ts = ? where id = ? ";

	private static final String UPDATE_OBJS_UPDATE_TS = "update object set modified_ts = ? where id in ( ? )";
	
	protected static final String COL_OBJ_ID = "id";
	protected static final String COL_OBJ_NAME = "name";
	protected static final String COL_OBJ_URI = "uri";
	protected static final String COL_OBJ_URI_HASH = "uri_hash";
	protected static final String COL_OBJ_CONTRACT_ID = "contract_id";
	protected static final String COL_OBJ_ISNULL = "isnullable";
	protected static final String COL_OBJ_ICON_ID = "icon_id";
	protected static final String COL_OBJ_DISPLAYNAME = "displayname";
	protected static final String COL_OBJ_DISPLAY = "display";
	protected static final String COL_OBJ_WRITABLE = "writable";
	protected static final String COL_OBJ_STATUS_ID = "status_id";
	protected static final String COL_OBJ_TYPE_ID = "type_id";
	protected static final String COL_OBJ_PARENT_ID = "parent_id";
	protected static final String COL_CREATE_TS = "created_ts";
	protected static final String COL_MODIFIED_TS = "modified_ts";
	
	protected static final String COL_OBJ_VALUE_INT = "value_int";
	protected static final String COL_OBJ_VALUE_TS = "value_ts";
	protected static final String COL_OBJ_VALUE_TEXT = "value_text";
	protected static final String COL_OBJ_VALUE_BOOL = "value_bool";
	protected static final String COL_OBJ_VALUE_REAL = "value_real";
	
	protected static final String COL_OBJ_MIN = "min";
	protected static final String COL_OBJ_MAX = "max";
	protected static final String COL_OBJ_MIN_REAL = "min_real";
	protected static final String COL_OBJ_MAX_REAL = "max_real";
	
	protected static final String COL_OBJ_PRECISION = "precision";
	
	
	protected static final String COL_OBJ_RANGE_URI_ID = "range_uri_id";
	protected static final String COL_OBJ_UNIT = "unit";
	protected static final String COL_OBJ_IN_CONTRACT_ID = "in_contract_id";
	protected static final String COL_OBJ_OF_CONTRACT_ID = "of_contract_id";



	private Obj obixObject;
	
	protected Integer objid;
	private String obj_uri;
	private Integer obj_contract_id;
	private EntityType objtype = EntityType.Obj;
	private Integer parent_id;
	private Date creationDate;
	private Date modificationDate;
	private Integer rangeUriId;
	private String unit;
	private Integer inContractId;
	private Integer ofContractId;
	

	
	private List<ObjEntity> childs;

	

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
		setObj_uri(entObj.getObj_uri());
		setParent_id(entObj.getParent_id());
		
		setCreationDate(entObj.getCreationDate());
		setModificationDate(entObj.getModificationDate());
		
		fetched = entObj.fetched;

		setChilds(entObj.getChilds());
	}

	public ObjEntity(Obj obixObj) {
		setObixObject(obixObj);
		setObjtype(EntityType.Obj);
	}

	/**
	 * Create a simple obix object in the database
	 * @throws EntityException
	 */
	public void create() throws EntityException{

		update(CREATE_OBJ, getCreateParam().toArray(), new ObjResultSetGeneratedKeysHandler<ObjEntity>(this));

		Iterator<Obj> objIter = obixObject.getChildrens().iterator();
		while (objIter.hasNext()) {
			Obj obixObj =  objIter.next();
			if( !(obixObj.getIs()!= null && obixObj.getIs().containsContract(Op.contract)))
				addChildren(obixObj);
		}
	}

	/**
	 * Create common parameters for creation a object
	 * @return List<Object>  a list of parameterers for the sql query
	 * @throws EntityException
	 */
	protected List<Object> getCreateParam() throws EntityException{
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(getObixObject().getName());
		
		Uri hrefUri = getObixObject().getHref();
		if (hrefUri != null && getObjtype() != EntityType.Ref) {
			params.add(getObixObject().getHref().getVal());
			params.add(hrefUri.getVal().hashCode());
		} else {
			params.add(null);
			params.add(null);
		}


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
		
		return params;

	}
	
	public void deleteChildByName( String name) throws EntityException {
		
		List<ObjEntity> childList = getChilds();
		
		for(int i = 0; i < childList.size(); i++){
			ObjEntity child = childList.get(i);
			if( child.getObixObject().getName().equals(name)){
				
				childList.remove(i);
				child.delete();
				break;
			}
		}
		
	}

	public void deleteChilds() throws EntityException {
		
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(this.getId());
		update(DELETE_CHILD_OBJ, params.toArray(), null);
	}
	
	/**
	 * Delete the matching obix object.
	 * @throws EntityException
	 */
	public void delete() throws EntityException {

		deleteReferences();

		ArrayList<Object> params = new ArrayList<Object>();
		params.add(this.getId());
		update(DELETE_OBJ, params.toArray(), null);

	}
	
	/**
	 * remove all objects that are linked to this one.
	 * @throws EntityException
	 */
	protected void deleteReferences() throws EntityException{

		if (getObj_contract_id() != null) {
			ContractEntity contractEntity = new ContractEntity(new Contract());
			contractEntity.setContractid(this.getObj_contract_id());
			contractEntity.delete();
		}

		List<ObjEntity> childs = this.getChilds();
		Iterator<ObjEntity> childIter = childs.iterator();
		while (childIter.hasNext()) {
			ObjEntity rootentity =  childIter.next();
			ObjEntity descentity = subClassToType( rootentity);
			if (descentity != null)
				descentity.delete();
		}
	}

	/**
	 * Update in database the matching obix object
	 * @throws EntityException
	 */
	public void update() throws EntityException {
		List<Object> params = getUpdateParam();
		params.add(getId());
		update(UPDATE_OBJ, params.toArray(), null);
		getObixObject().setUpdateTimeStamp(((Date)params.get(6)).getTime());

	}
	
	/**
	 * Create a list of common parameters for update
	 * @return a list of common parameters to update
	 * @throws EntityException
	 */
	public List<Object> getUpdateParam() throws EntityException{
		
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(getObixObject().getName());
		params.add(getObixObject().getIsNull());
		params.add(getObixObject().getDisplayName());
		params.add(getObixObject().getDisplay());
		params.add(getObixObject().getWritable());
		
		if( getObixObject().getStatus() != null ){
			params.add(getObixObject().getStatus().getId());
		} else params.add(null);
		
		params.add(Calendar.getInstance().getTime());
		
		return params;
	}
	
	public void updateModTimeStamp(List<ObjEntity> objEntities) throws EntityException {  
		
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(Calendar.getInstance().getTime());
		
		for(ObjEntity objEntity : objEntities){
			params.add(Integer.valueOf( objEntity.getId()));
		}
				
		String query = UPDATE_OBJS_UPDATE_TS;
		if( params.size() > 2) {
			query = query.replace( "( ? )", "(" .concat(( new String(new char[params.size() -2 ]).replace("\0", "?,")).concat("?"))).concat(" )");
		}
		
		update(query, params.toArray(), null);
	}
	
	public boolean fetchByHref() throws EntityException {

		ArrayList<Object> params = new ArrayList<Object>();
		params.add(getObixObject().getHref().getPath().hashCode());

		doQueryByHref(params);
		
		if (this.isFetched()) {
			if (getObj_contract_id() != null) {
				ContractEntity contractEntity = new ContractEntity(getObj_contract_id());
				contractEntity.fetch();
				getObixObject().setIs(contractEntity.getObixContract());
			}
			return true;
		}

		return false;
	}
	
	protected void doQueryByHref(List<Object> params) throws EntityException{
		query(SEARCH_OBJ_BY_HREF, params.toArray(), new ObjResultSetHandler<ObjEntity>(this));
	}
	
	
	
	public List<ObjEntity> fetchByHrefs(List<Uri> uris) throws EntityException{
		
		List<ObjEntity> results = new ArrayList<ObjEntity>();
		
		ArrayList<Object> params = new ArrayList<Object>();
		
		for(Uri uri : uris){
			params.add(Integer.valueOf(uri.getPath().hashCode()));
		}
				
		String query = SEARCH_OBJS_BY_HREFS;
		if( params.size() > 1) {
			query = query.replace( "?", ( new String(new char[params.size() -1 ]).replace("\0", "?,")).concat("?"));
		}
		
		queryMultiple(query, params.toArray(), new ObjResultSetMultipleHandler<ObjEntity>(results));
		
		for( int i = 0; i < results.size();i++){
			ObjEntity objEnt = (ObjEntity)results.get(i);

			if (objEnt.getObj_contract_id() != null) {
				ContractEntity contractEntity = new ContractEntity(objEnt.getObj_contract_id());
				contractEntity.fetch();
				objEnt.getObixObject().setIs(contractEntity.getObixContract());
			}
			
			if(!objEnt.getObjtype().equals(EntityType.Obj) && !objEnt.isDetailsfetched()) {
				// re-map it according to it type to the right obj (Int, Real, ... )
				ObjEntity resultObjEntity = objEnt.subClassToType( objEnt);
				if( resultObjEntity instanceof ValEntity) {
					((ValEntity)resultObjEntity).fetchDetails();
				}
				results.set(i, resultObjEntity);
			}
		}
		
		return results; 
	}
	
	public static ObjEntity fetchByHref(ObjEntity objEnt) throws EntityException {
		
		ObjEntity resultObjEntity = null;
		
		if( objEnt.fetchByHref()){
		
			// Obj could be of any obix type.
			if(!objEnt.getObjtype().equals(EntityType.Obj)) {
				// re-map it according to it type to the right obj (Int, Real, ... )
				resultObjEntity = objEnt.subClassToType( objEnt);
				if( resultObjEntity instanceof ValEntity) {
					((ValEntity)resultObjEntity).fetchDetails();
				}
			} else {
				resultObjEntity = objEnt;
			}
		}
		
		return resultObjEntity;
	}

	public List<ObjEntity> fetchByDisplayName() throws EntityException{
		
		List<ObjEntity> results = new ArrayList<ObjEntity>();
		
		ArrayList<Object> params = new ArrayList<Object>();
		params.add("%" + getObixObject().getDisplayName() + "%");
		
		queryMultiple(SEARCH_OBJ_BY_DISPLAYNAME, params.toArray(), new ObjResultSetMultipleHandler<ObjEntity>(results));
		
		for( int i = 0; i < results.size();i++){
			ObjEntity objEnt = (ObjEntity)results.get(i);

			if (objEnt.getObj_contract_id() != null) {
				ContractEntity contractEntity = new ContractEntity(objEnt.getObj_contract_id());
				contractEntity.fetch();
				objEnt.getObixObject().setIs(contractEntity.getObixContract());
			}
			
			if(!objEnt.getObjtype().equals(EntityType.Obj) && !objEnt.isDetailsfetched()) {
				// re-map it according to it type to the right obj (Int, Real, ... )
				ObjEntity resultObjEntity = objEnt.subClassToType( objEnt);
				if( resultObjEntity instanceof ValEntity) {
					((ValEntity)resultObjEntity).fetchDetails();
				}
				results.set(i, resultObjEntity);
			}
		}
		
		return results; 
		
	}

	public boolean fetchByObjectId() throws EntityException {

		ArrayList<Object> params = new ArrayList<Object>();
		params.add(getId());

		doQueryByObjectId(params);

		if (isFetched()) {
			if (getObj_contract_id() != null) {
				ContractEntity contractEntity = new ContractEntity(getObj_contract_id());
				contractEntity.fetch();
				getObixObject().setIs(contractEntity.getObixContract());
			}
			return true;
		}

		return false;
	}
	
	protected void doQueryByObjectId(List<Object> params) throws EntityException {
		query(SEARCH_OBJ_BY_ID, params.toArray(), new ObjResultSetHandler<ObjEntity>(this));
	}
	
	public List<ObjEntity> fetchByContract () throws EntityException {
		
		List<ObjEntity> objs = new ArrayList<ObjEntity>();
		
		ContractEntity contractEntity = new ContractEntity(obixObject.getIs());
		
		List<ContractEntity> contractEntityList = new ArrayList<ContractEntity>();
		for( Uri uri : contractEntity.getObixContract().getUris()){
			List<ContractEntity> list = contractEntity.searchContractByUri(uri.getPath());
			for( ContractEntity entity : list){
				contractEntityList.add(entity);
			}
		}
		
		//List<ContractEntity> contractEntityList = contractEntity.searchContractByUri(contractEntity.getObixContract().getUris()[0].getPath());

		if( contractEntityList != null && contractEntityList.size() > 0) {
			
			//ArrayList fetchedObj = new ArrayList();
			
			Iterator<ContractEntity> iter = contractEntityList.iterator();
			while( iter.hasNext()){
				
				ObjEntity objEnt = new ObjEntity(new Obj());
				ContractEntity contractEnt = (ContractEntity)iter.next();
				
				
				List<Object> params = new ArrayList<Object>();
				params.add(contractEnt.getContractid());
				query(SEARCH_OBJ_BY_CONTRACT_ID, params.toArray(), new ObjResultSetHandler<ObjEntity>(objEnt));
				
				// Obj could be of any obix type.
				if(!objEnt.getObjtype().equals(EntityType.Obj)) {
					// re-map it according to it type to the right obj (Int, Real, ... )
					ObjEntity subentity = subClassToType( objEnt);
					if( subentity != null) objEnt = subentity;
					if( objEnt instanceof ValEntity) {
						((ValEntity)objEnt).fetchDetails();
					}
				}
				
				if (objEnt.isFetched()) {
					
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

		List<Object> params = new ArrayList<Object>();
		params.add(getId());

		List<ObjEntity> childsList = new ArrayList<ObjEntity>();

		queryMultiple(SEARCH_OBJ_BY_PARENT_ID, params.toArray(), new ObjResultSetMultipleHandler<ObjEntity>(childsList));

		
		for( int i = 0; i < childsList.size();i++){
			ObjEntity objEnt = (ObjEntity)childsList.get(i);
			
			if (objEnt.getObj_contract_id() != null) {
				ContractEntity contractEntity = new ContractEntity(objEnt.getObj_contract_id());
				contractEntity.fetch();
				objEnt.getObixObject().setIs(contractEntity.getObixContract());
			}
			
			if(!objEnt.getObjtype().equals(EntityType.Obj)) {
				// re-map it according to it type to the right obj (Int, Real, ... )
				ObjEntity resultObjEntity = objEnt.subClassToType( objEnt);
				if( resultObjEntity instanceof ValEntity) {
					((ValEntity)resultObjEntity).fetchDetails();
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

			List<Object> params = new ArrayList<Object>();
			params.add(getId());
			params.add(new Date(millisFrom));
			params.add(new Date(millisTo));
			
			queryMultiple(SEARCH_OBJ_BY_PARENT_ID_AND_TIMESTAMP, params.toArray(), new ObjResultSetMultipleHandler<ObjEntity>(childsList));
		}

		
		for( int i = 0; i < childsList.size();i++){
			ObjEntity objEnt = (ObjEntity)childsList.get(i);
			
			if (objEnt.getObj_contract_id() != null) {
				ContractEntity contractEntity = new ContractEntity(objEnt.getObj_contract_id());
				contractEntity.fetch();
				objEnt.getObixObject().setIs(contractEntity.getObixContract());
			}
			
			if(!objEnt.getObjtype().equals(EntityType.Obj) && !objEnt.isDetailsfetched()) {
				// re-map it according to it type to the right obj (Int, Real, ... )
				ObjEntity resultObjEntity = objEnt.subClassToType( objEnt);
				if( resultObjEntity instanceof ValEntity) {
					((ValEntity)resultObjEntity).fetchDetails();
				}
				childsList.set(i, resultObjEntity);
			}
		}
		
		setChilds(childsList);
	}

	

	public void setId(Integer id) {
		this.objid = id;
	}

	public Integer getId() {
		return objid;
	}
	
	public void setObj_uri(String obj_uri) {
		this.obj_uri = obj_uri;
	}

	public String getObj_uri() {
		return obj_uri;
	}
	
	
	public void setObj_contract_id(Integer obj_contract_id) {
		this.obj_contract_id = obj_contract_id;
	}

	public Integer getObj_contract_id() {
		return obj_contract_id;
	}

	

	public void setObjtype(EntityType objtype) {
		this.objtype = objtype;
	}

	public EntityType getObjtype() {
		return objtype;
	}
	
	public void setParent_id(Integer parent_id) {
		this.parent_id = parent_id;
	}

	public Integer getParent_id() {
		return parent_id;
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
	
	public void setRangeUriId(Integer rangeUriId) {
		this.rangeUriId = rangeUriId;
	}

	public Integer getRangeUriId() {
		return rangeUriId;
	}
	

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getUnit() {
		return unit;
	}
	
	public void setInContractId(Integer inContractId) {
		this.inContractId = inContractId;
	}

	public Integer getInContractId() {
		return inContractId;
	}
	
	public void setOfContractId(Integer ofContractId) {
		this.ofContractId = ofContractId;
	}

	public Integer getOfContractId() {
		return ofContractId;
	}
	
	public void setObixObject(Obj obixObject) {
		this.obixObject = obixObject;
	}

	public Obj getObixObject() {
		return obixObject;
	}
	

	public void setChilds(List<ObjEntity> childs) {
		this.childs = childs;
	}

	public List<ObjEntity> getChilds() {
		if (childs == null)
			childs = new ArrayList<ObjEntity >();
		return childs;
	}

	public ObjEntity getChildByName( String name){
		
		List<ObjEntity> childList = getChilds();
		
		for(int i = 0; i < childList.size(); i++){
			ObjEntity child = childList.get(i);
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
	

	public boolean isDetailsfetched() {
		return detailsfetched;
	}

	public void setDetailsfetched(boolean detailsfetched) {
		this.detailsfetched = detailsfetched;
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
			subObjEntity = new AbsTimeEntity();
		else if (entType.equals(EntityType.Bool))
			subObjEntity = new BoolEntity();
		else if (entType.equals(EntityType.Enum))
			subObjEntity = new EnumEntity();
		else if (entType.equals(EntityType.Feed))
			subObjEntity = new FeedEntity();
		else if (entType.equals(EntityType.Int))
			subObjEntity = new IntEntity();
		else if (entType.equals(EntityType.List))
			subObjEntity = new ListEntity();
		else if (entType.equals(EntityType.Real))
			subObjEntity = new RealEntity();
		
		else if (entType.equals(EntityType.Ref))
			subObjEntity = new RefEntity();
		
		else if (entType.equals(EntityType.RelTime))
			subObjEntity = new RelTimeEntity();
		else if (entType.equals(EntityType.Str))
			subObjEntity = new StrEntity();
		else if (entType.equals(EntityType.Uri))
			subObjEntity = new UriEntity();
		
		if( subObjEntity != null){
			subObjEntity.copyFields(rootentity);
		}
		return subObjEntity;
	}
	
	public void copyFields(ObjEntity rootObject){
		
		objid = rootObject.getId();
		obj_uri = rootObject.getObj_uri();
		obj_contract_id = rootObject.getObj_contract_id();
		objtype = rootObject.getObjtype();
		parent_id = rootObject.getParent_id();
		creationDate = rootObject.getCreationDate();
		modificationDate = rootObject.getModificationDate();
		rangeUriId = rootObject.getRangeUriId();
		unit = rootObject.getUnit();
		inContractId = rootObject.getInContractId();
		ofContractId = rootObject.getOfContractId();
		obixObject = rootObject.getObixObject();
		fetched = rootObject.isFetched(); 
	}

	public class ObjResultSetMultipleHandler<T extends ObjEntity> extends ResultSetMultipleHandler {

		private List<T> entityList;
		protected T entity;

		public ObjResultSetMultipleHandler(List<T> entityList) {
			this.entityList = entityList;
		}

		public void getRowAsBean(ResultSet rs) throws Exception {

			Integer id = getInteger(rs, COL_OBJ_ID);
			if (id != null)
				entity.setId(id);
			
			entity.setObj_uri(getString(rs, COL_OBJ_URI));
			if( entity.getObj_uri() != null && getObjtype() != EntityType.Ref){
				Uri href = new Uri("", entity.getObj_uri());
				entity.getObixObject().setHref(href);
			}
			
			entity.setObj_contract_id(getInteger(rs, COL_OBJ_CONTRACT_ID));
			//entity.setObjtype(EntityType.getEnum(getInteger(rs, COL_OBJ_TYPE_ID)));
			entity.setParent_id(getInteger(rs, COL_OBJ_PARENT_ID));
			entity.setCreationDate(getDate(rs, COL_CREATE_TS));
			entity.setModificationDate(getDate(rs, COL_MODIFIED_TS));

			entity.setRangeUriId(getInteger(rs, COL_OBJ_RANGE_URI_ID));
			entity.setUnit(getString(rs, COL_OBJ_UNIT));
			
			entity.setInContractId(getInteger(rs, COL_OBJ_IN_CONTRACT_ID));
			entity.setOfContractId(getInteger(rs, COL_OBJ_OF_CONTRACT_ID));
			
			EntityType fetchType = EntityType.getEnum( getInteger(rs, COL_OBJ_TYPE_ID));
			if( entity.getObjtype() != fetchType){
				
				entity.setObjtype(fetchType);
				entity.setObixObject(subClassObjToType(entity));
			}
			
			
			
			entity.getObixObject().setName(getString(rs, COL_OBJ_NAME));
			entity.getObixObject().setDisplayName(getString(rs, COL_OBJ_DISPLAYNAME));
			entity.getObixObject().setDisplay(getString(rs, COL_OBJ_DISPLAY));
			entity.getObixObject().setIsNull(getBoolean(rs, COL_OBJ_ISNULL));
			entity.getObixObject().setWritable(getBoolean(rs, COL_OBJ_WRITABLE));
			
			Integer statusId = getInteger(rs, COL_OBJ_STATUS_ID);
			if( statusId != null) {
				entity.getObixObject().setStatus(Status.getStatusFromId(statusId.intValue()));
			}
			
			switch(fetchType.getIdent().intValue()){
				case 2 : // abstime
					((Abstime) entity.getObixObject()).setVal(getDate(rs, COL_OBJ_VALUE_TS));
					break;
				case 3 : //boolean
					((Bool) entity.getObixObject()).setVal(getBoolean(rs, COL_OBJ_VALUE_BOOL));
					break;
				case 5: //enum
					((com.ptoceti.osgi.obix.object.Enum)entity.getObixObject()).setVal(getString(rs, COL_OBJ_VALUE_TEXT));
					break;
				case 6: //feed
					break;
				case 7: // int
					((Int) entity.getObixObject()).setMax(getInteger(rs, COL_OBJ_MAX));
					((Int) entity.getObixObject()).setMin(getInteger(rs, COL_OBJ_MIN));
					((Int) entity.getObixObject()).setVal(getInteger(rs, COL_OBJ_VALUE_INT));
					if( entity.getUnit() != null && !entity.getUnit().isEmpty()){
						Uri unit = new Uri("", entity.getUnit());
						((Int)entity.getObixObject()).setUnit(unit);
					}
					break;
				case 8: //list
					((com.ptoceti.osgi.obix.object.List)entity.getObixObject()).setMax( getInteger(rs, COL_OBJ_MAX));
					((com.ptoceti.osgi.obix.object.List)entity.getObixObject()).setMin( getInteger(rs, COL_OBJ_MIN));
					break;
				case 9: //real
					((Real) entity.getObixObject()).setMax(getDouble(rs, COL_OBJ_MAX_REAL));
					((Real) entity.getObixObject()).setMin(getDouble(rs, COL_OBJ_MIN_REAL));
					((Real) entity.getObixObject()).setVal(getDouble(rs, COL_OBJ_VALUE_REAL));
					((Real) entity.getObixObject()).setPrecision(getInteger(rs, COL_OBJ_PRECISION));
					if( entity.getUnit() != null && !entity.getUnit().isEmpty()){
						Uri unit = new Uri("", entity.getUnit());
						((Real)entity.getObixObject()).setUnit(unit);
					}
					break;
				case 10: //ref
					((Ref) entity.getObixObject()).setHref(new Uri("", (getString(rs, COL_OBJ_VALUE_TEXT))));
					break;
				case 11: //realtime
					((Reltime) entity.getObixObject()).setVal(getString(rs,COL_OBJ_VALUE_TEXT));
					break;
				case 12: //str
					((Str) entity.getObixObject()).setMax(getInteger(rs, COL_OBJ_MAX));
					((Str) entity.getObixObject()).setMin(getInteger(rs, COL_OBJ_MIN));
					((Str) entity.getObixObject()).setVal(getString(rs, COL_OBJ_VALUE_TEXT));
					break;
				case 13: // uri
					((Uri) entity.getObixObject()).setVal(getString(rs, COL_OBJ_VALUE_TEXT)); 
					break;
			}

			entity.setFetched(true);
		}

		public void getNextRowAsBean(ResultSet rs) throws Exception {

			getNextBean();
			getRowAsBean(rs);
			entityList.add(entity);

		}

		public void getNextBean() {
			Obj obixObj = new Obj();
			entity = (T) new  ObjEntity(obixObj);
		}
	}
	

	public class ObjResultSetHandler<T extends ObjEntity> extends ResultSetSingleHandler {

		protected T entity;

		public ObjResultSetHandler(T entity) {
			this.entity = entity;
		}

		public void getRowAsBean(ResultSet rs) throws Exception {
	
			Integer id = getInteger(rs, COL_OBJ_ID);
			if (id != null)
				entity.setId(id);
			
			entity.setObj_uri(getString(rs, COL_OBJ_URI));
			if( entity.getObj_uri() != null && getObjtype() != EntityType.Ref){
				Uri href = new Uri("", entity.getObj_uri());
				entity.getObixObject().setHref(href);
			}
			
			entity.setObj_contract_id(getInteger(rs, COL_OBJ_CONTRACT_ID));
			//entity.setObjtype(EntityType.getEnum(getInteger(rs, COL_OBJ_TYPE_ID)));
			entity.setParent_id(getInteger(rs, COL_OBJ_PARENT_ID));
			entity.setCreationDate(getDate(rs, COL_CREATE_TS));
			entity.setModificationDate(getDate(rs, COL_MODIFIED_TS));

			entity.setRangeUriId(getInteger(rs, COL_OBJ_RANGE_URI_ID));
			entity.setUnit(getString(rs, COL_OBJ_UNIT));
			entity.setInContractId(getInteger(rs, COL_OBJ_IN_CONTRACT_ID));
			entity.setOfContractId(getInteger(rs, COL_OBJ_OF_CONTRACT_ID));
			
			EntityType fetchType = EntityType.getEnum( getInteger(rs, COL_OBJ_TYPE_ID));
			if( entity.getObjtype() != fetchType){
				entity.setObjtype(fetchType);
				entity.setObixObject(subClassObjToType(entity));
			}
			
		
			
			entity.getObixObject().setName(getString(rs, COL_OBJ_NAME));
			entity.getObixObject().setDisplayName(getString(rs, COL_OBJ_DISPLAYNAME));
			entity.getObixObject().setDisplay(getString(rs, COL_OBJ_DISPLAY));
			entity.getObixObject().setIsNull(getBoolean(rs, COL_OBJ_ISNULL));
			entity.getObixObject().setWritable(getBoolean(rs, COL_OBJ_WRITABLE));
			entity.getObixObject().setUpdateTimeStamp(entity.getModificationDate() == null ? entity.getCreationDate().getTime() : entity.getModificationDate().getTime());
			
			Integer statusId = getInteger(rs, COL_OBJ_STATUS_ID);
			if( statusId != null) {
				entity.getObixObject().setStatus(Status.getStatusFromId(statusId.intValue()));
			}
			
			switch(fetchType.getIdent().intValue()){
				case 2 : // abstime
					((Abstime) entity.getObixObject()).setVal(getDate(rs, COL_OBJ_VALUE_TS));
					break;
				case 3 : //boolean
					((Bool) entity.getObixObject()).setVal(getBoolean(rs, COL_OBJ_VALUE_BOOL));
					break;
				case 5: //enum
					((com.ptoceti.osgi.obix.object.Enum)entity.getObixObject()).setVal(getString(rs, COL_OBJ_VALUE_TEXT));
					break;
				case 6: //feed
					break;
				case 7: // int
					((Int) entity.getObixObject()).setMax(getInteger(rs, COL_OBJ_MAX));
					((Int) entity.getObixObject()).setMin(getInteger(rs, COL_OBJ_MIN));
					((Int) entity.getObixObject()).setVal(getInteger(rs, COL_OBJ_VALUE_INT));
					if( entity.getUnit() != null && !entity.getUnit().isEmpty()){
						Uri unit = new Uri("", entity.getUnit());
						((Int)entity.getObixObject()).setUnit(unit);
					}
					break;
				case 8: //list
					((com.ptoceti.osgi.obix.object.List)entity.getObixObject()).setMax( getInteger(rs, COL_OBJ_MAX));
					((com.ptoceti.osgi.obix.object.List)entity.getObixObject()).setMin( getInteger(rs, COL_OBJ_MIN));
					break;
				case 9: //real
					((Real) entity.getObixObject()).setMax(getDouble(rs, COL_OBJ_MAX_REAL));
					((Real) entity.getObixObject()).setMin(getDouble(rs, COL_OBJ_MIN_REAL));
					((Real) entity.getObixObject()).setVal(getDouble(rs, COL_OBJ_VALUE_REAL));
					((Real) entity.getObixObject()).setPrecision(getInteger(rs, COL_OBJ_PRECISION));
					if( entity.getUnit() != null && !entity.getUnit().isEmpty()){
						Uri unit = new Uri("", entity.getUnit());
						((Real)entity.getObixObject()).setUnit(unit);
					}
					break;
				case 10: //ref
					((Ref) entity.getObixObject()).setHref(new Uri("", (getString(rs, COL_OBJ_VALUE_TEXT))));
					break;
				case 11: //realtime
					((Reltime) entity.getObixObject()).setVal(getString(rs,COL_OBJ_VALUE_TEXT));
					break;
				case 12: //str
					((Str) entity.getObixObject()).setMax(getInteger(rs, COL_OBJ_MAX));
					((Str) entity.getObixObject()).setMin(getInteger(rs, COL_OBJ_MIN));
					((Str) entity.getObixObject()).setVal(getString(rs, COL_OBJ_VALUE_TEXT));
					break;
				case 13: // uri
					((Uri) entity.getObixObject()).setVal(getString(rs, COL_OBJ_VALUE_TEXT)); 
					break;
			}

			entity.setFetched(true);
		}
	}
	
		
		
		

	public class ObjResultSetGeneratedKeysHandler<T extends ObjEntity> extends ResultSetGeneratedKeysHandler {

		private T entity;

		public ObjResultSetGeneratedKeysHandler(T entity) {
			this.entity = entity;
		}

		public void getRowsKey(ResultSet rs) throws Exception {
			entity.setId(getRowID(rs));
		}
	}
	
}
