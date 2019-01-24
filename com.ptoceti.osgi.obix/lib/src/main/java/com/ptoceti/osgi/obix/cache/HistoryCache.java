package com.ptoceti.osgi.obix.cache;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : HistoryCache.java
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
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Int;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Ref;
import com.ptoceti.osgi.obix.object.Reltime;
import com.ptoceti.osgi.obix.object.Val;

public interface HistoryCache {

	/**
	 * Create a history element for a referenced object
	 * 
	 * @param ref to the object the history is for
	 * @return History the created istory
	 * @throws DomainException on accessing the history
	 */
	History make(Ref ref) throws DomainException;
	
	/**
	 * Create a history element of a type
	 * 
	 * @param of type of history
     * @param displayName name of the history
	 * @return History  the created history
	 * @throws DomainException on accessing the history
	 */
	History make(Contract of, String displayName) throws DomainException;
	
	/**
	 * Add an history observer to the observable object
	 * 
	 * @param uri uri of the history object
	 * @param observable the object to be observed by the history object
	 * @throws DomainException on accessing the history
	 */
	void addHistoryObserver(String uri, Obj observable) throws DomainException;
	
	/**
	 * Return a history and its configuration for the specified url
	 * 
	 * @param  uri uri of the history object
	 * @return History the retrieved history
	 * @throws DomainException on accessing the history
	 */
	History retrieve(String uri) throws DomainException;
	
	/**
	 * Delete an history object and references to it
	 * 
	 * @param uri uri of the history object
	 * @return boolean true if sucessfully deleted
	 * @throws DomainException on accessing the history
	 */
	boolean delete(String uri) throws DomainException;
	
	/**
	 * Add a value record to the history specified by the uri
	 * 
	 * @param uri uri to the history object
	 * @param value value to add to the history
	 * @throws DomainException on accessing the history
	 */
	void addRecord(String uri, Val value) throws DomainException;
	
	/**
	 * Get records from a history
	 * @param uri uri of the history object
	 * @param limit number  max of record to fetch
	 * @param start starting period of records
	 * @param end finishing period of records
	 * @return List of HistoryRecords
	 * @throws DomainException on accessing the history
	 */
	List<HistoryRecord> getRecords(String uri, Int limit, Abstime start, Abstime end) throws DomainException;
	
	/**
	 * Get rollup record from a history
	 * @param uri uri of the history object
	 * @param limit number  max of record to fetch
	 * @param start starting period of records
	 * @param end finishing period of records
	 * @param roolUpDuration sampling period
	 * @return List of HistoryRecords
	 * @throws DomainException on accessing the history
	 */
	List<HistoryRollupRecord> getRollUprecords(String uri, Int limit, Abstime start, Abstime end, Reltime roolUpDuration) throws DomainException;
	
}
