package com.ptoceti.osgi.obix.contract;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Api
 * FILENAME : WatchService.java
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


import java.io.Serializable;

import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Op;

public class WatchService extends Obj implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7195859863444573203L;
	public static final Contract contract = new Contract("obix:WatchService");
	
	public WatchService() {
		setIs(contract);
		
		Op make = new Op("make", Nil.contract, Watch.contract);
		this.addChildren(make);
	}
	
	public WatchService(String name) {
		super(name);
		
		setIs(contract);
		Op make = new Op("make", Nil.contract, Watch.contract);
		this.addChildren(make);
	}
	
	public Op getMake(){
		return (Op)(this.getChildren("make"));
	}

}
