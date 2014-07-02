package com.ptoceti.osgi.data.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Data
 * FILENAME : JdbcDeviceImpl.java
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


import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.log.LogService;

import biz.source_code.miniConnectionPoolManager.MiniConnectionPoolManager;

import com.ptoceti.osgi.data.IResultSetGeneratedKeysHandler;
import com.ptoceti.osgi.data.IResultSetMultipleHandler;
import com.ptoceti.osgi.data.IResultSetSingleHandler;
import com.ptoceti.osgi.data.JdbcDevice;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;


public class JdbcDeviceImpl implements JdbcDevice {

	public static final String DEVICE_SERIAL = "1.00";

	/**
	 * read only data source
	 */
	DataSource rdDataSource;
	
	/**
	 * read / write data source;
	 */
	DataSource rwDataSource;
	
	/**
	 * Pooled read only data source
	 */
	ConnectionPoolDataSource rdPooledDataSource;
	MiniConnectionPoolManager rPooledManager;
	
	/**
	 * Pooled read / write data source;
	 */
	ConnectionPoolDataSource rwPooledDataSource;
	MiniConnectionPoolManager rwPooledManager;
	
	/**
	 * the datasourcefactory service
	 */
	DataSourceFactory dataSourceFactory;
	
	private String dbUrl;
	private Properties props = new Properties();
	
	private ThreadLocal<Connection>  threadLocalConn = new ThreadLocal<Connection>();

	/**
	 * constructor
	 */
	public JdbcDeviceImpl(DataSourceFactory dataSourceFactory){
		this.dataSourceFactory = dataSourceFactory;
	}
	

	protected void configurePooledConnectionDataSource(){
	
		try {
			Properties rdprops = new Properties();
			rdprops.put(DataSourceFactory.JDBC_URL, dbUrl);
			//TODO need to find a way we are using SQLite
			// this maps to read only in SQLITE open mode flags
			rdprops.put("open_mode", (new Integer((int)0x00000001)).toString());
			rdPooledDataSource = dataSourceFactory.createConnectionPoolDataSource(rdprops);
			rPooledManager = new  MiniConnectionPoolManager(rdPooledDataSource, 5);
			
			Properties rwprops = new Properties();
			rwprops.put(DataSourceFactory.JDBC_URL, dbUrl);
			rwprops.put("open_mode", (new Integer((int)0x00000002)).toString());
			rwPooledDataSource = dataSourceFactory.createConnectionPoolDataSource(rwprops); 
			rwPooledManager = new  MiniConnectionPoolManager(rwPooledDataSource, 5);
			
		} catch (SQLException e) {
			Activator.log(LogService.LOG_ERROR, "Error creating datasource " + e.toString());
		}
		
	}
	
	protected void configureDataSource(){
		
		try {
			Properties rdprops = new Properties();
			rdprops.put(DataSourceFactory.JDBC_URL, dbUrl);
			//TODO need to find a way we are using SQLite
			// this maps to read only in SQLITE open mode flags
			rdprops.put("open_mode", (new Integer((int)0x00000001)).toString());
			rdDataSource = dataSourceFactory.createDataSource(rdprops);
			
			Properties rwprops = new Properties();
			rwprops.put(DataSourceFactory.JDBC_URL, dbUrl);
			rwprops.put("open_mode", (new Integer((int)0x00000002)).toString());
			rwDataSource = dataSourceFactory.createDataSource(rwprops); 
			
		} catch (SQLException e) {
			Activator.log(LogService.LOG_ERROR, "Error creating datasource " + e.toString());
		}
	}

