package com.ptoceti.osgi.sqlite;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : SQLite
 * FILENAME : SQLiteConnectionPoolDataSourceWrapper.java
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


import java.sql.SQLException;
import java.util.Properties;

import javax.sql.PooledConnection;

import org.sqlite.javax.SQLiteConnectionPoolDataSource;


public class SQLiteConnectionPoolDataSourceWrapper extends SQLiteDataSourceWrapper implements javax.sql.ConnectionPoolDataSource {

	SQLiteConnectionPoolDataSource wrapper ;
	
	SQLiteConnectionPoolDataSourceWrapper(Properties props) {
		super(props);
	}

	@Override
	public PooledConnection getPooledConnection() throws SQLException {
		return getPooledConnection(null,null);
	}

	@Override
	public PooledConnection getPooledConnection(String user, String password) throws SQLException {
		return new SQLitePooledConnectionProxy(getConnection(user, password));
	}

	
}
