package com.ptoceti.osgi.obix.object;

import java.util.Objects;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Api
 * FILENAME : Str.java
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


public class Str extends Val {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3119088030461698870L;

	private static final Contract contract = new Contract("obix:str");
	
	protected Integer min;
	protected Integer max;
	
	public Str() { super();}
	
	public Str(String name) {
		super(name);
	}
	
	public Str( Obj model){
		super(model);
	}
	
	public Str( Str model){
		super(model);
		if( model.min != null) min = new Integer(model.min);
		if( model.max != null) min = new Integer(model.max);
		if( model.val != null) val = new String( ((String)model.val));
	}
	
	public Str(String name, String value){
		super( name, value);
	}

	@Override
	public Obj clone() throws CloneNotSupportedException {
		Str clone = (Str)super.clone();
		
		clone.setMax(this.getMax() != null ? new Integer(this.getMax()) : null);
		clone.setMin(this.getMin() != null ? new Integer(this.getMin()) : null);
		clone.setVal(this.getVal() != null ? new String( ((String)this.getVal())) : null);
		
		return clone;
	}
	
	@Override
	public boolean updateWith(Obj other){
		boolean different = false;
		
		if( !Objects.equals(getMax(), ((Str)other).getMax())){
			setMax(((Str)other).getMax());
			different = true;
		}
		if( !Objects.equals(getMin(), ((Str)other).getMin())){
			setMin(((Str)other).getMin());
			different = true;
		}
		if(!Objects.equals(getVal(), ((Str)other).getVal())){
			setVal(((Str)other).getVal());
			different = true;
		}
		
		return super.updateWith(other, different);
	}
	
	public void setMin(Integer min) {
		this.min = min;
	}

	public Integer getMin() {
		return min;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

	public Integer getMax() {
		return max;
	}
	
	@Override
	public Obj cloneEmpty() {
		return new Str();
	}

	public Val getDiff(Val val) {
		Str result = null;
		if( val instanceof Str){
			
			result = new Str(this);
		}
		
		return result;
	}
	@Override
	public Contract getContract(){
		return contract;
	}
}