	public boolean setupDatabase(String databasePath, String setupScript) {

		boolean success = false;
		boolean initializeDb = false;

		try {
			
			Activator.log(LogService.LOG_INFO, "Verifying database file at " + databasePath);
			
			Properties driverProps = new Properties();
			Driver jdbcDriver = dataSourceFactory.createDriver(props);
			
			// take in input a pathname, not an uri
			File file = new File(databasePath);
			dbUrl = file.toURI().toURL().toString();

			if (file.exists()) {

				try {
					if (jdbcDriver.acceptsURL(dbUrl)) {
						Connection con = jdbcDriver.connect(dbUrl, props);
						Activator.log(LogService.LOG_INFO,
								"Connection to database file " + dbUrl
										+ " sucessfull.");

						con.close();
						success = true;
						initializeDb = false;
						configurePooledConnectionDataSource();
					} else {
						file.delete();
						initializeDb = true;
					}
				} catch (SQLException e) {
					Activator.log(LogService.LOG_ERROR,
							"Connection to database file " + dbUrl
									+ " failed: " + e.toString());
					return success;
				}
			} else {
				initializeDb = true;
			}

			if (initializeDb) {
				try {
					Activator.log(LogService.LOG_INFO,
							"Creating database file " + dbUrl);
					file.createNewFile();

					jdbcDriver.acceptsURL(dbUrl.toString());

					Activator.log(LogService.LOG_INFO,
							"Creating database objects.");

					Connection conn = jdbcDriver.connect(dbUrl, props);
					if( conn != null){
						this.update(conn, setupScript, null);
						conn.commit();
						conn.close();
						
						configurePooledConnectionDataSource();
					}

				} catch (IOException e) {
					Activator.log(LogService.LOG_ERROR,
							"Could not create database file " + dbUrl + ". "
									+ e.toString());
				} catch (SQLException e) {
					Activator.log(LogService.LOG_ERROR, "Setup of database "
							+ dbUrl + " failed. " + e.toString());
				}
			}
		}

		catch (MalformedURLException e) {
			Activator.log(LogService.LOG_ERROR, e.toString());
		} catch (SQLException e) {
			Activator.log(LogService.LOG_ERROR, "Error creating JDBC driver: " + e.toString());
		}

		return success;
	}

	public Connection getCurrentConnection() {
		return threadLocalConn.get();
	}

	public synchronized Connection getConnectionRx() {
		
		Connection newConn = null;
		
		try {
			
			newConn = rPooledManager.getConnection();
			newConn.setAutoCommit(false);
			
			Connection currentConn = threadLocalConn.get();
			if( currentConn != null && !currentConn.isClosed()){
				currentConn.close();
			}
			
			threadLocalConn.set(newConn);
			
		} catch (SQLException e) {
			//taken = false;
			Activator.log(LogService.LOG_ERROR, "getConnectionRx: " +  e.toString());
		} 
		
	
		return newConn;
	}
	
