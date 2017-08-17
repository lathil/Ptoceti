package com.ptoceti.osgi.obix.impl.entity;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : ListEntity.java
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

import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.List;
import com.ptoceti.osgi.obix.object.Uri;


public class ListEntity extends ObjEntity implements ValEntity {

	private static final String SEARCH_LIST_BY_HREF = "select object.* from object where object.type_id=8 and object.uri_hash=?";
	private static final String CREATE_LIST = "insert into object (name, uri, uri_hash, contract_id, isnullable, displayname, display, writable, status_id, type_id, parent_id, created_ts, min, max, of_contract_id ) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String DELETE_LIST = "delete from object where id=?";

	//private static final String UPDATE_LIST = "update object set set name = ?, isnullable = ?, displayname = ?, display = ?,writable = ?, status_id = ?, modified_ts = ?, min = ?, max = ?, of_contract_id = ? where id = ? ";

	
	public ListEntity() {
		super(EntityType.List);
	}
	
	public ListEntity(ObjEntity entObj) throws EntityException {
		super(entObj);
		fetchByObjectId();
	}
	
	public ListEntity(List obixList) {
		super(obixList);
		setObjtype(EntityType.List);
	}
	
	public void create() throws EntityException {

		java.util.List<Object> params = getCreateParam();
		params.add(((List)getObixObject()).getMin());
		params.add(((List)getObixObject()).getMax());
		
		Contract ofContract = ((List)getObixObject()).getOf();
		if (ofContract != null) {
			ContractEntity contractEntity = new ContractEntity(ofContract);
			contractEntity.create();
			setOfContractId(contractEntity.getContractid());
		}
		params.add(getOfContractId());
		update(CREATE_LIST, params.toArray(), new ListResultSetGeneratedKeysHandler(this));
	}
	
	public void delete() throws EntityException {
		
		deleteReferences();
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(getId());
		update(DELETE_LIST, params.toArray(), null);
	}
	@Override
	protected void deleteReferences() throws EntityException{
		super.deleteReferences();
		
		if (getOfContractId() != null) {
			ContractEntity contractEntity = new ContractEntity(getOfContractId());
			contractEntity.delete();
		}
	}

	public boolean fetchByHref() throws EntityException {
		
		boolean found = super.fetchByHref();
		if( found){
			fetchDetails();
		}
		
		return found;
	}
	@Override
	protected void doQueryByHref(java.util.List<Object> params) throws EntityException{
		query(SEARCH_LIST_BY_HREF, params.toArray(), new ListResultSetHandler(this));
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
		query(SEARCH_OBJ_BY_ID, params.toArray(), new ListResultSetHandler(this));
	}
	
	public void fetchDetails() throws EntityException{
		if( getObj_uri() != null){
			Uri href = new Uri("", getObj_uri());
			getObixObject().setHref(href);
		}
		if (getOfContractId() != null) {
			ContractEntity contractEntity = new ContractEntity(getOfContractId());
			contractEntity.fetch();
			((List)getObixObject()).setOf(contractEntity.getObixContract());
		}
		setDetailsfetched(true);
	}
	
	public class ListResultSetHandler extends ObjResultSetHandler<ListEntity> {
		public ListResultSetHandler(ListEntity entity) {
			super(entity);
		}

		public void getRowAsBean(ResultSet rs) throws Exception {
			super.getRowAsBean(rs);
		}
	}

	public class ListResultSetGeneratedKeysHandler extends ObjResultSetGeneratedKeysHandler<ListEntity> {
		public ListResultSetGeneratedKeysHandler(ListEntity entity) {
			super(entity);
		}
	}
	
}
