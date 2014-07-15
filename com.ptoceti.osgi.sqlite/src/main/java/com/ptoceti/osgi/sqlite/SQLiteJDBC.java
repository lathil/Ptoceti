package com.ptoceti.osgi.sqlite;

import java.sql.Connection;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import org.osgi.service.log.LogService;
import org.sqlite.JDBC;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteOpenMode;
import org.sqlite.SQLiteConfig.TransactionMode;

public class SQLiteJDBC implements java.sql.Driver{

	/**
	 * The sqlite jdbc driver
	 */
	JDBC sqliteJDBC = null;
	
	/**
	 * prefix for accessing sqlite driver
	 */
	private static final String SQLITE_JDBC_PREFIX = "jdbc:sqlite:";
	/**
	 * prefix for accessing file based database
	 */
	private static final String FILE_PREFIX = "file:";
	
	/**
	 * Creator.
	 * 
	 * Implements a java.sql.Driver that attach itself to a com.ptoceti.osgi.data.JdbcDevice service.
	 * 
	 * @param bc the bundle context
	 * @param ref the JdbcDevice service reference
	 */
	public SQLiteJDBC()
	{
		sqliteJDBC = new JDBC();
		Activator.log(LogService.LOG_INFO, "Sqlite major version: " + this.getMajorVersion() + ", minor version: " + this.getMinorVersion() +
				" , compliant: " + new Boolean(sqliteJDBC.jdbcCompliant()).toString());
		
	}
	
	
	/**
	 * Create a connection to the SQLite db
	 * 
	 * @param url an url formating string to the sqlte db
	 * @param info aditional properties for the connection
	 * @return Connection the jdbc connection
	 */
	public Connection connect(final String url, final Properties info) throws SQLException
	{
		String sqliteUrl = adaptUrl(url);
			
		SQLiteConfig config = new SQLiteConfig(info);
	
		//SQLiteConfig newConfig = new SQLiteConfig();
		config.setSharedCache(true);
		
		
		if(( config.getOpenModeFlags() & SQLiteOpenMode.READWRITE.flag) > 0) {
			config.setTransactionMode(TransactionMode.IMMEDIATE);
		} else if(( config.getOpenModeFlags() & SQLiteOpenMode.READONLY.flag) > 0) {
			config.setTransactionMode(TransactionMode.DEFFERED);
		}
		
		Connection connection = sqliteJDBC.connect(sqliteUrl, config.toProperties());
	
		return new SQLiteConnectionWrapper(connection);
	}
	
	/**
	 * Strip the file prefix from the url and reformat as an sqlite db url.
	 * 
	 * @param url the incoming db url
	 * @return an sqlite db url
	 */
	private String adaptUrl(String url) {
		
		String strippedUrl = url;
		int lastIndex = strippedUrl.lastIndexOf(FILE_PREFIX)+ FILE_PREFIX.length();
		strippedUrl = strippedUrl.substring(lastIndex );
		
		strippedUrl = SQLITE_JDBC_PREFIX + strippedUrl;
		
		return strippedUrl;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean acceptsURL(String url)
	{
		String sqliteUrl = url;
		if( ! sqliteUrl.startsWith(FILE_PREFIX)){
			return false;
		} else {
			sqliteUrl = adaptUrl(sqliteUrl);
		}
		
		return sqliteJDBC.acceptsURL(sqliteUrl);
	}
	/**
	 * {@inheritDoc}
	 */
	public int getMajorVersion()
	{
		return sqliteJDBC.getMajorVersion();
	}
	/**
	 * {@inheritDoc}
	 */
	public int getMinorVersion()
	{
		return sqliteJDBC.getMinorVersion();
	}
	/**
	 * {@inheritDoc}
	 */
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException
	{
		return sqliteJDBC.getPropertyInfo(url, info);
	}
	/**
	 * {@inheritDoc}
	 */
	public boolean jdbcCompliant()
	{
		return sqliteJDBC.jdbcCompliant();
	}
	/**
	 * {@inheritDoc}
	 */
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}
}
