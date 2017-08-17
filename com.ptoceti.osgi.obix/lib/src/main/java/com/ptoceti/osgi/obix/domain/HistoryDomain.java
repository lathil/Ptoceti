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
	 * Create a hitory element
	 * 
	 * @param of
	 * @return
	 * @throws DomainException
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	History make(Contract of, String displayName) throws DomainException;
	
	/**
	 * Remove a hitory element
	 * 
	 * @param of
	 * @return
	 * @throws DomainException
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	void remove(String uri) throws DomainException;
	
	/**
	 * Return a history and its configuration froml the specified urlHistoryDomain
	 * 
	 * @param uri
	 * @return
	 * @throws DomainException
	 */
	@JdbcConnection(type = ConnectionType.RX)
	History retrieve(String uri) throws DomainException;
	
	/**
	 * Add a value record to the history specified by the uri
	 * 
	 * @param uri
	 * @param value
	 * @throws DomainException
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	void addRecord(String uri, Val value) throws DomainException;
	
	/**
	 * Get records from a history
	 * @param uri
	 * @param limit
	 * @param from
	 * @param to
	 * @return
	 * @throws DomainException
	 */
	@JdbcConnection(type = ConnectionType.RX)
	List<HistoryRecord> getRecords(String uri, Int limit, Abstime from, Abstime to) throws DomainException;
	
	/**
	 * Get rollup record from a history
	 * @param uri
	 * @param limit
	 * @param from
	 * @param to
	 * @param roolUpDuration
	 * @return
	 * @throws DomainException
	 */
	@JdbcConnection(type = ConnectionType.RX)
	List<HistoryRollupRecord> getRollUprecords(String uri, Int limit, Abstime from, Abstime to, Reltime roolUpDuration) throws DomainException;
	
}
