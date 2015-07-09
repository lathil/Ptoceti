package com.ptoceti.osgi.obix.impl.entity;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : IntEntity.java
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
import java.util.Date;
import java.util.List;

import com.ptoceti.osgi.obix.object.Int;
import com.ptoceti.osgi.obix.object.Uri;


public class IntEntity extends ObjEntity implements ValEntity {

	private static final String SEARCH_INT_BY_HREF = "select object.* from object where object.type_id=7 and object.uri_hash=?";
	private static final String CREATE_INT = "insert into object (name, uri, uri_hash, contract_id, isnullable, displayname, display, writable, status_id, type_id, parent_id, created_ts, min, max, unit, value_int ) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String DELETE_INT = "delete from object where id=?";

	private static final String SEARCH_OBJ_BY_PARENT_ID_AND_TIMESTAMP = "select object.* from object  where object.type_id=7 and object.parent_id = ? and object.created_ts > ? and object.created_ts <= ?";

	private static final String UPDATE_INT = "update object set name = ?, isnullable = ?, displayname = ?, display = ?,writable = ?, status_id = ?, modified_ts = ?, min = ?, max = ?, unit = ?, value_int = ? where id = ? ";



	public IntEntity(){
		super(EntityType.Int);
	}
	
	public IntEntity(ObjEntity entObj) throws EntityException {
		super(entObj);
		fetchByObjectId();
	}

	public IntEntity(Int obixInt) {
		super(obixInt);
		setObjtype(EntityType.Int);
	}

	public void create() throws EntityException {


		List<Object> params = getCreateParam();
		params.add(((Int) getObixObject()).getMin());
		params.add(((Int) getObixObject()).getMax());

		Uri unitUri = ((Int) getObixObject()).getUnit();
		if (unitUri != null) {
			params.add(unitUri.getVal());
		} else {
			params.add(null);
		}

		params.add(((Int) getObixObject()).getVal());
		update(CREATE_INT, params.toArray(), new IntResultSetGeneratedKeysHandler(this));
	}

	public void delete() throws EntityException {

		deleteReferences();
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(getId());
		update(DELETE_INT, params.toArray(), null);
	}
	
	@Override
	protected void deleteReferences() throws EntityException{
		super.deleteReferences();
		
		if (getId() != null) {
			UriEntity uriEntity = new UriEntity(new Uri());
			uriEntity.setId(getId());
			uriEntity.delete();
		}
	}
	
	public void update() throws EntityException {

		List<Object> params = getUpdateParam();
		params.add(((Int) getObixObject()).getMin());
		params.add(((Int) getObixObject()).getMax());
		Uri unitUri = ((Int) getObixObject()).getUnit();
		if (unitUri != null) {
			params.add(unitUri.getVal());
		} else {
			params.add(null);
		}
		params.add(((Int) getObixObject()).getVal());
		params.add(getId());

		update(UPDATE_INT, params.toArray(), null);
		getObixObject().setUpdateTimeStamp(((Date)params.get(6)).getTime());
	}

	public boolean fetchByHref() throws EntityException {
		boolean found = super.fetchByHref();
		if( found){
			fetchDetails();
		}
		return found;
	}
	@Override
	protected void doQueryByHref(List<Object> params) throws EntityException{
		query(SEARCH_INT_BY_HREF, params.toArray(), new IntResultSetHandler(this));
	}

	public boolean fetchByObjectId() throws EntityException {
		boolean found = super.fetchByObjectId();
		if( found) {
			fetchDetails();
		}
		return found;
	}
	@Override
	protected void doQueryByObjectId(List<Object> params) throws EntityException {
		query(SEARCH_OBJ_BY_ID, params.toArray(), new IntResultSetHandler(this));
	}

	public List<IntEntity> fetchByParentIdFilterByTimestamsp(int parentId, long millisFrom, long millisTo) throws EntityException  {
		
		List<Object> params = new ArrayList<Object>();
		params.add(parentId);
		params.add(millisFrom);
		params.add(millisTo);

		List<IntEntity> childsList = new ArrayList<IntEntity>();

		queryMultiple(SEARCH_OBJ_BY_PARENT_ID_AND_TIMESTAMP, params.toArray(),
				new ObjIntResultSetMultipleHandler(childsList));

		return childsList;
	}
	
	public void fetchDetails() throws EntityException {
		if( getObj_uri() != null){
			Uri href = new Uri("", getObj_uri());
			getObixObject().setHref(href);
		}
		if( getUnit() != null && !getUnit().isEmpty()){
			Uri unit = new Uri("", getUnit());
			((Int)getObixObject()).setUnit(unit);
		}
		setDetailsfetched(true);
	}


	
	public class ObjIntResultSetMultipleHandler extends ObjResultSetMultipleHandler<IntEntity> {

		public ObjIntResultSetMultipleHandler(List<IntEntity> entityList) {
			super(entityList);
	
		}
		
		@Override
		public void getNextBean() {
			Int obixObj = new Int();
			entity = new IntEntity(obixObj);
		}
	}

	public class IntResultSetHandler extends ObjResultSetHandler<IntEntity> {

		public IntResultSetHandler(IntEntity entity) {
			super(entity);
		}

		public void getRowAsBean(ResultSet rs) throws Exception {

			super.getRowAsBean(rs);
		}
	}

	public class IntResultSetGeneratedKeysHandler extends ObjResultSetGeneratedKeysHandler<IntEntity> {

		public IntResultSetGeneratedKeysHandler(IntEntity entity) {
			super(entity);
		}
	}
	
}
