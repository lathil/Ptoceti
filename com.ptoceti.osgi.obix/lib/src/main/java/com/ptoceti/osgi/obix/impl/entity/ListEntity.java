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
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Feed;
import com.ptoceti.osgi.obix.object.Int;
import com.ptoceti.osgi.obix.object.List;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.impl.entity.FeedEntity.FeedResultSetHandler;
import com.ptoceti.osgi.obix.impl.entity.IntEntity.IntResultSetGeneratedKeysHandler;

public class ListEntity extends ObjEntity implements ValEntity {

	private static final String CREATE_LIST = "insert into list (object_id, min, max, of_contract_id ) values (?,?,?,?)";
	private static final String DELETE_LIST = "delete from list where list.id=?";
	private static final String SEARCH_LIST_BY_OBJECT_ID = "select list.* from list where list.object_id=?";

	private static final String UPDATE_LIST = "update list set object_id = ?, min = ?, max = ?, of_contract_id = ? where id = ? ";

	private static final String COL_LIST_ID = "id";
	private static final String COL_LIST_OBJECT_ID = "object_id";
	private static final String COL_LIST_MIN = "min";
	private static final String COL_LIST_MAX = "max";
	private static final String COL_LIST_OF_CONTRACT_ID = "of_contract_id";

	private Integer listId;
	private Integer ofContractId;
	
	public ListEntity() {
		super(EntityType.List);
	}
	
	public ListEntity(ObjEntity entObj) throws EntityException {
		super(entObj);
		fetchDetailsById();
	}
	
	public ListEntity(List obixList) {
		super(obixList);
		setObjtype(EntityType.List);
	}
	
	public void create() throws EntityException {

		super.create();
		
		ArrayList params = new ArrayList();
		params.add(getId());
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
		
		super.delete();
		
		if (getOfContractId() != null) {
			ContractEntity contractEntity = new ContractEntity(getOfContractId());
			contractEntity.delete();
		}
		
		ArrayList params = new ArrayList();
		params.add(getListId());
		update(DELETE_LIST, params.toArray(), null);
	}

	public boolean fetchByHref() throws EntityException {
		
		boolean found = super.fetchByHref();
		if( found ) {
		ArrayList params = new ArrayList();
		params.add(getId());
		
		query(SEARCH_LIST_BY_OBJECT_ID, params.toArray(), new ListResultSetHandler(this) );
		}
		
		if (ofContractId != null) {
			ContractEntity contractEntity = new ContractEntity(ofContractId);
			contractEntity.fetch();
			((List)getObixObject()).setOf(contractEntity.getObixContract());
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
		
		query(SEARCH_LIST_BY_OBJECT_ID, params.toArray(), new ListResultSetHandler(this) );
		
		if (ofContractId != null) {
			ContractEntity contractEntity = new ContractEntity(ofContractId);
			contractEntity.fetch();
			((List)getObixObject()).setOf(contractEntity.getObixContract());
		}
		
		
	}
	
	
	public void setListId(Integer listId) {
		this.listId = listId;
	}

	public Integer getListId() {
		return listId;
	}

	public void setOfContractId(Integer ofContractId) {
		this.ofContractId = ofContractId;
	}

	public Integer getOfContractId() {
		return ofContractId;
	}
	
	public class ListResultSetHandler extends ResultSetSingleHandler {

		private ListEntity entity;

		public ListResultSetHandler(ListEntity entity) {
			this.entity = entity;
		}

		public void getRowAsBean(ResultSet rs) throws Exception {

			Integer id = getInteger(rs,COL_LIST_ID) ;
			if( id != null) entity.setListId( id );
			
			((List)entity.getObixObject()).setMax( getInteger(rs, COL_LIST_MAX));
			((List)entity.getObixObject()).setMin( getInteger(rs, COL_LIST_MIN));

			entity.setOfContractId( getInteger(rs, COL_LIST_OF_CONTRACT_ID));
			
			entity.setDetailsfetched(true);
		}
	}

	public class ListResultSetGeneratedKeysHandler extends
			ResultSetGeneratedKeysHandler {

		private ListEntity entity;

		public ListResultSetGeneratedKeysHandler(ListEntity entity) {
			this.entity = entity;
		}

		public void getRowsKey(ResultSet rs) throws Exception {
			entity.setListId(getRowID(rs));
		}
	}
}
