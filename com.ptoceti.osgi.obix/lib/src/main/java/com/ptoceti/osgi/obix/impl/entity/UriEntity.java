package com.ptoceti.osgi.obix.impl.entity;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : UriEntity.java
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
import java.util.List;

import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.object.Val;


public class UriEntity extends ObjEntity implements ValEntity {

	private static final String SEARCH_URI_BY_HREF = "select object.* from object where object.type_id=13 and object.uri_hash=?";
	private static final String CREATE_URI = "insert into object (name, uri, uri_hash, contract_id, isnullable, displayname, display, writable, status_id, type_id, parent_id, created_ts, value_text ) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String DELETE_URI = "delete from object where id=?";
	
	private static final String SEARCH_URI_BY_CONTRACT_ID = "select object.* from  object, contracturi where object.id = contracturi.uri_id  and contracturi.contract_id=?";

	private static final String UPDATE_URI = "update object set name = ?, isnullable = ?, displayname = ?, display = ?,writable = ?, status_id = ?, modified_ts = ?, value_text = ? where id = ? ";

	public UriEntity() {
		super(EntityType.Uri);
	}
	
	public UriEntity(ObjEntity entObj) throws EntityException {
		super(entObj);
		fetchByObjectId();
	}

	public UriEntity(Uri obixObj) {
		super(obixObj);
		setObjtype(EntityType.Uri);
	}

	public void create() throws EntityException {
		List<Object> params = getCreateParam();
		params.add(((Val) getObixObject()).getVal());
		update(CREATE_URI, params.toArray(), new UriResultSetGeneratedKeysHandler(this));
	}

	public void delete() throws EntityException {

		deleteReferences();
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(getId());
		update(DELETE_URI , params.toArray(), null);

	}
	
	@Override
	protected void deleteReferences() throws EntityException{
		super.deleteReferences();
	}
	
	public void update() throws EntityException {

		List<Object> params = getUpdateParam();
		params.add(((Uri) getObixObject()).getVal());
		params.add(getId());
		update(UPDATE_URI, params.toArray(), null);
	}

	public boolean fetchByHref() throws EntityException {
		return super.fetchByHref();
	}
	
	@Override
	protected void doQueryByHref(List<Object> params) throws EntityException{
		query(SEARCH_URI_BY_HREF, params.toArray(), new UriResultSetHandler(this));
	}

	public boolean fetchByObjectId() throws EntityException {
		return super.fetchByObjectId();
	}
	
	@Override
	protected void doQueryByObjectId(List<Object> params) throws EntityException {
		query(SEARCH_OBJ_BY_ID, params.toArray(), new UriResultSetHandler(this));
	}

	public List<UriEntity> searchUriByContractID(Integer contractId) throws EntityException {

		List<UriEntity> uriList = new ArrayList<UriEntity>();
		List<Object> params = new ArrayList<Object>();
		params.add(contractId);
		queryMultiple(SEARCH_URI_BY_CONTRACT_ID, params.toArray(), new UriResultSetMultipleHandler(uriList));

		return uriList;

	}

	public void fetchDetails() throws EntityException{
		// nothing to do here;
		setDetailsfetched(true);
	}
	
	public class UriResultSetMultipleHandler extends ObjResultSetMultipleHandler<UriEntity> {

		public UriResultSetMultipleHandler(List<UriEntity> entityList) {
			super(entityList);
		}

		public void getRowAsBean(ResultSet rs) throws Exception {
			super.getRowAsBean(rs);
			((Uri) entity.getObixObject()).setVal(getString(rs, COL_OBJ_VALUE_TEXT));
		}

		public void getNextBean() {
			Uri obixUri = new Uri();
			entity = new UriEntity(obixUri);
		}

	}

	public class UriResultSetHandler extends ObjResultSetHandler<UriEntity> {

		public UriResultSetHandler(UriEntity entity) {
			super(entity);
		}

		public void getRowAsBean(ResultSet rs) throws Exception {
			super.getRowAsBean(rs);
		}
	}

	public class UriResultSetGeneratedKeysHandler extends ObjResultSetGeneratedKeysHandler<UriEntity> {
		public UriResultSetGeneratedKeysHandler(UriEntity entity) {
			super(entity);
		}
	}
}
