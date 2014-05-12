package com.ptoceti.osgi.sqlite;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : SQLite
 * FILENAME : SQlitePrepStmtWrapper.java
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


import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import org.osgi.service.log.LogService;
import org.sqlite.SQLiteErrorCode;


/**
 * Wrapper for an SQlite jdbc PreparedStatement. Allows for special handling of SQlite
 * "database locked" and "database busy" exceptions when more that one thread
 * are acting as reader/writer on the database.
 * 
 * @author Laurent Thil
 * @version 1.0
 * 
 */
public class SQlitePrepStmtWrapper implements PreparedStatement  {

	/**
	 * The wrapped statement
	 */
	private PreparedStatement wrapped;
	
	/**
	 * Creator
	 * 
	 * @param wrapped the prepared statement to wrap on
	 */
	SQlitePrepStmtWrapper(PreparedStatement wrapped) {
		this.wrapped = wrapped;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void addBatch(String arg0) throws SQLException {
		wrapped.addBatch(arg0);
		
	}

	/**
	 * {@inheritDoc}
	 */
	public void cancel() throws SQLException {
		wrapped.cancel();
	}

	/**
	 * {@inheritDoc}
	 */
	public void clearBatch() throws SQLException {
		wrapped.clearBatch();
		
	}

	/**
	 * {@inheritDoc}
	 */
	public void clearWarnings() throws SQLException {
		wrapped.clearWarnings();
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() throws SQLException {
		wrapped.close();
	}

	/**
	 * {@inheritDoc}
	 */
	public void closeOnCompletion() throws SQLException {
		wrapped.closeOnCompletion();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean execute(String arg0) throws SQLException {
		for(;;) {
			try{
				return wrapped.execute(arg0);
			} catch (SQLException ex){
				Activator.log(LogService.LOG_DEBUG, "execute:" + ex.getMessage() + ", " + ex.getCause());
				if(isSqlBusyException(ex)){
					continue;
				}
				
				throw ex;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean execute(String arg0, int arg1) throws SQLException {
		for(;;) {
			try{
				return wrapped.execute(arg0, arg1);
			} catch (SQLException ex){
				Activator.log(LogService.LOG_DEBUG, "execute:" + ex.getMessage() + ", " + ex.getCause());
				if(isSqlBusyException(ex)){
					continue;
				}
				
				throw ex;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean execute(String arg0, int[] arg1) throws SQLException {
		for(;;) {
			try{
				return wrapped.execute(arg0, arg1);
			} catch (SQLException ex){
				Activator.log(LogService.LOG_DEBUG, "execute:" + ex.getMessage() + ", " + ex.getCause());
				if( isSqlBusyException(ex)){
					continue;
				}
				
				throw ex;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean execute(String arg0, String[] arg1) throws SQLException {
		for(;;) {
			try{
				return wrapped.execute(arg0, arg1);
			} catch (SQLException ex){
				Activator.log(LogService.LOG_DEBUG, "execute:" + ex.getMessage() + ", " + ex.getCause());
				if( isSqlBusyException(ex)){
					continue;
				}
				
				throw ex;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public int[] executeBatch() throws SQLException {
		for(;;) {
			try{
				return wrapped.executeBatch();
			} catch (SQLException ex){
				Activator.log(LogService.LOG_DEBUG, "executeBatch:" + ex.getMessage() + ", " + ex.getCause());
				if( isSqlBusyException(ex)){
					continue;
				}
				
				throw ex;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public ResultSet executeQuery() throws SQLException {
		for(;;) {
			try{
				return wrapped.executeQuery();
			} catch (SQLException ex){
				Activator.log(LogService.LOG_DEBUG, "executeQuery:" + ex.getMessage() + ", " + ex.getCause());
				if( isSqlBusyException(ex)){
					continue;
				}
				
				throw ex;
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public ResultSet executeQuery(String arg0) throws SQLException {
		for(;;) {
			try{
				return wrapped.executeQuery(arg0);
			} catch (SQLException ex){
				Activator.log(LogService.LOG_DEBUG, "executeQuery:" + ex.getMessage() + ", " + ex.getCause());
				if( isSqlBusyException(ex)){
					continue;
				}
				
				throw ex;
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int executeUpdate() throws SQLException {
		for(;;) {
			try{
				return wrapped.executeUpdate();
			} catch (SQLException ex){
				Activator.log(LogService.LOG_DEBUG, "executeUpdate:" + ex.getMessage() + ", " + ex.getCause());
				if( isSqlBusyException(ex)){
					continue;
				}
				
				throw ex;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public int executeUpdate(String arg0) throws SQLException {
		
		for(;;) {
			try{
				return wrapped.executeUpdate(arg0);
			} catch (SQLException ex){
				Activator.log(LogService.LOG_DEBUG, "executeUpdate:" + ex.getMessage() + ", " + ex.getCause());
				if(isSqlBusyException(ex)){
					continue;
				}
				
				throw ex;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public int executeUpdate(String arg0, int arg1) throws SQLException {
		for(;;) {
			try{
				return wrapped.executeUpdate(arg0, arg1);
			} catch (SQLException ex){
				Activator.log(LogService.LOG_DEBUG, "executeUpdate:" + ex.getMessage() + ", " + ex.getCause());
				if( isSqlBusyException(ex)){
					continue;
				}
				
				throw ex;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public int executeUpdate(String arg0, int[] arg1) throws SQLException {
		for(;;) {
			try{
				return wrapped.executeUpdate(arg0, arg1);
			} catch (SQLException ex){
				Activator.log(LogService.LOG_DEBUG, "executeUpdate:" + ex.getMessage() + ", " + ex.getCause());
				if(isSqlBusyException(ex)){
					continue;
				}
				
				throw ex;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public int executeUpdate(String arg0, String[] arg1) throws SQLException {
		for(;;) {
			try{
				return wrapped.executeUpdate(arg0, arg1);
			} catch (SQLException ex){
				Activator.log(LogService.LOG_DEBUG, "executeUpdate:" + ex.getMessage() + ", " + ex.getCause());
				if( isSqlBusyException(ex)){
					continue;
				}
				
				throw ex;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Connection getConnection() throws SQLException {
		return wrapped.getConnection();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getFetchDirection() throws SQLException {
		return wrapped.getFetchDirection();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getFetchSize() throws SQLException {
		return wrapped.getFetchSize();
	}

	/**
	 * {@inheritDoc}
	 */
	public ResultSet getGeneratedKeys() throws SQLException {
		return wrapped.getGeneratedKeys();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getMaxFieldSize() throws SQLException {
		return wrapped.getMaxFieldSize();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getMaxRows() throws SQLException {
		return wrapped.getMaxRows();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean getMoreResults() throws SQLException {
		return wrapped.getMoreResults();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean getMoreResults(int arg0) throws SQLException {
		return wrapped.getMoreResults(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	public int getQueryTimeout() throws SQLException {
		return wrapped.getQueryTimeout();
	}

	/**
	 * {@inheritDoc}
	 */
	public ResultSet getResultSet() throws SQLException {
		return wrapped.getResultSet();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getResultSetConcurrency() throws SQLException {
		return wrapped.getResultSetConcurrency();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getResultSetHoldability() throws SQLException {
		return wrapped.getResultSetHoldability();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getResultSetType() throws SQLException {
		return wrapped.getResultSetType();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getUpdateCount() throws SQLException {
		return wrapped.getUpdateCount();
	}

	/**
	 * {@inheritDoc}
	 */
	public SQLWarning getWarnings() throws SQLException {
		return wrapped.getWarnings();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isCloseOnCompletion() throws SQLException {
		return wrapped.isCloseOnCompletion();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isClosed() throws SQLException {
		return wrapped.isClosed();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isPoolable() throws SQLException {
		return wrapped.isPoolable();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setCursorName(String arg0) throws SQLException {
		wrapped.setCursorName(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setEscapeProcessing(boolean arg0) throws SQLException {
		wrapped.setEscapeProcessing(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setFetchDirection(int arg0) throws SQLException {
		wrapped.setFetchDirection(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setFetchSize(int arg0) throws SQLException {
		wrapped.setFetchSize(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setMaxFieldSize(int arg0) throws SQLException {
		wrapped.setMaxFieldSize(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setMaxRows(int arg0) throws SQLException {
		wrapped.setMaxRows(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setPoolable(boolean arg0) throws SQLException {
		wrapped.setPoolable(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setQueryTimeout(int arg0) throws SQLException {
		wrapped.setQueryTimeout(arg0);
	}


	/**
	 * {@inheritDoc}
	 */
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}



	/**
	 * {@inheritDoc}
	 */
	public void addBatch() throws SQLException {
		wrapped.addBatch();
	}

	/**
	 * {@inheritDoc}
	 */
	public void clearParameters() throws SQLException {
		wrapped.clearParameters();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean execute() throws SQLException {
		return wrapped.execute();
	}

	

	/**
	 * {@inheritDoc}
	 */
	public ResultSetMetaData getMetaData() throws SQLException {
		return wrapped.getMetaData();
	}

	/**
	 * {@inheritDoc}
	 */
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return wrapped.getParameterMetaData();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setArray(int arg0, Array arg1) throws SQLException {
		wrapped.setArray(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setAsciiStream(int arg0, InputStream arg1) throws SQLException {
		wrapped.setAsciiStream(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setAsciiStream(int arg0, InputStream arg1, int arg2)
			throws SQLException {
		wrapped.setAsciiStream(arg0, arg1, arg2);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setAsciiStream(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		wrapped.setAsciiStream(arg0, arg1, arg2);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setBigDecimal(int arg0, BigDecimal arg1) throws SQLException {
		wrapped.setBigDecimal(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setBinaryStream(int arg0, InputStream arg1) throws SQLException {
		wrapped.setBinaryStream(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setBinaryStream(int arg0, InputStream arg1, int arg2)
			throws SQLException {
		wrapped.setBinaryStream(arg0, arg1, arg2);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setBinaryStream(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		wrapped.setBinaryStream(arg0, arg1, arg2);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setBlob(int arg0, Blob arg1) throws SQLException {
		wrapped.setBlob(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setBlob(int arg0, InputStream arg1) throws SQLException {
		wrapped.setBlob(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setBlob(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		wrapped.setBlob(arg0, arg1, arg2);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setBoolean(int arg0, boolean arg1) throws SQLException {
		wrapped.setBoolean(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setByte(int arg0, byte arg1) throws SQLException {
		wrapped.setByte(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setBytes(int arg0, byte[] arg1) throws SQLException {
		wrapped.setBytes(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setCharacterStream(int arg0, Reader arg1) throws SQLException {
		wrapped.setCharacterStream(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setCharacterStream(int arg0, Reader arg1, int arg2)
			throws SQLException {
		wrapped.setCharacterStream(arg0, arg1, arg2);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setCharacterStream(int arg0, Reader arg1, long arg2)
			throws SQLException {
		wrapped.setCharacterStream(arg0, arg1, arg2);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setClob(int arg0, Clob arg1) throws SQLException {
		wrapped.setClob(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setClob(int arg0, Reader arg1) throws SQLException {
		wrapped.setClob(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setClob(int arg0, Reader arg1, long arg2) throws SQLException {
		wrapped.setClob(arg0, arg1, arg2);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDate(int arg0, Date arg1) throws SQLException {
		wrapped.setDate(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDate(int arg0, Date arg1, Calendar arg2) throws SQLException {
		wrapped.setDate(arg0, arg1, arg2);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDouble(int arg0, double arg1) throws SQLException {
		wrapped.setDouble(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setFloat(int arg0, float arg1) throws SQLException {
		wrapped.setFloat(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setInt(int arg0, int arg1) throws SQLException {
		wrapped.setInt(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setLong(int arg0, long arg1) throws SQLException {
		wrapped.setLong(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setNCharacterStream(int arg0, Reader arg1) throws SQLException {
		wrapped.setNCharacterStream(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setNCharacterStream(int arg0, Reader arg1, long arg2)
			throws SQLException {
		wrapped.setNCharacterStream(arg0, arg1, arg2);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setNClob(int arg0, NClob arg1) throws SQLException {
		wrapped.setNClob(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setNClob(int arg0, Reader arg1) throws SQLException {
		wrapped.setNClob(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setNClob(int arg0, Reader arg1, long arg2) throws SQLException {
		wrapped.setNClob(arg0, arg1, arg2);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setNString(int arg0, String arg1) throws SQLException {
		wrapped.setNString(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setNull(int arg0, int arg1) throws SQLException {
		wrapped.setNull(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setNull(int arg0, int arg1, String arg2) throws SQLException {
		wrapped.setNull(arg0, arg1, arg2);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setObject(int arg0, Object arg1) throws SQLException {
		wrapped.setObject(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setObject(int arg0, Object arg1, int arg2) throws SQLException {
		wrapped.setObject(arg0, arg1, arg2);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setObject(int arg0, Object arg1, int arg2, int arg3)
			throws SQLException {
		wrapped.setObject(arg0, arg1, arg2, arg3);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setRef(int arg0, Ref arg1) throws SQLException {
		wrapped.setRef(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setRowId(int arg0, RowId arg1) throws SQLException {
		wrapped.setRowId(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setSQLXML(int arg0, SQLXML arg1) throws SQLException {
		wrapped.setSQLXML(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setShort(int arg0, short arg1) throws SQLException {
		wrapped.setShort(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setString(int arg0, String arg1) throws SQLException {
		wrapped.setString(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTime(int arg0, Time arg1) throws SQLException {
		wrapped.setTime(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTime(int arg0, Time arg1, Calendar arg2) throws SQLException {
		wrapped.setTime(arg0, arg1, arg2);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTimestamp(int arg0, Timestamp arg1) throws SQLException {
		wrapped.setTimestamp(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTimestamp(int arg0, Timestamp arg1, Calendar arg2)
			throws SQLException {
		wrapped.setTimestamp(arg0, arg1, arg2);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setURL(int arg0, URL arg1) throws SQLException {
		wrapped.setURL(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("deprecation")
	public void setUnicodeStream(int arg0, InputStream arg1, int arg2)
			throws SQLException {
		wrapped.setUnicodeStream(arg0, arg1, arg2);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Test if exception is of type database busy
	 * @param ex the exception to test
	 * @return boolean true if the exception is about database is busy
	 */
	protected boolean isSqlBusyException(SQLException ex){
		return ( ex.getErrorCode() == SQLiteErrorCode.SQLITE_BUSY.code) || (ex.getMessage().contains("SQLITE_BUSY"));
				
	}

}