	public synchronized Connection getConnectionRWx() {
		
		Connection newConn = null;
		
		try {
			newConn = rwPooledManager.getConnection();
			newConn.setAutoCommit(false);
			
			Connection currentConn = threadLocalConn.get();
			if( currentConn != null && !currentConn.isClosed()){
				currentConn.close();
			}
			
			threadLocalConn.set(newConn);
			
		} catch (SQLException e) {
			//taken = false;
			Activator.log(LogService.LOG_ERROR, "getConnectionRWx: " +  e.toString());
		}
		
	
		return newConn;
	}
	
	
	private synchronized void releaseConnection(Connection conn) throws SQLException{
		
		try {
			if(!conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			Activator.log(LogService.LOG_ERROR, "releaseConnection: " + e.toString());
			throw e;
		}
		
	}
	
	
	public void closeCurrentConnection() throws SQLException{
		Connection conn = threadLocalConn.get();
		if( conn == null){
			return;
		}
		try {
			if(!conn.isClosed()){
				releaseConnection(conn);
			}
		} catch (SQLException e) {
			Activator.log(LogService.LOG_ERROR, "closeCurrentConnection: " + e.toString());
			throw e;
		}
		threadLocalConn.set(null);
	}
	
	public void commitAndCloseCurrentConnection()  throws SQLException{
		Connection conn = threadLocalConn.get();
		if( conn == null ) {
			return;
		}
		try {
			conn.commit();
		} catch (SQLException e) {
			Activator.log(LogService.LOG_ERROR, "commitAndCloseCurrentConnection: " + e.toString());
			throw e;
		} finally {
			releaseConnection(conn);
			threadLocalConn.set(null);
		}
	}
	
	public void rollbackAndCloseCurrentConnection() throws SQLException{
		Connection conn = threadLocalConn.get();
		if( conn == null ) {
			return;
		}
		try {
			conn.rollback();
		} catch (SQLException e) {
			Activator.log(LogService.LOG_ERROR, "rollbackAndCloseCurrentConnection: " + e.toString());
			throw e;
		} finally {
			releaseConnection(conn);
			threadLocalConn.set(null);
		}
	}

	private void setStatementParams(PreparedStatement statement, Object[] params)
			throws SQLException {

		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				Object obj = params[i];
				if (obj == null)
					statement.setNull(i + 1, Types.VARCHAR);
				else if (obj instanceof Date) {
					Date date = (Date) obj;
					statement
							.setTimestamp(i + 1, new Timestamp(date.getTime()));

				} else if (obj instanceof Calendar) {

					Calendar calendar = (Calendar) obj;
					statement.setTimestamp(i + 1, new Timestamp(calendar
							.getTimeInMillis()));

				} else {
					statement.setObject(i + 1, params[i]);
				}
			}
		}

	}

	public void query(Connection conn, String query, IResultSetSingleHandler resultSetHandler) throws SQLException{

		query(conn, query, new Object[0], resultSetHandler);
	}

	public void queryMultiple(Connection conn, String query,
			IResultSetMultipleHandler resultSetHandler) throws SQLException{

		queryMultiple(conn, query, new Object[0], resultSetHandler);
	}

	public void query(Connection conn, String query, Object[] params,
			IResultSetSingleHandler resultSetHandler) throws SQLException{

		try {
			doQuery(conn, query, params, resultSetHandler);

		} catch (SQLException ex) {
			Activator.log(LogService.LOG_ERROR,"Error excuting statement: " + ex.toString());
			throw ex;
		} 

	}

	public void queryMultiple(Connection conn, String query, Object[] params,
			IResultSetMultipleHandler resultSetHandler) throws SQLException{

		try {
			doQueryMultiple(conn, query, params, resultSetHandler);

		} catch (SQLException ex) {
			Activator.log(LogService.LOG_ERROR,"Error excuting statement: " + ex.toString());
			throw ex;
		} 
	}

	private void doQuery(Connection connection, String query, Object[] params,
			IResultSetSingleHandler resultSetHandler) throws SQLException {

		PreparedStatement statement = connection.prepareStatement(query);

		try {
			setStatementParams(statement, params);

			ResultSet rs = statement.executeQuery();

			try {
				if( rs.next()) {
					resultSetHandler.getRowAsBean(rs);
				}
			} catch (Exception ex) {
				Activator.log(LogService.LOG_ERROR,"Error excuting statement: " + ex.toString());
			} finally {
				rs.close();
			}
		} catch (SQLException ex) {
			Activator.log(LogService.LOG_ERROR,"Error excuting statement: " + ex.toString());
			throw ex;
		} finally {
			statement.close();
		}


	}

	private void doQueryMultiple(Connection connection, String query,
			Object[] params, IResultSetMultipleHandler resultSetHandler)
			throws SQLException {

		PreparedStatement statement = connection.prepareStatement(query);


		try {
			setStatementParams(statement, params);

			ResultSet rs = statement.executeQuery();

			try {
				while( rs.next()) {
					resultSetHandler.getNextRowAsBean(rs);
				}

			} catch (Exception ex) {
				Activator.log(LogService.LOG_ERROR,"Error excuting statement: " + ex.toString());
			} finally {
				rs.close();
			}
		} catch (SQLException ex) {
			Activator.log(LogService.LOG_ERROR,"Error excuting statement: " + ex.toString());
			throw ex;
		} finally {
			statement.close();
		}

	}

	public int update(Connection conn, String query, IResultSetGeneratedKeysHandler handler) throws SQLException {

		int updatedRows = 0;

		updatedRows = update(conn, query, new Object[0], handler);
		return updatedRows;
	}

	public int update(Connection conn, String query, Object[] params, IResultSetGeneratedKeysHandler handler) throws SQLException {

		int updatedRows = 0;

		updatedRows = doUpdate(conn, query, params, handler);

		return updatedRows;
	}

	private int doUpdate(Connection connection, String query, Object[] params, IResultSetGeneratedKeysHandler handler)
			throws SQLException {

		int updatedRows = 0;

		if (params.length > 0) {
			PreparedStatement statement = connection.prepareStatement(query);

			try {
				setStatementParams(statement, params);
				updatedRows = statement.executeUpdate();
				
				try {
					if( handler != null) {
						handler.getRowsKey(statement.getGeneratedKeys());
					}
				} catch ( Exception ex)  {
					
				}
				
			} catch (SQLException ex) {

				Activator.log(LogService.LOG_ERROR,
						"Error excuting statement: " + ex.toString());

				throw ex;
			} finally {
				statement.close();
			}
		} else {
			Statement statement = connection.createStatement();
			try {
				updatedRows = statement.executeUpdate(query);
			} catch (SQLException ex) {

				Activator.log(LogService.LOG_ERROR,
						"Error excuting statement: " + ex.toString());
				
				throw ex;

			} finally {
				statement.close();
			}
		}

		return updatedRows;
	}

}
