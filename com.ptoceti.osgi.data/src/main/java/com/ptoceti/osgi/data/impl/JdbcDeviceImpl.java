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


import org.osgi.framework.Constants;
import org.osgi.service.device.Device;
import org.osgi.service.log.LogService;

import com.ptoceti.osgi.data.IResultSetGeneratedKeysHandler;
import com.ptoceti.osgi.data.IResultSetMultipleHandler;
import com.ptoceti.osgi.data.JdbcDevice;
import com.ptoceti.osgi.data.IResultSetSingleHandler;
import com.ptoceti.osgi.data.JdbcDeviceService;
import com.ptoceti.osgi.data.ResultSetMultipleHandler;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
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
import java.util.Hashtable;
import java.util.Properties;

public class JdbcDeviceImpl extends JdbcDeviceService {

	public static final String DEVICE_SERIAL = "1.00";

	private Driver jdbcDriver;
	private String dbUrl;
	private Properties props = new Properties();
	
	private ThreadLocal<Connection>  threadLocalConn = new ThreadLocal<Connection>();

	public void noDriverFound() {
		Activator.log(LogService.LOG_INFO, "No driver found.");
	}

	public void setJDBCDriver(Driver driver) {
		jdbcDriver = driver;
		Activator.log(LogService.LOG_INFO, "Jdbc driver attached: "
				+ jdbcDriver.getClass().getName());
		
		// register the class as a managed service.
		Hashtable properties = new Hashtable();
		properties.put( Constants.SERVICE_PID, JdbcDeviceService.class.getName());
		
		String[] clazzes = new String[1];
		clazzes[0] = JdbcDeviceService.class.getName();
				
		Activator.bc.registerService(clazzes, this, properties );		
	}

	public boolean setupDatabase(String databasePath, String setupScript) {

		boolean success = false;
		boolean initializeDb = false;

		try {
			
			Activator.log(LogService.LOG_INFO, "Verifying database file at " + databasePath);
			
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
		} catch (IOException e) {
			Activator.log(LogService.LOG_ERROR, e.toString());
		}

		return success;
	}

	public Connection getCurrentConnection() {
		Connection conn = threadLocalConn.get();
		
		if( conn != null ){
			return conn;
		}
		
		conn = getConnection();
		threadLocalConn.set(conn);
		
		return conn;
	}

	private boolean  taken = false;
	private synchronized Connection getConnection() {
		
		while(taken){
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		
		taken = true;
		Connection conn = null;
		try {
			conn = jdbcDriver.connect(dbUrl, props);
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			taken = false;
			Activator.log(LogService.LOG_ERROR, e.toString());
		}
		
		return conn;
	}
	
	
	private synchronized void releaseConnection(Connection conn) throws SQLException{
		
		while(!taken){
			try{
				wait();
			} catch (InterruptedException e) {}
		}
		
		
		try {
			conn.close();
		} catch (SQLException e) {
			Activator.log(LogService.LOG_ERROR, e.toString());
			throw e;
		}
		
		taken = false;
		notifyAll();
		
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
			Activator.log(LogService.LOG_ERROR, e.toString());
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
			if( !conn.isClosed()){
				conn.commit();
				releaseConnection(conn);
			}
		} catch (SQLException e) {
			Activator.log(LogService.LOG_ERROR, e.toString());
			throw e;
		}
		threadLocalConn.set(null);
	}
	
	public void rollbackAndCloseCurrentConnection() throws SQLException{
		Connection conn = threadLocalConn.get();
		if( conn == null ) {
			return;
		}
		try {
			if( !conn.isClosed()){
				conn.rollback();
				releaseConnection(conn);
			}
		} catch (SQLException e) {
			Activator.log(LogService.LOG_ERROR, e.toString());
			throw e;
		}
		threadLocalConn.set(null);
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
