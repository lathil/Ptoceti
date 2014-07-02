package com.ptoceti.osgi.sqlite;

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
		String url = props.getProperty(DataSourceFactory.JDBC_URL);
		
		wrapped = new SQLiteDataSource(config);
		wrapped.setUrl(url);
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
