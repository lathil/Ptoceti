package com.ptoceti.osgi.obix.impl.proxy.jdbc;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : JdbcConnectionHandler.java
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

import com.ptoceti.osgi.obix.domain.BaseDomain;
import com.ptoceti.osgi.obix.impl.proxy.jdbc.JdbcConnection.ConnectionType;
import com.ptoceti.osgi.obix.impl.service.ObixDataHandler;

/**
 * Handler that implement a proxy around a BaseDomain class. For each invoked method of the proxied class, chek if a JdbcConnection annotation is present.
 * If present create a connection of the type indicated by the annotation on the thread local before invoking the proxied method. On return of the proxied
 * method, commit and close the connection on the thread local.
 * 
 * @author LATHIL
 *
 * @param <T> the type of proxied class that the handle should be build around.
 */
public class JdbcConnectionHandler<T extends BaseDomain> implements InvocationHandler {

	/**
	 * The proxied object
	 */
	private T proxiedObject;
	
	/**
	 * Constructor. 
	 * @param proxiedClass the proxied object
	 */
	public JdbcConnectionHandler( T proxiedClass) {
		this.proxiedObject = proxiedClass;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] params)
			throws Throwable {
		
		Object result = null;
		
			
			if( method.isAnnotationPresent(JdbcConnection.class)){
				
				ConnectionType connType = method.getAnnotation(JdbcConnection.class).type();
				switch( connType) {
					case RX:
						proxiedObject.setConnection( ObixDataHandler.getInstance().getConnectionRx());
						break;
					case RWX:
						proxiedObject.setConnection( ObixDataHandler.getInstance().getConnectionRWx());
						break;
					default:
						proxiedObject.setConnection( ObixDataHandler.getInstance().getConnectionRx());
				}
				
				try {
					result =  method.invoke(proxiedObject, params);
					
					switch( connType) {
						case RX:
							ObixDataHandler.getInstance().commitTransaction();
							break;
						case RWX:
							ObixDataHandler.getInstance().commitTransaction();
							break;
						default:	
							ObixDataHandler.getInstance().closeConnection();
					}
				} catch (InvocationTargetException ex) {
					// catch exception thrown by method
					//if( ex.getCause() instanceof EntityException && hasConnection){
						ObixDataHandler.getInstance().rollbackTransaction();
					//}
					
				}
			} else {
			    result =  method.invoke(proxiedObject, params);
			}
		
		return result;
	}

}
