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

import com.ptoceti.osgi.data.ResultSetGeneratedKeysHandler;
import com.ptoceti.osgi.data.ResultSetSingleHandler;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Feed;
import com.ptoceti.osgi.obix.object.Int;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.impl.entity.IntEntity.IntResultSetGeneratedKeysHandler;
import com.ptoceti.osgi.obix.impl.entity.IntEntity.IntResultSetHandler;

public class FeedEntity extends ObjEntity implements ValEntity {

	private static final String CREATE_FEED = "insert into feed (object_id, in_contract_id, of_contract_id ) values (?,?,?)";
	private static final String DELETE_FEED = "delete from feed where feed.id=?";
	private static final String SEARCH_FEED_BY_OBJECT_ID = "select feed.* from feed where feed.object_id=?";

	private static final String COL_FEED_ID = "id";
	private static final String COL_FEED_IN_CONTRACT_ID = "in_contract_id";
	private static final String COL_FEED_OF_CONTRACT_ID = "of_contract_id";

	private Integer feedId;
	private Integer ofContractId;
	private Integer inContractId;

	public FeedEntity() {
		super(EntityType.Feed);
	}
	
	public FeedEntity(ObjEntity entObj) throws EntityException {
		super(entObj);
		fetchDetailsById();
	}

	public FeedEntity(Feed obixFeed) {
		super(obixFeed);
		setObjtype(EntityType.Feed);
	}

	public void create() throws EntityException {

		super.create();

		ArrayList params = new ArrayList();
		params.add(getId());

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

		update(CREATE_FEED, params.toArray(),
				new FeedResultSetGeneratedKeysHandler(this));
	}

	public void delete() throws EntityException {

		super.delete();

		if (getInContractId() != null) {
			ContractEntity contractEntity = new ContractEntity(
					getInContractId());
			contractEntity.delete();
		}

		if (getOfContractId() != null) {
			ContractEntity contractEntity = new ContractEntity(
					getOfContractId());
			contractEntity.delete();
		}

		ArrayList params = new ArrayList();
		params.add(getFeedId());
		update(DELETE_FEED, params.toArray(), null);
	}

	public boolean fetchByHref() throws EntityException {

		boolean found = super.fetchByHref();

		if (found) {
			ArrayList params = new ArrayList();
			params.add(getId());

			query(SEARCH_FEED_BY_OBJECT_ID, params.toArray(),
					new FeedResultSetHandler(this));
		}
		return found;
	}

	public boolean fetchByObjectId() throws EntityException {

		boolean found = super.fetchByObjectId();
		if( found ) fetchDetailsById();
		return found;
	}

	public void fetchDetailsById() throws EntityException {
		ArrayList params = new ArrayList();
		params.add(getId());

		query(SEARCH_FEED_BY_OBJECT_ID, params.toArray(),
				new FeedResultSetHandler(this));
	}

	public void setFeedId(Integer feedId) {
		this.feedId = feedId;
	}

	public Integer getFeedId() {
		return feedId;
	}

	public void setOfContractId(Integer ofContractId) {
		this.ofContractId = ofContractId;
	}

	public Integer getOfContractId() {
		return ofContractId;
	}

	public void setInContractId(Integer inContractId) {
		this.inContractId = inContractId;
	}

	public Integer getInContractId() {
		return inContractId;
	}

	public class FeedResultSetHandler extends ResultSetSingleHandler {

		private FeedEntity entity;

		public FeedResultSetHandler(FeedEntity entity) {
			this.entity = entity;
		}

		public void getRowAsBean(ResultSet rs) throws Exception {

			Integer id = getInteger(rs, COL_FEED_ID);
			if (id != null)
				entity.setFeedId(id);

			entity.setInContractId(getInteger(rs, COL_FEED_IN_CONTRACT_ID));
			entity.setOfContractId(getInteger(rs, COL_FEED_OF_CONTRACT_ID));
			
			entity.setDetailsfetched(true);
		}
	}

	public class FeedResultSetGeneratedKeysHandler extends
			ResultSetGeneratedKeysHandler {

		private FeedEntity entity;

		public FeedResultSetGeneratedKeysHandler(FeedEntity entity) {
			this.entity = entity;
		}

		public void getRowsKey(ResultSet rs) throws Exception {
			entity.setFeedId(getRowID(rs));
		}
	}
}
