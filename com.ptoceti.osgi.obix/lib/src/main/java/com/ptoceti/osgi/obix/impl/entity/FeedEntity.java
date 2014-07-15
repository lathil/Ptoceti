package com.ptoceti.osgi.obix.impl.entity;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : FeedEntity.java
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

import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Feed;
import com.ptoceti.osgi.obix.object.Uri;


public class FeedEntity extends ObjEntity implements ValEntity {

	private static final String SEARCH_FEED_BY_HREF = "select object.* from object where object.type_id=6 and object.uri_hash=?";
	private static final String CREATE_FEED = "insert into object (name, uri, uri_hash, contract_id, isnullable, displayname, display, writable, status_id, type_id, parent_id, created_ts, in_contract_id, of_contract_id ) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String DELETE_FEED = "delete from object where id=?";

	
	public FeedEntity() {
		super(EntityType.Feed);
	}
	
	public FeedEntity(ObjEntity entObj) throws EntityException {
		super(entObj);
		fetchByObjectId();
	}

	public FeedEntity(Feed obixFeed) {
		super(obixFeed);
		setObjtype(EntityType.Feed);
	}

	public void create() throws EntityException {

		List<Object> params = getCreateParam();
		
		Contract inContract = ((Feed) getObixObject()).getIn();
		if (inContract != null) {
			ContractEntity contractEntity = new ContractEntity(inContract);
			contractEntity.create();
			setInContractId(contractEntity.getContractid());
		}
		params.add(getInContractId());

		Contract ofContract = ((Feed) getObixObject()).getOf();
		if (ofContract != null) {
			ContractEntity contractEntity = new ContractEntity(ofContract);
			contractEntity.create();
			setOfContractId(contractEntity.getContractid());
		}
		params.add(getOfContractId());

		update(CREATE_FEED, params.toArray(), new FeedResultSetGeneratedKeysHandler(this));
	}

	public void delete() throws EntityException {
		deleteReferences();
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(getId());
		update(DELETE_FEED, params.toArray(), null);
	}
	
	@Override
	protected void deleteReferences() throws EntityException{
		super.deleteReferences();
		
		if (getInContractId() != null) {
			ContractEntity contractEntity = new ContractEntity(getInContractId());
			contractEntity.delete();
		}

		if (getOfContractId() != null) {
			ContractEntity contractEntity = new ContractEntity(getOfContractId());
			contractEntity.delete();
		}
	}

	public boolean fetchByHref() throws EntityException {
		boolean found = super.fetchByHref();
		if( found ){
			fetchDetails();
		}
		return found;
	}
	
	@Override
	protected void doQueryByHref(java.util.List<Object> params) throws EntityException{
		query(SEARCH_FEED_BY_HREF, params.toArray(), new FeedResultSetHandler(this));
	}

	public boolean fetchByObjectId() throws EntityException {
		boolean found =  super.fetchByObjectId();
		if( found ){
			fetchDetails();
		}
		return found;
	}

	@Override
	protected void doQueryByObjectId(java.util.List<Object> params) throws EntityException {
		query(SEARCH_OBJ_BY_ID, params.toArray(), new FeedResultSetHandler(this));
	}
	
	public void fetchDetails() throws EntityException {
		if( getObj_uri() != null){
			Uri href = new Uri("", getObj_uri());
			getObixObject().setHref(href);
		}
		if(getOfContractId() != null) {
			ContractEntity contractEntity = new ContractEntity(new Contract());
			contractEntity.setContractid(getOfContractId());
			contractEntity.fetch();
			((Feed)getObixObject()).setOf((Contract)contractEntity.getObixContract());
		}
		if(getInContractId() != null) {
			ContractEntity contractEntity = new ContractEntity(new Contract());
			contractEntity.setContractid(getInContractId());
			contractEntity.fetch();
			((Feed)getObixObject()).setIn((Contract)contractEntity.getObixContract());
		}
		setDetailsfetched(true);
	}

	public class FeedResultSetHandler extends ObjResultSetHandler<FeedEntity> {

		public FeedResultSetHandler(FeedEntity entity) {
			super(entity);
		}

		public void getRowAsBean(ResultSet rs) throws Exception {
			super.getRowAsBean(rs);
		}
	}

	public class FeedResultSetGeneratedKeysHandler extends ObjResultSetGeneratedKeysHandler<FeedEntity> {
		public FeedResultSetGeneratedKeysHandler(FeedEntity entity) {
			super(entity);
		}
	}
	
}
