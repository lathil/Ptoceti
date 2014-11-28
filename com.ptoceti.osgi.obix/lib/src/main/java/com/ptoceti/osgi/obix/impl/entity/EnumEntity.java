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
import java.util.Date;
import java.util.List;

import com.ptoceti.osgi.obix.object.Enum;
import com.ptoceti.osgi.obix.object.Uri;


public class EnumEntity extends ObjEntity {
	private static final String SEARCH_ENUM_BY_HREF = "select object.* from object where object.type_id=5 and object.uri_hash=?";
	private static final String CREATE_ENUM = "insert into object (name, uri, uri_hash, contract_id, isnullable, displayname, display, writable, status_id, type_id, parent_id, created_ts, value_text, range_uri_id ) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String DELETE_ENUM = "delete from object where id=?";


	private static final String UPDATE_ENUM = "update object set name = ?, isnullable = ?, displayname = ?, display = ?,writable = ?, status_id = ?, modified_ts = ?, value_text = ?, range_uri_id = ? where id = ? ";


	public EnumEntity() {
		super(EntityType.Enum);
	}
	
	public EnumEntity(ObjEntity entObj) throws EntityException {
		super(entObj);
		fetchByObjectId();
	}
	
	public EnumEntity(Enum obixObj) {
		super(obixObj);
		setObjtype(EntityType.Enum);
	}

	public void create() throws EntityException {

		List<Object> params = getCreateParam();
		params.add(((com.ptoceti.osgi.obix.object.Enum)getObixObject()).getVal());
		
		Uri rangeUri = ((com.ptoceti.osgi.obix.object.Enum)getObixObject()).getRange();
		if (rangeUri != null) {
			UriEntity uriEntity = new UriEntity(rangeUri);
			uriEntity.create();
			setRangeUriId(uriEntity.getId());
		}
		
		params.add(getRangeUriId());
		update(CREATE_ENUM, params.toArray(), new EnumResultSetGeneratedKeysHandler(this));
	}
	
	public void delete() throws EntityException {
		
		deleteReferences();
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(getId());
		update(DELETE_ENUM, params.toArray(), null);
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
		params.add(((com.ptoceti.osgi.obix.object.Enum) getObixObject()).getVal());
		params.add(getRangeUriId());

		update(UPDATE_ENUM, params.toArray(), null);
		getObixObject().setUpdateTimeStamp(((Date)params.get(6)).getTime());
	}
	 
	public boolean fetchByHref() throws EntityException {
		boolean found =  super.fetchByHref();
		if( found) {
			fetchDetails();
		}
		return found;
	}
	
	@Override
	protected void doQueryByHref(java.util.List<Object> params) throws EntityException{
		query(SEARCH_ENUM_BY_HREF, params.toArray(), new EnumResultSetHandler(this));
	}

	public boolean fetchByObjectId() throws EntityException {
		boolean found = super.fetchByObjectId();
		if( found) {
			fetchDetails();
		}
		return found;
	}
	
	@Override
	protected void doQueryByObjectId(java.util.List<Object> params) throws EntityException {
		query(SEARCH_OBJ_BY_ID, params.toArray(), new EnumResultSetHandler(this));
	}

	public void fetchDetails() throws EntityException {
		if( getObj_uri() != null){
			Uri href = new Uri("", getObj_uri());
			getObixObject().setHref(href);
		}
		
		if(getRangeUriId() != null) {
			UriEntity uriEntity = new UriEntity(new Uri());
			uriEntity.setId(getRangeUriId());
			if( uriEntity.fetchByObjectId()){
				((Enum)getObixObject()).setRange((Uri)uriEntity.getObixObject());
			}
		}
		setDetailsfetched(true);
	}

	public class EnumResultSetHandler extends ObjResultSetHandler<EnumEntity> {
		public EnumResultSetHandler(EnumEntity entity) {
			super(entity);
		}

		public void getRowAsBean(ResultSet rs) throws Exception {
			super.getRowAsBean(rs);
		}
	}

	public class EnumResultSetGeneratedKeysHandler extends ObjResultSetGeneratedKeysHandler<EnumEntity> {
		public EnumResultSetGeneratedKeysHandler(EnumEntity entity) {
			super(entity);
		}
	}
}
