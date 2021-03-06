package com.ptoceti.osgi.data;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Data
 * FILENAME : ResultSetGeneratedKeysHandler.java
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


import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetGeneratedKeysHandler implements IResultSetGeneratedKeysHandler{

	IResultSetGeneratedKeysHandler handler;
	
	private static final String ROW_ID = "last_insert_rowid()";
	
	public ResultSetGeneratedKeysHandler() {
		handler = null;
	}
	
	public ResultSetGeneratedKeysHandler(IResultSetGeneratedKeysHandler handler) {
		this.handler = handler;
	}
	
	protected Integer getRowID(ResultSet rs) {
		Integer result = null;
		try {
			ResultSet krs = rs.getStatement().getGeneratedKeys();
			if( krs.next()){
				int value = krs.getInt(1);
				result = new Integer(value);
			}
			//int value = rs.getInt(ROW_ID);
			//if (!rs.wasNull())
			//	result = new Integer(value);

		} catch (SQLException ex) {

		}

		return result;
	}
	
	public void getRowsKey(ResultSet rs) throws Exception {
		if( this.handler != null) this.handler.getRowsKey(rs);
	}

}
