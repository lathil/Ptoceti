package com.ptoceti.osgi.data;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Data
 * FILENAME : JdbcDevice.java
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

import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;

public interface JdbcDevice extends Device {
	
	public static final String NAME = JdbcDevice.class.getName();
	public static final String[] DEVICE_CATEGORY= {"JDBC"};
	public static final String DEVICE_DESCRIPTION="JDBC Device";
	
	public static final int MATCH_CLASS = 10;

	public void setJDBCDriver(Driver driver);
	
	public boolean setupDatabase(String databasePath, String setupScript );
	public Connection getCurrentConnection();
	public void closeCurrentConnection() throws SQLException;
	public void commitAndCloseCurrentConnection()  throws SQLException;
	public void rollbackAndCloseCurrentConnection() throws SQLException;
	
	public void query(Connection conn, String query, IResultSetSingleHandler resultSetHandler) throws SQLException;
	public void query(Connection conn, String query, Object[] params,IResultSetSingleHandler resultSetHandler) throws SQLException;
	public void queryMultiple(Connection conn, String query, Object[] params, IResultSetMultipleHandler resultSetHandler) throws SQLException;
	public void queryMultiple(Connection conn, String query, IResultSetMultipleHandler resultSetHandler) throws SQLException;
	
	public int update(Connection conn, String query, IResultSetGeneratedKeysHandler handler) throws SQLException;
	public int update(Connection conn, String query, Object[] params, IResultSetGeneratedKeysHandler handler) throws SQLException;
}
