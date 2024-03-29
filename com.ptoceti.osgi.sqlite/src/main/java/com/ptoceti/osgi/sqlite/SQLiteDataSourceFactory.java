package com.ptoceti.osgi.sqlite;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : SQLite
 * FILENAME : SQLiteDataSourceFactory.java
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


import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.osgi.service.jdbc.DataSourceFactory;

/**
 * 
 * A DatabaseDriver implementation according to Osgi 4.2 Enterprise
 * 
 * @author LATHIL
 *
 */
public class SQLiteDataSourceFactory implements DataSourceFactory {

	@Override
	public DataSource createDataSource(Properties props) throws SQLException {
		return new SQLiteDataSourceWrapper(props);
	}

	@Override
	public ConnectionPoolDataSource createConnectionPoolDataSource( Properties props) throws SQLException {
		return new SQLiteConnectionPoolDataSourceWrapper(props);
	}

	@Override
	public XADataSource createXADataSource(Properties props) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Driver createDriver(Properties props) throws SQLException {
		return Activator.getSqliteJDBC();
	}

}
