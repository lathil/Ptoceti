package com.ptoceti.osgi.sqlite;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : SQLite
 * FILENAME : SQLiteDataSourceWrapper.java
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


import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.osgi.service.jdbc.DataSourceFactory;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

/**
 * A wrapped DataSource around SQLiteDataSource that alows use of a SQLiteConnectionWrapper
 * 
 * @author LATHIL
 *
 */
public class SQLiteDataSourceWrapper implements DataSource {

	private SQLiteDataSource wrapped;
	
	SQLiteDataSourceWrapper(Properties props) {
		SQLiteConfig config = new SQLiteConfig(props);
		String dbName = props.getProperty(DataSourceFactory.JDBC_DATABASE_NAME);
		
		wrapped = new SQLiteDataSource(config);
		wrapped.setUrl(wrapped.getUrl() + dbName);
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return wrapped.getLogWriter();
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return wrapped.getLoginTimeout();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return wrapped.getParentLogger();
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		wrapped.setLogWriter(out);
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		wrapped.setLoginTimeout(seconds);
	}

	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		if( DataSource.class.getName().equals(arg0.getName())
				|| "java.sql.Wrapper.class".equals(arg0.getName())) {
			return true;
		}
		return wrapped.isWrapperFor(arg0);
	}

	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		try {
			if ("java.sql.Connection".equals(arg0.getName())
					|| "java.sql.Wrapper.class".equals(arg0.getName())) {
				return arg0.cast(this);
			}

			return wrapped.unwrap(arg0);
		} catch (ClassCastException cce) {
			throw new SQLException("Unable to unwrap to " + arg0.toString(),
					cce);
		}
	}

	@Override
	public Connection getConnection() throws SQLException {
		return Activator.getSqliteJDBC().connect(wrapped.getUrl(), wrapped.getConfig().toProperties());
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return Activator.getSqliteJDBC().connect(wrapped.getUrl(), wrapped.getConfig().toProperties());
	}
}
