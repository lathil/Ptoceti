package com.ptoceti.osgi.control;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Control
 * FILENAME : StatusCode.java
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



public enum StatusCode {

	OK(0,"Ok"),
	FAULT(1,"Fault");
	
	private int code;
	private String name;
	
	StatusCode(){
		
	}
	
	StatusCode(int code, String name){
		this.code = code;
		this.name = name;
	}
	
	public int getCode(){
		return code;
	}
	public void setCode(int code){
		this.code = code;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public static StatusCode getStatusFromId( int id){
		
		StatusCode result = null;
		for( StatusCode status : StatusCode.values()){
			if( status.getCode() == id) {result = status; break;}
		}
		return result;
	}
}
