package com.ptoceti.osgi.obix.object;

import java.util.ArrayList;
import java.util.Objects;

import com.ptoceti.osgi.obix.observable.ObservableEvent;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Api
 * FILENAME : Bool.java
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




public class Bool extends Val{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5703560627132064965L;

<<<<<<< Upstream, based on develop
	public static final Contract contract = new Contract("obix:bool");
=======
	protected static final Contract contract = new Contract("obix:bool");
>>>>>>> 65471ee Removed cloning of input values in history / changed contract visibility in base obix objects
	
	protected Uri range;
	
	public Bool() {
		super();
	}
	
	public Bool( Obj model){
		super(model);
	}
	
	public Bool( Bool model){
		super(model);
		if( model.range != null) range = new Uri(model.range);
		if( model.val != null) val = new Boolean( ((Boolean)model.val).booleanValue());
	}
	
	public Bool(String name) {
		super(name);
		setVal(Boolean.FALSE);
	}
	
	public Bool(String name, Boolean value) {
		super(name);
		setVal(value);
	}
	
	@Override
	public Obj clone() throws CloneNotSupportedException {
		Bool clone = (Bool)super.clone();
		
		clone.setRange(this.getRange() != null ? this.getRange().clone(): null);
		clone.setVal(this.getVal() != null ? new Boolean( ((Boolean)this.getVal()).booleanValue()) : null);
		
		return clone;
	}
	
	@Override
	public synchronized boolean updateWith(Obj other){
		boolean different = false;
		
		ArrayList<ObservableEvent> changeEvents = new ArrayList<ObservableEvent>();
		
		if(!Objects.equals(getVal(), ((Bool)other).getVal())){
			setVal(((Bool)other).getVal());
			changeEvents.add(ObservableEvent.VALCHANGED);
			different = true;
		}
		
		return super.updateWith(other, different, changeEvents);
	}
	
	public void setRange(Uri range) {
		this.range = range;
	}

	public Uri getRange() {
		return range;
	}
	
	@Override
	public String encodeVal() {
		if( getVal() != null )
		return ((Boolean)this.getVal()).toString();
		else return null;
	}

	@Override
	public void decodeVal(String val ) {
		this.setVal(Boolean.parseBoolean(val));
	}
	
	@Override
	public Obj cloneEmpty() {
		return new Bool();
	}
	
	public Val getDiff(Val val) {
		Bool result = null;
		if( val instanceof Bool){
			
			result = new Bool(this);
			result.setVal(new Boolean(!((Boolean)getVal()).booleanValue() & ((Boolean)((Bool)val).getVal()).booleanValue()));
		}
		
		return result;
	}
	
	@Override
	public Contract getContract(){
		return contract;
	}
	
	@Override
	public int compareTo(Object o) {
		return ((Bool)this.val).compareTo((Bool)((Val)o).getVal());
	}
}
