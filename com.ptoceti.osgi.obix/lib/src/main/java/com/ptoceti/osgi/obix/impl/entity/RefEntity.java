package com.ptoceti.osgi.obix.impl.entity;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : RefEntity.java
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

import com.ptoceti.osgi.obix.impl.entity.ObjEntity.ObjResultSetHandler;
import com.ptoceti.osgi.obix.object.Ref;
import com.ptoceti.osgi.obix.object.Uri;

public class RefEntity extends ObjEntity {
	
	private static final String SEARCH_REF = "select object.* from object where object.type_id=? and value_text=?";
	private static final String CREATE_REF = "insert into object (name, uri, uri_hash, contract_id, isnullable, displayname, display, writable, status_id, type_id, parent_id, created_ts, value_text ) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String DELETE_REF = "delete from object where id=?";
	private static final String UPDATE_REF = "update object set name = ?, isnullable = ?, displayname = ?, display = ?,writable = ?, status_id = ?, modified_ts = ?, value_text = ? where id = ? ";


	public RefEntity(){
		super(EntityType.Ref);
	}
	
	public RefEntity(ObjEntity entObj)  throws EntityException {
		super(entObj);
		fetchByObjectId();
	}
	
	public RefEntity(Ref obixObj) {
		super(obixObj);
		setObjtype(EntityType.Ref);
	}

	public boolean fetchByHref() throws EntityException {

		ArrayList<Object> params = new ArrayList<Object>();
		params.add(EntityType.Ref.getIdent());
		params.add(getObixObject().getHref().getPath());
		
		query(SEARCH_REF, params.toArray(), new ObjResultSetHandler<ObjEntity>(this));

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
	
	public void create() throws EntityException {

		List<Object> params = getCreateParam();
		params.add(getObixObject().getHref().getPath());
		update(CREATE_REF, params.toArray(), new RefResultSetGeneratedKeysHandler(this));
	}

	public void delete() throws EntityException {

		deleteReferences();
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(getId());
		update(DELETE_REF, params.toArray(), null);
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
		params.add(getObixObject().getHref().getPath());
		update(UPDATE_REF, params.toArray(), null);
		getObixObject().setUpdateTimeStamp(((Date)params.get(6)).getTime());
	}
	
	public boolean fetchByObjectId() throws EntityException {
		boolean found = super.fetchByObjectId();
		if( found){
			fetchDetails();
		}
		return found;
	}
	
	public void fetchDetails() throws EntityException{
		setDetailsfetched(true);
	}
	
	public class RefResultSetHandler extends ObjResultSetHandler<RefEntity> {

		public RefResultSetHandler(RefEntity entity) {
			super(entity);
		}

		public void getRowAsBean(ResultSet rs) throws Exception {
			super.getRowAsBean(rs);
		}
	}

	public class RefResultSetGeneratedKeysHandler extends ObjResultSetGeneratedKeysHandler<RefEntity> {
		public RefResultSetGeneratedKeysHandler(RefEntity entity) {
			super(entity);
		}
	}
}
