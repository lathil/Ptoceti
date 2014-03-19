package com.ptoceti.osgi.obix.object;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Api
 * FILENAME : Status.java
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


public enum Status {
	
	DISABLED( 1, "disabled"),
	FAULT(2, "fault"),
	DOWN( 3,"down"),
	UNAKEDALARM( 4,"unackedAlarm"),
	ALARM( 5, "alarm"),
	UNACKED( 6, "unacked"),
	OVERRIDEN( 7,"overridden"),
	OK(8,"ok");


	final protected String name;
	final protected int id;

	Status(int id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	public static Status getStatusFromName( String name){
		
		Status result = null;
		for( Status status : Status.values()){
			if( status.getName().equals(name)) {result = status; break;}
		}
		return result;
	}
	
	public static Status getStatusFromId( int id){
		
		Status result = null;
		for( Status status : Status.values()){
			if( status.getId() == id) {result = status; break;}
		}
		return result;
	}
	
}
