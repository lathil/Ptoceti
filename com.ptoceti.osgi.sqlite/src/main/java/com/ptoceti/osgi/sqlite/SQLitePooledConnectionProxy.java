package com.ptoceti.osgi.sqlite;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : SQLite
 * FILENAME : SQLitePooledConnectionProxy.java
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
