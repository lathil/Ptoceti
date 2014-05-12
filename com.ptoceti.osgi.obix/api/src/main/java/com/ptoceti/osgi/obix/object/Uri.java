package com.ptoceti.osgi.obix.object;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Api
 * FILENAME : Uri.java
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


public class Uri extends Val {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8040144014174027501L;
	
	private static final Contract contract = new Contract("obix:uri");
	
	public Uri(){
		super();
	}
	
	public Uri( Obj model ) {
		super(model);
	}
	
	public Uri( Uri model ) {
		super(model);
		if( model.getPath() != null ) val = new String(model.getPath());
	}
	
	public Uri(String name) {
		super(name);
	}
	
	public Uri(String name, String value) {
		super (name, value);
	}
	
	public String getPath() {
		return (String) this.getVal();
	}

	@Override
	public Obj cloneEmpty() {
		return new Uri();
	}
	
	public Val getDiff(Val val) {
		Uri result = null;
		if( val instanceof Uri){
			
			result = new Uri(this);
		}
		
		return result;
	}
	
	@Override
	public Contract getContract(){
		return contract;
	}
}
