package com.ptoceti.osgi.sqlite;

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
