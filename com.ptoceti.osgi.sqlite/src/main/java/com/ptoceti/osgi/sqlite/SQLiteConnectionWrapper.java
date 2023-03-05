package com.ptoceti.osgi.sqlite;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : SQLite
 * FILENAME : SQLiteConnectionWrapper.java
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

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.osgi.service.log.LogService;
import org.sqlite.SQLiteErrorCode;

/**
 * Wrapper for an SQlite jdbc Connection. Allows for special handling of SQlite
 * "database locked" and "database busy" exceptions when more that one thread
 * are acting as reader/writer on the database.
 * 
 * @author Laurent Thil
 * @version 1.0
 * 
 */
public class SQLiteConnectionWrapper implements Connection {

	/**
	 * The wrapped connection
	 */
	private Connection wrappedConnection;

	/**
	 * Creator
	 * 
	 * @param wrappedConnection the connection to wrap on
	 */
	public SQLiteConnectionWrapper(Connection wrappedConnection) {
		this.wrappedConnection = wrappedConnection;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isWrapperFor(@SuppressWarnings("rawtypes") Class arg0)
			throws SQLException {
		if ("java.sql.Connection".equals(arg0.getName())
				|| "java.sql.Wrapper.class".equals(arg0.getName())) {
			return true;
		}
		return wrappedConnection.isWrapperFor(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		try {
			if ("java.sql.Connection".equals(arg0.getName())
					|| "java.sql.Wrapper.class".equals(arg0.getName())) {
				return arg0.cast(this);
			}

			return wrappedConnection.unwrap(arg0);
		} catch (ClassCastException cce) {
			throw new SQLException("Unable to unwrap to " + arg0.toString(),
					cce);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void abort(Executor arg0) throws SQLException {
		wrappedConnection.abort(arg0);

	}

	/**
	 * {@inheritDoc}
	 */
	public void clearWarnings() throws SQLException {
		wrappedConnection.clearWarnings();

	}

	/**
	 * {@inheritDoc}
	 */
	public void close() throws SQLException {
		wrappedConnection.close();

	}

	/**
	 * {@inheritDoc}
	 */
	public void commit() throws SQLException {
		for (;;) {
			try {
				wrappedConnection.commit();
				break;
			} catch (SQLException ex) {
				Activator.log(LogService.LOG_DEBUG, "commit:" + ex.getMessage() + ", " + ex.getCause());
				if (ex.getErrorCode() == SQLiteErrorCode.SQLITE_BUSY.code) {
					continue;
				} else if ( ex.getMessage().trim().equals("cannot commit - no transaction is active")){
					return;
				}

				throw ex;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Array createArrayOf(String arg0, Object[] arg1) throws SQLException {
		return wrappedConnection.createArrayOf(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public Blob createBlob() throws SQLException {
		return wrappedConnection.createBlob();
	}

	/**
	 * {@inheritDoc}
	 */
	public Clob createClob() throws SQLException {
		return wrappedConnection.createClob();
	}

	/**
	 * {@inheritDoc}
	 */
	public NClob createNClob() throws SQLException {
		return wrappedConnection.createNClob();
	}

	/**
	 * {@inheritDoc}
	 */
	public SQLXML createSQLXML() throws SQLException {
		return wrappedConnection.createSQLXML();
	}

	/**
	 * {@inheritDoc}
	 */
	public Statement createStatement() throws SQLException {
		return wrappedConnection.createStatement();
	}

	/**
	 * {@inheritDoc}
	 */
	public Statement createStatement(int arg0, int arg1) throws SQLException {
		return wrappedConnection.createStatement(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public Statement createStatement(int arg0, int arg1, int arg2)
			throws SQLException {
		return wrappedConnection.createStatement(arg0, arg1, arg2);
	}

	/**
	 * {@inheritDoc}
	 */
	public Struct createStruct(String arg0, Object[] arg1) throws SQLException {
		return wrappedConnection.createStruct(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean getAutoCommit() throws SQLException {
		return wrappedConnection.getAutoCommit();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getCatalog() throws SQLException {
		return wrappedConnection.getCatalog();
	}

	/**
	 * {@inheritDoc}
	 */
	public Properties getClientInfo() throws SQLException {
		return wrappedConnection.getClientInfo();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getClientInfo(String arg0) throws SQLException {
		return wrappedConnection.getClientInfo(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	public int getHoldability() throws SQLException {
		return wrappedConnection.getHoldability();
	}

	/**
	 * {@inheritDoc}
	 */
	public DatabaseMetaData getMetaData() throws SQLException {
		return wrappedConnection.getMetaData();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getNetworkTimeout() throws SQLException {
		return wrappedConnection.getNetworkTimeout();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getSchema() throws SQLException {
		return wrappedConnection.getSchema();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getTransactionIsolation() throws SQLException {
		return wrappedConnection.getTransactionIsolation();
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return wrappedConnection.getTypeMap();
	}

	/**
	 * {@inheritDoc}
	 */
	public SQLWarning getWarnings() throws SQLException {
		return wrappedConnection.getWarnings();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isClosed() throws SQLException {
		return wrappedConnection.isClosed();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isReadOnly() throws SQLException {
		return wrappedConnection.isReadOnly();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isValid(int arg0) throws SQLException {
		return wrappedConnection.isValid(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	public String nativeSQL(String arg0) throws SQLException {
		return wrappedConnection.nativeSQL(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	public CallableStatement prepareCall(String arg0) throws SQLException {
		return wrappedConnection.prepareCall(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	public CallableStatement prepareCall(String arg0, int arg1, int arg2)
			throws SQLException {
		return wrappedConnection.prepareCall(arg0, arg1, arg2);
	}

	/**
	 * {@inheritDoc}
	 */
	public CallableStatement prepareCall(String arg0, int arg1, int arg2,
			int arg3) throws SQLException {
		return wrappedConnection.prepareCall(arg0, arg1, arg2, arg3);
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement prepareStatement(String arg0) throws SQLException {
		return new SQlitePrepStmtWrapper(
				wrappedConnection.prepareStatement(arg0));
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement prepareStatement(String arg0, int arg1)
			throws SQLException {
		return new SQlitePrepStmtWrapper(wrappedConnection.prepareStatement(
				arg0, arg1));
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement prepareStatement(String arg0, int[] arg1)
			throws SQLException {
		return new SQlitePrepStmtWrapper(wrappedConnection.prepareStatement(
				arg0, arg1));
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement prepareStatement(String arg0, String[] arg1)
			throws SQLException {
		return new SQlitePrepStmtWrapper(wrappedConnection.prepareStatement(
				arg0, arg1));
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2)
			throws SQLException {
		return new SQlitePrepStmtWrapper(wrappedConnection.prepareStatement(
				arg0, arg1, arg2));
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2,
			int arg3) throws SQLException {
		PreparedStatement prepStmt = wrappedConnection.prepareStatement(arg0,
				arg1, arg2, arg3);
		return new SQlitePrepStmtWrapper(prepStmt);
	}

	/**
	 * {@inheritDoc}
	 */
	public void releaseSavepoint(Savepoint arg0) throws SQLException {
		wrappedConnection.releaseSavepoint(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	public void rollback() throws SQLException {
		try {
			wrappedConnection.rollback();
		} catch (SQLException ex ) {
			Activator.log(LogService.LOG_DEBUG, "rollback;:" + ex.getMessage() + ", " + ex.getCause());
			if ( ex.getMessage().trim().equals("cannot rollback - no transaction is active")){
				return;
			}
			
			throw ex;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void rollback(Savepoint arg0) throws SQLException {
		try {
			wrappedConnection.rollback(arg0);
		} catch (SQLException ex ) {
			Activator.log(LogService.LOG_DEBUG, "rollback;:" + ex.getMessage() + ", " + ex.getCause());
			if ( ex.getMessage() == "cannot rollback - no transaction is active"){
				return;
			}
			
			throw ex;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setAutoCommit(boolean arg0) throws SQLException {
		for (;;) {
			try {
				wrappedConnection.setAutoCommit(arg0);
				break;
			} catch (SQLException ex) {
				Activator.log(LogService.LOG_DEBUG,"setAutoCommit:" + ex.getMessage() + ", "+ ex.getCause());
				if ((ex.getErrorCode() == SQLiteErrorCode.SQLITE_LOCKED.code)
						|| (ex.getMessage().equals("database is locked"))) {
					continue;
				}

				throw ex;
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public void setCatalog(String arg0) throws SQLException {
		wrappedConnection.setCatalog(arg0);

	}

	/**
	 * {@inheritDoc}
	 */
	public void setClientInfo(Properties arg0) throws SQLClientInfoException {
		wrappedConnection.setClientInfo(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setClientInfo(String arg0, String arg1)
			throws SQLClientInfoException {
		wrappedConnection.setClientInfo(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setHoldability(int arg0) throws SQLException {
		wrappedConnection.setHoldability(arg0);

	}

	/**
	 * {@inheritDoc}
	 */
	public void setNetworkTimeout(Executor arg0, int arg1) throws SQLException {
		wrappedConnection.setNetworkTimeout(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setReadOnly(boolean arg0) throws SQLException {
		wrappedConnection.setReadOnly(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	public Savepoint setSavepoint() throws SQLException {
		return wrappedConnection.setSavepoint();
	}

	/**
	 * {@inheritDoc}
	 */
	public Savepoint setSavepoint(String arg0) throws SQLException {
		return wrappedConnection.setSavepoint(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setSchema(String arg0) throws SQLException {
		wrappedConnection.setSchema(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTransactionIsolation(int arg0) throws SQLException {
		wrappedConnection.setTransactionIsolation(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTypeMap(Map<String, Class<?>> arg0) throws SQLException {
		wrappedConnection.setTypeMap(arg0);
	}

}
