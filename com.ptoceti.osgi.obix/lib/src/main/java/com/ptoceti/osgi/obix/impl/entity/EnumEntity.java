package com.ptoceti.osgi.obix.impl.entity;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : EnumEntity.java
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
import com.ptoceti.osgi.obix.object.Enum;
import com.ptoceti.osgi.obix.object.Int;
import com.ptoceti.osgi.obix.object.Real;
import com.ptoceti.osgi.obix.object.Str;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.impl.entity.EnumEntity.EnumResultSetGeneratedKeysHandler;
import com.ptoceti.osgi.obix.impl.entity.EnumEntity.EnumResultSetHandler;

public class EnumEntity extends ObjEntity {

	private Integer enumid;
	private Integer rangeUriId;

	private static final String CREATE_ENUM = "insert into enum (object_id, value, range_uri_id ) values (?,?,?,?)";
	private static final String DELETE_ENUM = "delete from enum where enum_id=?";
	
	private static final String SEARCH_ENUM_BY_OBJECT_ID = "select enum.* from enum where enum.object_id=?";

	private static final String UPDATE_ENUM = "update enum set object_id = ?, value = ?, range_uri_id = ? where id = ? ";

	private static final String COL_ENUM_ID = "id";
	private static final String COL_ENUM_OBJECT_ID = "object_id";
	private static final String COL_ENUM_VALUE = "value";
	private static final String COL_ENUM_RANGE_URI_ID = "range_uri_id";

	public EnumEntity() {
		super(EntityType.Enum);
	}
	
	public EnumEntity(ObjEntity entObj) throws EntityException {
		super(entObj);
		fetchDetailsById();
	}
	
	public EnumEntity(Enum obixObj) {
		super(obixObj);
		setObjtype(EntityType.Enum);
	}

	public void setEnumid(Integer enumid) {
		this.enumid = enumid;
	}

	public Integer getEnumid() {
		return enumid;
	}

	public void create() throws EntityException {

		super.create();
		
		ArrayList params = new ArrayList();
		params.add(getId());
		params.add(((Int)getObixObject()).getVal());
		
		Uri rangeUri = ((Enum)getObixObject()).getRange();
		if (rangeUri != null) {
			UriEntity uriEntity = new UriEntity(rangeUri);
			uriEntity.create();
			setRangeUriId(uriEntity.getUriId());
		}
		
		params.add(getRangeUriId());
		update(CREATE_ENUM, params.toArray(), new EnumResultSetGeneratedKeysHandler(this));
	}
	
	public void delete() throws EntityException {
		
		super.delete();
		
		if (getEnumid() != null) {
			UriEntity uriEntity = new UriEntity(new Uri());
			uriEntity.setId(getEnumid());
			uriEntity.delete();
		}
		
		ArrayList params = new ArrayList();
		params.add(getEnumid());
		update(DELETE_ENUM, params.toArray(), null);
	}
	
	public boolean fetchByHref() throws EntityException {

		boolean found = super.fetchByHref();
		if( found) {
		ArrayList params = new ArrayList();
		params.add(getId());

		query(SEARCH_ENUM_BY_OBJECT_ID, params.toArray(),
				new EnumResultSetHandler(this));
		
		}
		return found;
	}

	public boolean fetchByObjectId() throws EntityException {

		boolean found = super.fetchByObjectId();
		if( found) fetchDetailsById();
		return found;
	}
	
	private void fetchDetailsById() throws EntityException {
		ArrayList params = new ArrayList();
		params.add(getId());

		query(SEARCH_ENUM_BY_OBJECT_ID, params.toArray(),
				new EnumResultSetHandler(this));
	}

	public void update() throws EntityException {

		super.update();

		ArrayList params = new ArrayList();
		params.add(getId());
		params.add(((Real) getObixObject()).getVal());
		params.add(getRangeUriId());
		params.add(getEnumid());

		update(UPDATE_ENUM, params.toArray(), null);
	}

	public void setRangeUriId(Integer rangeUriId) {
		this.rangeUriId = rangeUriId;
	}

	public Integer getRangeUriId() {
		return rangeUriId;
	}

	public class EnumResultSetHandler extends ResultSetSingleHandler {

		private EnumEntity entity;

		public EnumResultSetHandler(EnumEntity entity) {
			this.entity = entity;
		}

		public void getRowAsBean(ResultSet rs) throws Exception {

			Integer id = getInteger(rs, COL_ENUM_ID);
			if (id != null)
				entity.setEnumid(id);

			((Str) entity.getObixObject())
					.setVal(getString(rs, COL_ENUM_VALUE));
			
			entity.setRangeUriId( getInteger(rs, COL_ENUM_RANGE_URI_ID));
			entity.setDetailsfetched(true);
		}
	}

	public class EnumResultSetGeneratedKeysHandler extends
			ResultSetGeneratedKeysHandler {

		private EnumEntity entity;

		public EnumResultSetGeneratedKeysHandler(EnumEntity entity) {
			this.entity = entity;
		}

		public void getRowsKey(ResultSet rs) throws Exception {
			entity.setEnumid(getRowID(rs));
		}
	}
}
