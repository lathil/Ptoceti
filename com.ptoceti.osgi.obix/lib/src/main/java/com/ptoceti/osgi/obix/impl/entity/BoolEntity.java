package com.ptoceti.osgi.obix.impl.entity;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : BoolEntity.java
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

import com.ptoceti.osgi.data.ResultSetGeneratedKeysHandler;
import com.ptoceti.osgi.data.ResultSetSingleHandler;
import com.ptoceti.osgi.obix.object.Bool;
import com.ptoceti.osgi.obix.object.Int;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Real;
import com.ptoceti.osgi.obix.object.Str;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.impl.entity.IntEntity.IntResultSetGeneratedKeysHandler;
import com.ptoceti.osgi.obix.impl.entity.RealEntity.RealResultSetHandler;

public class BoolEntity extends ObjEntity implements ValEntity {

	private Integer boolid;
	private Integer rangeUriId;

	private static final String CREATE_BOOL = "insert into bool (object_id, value, range_uri_id ) values (?,?,?,?)";
	private static final String DELETE_BOOL = "delete from bool where bool.id=?";

	private static final String SEARCH_BOOL_BY_OBJECT_ID = "select bool.* from bool where bool.object_id=?";

	private static final String UPDATE_BOOL = "update bool set object_id = ?, value = ?, range_uri_id = ? where id = ? ";

	private static final String COL_BOOL_ID = "id";
	private static final String COL_BOOL_OBJECT_ID = "object_id";
	private static final String COL_BOOL_VALUE = "value";
	private static final String COL_BOOL_RANGE_URI_ID = "range_uri_id";

	public BoolEntity(){
		super(EntityType.Bool);
	}
	
	public BoolEntity(ObjEntity entObj) throws EntityException {
		super(entObj);
		fetchDetailsById();
	}

	public BoolEntity(Bool obixObj) {
		super(obixObj);
		setObjtype(EntityType.Bool);
	}

	public void setBoolid(Integer boolid) {
		this.boolid = boolid;
	}

	public Integer getBoolid() {
		return boolid;
	}

	public void create() throws EntityException {

		super.create();

		ArrayList params = new ArrayList();
		params.add(getId());
		params.add(((Int) getObixObject()).getVal());

		Uri rangeUri = ((Bool) getObixObject()).getRange();
		if (rangeUri != null) {
			UriEntity uriEntity = new UriEntity(rangeUri);
			uriEntity.create();
			setRangeUriId(uriEntity.getUriId());
		}

		params.add(getRangeUriId());
		update(CREATE_BOOL, params.toArray(),
				new BoolResultSetGeneratedKeysHandler(this));
	}

	public void delete() throws EntityException {

		super.delete();

		if (getBoolid() != null) {
			UriEntity uriEntity = new UriEntity(new Uri());
			uriEntity.setId(getBoolid());
			uriEntity.delete();
		}

		ArrayList params = new ArrayList();
		params.add(getBoolid());
		update(DELETE_BOOL, params.toArray(), null);
	}

	public boolean fetchByHref() throws EntityException {

		boolean found = super.fetchByHref();

		if (found) {
			ArrayList params = new ArrayList();
			params.add(getId());

			query(SEARCH_BOOL_BY_OBJECT_ID, params.toArray(),
					new BoolResultSetHandler(this));
		}
		return found;
	}

	public boolean fetchByObjectId() throws EntityException {

		boolean found = super.fetchByObjectId();
		if( found) fetchDetailsById();
		return found;
	}

	public void fetchDetailsById() throws EntityException {
		ArrayList params = new ArrayList();
		params.add(getId());

		query(SEARCH_BOOL_BY_OBJECT_ID, params.toArray(),
				new BoolResultSetHandler(this));
	}

	public void update() throws EntityException {

		super.update();

		ArrayList params = new ArrayList();
		params.add(getId());
		params.add(((Real) getObixObject()).getVal());
		params.add(getRangeUriId());
		params.add(getBoolid());

		update(UPDATE_BOOL, params.toArray(), null);
	}

	public void setRangeUriId(Integer rangeUriId) {
		this.rangeUriId = rangeUriId;
	}

	public Integer getRangeUriId() {
		return rangeUriId;
	}

	public class BoolResultSetHandler extends ResultSetSingleHandler {

		private BoolEntity entity;

		public BoolResultSetHandler(BoolEntity entity) {
			this.entity = entity;
		}

		public void getRowAsBean(ResultSet rs) throws Exception {

			Integer id = getInteger(rs, COL_BOOL_ID);
			if (id != null)
				entity.setBoolid(id);

			((Str) entity.getObixObject())
					.setVal(getString(rs, COL_BOOL_VALUE));

			entity.setRangeUriId(getInteger(rs, COL_BOOL_RANGE_URI_ID));
			entity.setDetailsfetched(true);
		}
	}

	public class BoolResultSetGeneratedKeysHandler extends
			ResultSetGeneratedKeysHandler {

		private BoolEntity entity;

		public BoolResultSetGeneratedKeysHandler(BoolEntity entity) {
			this.entity = entity;
		}

		public void getRowsKey(ResultSet rs) throws Exception {
			entity.setBoolid(getRowID(rs));
		}
	}
}
