package com.ptoceti.osgi.obix.object;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Api
 * FILENAME : Val.java
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


public abstract class Val extends Obj {

	protected Object val;
	
	public Val() {
		super();
	}
	
	public Val(Obj model) {
		super(model);
	}
	
	public Val(Val model) {
		super(model);
	}
	
	public Val(String name) {
		super(name);
	}

	public Val( String name, Object value) {
		super (name);
		setVal(value);
	}

	public void setVal(Object val) {
		this.val = val;
	}

	public Object getVal() {
		return val;
	}
	
	public String encodeVal() {
		if( val != null) return val.toString();
		else return "";
	}
	
	public void decodeVal(String value) {
		val = value;
	}
	
	public Val getDiff(Val val ) {
		return null;
	}
		
}
