package com.ptoceti.osgi.obix.contract;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Api
 * FILENAME : BatchIn.java
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
import com.ptoceti.osgi.obix.object.List;
import com.ptoceti.osgi.obix.object.Uri;

public class BatchIn extends List implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4862216033009501865L;
	public static final Contract contract = new Contract("obix:BatchIn");
	
	public BatchIn() {
		
		this.setIs(contract);
	}
	
	public BatchIn(String name, int min, int max) {
		super(name, min, max);
		
		this.setIs(contract);
	}
	
	public void addUri(Read uri){
		this.addChildren(uri);
	}
	
	public void addUri(Write uri){
		this.addChildren(uri);
	}
	
	public void addUri(Invoke uri){
		this.addChildren(uri);
	}
}
