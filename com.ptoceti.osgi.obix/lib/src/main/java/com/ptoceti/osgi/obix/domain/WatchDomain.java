package com.ptoceti.osgi.obix.domain;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : WatchDomain.java
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


import java.util.List;

import com.ptoceti.osgi.obix.contract.Watch;
import com.ptoceti.osgi.obix.contract.WatchIn;
import com.ptoceti.osgi.obix.contract.WatchOut;
import com.ptoceti.osgi.obix.impl.proxy.jdbc.JdbcConnection;
import com.ptoceti.osgi.obix.impl.proxy.jdbc.JdbcConnection.ConnectionType;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Uri;

public interface WatchDomain extends BaseDomain {

	/**
	 * Create a new watch
	 * @return
	 * @throws DomainException
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	public Watch make() throws DomainException ;
	
	/**
	 * Return a watch from the specified uri
	 * @param uri
	 * @return
	 * @throws DomainException
	 */
	@JdbcConnection(type = ConnectionType.RX)
	public Watch retrieve(String uri ) throws DomainException ;
	
	/**
	 * Add an list of monitored uri to the watch.
	 * 
	 * @param uri the uri of the watch
	 * @param in the list og uri to monitor
	 * @return a list containing the newly added objects.
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	public List<Uri> addWatch(String uri, WatchIn in) throws DomainException ;
	
	/**
	 *  delete a watch
	 *  
	 *  @param uri the uri of the watch to be deleted
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	public void deleteWatch(String uri) throws DomainException ;
	
	/**
	 * return only the item of a watch that have been updated since last time
	 * @param uri
	 * @return
	 * @throws DomainException
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	public WatchOut poolChanges(String uri) throws DomainException ;
	
	/**
	 * return the full list of a watch
	 * @param uri
	 * @return
	 * @throws DomainException
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	public WatchOut poolRefresh(String uri) throws DomainException ;
	
	/**
	 * remove from the watch a list of monitored uri
	 * 
	 * @param uri the uri of the watch
	 * @param in the list of uri to remove
	 * @return List<Uri> a list of removed uris
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	public List<Uri> removeWatch(String uri, WatchIn in) throws DomainException ;
	
	/**
	 * get a list of all the watches availables
	 * @return
	 */
	@JdbcConnection(type = ConnectionType.RX)
	public List<Obj> getObixWatches() throws DomainException ;
	
	/**
	 * Update a watch witha list of new item to monitor
	 * 
	 * @param uri
	 * @param watchIn
	 * @throws DomainException
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	public void update(String uri, Watch watchIn) throws DomainException;
}
