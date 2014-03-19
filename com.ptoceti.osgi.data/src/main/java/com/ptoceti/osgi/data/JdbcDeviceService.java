package com.ptoceti.osgi.data;

import java.sql.Connection;
import java.sql.SQLException;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Data
 * FILENAME : JdbcDeviceService.java
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


public abstract class JdbcDeviceService implements JdbcDevice{

	public abstract boolean setupDatabase(String databasePath, String setupScript );
	
	public abstract Connection getCurrentConnection();
	public abstract void closeCurrentConnection() throws SQLException;
	public abstract void commitAndCloseCurrentConnection()  throws SQLException;
	public abstract void rollbackAndCloseCurrentConnection() throws SQLException;
	
	public abstract void query(Connection conn, String query, IResultSetSingleHandler resultSetHandler) throws SQLException;
	public abstract void query(Connection conn, String query, Object[] params,IResultSetSingleHandler resultSetHandler) throws SQLException;
	public abstract void queryMultiple(Connection conn, String query, Object[] params, IResultSetMultipleHandler resultSetHandler) throws SQLException;
	public abstract void queryMultiple(Connection conn, String query, IResultSetMultipleHandler resultSetHandler) throws SQLException;
	
	public abstract int update(Connection conn, String query, IResultSetGeneratedKeysHandler handler) throws SQLException;
	public abstract int update(Connection conn, String query, Object[] params, IResultSetGeneratedKeysHandler handler) throws SQLException;
}
