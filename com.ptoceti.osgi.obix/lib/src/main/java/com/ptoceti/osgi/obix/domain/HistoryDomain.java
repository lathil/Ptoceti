package com.ptoceti.osgi.obix.domain;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : HistoryDomain.java
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


import java.util.List;

import com.ptoceti.osgi.obix.contract.History;
import com.ptoceti.osgi.obix.contract.HistoryRecord;
import com.ptoceti.osgi.obix.contract.HistoryRollupRecord;
import com.ptoceti.osgi.obix.impl.proxy.jdbc.JdbcConnection;
import com.ptoceti.osgi.obix.impl.proxy.jdbc.JdbcConnection.ConnectionType;
import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Int;
import com.ptoceti.osgi.obix.object.Reltime;
import com.ptoceti.osgi.obix.object.Val;

public interface HistoryDomain extends BaseDomain {

	/**
	 * Create a history element
	 * 
	 * @param of type of history to make
	 * @param displayName name of the history
	 * @return History created history
	 * @throws DomainException on accessing history
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	History make(Contract of, String displayName) throws DomainException;
	
	/**
	 * Remove a history element
	 * 
	 * @param uri uri of the history to remove
	 * @throws DomainException on accessing history
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	void remove(String uri) throws DomainException;
	
	/**
	 * Return a history and its configuration froml the specified urlHistoryDomain
	 * 
	 * @param uri uri of the history to retrieve
	 * @return History the history searched
	 * @throws DomainException on accessing history
	 */
	@JdbcConnection(type = ConnectionType.RX)
	History retrieve(String uri) throws DomainException;
	
	/**
	 * Add a value record to the history specified by the uri
	 * 
	 * @param uri uri of the history
	 * @param value valu to add to the history
	 * @throws DomainException on accessing history
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	void addRecord(String uri, Val value) throws DomainException;
	
	/**
	 * Get records from a history
	 * @param uri uri of the history
	 * @param limit number of recorde max to fetch
	 * @param start period start timestamp
	 * @param end period end time stamp
	 * @return List of of records
	 * @throws DomainException on accessing history
	 */
	List<HistoryRecord> getRecords(String uri, Int limit, Abstime start, Abstime end) throws DomainException;
	
	/**
	 * Get rollup record from a history
	 * @param uri uri of the history
	 * @param limit number of recorde max to fetch
	 * @param start period start timestamp
	 * @param end period end time stamp
	 * @param roolUpDuration duration of aggregation samples
	 * @return List list of records
	 * @throws DomainException on accessing history
	 */
	List<HistoryRollupRecord> getRollUprecords(String uri, Int limit, Abstime start, Abstime end, Reltime roolUpDuration) throws DomainException;
	
}
