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
	 * @return Watch the new watch
	 * @throws DomainException on accessing watch
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	public Watch make() throws DomainException ;
	
	/**
	 * Return a watch from the specified uri
	 * @param uri the uri of the watch
	 * @return Watch the retrived watch
	 * @throws DomainException on accessing watch
	 */
	@JdbcConnection(type = ConnectionType.RX)
	public Watch retrieve(String uri ) throws DomainException ;
	
	/**
	 * Add an list of monitored uri to the watch.
	 * 
	 * @param uri the uri of the watch
	 * @param in the list og uri to monitor
	 * @return List a list containing the newly added objects.
	 * @throws DomainException on accessing watch
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	public List<Uri> addWatch(String uri, WatchIn in) throws DomainException ;
	
	/**
	 *  delete a watch
	 *  
	 *  @param uri the uri of the watch to be deleted
	 *  @throws DomainException on accessing watch
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	public void deleteWatch(String uri) throws DomainException ;
	
	/**
	 * return only the item of a watch that have been updated since last time
	 * @param uri the uri of the watch
	 * @return WatchOut the objects that have changed
	 * @throws DomainException on accessing watch
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	public WatchOut poolChanges(String uri) throws DomainException ;
	
	/**
	 * return the full list of a watch
	 * @param uri the uri of the watch
	 * @return WatchOut the object monitored by the watch
	 * @throws DomainException on accessing watch
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	public WatchOut poolRefresh(String uri) throws DomainException ;
	
	/**
	 * remove from the watch a list of monitored uri
	 * 
	 * @param uri the uri of the watch
	 * @param in the list of uri to remove
	 * @return List a list of removed uris
	 * @throws DomainException on accessing watch
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	public List<Uri> removeWatch(String uri, WatchIn in) throws DomainException ;
	
	/**
	 * get a list of all the watches availables
	 * @return List a list of watches
	 * @throws DomainException on accessing watch
	 */
	@JdbcConnection(type = ConnectionType.RX)
	public List<Obj> getObixWatches() throws DomainException ;
	
	/**
	 * Update a watch witha list of new item to monitor
	 * 
	 * @param uri the uri of the watch
	 * @param watchIn the lis t of object to add to the watch
	 * @throws DomainException on accessing watch
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	public void update(String uri, Watch watchIn) throws DomainException;
}
