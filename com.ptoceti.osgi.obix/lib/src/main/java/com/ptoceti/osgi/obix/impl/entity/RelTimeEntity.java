package com.ptoceti.osgi.obix.impl.entity;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : RelTimeEntity.java
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

import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Reltime;
import com.ptoceti.osgi.obix.object.Uri;


public class RelTimeEntity extends ObjEntity implements ValEntity {

	private static final String SEARCH_RELTIME_BY_HREF = "select object.* from object where object.type_id=11 and object.uri_hash=?";
	
	private static final String CREATE_RELTIME = "insert into object (name, uri, uri_hash, contract_id, isnullable, displayname, display, writable, status_id, type_id, parent_id, created_ts, min, max, value_text ) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	private static final String DELETE_RELTIME = "delete from object where id=?";
	//private static final String SEARCH_RELTIME_BY_OBJECT_ID = "select reltime.* from reltime where reltime.object_id=?";

	private static final String UPDATE_RELTIME = "update object set name = ?, isnullable = ?, displayname = ?, display = ?,writable = ?, status_id = ?, modified_ts = ?, min = ?, max = ?, value_text = ? where id = ? ";


	public RelTimeEntity() {
		super(EntityType.RelTime);
	}
	
	public RelTimeEntity(ObjEntity entObj) throws EntityException {
		super(entObj);
		fetchByObjectId();
	}

	public RelTimeEntity(Reltime obixObj) {
		super(obixObj);
		setObjtype(EntityType.RelTime);
	}

	public void create() throws EntityException {
		List<Object> params = getCreateParam();
		Reltime min = ((Reltime) getObixObject()).getMin();
		Reltime max = ((Reltime) getObixObject()).getMax();
		params.add(min != null ? min.getVal() : null);
		params.add(max != null ? max.getVal() : null);
		params.add(((Reltime) getObixObject()).getVal());
		update(CREATE_RELTIME, params.toArray(), new RelTimeResultSetGeneratedKeysHandler(this));
	}

	public void delete() throws EntityException {
		deleteReferences();
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(getId());
		update(DELETE_RELTIME, params.toArray(), null);
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
		Reltime min = ((Reltime) getObixObject()).getMin();
		Reltime max = ((Reltime) getObixObject()).getMax();
		params.add(min != null ? min.getVal() : null);
		params.add(max != null ? max.getVal() : null);
		params.add(((Reltime) getObixObject()).getVal());
		params.add(getId());

		update(UPDATE_RELTIME, params.toArray(), null);
		getObixObject().setUpdateTimeStamp(((Date)params.get(6)).getTime());
	}

	public boolean fetchByHref() throws EntityException {
		return super.fetchByHref();
	}
	@Override
	protected void doQueryByHref(List<Object> params) throws EntityException{
		query(SEARCH_RELTIME_BY_HREF, params.toArray(), new RelTimeResultSetHandler(this));
	}

	public boolean fetchByObjectId() throws EntityException {
		return super.fetchByObjectId();
	}
	@Override
	protected void doQueryByObjectId(List<Object> params) throws EntityException {
		query(SEARCH_OBJ_BY_ID, params.toArray(), new RelTimeResultSetHandler(this));
	}

	public void fetchDetails() throws EntityException{
		if( getObj_uri() != null){
			Uri href = new Uri("", getObj_uri());
			getObixObject().setHref(href);
		}
		setDetailsfetched(true);
	}
	
	public class RelTimeResultSetHandler extends ObjResultSetHandler<RelTimeEntity> {

		public RelTimeResultSetHandler(RelTimeEntity entity) {
			super(entity);
		}

		public void getRowAsBean(ResultSet rs) throws Exception {
			super.getRowAsBean(rs);
			// ((Reltime)entity.getObixObject()).setMax( getInteger(rs,
			// COL_INT_MAX));
			// ((Reltime)entity.getObixObject()).setMin( getInteger(rs,
			// COL_INT_MIN));
			
			
		}
	}

	public class RelTimeResultSetGeneratedKeysHandler extends ObjResultSetGeneratedKeysHandler<RelTimeEntity> {
		public RelTimeResultSetGeneratedKeysHandler(RelTimeEntity entity) {
			super(entity);
		}
	}
	
}
