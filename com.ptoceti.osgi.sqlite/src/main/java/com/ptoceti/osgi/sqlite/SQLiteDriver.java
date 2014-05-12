package com.ptoceti.osgi.sqlite;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : SQLite
 * FILENAME : SQLiteDriver.java
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


import org.osgi.service.device.Device;
import org.osgi.service.device.Driver;
import org.osgi.service.log.LogService;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Filter;

import com.ptoceti.osgi.data.JdbcDevice;

/**
 * A Osgi Driver object to publish an SQLite driver in OSGI space. The Driver will be matche to any device that want an
 * com.ptoceti.osgi.data.JdbcDevice ( implemented in com.ptoceti.osgi.data bundle ).
 * 
 * @author Laurent Thil
 * @version 1.0
 *
 */
public class SQLiteDriver implements Driver {
	
	/**
	 * The filter to match devices
	 */
	static final String spec = "(&"
		+ "(objectclass=" + JdbcDevice.NAME +")"
		+ "(" + org.osgi.service.device.Constants.DEVICE_CATEGORY + "=" + JdbcDevice.DEVICE_CATEGORY[0] + ")"
		+ ")";
	
	Filter filter;
	
	/**
	 * Creator.
	 */
	public SQLiteDriver()
	{
		try {
			filter = Activator.bc.createFilter(spec);
		} catch ( InvalidSyntaxException e ) {
			Activator.log(LogService.LOG_ERROR,"Error in filter string while registering SQLiteDriver." + e.toString());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String attach( final ServiceReference reference)
	{
		new SQLiteJDBC(Activator.bc, reference);
		// return null to indicate that device was properly attach to the driver
		return(null);
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int match(final ServiceReference reference)
	{
		if( filter.match(reference)){
			return JdbcDevice.MATCH_CLASS;
		}else 
			return Device.MATCH_NONE;
	}
	
	
}
