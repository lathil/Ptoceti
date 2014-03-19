package com.ptoceti.osgi.sqlite;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : SQLite
 * FILENAME : SQLiteJDBC.java
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


import java.sql.Connection;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import org.sqlite.JDBC;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteConfig.JournalMode;

import com.ptoceti.osgi.data.JdbcDevice;


import org.osgi.framework.ServiceReference;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.*;

public class SQLiteJDBC extends ServiceTracker implements java.sql.Driver{

	JDBC sqliteJDBC = null;
	
	private static String SQLITE_JDBC_PREFIX = "jdbc:sqlite:";
	private static String FILE_PREFIX = "file:";
	
	public SQLiteJDBC(BundleContext bc, ServiceReference ref)
	{
		// we ask the service tracker to track the device service based on it service reference
		super( bc, ref, null);
		sqliteJDBC = new JDBC();
		// begin to track the service now
		open();
		
		Activator.log(LogService.LOG_INFO, "Sqlite major version: " + this.getMajorVersion() + ", minor version: " + this.getMinorVersion() +
				" , compliant: " + new Boolean(sqliteJDBC.jdbcCompliant()).toString());
		
	}
	
	public Object addingService(ServiceReference ref) {
		
		JdbcDevice jDev = (JdbcDevice) Activator.bc.getService(ref);
		// we pass this class as the driver to the device.
		jDev.setJDBCDriver(this);
		return jDev;
	}
	
	public void removedService(ServiceReference ref, Object service) {
		
		Activator.bc.ungetService(ref);
	}
	
	private String adaptUrl(String url) {
		
		String strippedUrl = url;
		int lastIndex = strippedUrl.lastIndexOf(FILE_PREFIX)+ FILE_PREFIX.length();
		strippedUrl = strippedUrl.substring(lastIndex );
		
		strippedUrl = SQLITE_JDBC_PREFIX + strippedUrl;
		
		return strippedUrl;
	}
	
	public boolean acceptsURL(String url)
	{
		String sqliteUrl = url;
		if( ! sqliteUrl.startsWith(FILE_PREFIX)){
			return false;
		} else {
			sqliteUrl = adaptUrl(sqliteUrl);
		}
		
		return sqliteJDBC.acceptsURL(sqliteUrl);
	}
	public Connection connect(String url, Properties info) throws SQLException
	{
		String sqliteUrl = adaptUrl(url);
			
		SQLiteConfig config = new SQLiteConfig();
		config.setJournalMode(JournalMode.WAL);
		Connection connection = sqliteJDBC.connect(sqliteUrl, config.toProperties());
	
		return connection;
	}
	public int getMajorVersion()
	{
		return sqliteJDBC.getMajorVersion();
	}
	public int getMinorVersion()
	{
		return sqliteJDBC.getMinorVersion();
	}
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException
	{
		return sqliteJDBC.getPropertyInfo(url, info);
	}
	public boolean jdbcCompliant()
	{
		return sqliteJDBC.jdbcCompliant();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}
}
