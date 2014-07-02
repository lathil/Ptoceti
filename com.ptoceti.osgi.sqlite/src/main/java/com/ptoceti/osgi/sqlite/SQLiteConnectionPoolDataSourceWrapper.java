package com.ptoceti.osgi.sqlite;

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
