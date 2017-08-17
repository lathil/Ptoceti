package com.ptoceti.osgi.obix.impl.proxy.jdbc;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : JdbcConnectionProxyFactory.java
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


import java.lang.reflect.Proxy;

import org.osgi.service.log.LogService;

import com.ptoceti.osgi.obix.domain.BaseDomain;
import com.ptoceti.osgi.obix.impl.service.Activator;

/**
 * Factory to create JdbcConnectionHandler proxy around a BaseDomain object
 * @author LATHIL
 *
 * @param <T> the type of BaseDomain object the the proxy to create should be proxying.
 */
public class JdbcConnectionProxyFactory<T extends BaseDomain> {
	
	
	/**
	 * 
	 * Install a JdbcConnectionHandler proxy around a BaseDomain object.
	 * 
	 * @param proxiedClass the class of the desired object the proxy should be wrapped around.
	 * @param proxiedinterface the interface of the desired object the proxy should be wrapped around
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T createProxy ( Class< ? extends T> proxiedClass, Class<T> proxiedinterface ) {
		
		T result = null;
		JdbcConnectionHandler<T> handler;
		try {
			handler = new JdbcConnectionHandler<T>(proxiedClass.newInstance());
			result = (T) Proxy.newProxyInstance( this.getClass().getClassLoader(), new Class[] {proxiedinterface}, handler);
			//result = (T) Proxy.newProxyInstance( Thread.currentThread().getContextClassLoader(), new Class[] {proxiedinterface}, handler);
			
		} catch (InstantiationException e) {
			Activator.log(LogService.LOG_ERROR, "Error creating JdbcConnectionHandler proxy: " + e);
		} catch (IllegalAccessException e) {
			Activator.log(LogService.LOG_ERROR, "Error creating JdbcConnectionHandler proxy: " + e);
		}
		
		return result;
	}
}
