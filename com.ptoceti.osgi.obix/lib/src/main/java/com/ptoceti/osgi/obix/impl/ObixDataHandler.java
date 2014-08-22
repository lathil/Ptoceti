 package com.ptoceti.osgi.obix.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : ObixDataHandler.java
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

import com.ptoceti.osgi.data.JdbcDevice;

public class ObixDataHandler {

	// The instance of the data service
	private JdbcDevice dataDevice;
	
	private static final ObixDataHandler instance = new ObixDataHandler();
	
	private ObixDataHandler(){
		
	}

	public void setDataDevice(JdbcDevice dataDevice) {
		this.dataDevice = dataDevice;
	}

	public JdbcDevice getDataDevice() {
		return dataDevice;
	}
	
	public static ObixDataHandler getInstance() {
		return instance;
	}
	
	public Connection getConnectionRx() {
		if( dataDevice != null){
			return dataDevice.getConnectionRx();
		}
		
		return null;
	}
	
	public Connection getConnectionRWx() {
		if( dataDevice != null){
			return dataDevice.getConnectionRWx();
		}
		
		return null;
	}
	
	public Connection getCurrentConnection() {
		if( dataDevice != null){
			return dataDevice.getCurrentConnection();
		}
		
		return null;
	}
	
	
	
	public void closeConnection() {
		if( dataDevice != null){
			try {
				dataDevice.closeCurrentConnection();
			} catch ( SQLException ex) {
				
			}
		}
	}
	
	
	public void commitTransaction() {
		if( dataDevice != null){
			try {
				dataDevice.commitAndCloseCurrentConnection();
			} catch ( SQLException ex) {
				
			}
		}
	}
	
	public void rollbackTransaction() {
		if( dataDevice != null){
			try {
				dataDevice.rollbackAndCloseCurrentConnection();
			} catch ( SQLException ex) {
							
			}
		}
	}
	
	
}
