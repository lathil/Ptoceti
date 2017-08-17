package com.ptoceti.osgi.obix.object;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Api
 * FILENAME : Enum.java
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


public class Enum extends Val {

	/**
	 * 
	 */
	private static final long serialVersionUID = 390838246631189012L;

	public static final Contract contract = new Contract("obix:enum");
	
	protected Uri range;

	public Enum() {
		super();
	}
	
	public Enum( Obj model){
		super(model);
		
	}
	public Enum( Enum model){
		super(model);
		if( model.range != null) range = new Uri(model.range);
		if( model.val != null) val = new String( ((String)model.val));
	}

	public Enum(String name) {
		super(name);
	}

	public Enum(String name, String val) {
		super(name, val);
	}

	
	public void setRange(Uri range) {
		this.range = range;
	}

	public Uri getRange() {
		return range;
	}

	@Override
	public String encodeVal() {
		if (getVal() != null)
			return ((String) getVal());
		else
			return null;
	}

	@Override
	public void decodeVal(String value) {
		setVal(value);
	}
	
	@Override
	public Obj cloneEmpty() {
		return new Enum();
	}
	
	public Val getDiff(Val val) {
		Enum result = null;
		if( val instanceof Abstime){
			
			result = new Enum(this);
		}
		
		return result;
	}
	
	@Override
	public Contract getContract(){
		return contract;
	}
	
	@Override
	public int compareTo(Object o) {
		return ((String)this.val).compareTo((String)((Val)o).getVal());
	}
}
