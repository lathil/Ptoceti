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

import com.ptoceti.osgi.data.ResultSetGeneratedKeysHandler;
import com.ptoceti.osgi.data.ResultSetMultipleHandler;
import com.ptoceti.osgi.data.ResultSetSingleHandler;
import com.ptoceti.osgi.obix.object.Int;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.object.Val;
import com.ptoceti.osgi.obix.impl.entity.IntEntity.IntResultSetGeneratedKeysHandler;
import com.ptoceti.osgi.obix.impl.entity.IntEntity.IntResultSetHandler;

public class UriEntity extends ObjEntity implements ValEntity {

	private Integer uriId;

	private static final String CREATE_URI = "insert into uri (object_id, value ) values (?,?)";
	// private static final String SEARCH_INT_BY_HREF =
	// "select object.* from object, uri where object.uri_id = uri.id and uri.value=?"
	// ;
	private static final String SEARCH_URI_BY_ID = "select uri.* from uri where uri.id=?";
	private static final String SEARCH_URI_BY_OBJECT_ID = "select uri.* from uri where uri.object_id=?";
	private static final String SEARCH_URI_BY_CONTRACT_ID = "select object.*, uri.* from uri, object, contracturi where object.id = uri.object_id  and contracturi.uri_id = uri.id and contracturi.contract_id=?";

	private static final String DELETE_URI = "delete from uri where uri.id = ?";

	private static final String UPDATE_URI = "update uri set object_id = ?, value = ? where id = ? ";

	private static final String COL_URI_ID = "id";
	private static final String COL_URI_OBJECT_ID = "object_id";
	private static final String COL_URI_VALUE = "value";

	public UriEntity() {
		super(EntityType.Uri);
	}
	
	public UriEntity(ObjEntity entObj) throws EntityException {
		super(entObj);
		fetchDetailsById();
	}

	public UriEntity(Uri obixObj) {
		super(obixObj);
		// TODO Auto-generated constructor stub
		setObjtype(EntityType.Uri);
	}

	public void create() throws EntityException {

		super.create();

		ArrayList params = new ArrayList();
		params.add(getId());
		params.add(((Val) getObixObject()).getVal());
		update(CREATE_URI, params.toArray(),
				new UriResultSetGeneratedKeysHandler(this));
	}

	public void delete() throws EntityException {

		super.delete();

		ArrayList params = new ArrayList();
		params.add(this.getUriId());
		update(DELETE_URI, params.toArray(), null);

	}

	public boolean fetchByHref() throws EntityException {

		boolean found = super.fetchByHref();
		if (found) {
			ArrayList params = new ArrayList();
			params.add(getId());

			query(SEARCH_URI_BY_OBJECT_ID, params.toArray(),
					new UriResultSetHandler(this));
		}
		return found;
	}

	public boolean fetchByObjectId() throws EntityException {

		boolean found = super.fetchByObjectId();
		if (found)
			fetchDetailsById();
		return found;

	}
	
	public boolean fetchById() throws EntityException {

		ArrayList params = new ArrayList();
		params.add(getUriId());

		query(SEARCH_URI_BY_ID, params.toArray(),
				new UriResultSetHandler(this));
		
		return super.fetchByObjectId();

	}

	public void fetchDetailsById() throws EntityException {
		ArrayList params = new ArrayList();
		params.add(getId());

		query(SEARCH_URI_BY_OBJECT_ID, params.toArray(),
				new UriResultSetHandler(this));
	}

	public List searchUriByContractID(Integer contractId) throws EntityException {

		ArrayList uriList = new ArrayList();
		ArrayList params = new ArrayList();
		params.add(contractId);
		queryMultiple(SEARCH_URI_BY_CONTRACT_ID,
				params.toArray(), new UriResultSetMultipleHandler(uriList));

		return uriList;

	}

	public void update() throws EntityException {

		super.update();

		ArrayList params = new ArrayList();
		params.add(getId());
		params.add(((Uri) getObixObject()).getVal());
		params.add(getUriId());

		update(UPDATE_URI, params.toArray(), null);
	}

	public void setUriId(Integer uriId) {
		this.uriId = uriId;
	}

	public Integer getUriId() {
		return uriId;
	}

	public class UriResultSetMultipleHandler extends
			ObjResultSetMultipleHandler {

		public UriResultSetMultipleHandler(List entityList) {
			super(entityList);
		}

		public void getRowAsBean(ResultSet rs) throws Exception {
			super.getRowAsBean(rs);

			Integer uriId = getInteger(rs, COL_URI_ID);
			if (uriId != null)
				((UriEntity)entity).setUriId(uriId);
			Integer objId = getInteger(rs, COL_URI_OBJECT_ID);
			if ((objId != null) && (getId() == null))
				entity.setId(objId);

			((Uri) entity.getObixObject())
					.setVal(getString(rs, COL_URI_VALUE));
			
			entity.setDetailsfetched(true);
		}

		public void getNextBean() {
			Uri obixUri = new Uri();
			entity = new UriEntity(obixUri);
		}

	}

	public class UriResultSetHandler extends ResultSetSingleHandler {

		private UriEntity entity;

		public UriResultSetHandler(UriEntity entity) {
			this.entity = entity;
		}

		public void getRowAsBean(ResultSet rs) throws Exception {

			Integer uriId = getInteger(rs, COL_URI_ID);
			if (uriId != null)
				entity.setUriId(uriId);
			Integer objId = getInteger(rs, COL_URI_OBJECT_ID);
			if ((objId != null) && (getId() == null))
				entity.setId(objId);
			
			((Uri) entity.getObixObject())
					.setVal(getString(rs, COL_URI_VALUE));
			
			entity.setDetailsfetched(true);
		}
	}

	public class UriResultSetGeneratedKeysHandler extends
			ResultSetGeneratedKeysHandler {

		private UriEntity entity;

		public UriResultSetGeneratedKeysHandler(UriEntity entity) {
			this.entity = entity;
		}

		public void getRowsKey(ResultSet rs) throws Exception {
			entity.setUriId(getRowID(rs));
		}
	}
}
