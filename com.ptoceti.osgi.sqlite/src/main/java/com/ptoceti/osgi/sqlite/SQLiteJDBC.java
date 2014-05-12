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
import org.sqlite.SQLiteConfig.TransactionMode;
import org.sqlite.SQLiteOpenMode;

import com.ptoceti.osgi.data.JdbcDevice;


import org.osgi.framework.ServiceReference;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.*;

/**
 * Wrapper for a SQLite java.sql.Driver that extends Osgi's ServiceTracker. Attach te driver to an Osgi Device when this one become available.
 * 
 * 
 * @author Laurent Thil
 * @version 1.0
 *
 */
public class SQLiteJDBC extends ServiceTracker implements java.sql.Driver{

	/**
	 * The sqlite jdbc driver
	 */
	JDBC sqliteJDBC = null;
	
	/**
	 * prefix for accessing sqlite driver
	 */
	private static final String SQLITE_JDBC_PREFIX = "jdbc:sqlite:";
	/**
	 * prefix for accessing file based database
	 */
	private static final String FILE_PREFIX = "file:";
	
	/**
	 * Creator.
	 * 
	 * Implements a java.sql.Driver that attach itself to a com.ptoceti.osgi.data.JdbcDevice service.
	 * 
	 * @param bc the bundle context
	 * @param ref the JdbcDevice service reference
	 */
	public SQLiteJDBC(final BundleContext bc, final ServiceReference ref)
	{
		// we ask the service tracker to track the device service based on it service reference
		super( bc, ref, null);
		sqliteJDBC = new JDBC();
		// begin to track the service now
		open();
		
		Activator.log(LogService.LOG_INFO, "Sqlite major version: " + this.getMajorVersion() + ", minor version: " + this.getMinorVersion() +
				" , compliant: " + new Boolean(sqliteJDBC.jdbcCompliant()).toString());
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Object addingService(final ServiceReference ref) {
		
		JdbcDevice jDev = (JdbcDevice) Activator.bc.getService(ref);
		// we pass this class as the driver to the device.
		jDev.setJDBCDriver(this);
		return jDev;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void removedService(final ServiceReference ref, final Object service) {
		
		Activator.bc.ungetService(ref);
	}
	
	/**
	 * Create a connection to the SQLite db
	 * 
	 * @param url an url formating string to the sqlte db
	 * @param info aditional properties for the connection
	 * @return Connection the jdbc connection
	 */
	public Connection connect(final String url, final Properties info) throws SQLException
	{
		String sqliteUrl = adaptUrl(url);
			
		SQLiteConfig config = new SQLiteConfig(info);
		config.setJournalMode(JournalMode.WAL);
		
		// check if we are in read-write only mode
		
		if(( config.getOpenModeFlags() & SQLiteOpenMode.READWRITE.flag) > 0) {
			config.setTransactionMode(TransactionMode.IMMEDIATE);
		} else if(( config.getOpenModeFlags() & SQLiteOpenMode.READONLY.flag) > 0) {
			config.setTransactionMode(TransactionMode.DEFFERED);
		}
		

		Connection connection = sqliteJDBC.connect(sqliteUrl, config.toProperties());
	
		return new SQLiteConnectionWrapper(connection);
	}
	
	/**
	 * Strip the file prefix from the url and reformat as an sqlite db url.
	 * 
	 * @param url the incoming db url
	 * @return an sqlite db url
	 */
	private String adaptUrl(String url) {
		
		String strippedUrl = url;
		int lastIndex = strippedUrl.lastIndexOf(FILE_PREFIX)+ FILE_PREFIX.length();
		strippedUrl = strippedUrl.substring(lastIndex );
		
		strippedUrl = SQLITE_JDBC_PREFIX + strippedUrl;
		
		return strippedUrl;
	}
	
	/**
	 * {@inheritDoc}
	 */
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
	/**
	 * {@inheritDoc}
	 */
	public int getMajorVersion()
	{
		return sqliteJDBC.getMajorVersion();
	}
	/**
	 * {@inheritDoc}
	 */
	public int getMinorVersion()
	{
		return sqliteJDBC.getMinorVersion();
	}
	/**
	 * {@inheritDoc}
	 */
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException
	{
		return sqliteJDBC.getPropertyInfo(url, info);
	}
	/**
	 * {@inheritDoc}
	 */
	public boolean jdbcCompliant()
	{
		return sqliteJDBC.jdbcCompliant();
	}
	/**
	 * {@inheritDoc}
	 */
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}
}
