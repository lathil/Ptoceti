package com.ptoceti.osgi.obix.impl.entity;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : ContractEntity.java
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
import java.util.Iterator;
import java.util.List;

import com.ptoceti.osgi.data.ResultSetGeneratedKeysHandler;
import com.ptoceti.osgi.data.ResultSetMultipleHandler;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Uri;


public class ContractEntity extends AbstractEntity  {

	private static final String CREATE_CONTRACT = "insert into contract ( object_id) values (?)";
	private static final String CREATE_CONTRACTURI = "insert into contracturi (contract_id, uri_id) values (?,?) ";
	
	private static final String SEARCH_CONTRACT_BY_URI = "select contract.* from contract, contracturi, object where contract.id = contracturi.contract_id and contracturi.uri_id = object.id and object.value_text=?";
		
	//private static final String SEARCH_CONTRACT_BY_URI = "select distinct contracturi.* from contracturi, uri where  contracturi.uri_id = uri.id and uri.value=?";
	
	private static final String DELETE_CONTRACTURI = "delete from contracturi where contract_id=? and uri_id=?";
	private static final String DELETE_CONTRACT = "delete from contract where contract.id=?";
	
	private static final String COL_CONTRACT_ID = "id";
	private static final String COL_CONTRACT_OBJECT_ID = "object_id";
	
	private static final String COL_CONTRACTURI_ID = "contract_id";
	private static final String COL_CONTRACTURI_OBJECT_ID = "uri_id";
	
	private Integer contractid;
	
	private Contract obixContract;

	public ContractEntity(Integer id) {
		this.contractid = id;
	}
	
	public ContractEntity( Contract obixContract) {
		this.setObixContract(obixContract);
	}
	
	/**
	 * Create the contract and its uris
	 * @throws EntityException
	 */
	public void create() throws EntityException {
		
		// create the contract
		List<Object> params = new ArrayList<Object>();
		params.add(null); // need to insert at least a value, fake here
		update(CREATE_CONTRACT, params.toArray(), new ContractResultSetGeneratedKeysHandler(this));
		// then for each of its uris
		Uri[] uris = getObixContract().getUris();
		for( int i = 0; i < uris.length; i++) {
			// create each one
			UriEntity uriEntity = new UriEntity( uris[i]);
			uriEntity.create();
			// and create a link from it to the contract
			List<Object> params2 = new ArrayList<Object>();
			params2.add(contractid);
			params2.add(uriEntity.getId());
			update(CREATE_CONTRACTURI, params2.toArray(),null);
		}
		
	}
	
	/**
	 * Delete the contract and uris that compose it.
	 * @throws EntityException
	 */
	public void delete() throws EntityException {
		
		UriEntity uriEntity = new UriEntity( new Uri());
		
		// fetch all uri object in the table object
		List<UriEntity> uriList = uriEntity.searchUriByContractID(this.getContractid());
		for( int i = 0; i < uriList.size(); i++) {
			UriEntity nextUriEntity = uriList.get(i);
			// delete the uri
			nextUriEntity.delete();
			// and delete the link to the uri in the table contracturi
			List<Object> params = new ArrayList<Object>();
			params.add(this.getContractid());
			params.add(nextUriEntity.getId());
			update(DELETE_CONTRACTURI, params.toArray(), null);
		}
		
		// finish by removing the contract itself.
		List<Object> params = new ArrayList<Object>();
		params.add(this.getContractid());
		update(DELETE_CONTRACT, params.toArray(), null);
		
	}
	
	/**
	 * Load contract data from database
	 * @throws EntityException
	 */
	public void fetch() throws EntityException {
		
		UriEntity uriEntity = new UriEntity( new Uri());
		// load all uri belonging to this contract
		List<UriEntity> uriEntityist = uriEntity.searchUriByContractID(this.getContractid());
		
		if( getObixContract() == null) setObixContract(new Contract());
		List<Uri> uriList = new ArrayList<Uri>();
		Iterator<UriEntity> uriEntityIter = uriEntityist.iterator();
		while(uriEntityIter.hasNext()) {
			// for each get Uri from UriEntity
			UriEntity nextUriEntity = (UriEntity)uriEntityIter.next();
			uriList.add((Uri)nextUriEntity.getObixObject());
		}
		
		getObixContract().setUris((Uri[])uriList.toArray(new Uri[uriList.size()]));
		
	}
	
	
	public List<ContractEntity> searchContractByUri(String uripath) throws EntityException {
		
		List<ContractEntity> contractList = new ArrayList<ContractEntity>();
		List<Object> params = new ArrayList<Object>();
		params.add(uripath);
		queryMultiple(SEARCH_CONTRACT_BY_URI, params.toArray(), new ContractResultSetMultipleRowHandler(contractList));
		
		return contractList;
	}

	public void setContractid(Integer contractid) {
		this.contractid = contractid;
	}

	public Integer getContractid() {
		return contractid;
	}
	
	public void setObixContract(Contract obixContract) {
		this.obixContract = obixContract;
	}

	public Contract getObixContract() {
		return obixContract;
	}

	public class ContractResultSetMultipleRowHandler extends ResultSetMultipleHandler {
		
		private List<ContractEntity> entityList;
		private ContractEntity entity;
		
		public ContractResultSetMultipleRowHandler( List<ContractEntity> entityList){
			this.entityList = entityList;
		}
		
		public void getRowAsBean(ResultSet rs) throws Exception {
			
			Integer id = getInteger(rs,COL_CONTRACT_ID) ;
			if( id != null) entity.setContractid( id );
			
		}
		
		public void getNextRowAsBean(ResultSet rs) throws Exception {

			getNextBean();
			getRowAsBean(rs);
			entityList.add(entity);

		}
		
		public void getNextBean() {
			Contract contract = new Contract();
			entity = new  ContractEntity(contract);
		}
	}
	
	public class ContractResultSetGeneratedKeysHandler extends ResultSetGeneratedKeysHandler {
		
		private ContractEntity entity;
		
		public ContractResultSetGeneratedKeysHandler( ContractEntity entity){
			this.entity = entity;
		}
		
		public void getRowsKey(ResultSet rs) throws Exception {
			entity.setContractid( getRowID(rs));
		}
	}
}
