package com.ptoceti.osgi.sqlite;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.ConnectionEvent;

import org.sqlite.javax.SQLitePooledConnection;

public class SQLitePooledConnectionProxy extends SQLitePooledConnection{

	protected SQLitePooledConnectionProxy(Connection physicalConn) {
		super(physicalConn);
	}

	 /**
     * @see javax.sql.PooledConnection#getConnection()
     */
    public Connection getConnection() throws SQLException {
        if (handleConn != null)
            handleConn.close();

        handleConn = (Connection)Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Connection.class},
            new InvocationHandler() {
                boolean isClosed;

                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    try {
                        String name = method.getName();
                        if ("close".equals(name)) {
                            ConnectionEvent event = new ConnectionEvent(SQLitePooledConnectionProxy.this);

                            for (int i = listeners.size() - 1; i >= 0; i--) {
                                listeners.get(i).connectionClosed(event);
                            }

                            if (!physicalConn.getAutoCommit()) {
                                physicalConn.rollback();
                            }
                            //physicalConn.setAutoCommit(true);
                            isClosed = true;
                            handleConn = null;

                            return null; // don't close physical connection
                        }
                        else if ("isClosed".equals(name)) {
                            if (!isClosed)
                                isClosed = ((Boolean)method.invoke(physicalConn, args)).booleanValue();

                            return isClosed;
                        }

                        if (isClosed) {
                            throw new SQLException ("Connection is closed");
                        }

                        return method.invoke(physicalConn, args);
                    }
                    catch (SQLException e){
                        if ("database connection closed".equals(e.getMessage())) {
                            ConnectionEvent event = new ConnectionEvent(SQLitePooledConnectionProxy.this, e);

                            for (int i = listeners.size() - 1; i >= 0; i--) {
                                listeners.get(i).connectionErrorOccurred(event);
                            }
                        }

                        throw e;
                    }
                    catch (InvocationTargetException ex) {
                        throw ex.getCause();
                    }
                }
            });

        return handleConn;
    }
}
