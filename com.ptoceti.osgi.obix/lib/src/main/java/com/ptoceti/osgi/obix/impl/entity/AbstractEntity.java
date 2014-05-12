package com.ptoceti.osgi.obix.impl.entity;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : AbstractEntity.java
 * 
 * This file is part of the Ptoceti project. More information about
 * this project can be found here: http://www.ptoceti.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2013 - 2014 ptoceti
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

import java.sql.Connection;
import java.sql.SQLException;

import com.ptoceti.osgi.data.IResultSetGeneratedKeysHandler;
import com.ptoceti.osgi.data.IResultSetMultipleHandler;
import com.ptoceti.osgi.data.IResultSetSingleHandler;
import com.ptoceti.osgi.data.JdbcDevice;
import com.ptoceti.osgi.obix.impl.ObixDataHandler;


public class AbstractEntity {

	private Connection getCurrentConnection() {
		return ObixDataHandler.getInstance().getCurrentConnection();
	}
	
	private JdbcDevice getDevice(){
		return ObixDataHandler.getInstance().getDataDevice();
	}

	public void query(String query, IResultSetSingleHandler resultSetHandler) throws EntityException{
		try {
			getDevice().query(getCurrentConnection(), query, resultSetHandler);
		} catch (SQLException ex) {
			throw new EntityException("Exception in " + this.getClass().getName() + " queryMultiple", ex);
		}
	}

	public void query(String query, Object[] params, IResultSetSingleHandler resultSetHandler) throws EntityException {
		try {
			getDevice().query(getCurrentConnection(), query, params, resultSetHandler);
		} catch (SQLException ex) {
			throw new EntityException("Exception in " + this.getClass().getName() + " query", ex);
		}
	}

	public void queryMultiple(String query, Object[] params,IResultSetMultipleHandler resultSetHandler) throws EntityException{

		try {
			getDevice().queryMultiple(getCurrentConnection(), query, params,resultSetHandler);
		} catch (SQLException ex) {
			throw new EntityException("Exception in " + this.getClass().getName() + " queryMultiple", ex);
		}
	}

	public void queryMultiple(String query, IResultSetMultipleHandler resultSetHandler) throws EntityException {
		try {
			getDevice().queryMultiple(getCurrentConnection(), query,resultSetHandler);
		} catch (SQLException ex) {
			throw new EntityException("Exception in " + this.getClass().getName() + " queryMultiple", ex);
		}
	}

	public int update(String query, IResultSetGeneratedKeysHandler handler) throws EntityException {
		
		int result = -1;
		try {
			result = getDevice().update(getCurrentConnection(), query, handler);
		} catch (SQLException ex) {
			throw new EntityException("Exception in " + this.getClass().getName() + " update", ex);
		}
		
		return result;
	}

	public int update(String query, Object[] params, IResultSetGeneratedKeysHandler handler) throws EntityException {
		int result = -1;
		try {
			result = getDevice().update(getCurrentConnection(), query, params,handler);
		} catch (SQLException ex) {
			throw new EntityException("Exception in " + this.getClass().getName() + " update", ex);
		}
		
		return result;
	}

}
