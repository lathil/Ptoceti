package com.ptoceti.osgi.obix.object;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import com.ptoceti.osgi.obix.observable.ObservableEvent;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Api
 * FILENAME : Int.java
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


public class Int extends Val {

	/**
	 * 
	 */
	private static final long serialVersionUID = 499008084834546740L;

	public static final Contract contract = new Contract("obix:int");
	
	protected Integer min;
	protected Integer max;
	protected Uri unit;

	public Int() {
		super();
	}
	
	public Int( Obj model){
		super(model);
	}
	
	public Int( Int model){
		super(model);
		if( model.min != null) min = new Integer(model.min);
		if( model.max != null) min = new Integer(model.max);
		if( model.unit != null) unit = new Uri(model.unit);
		if( model.val != null) val = new Integer( ((Integer)model.val).intValue());
	}

	public Int(String name) {
		super(name);
	}

	
	public Int(String name, Integer value) {
		super(name, value);
	}

	@Override
	public Obj clone() throws CloneNotSupportedException  {
		Int clone = (Int)super.clone();
		
		clone.setMax(this.getMax() != null ? new Integer(this.getMax()) : null);
		clone.setMin(this.getMin() != null ? new Integer(this.getMin()) : null);
		clone.setUnit(this.getUnit() != null ? this.getUnit().clone() : null);
		clone.setVal(this.getVal() != null ?  new Integer( ((Integer)this.getVal()).intValue()) : null);
		
		return clone;
	}
	
	@Override
	public synchronized boolean updateWith(Obj other){
		boolean different = false;
		
		ArrayList<ObservableEvent> changeEvents = new ArrayList<ObservableEvent>();
		
		if( !Objects.equals(getMax(), ((Int)other).getMax())){
			setMax(((Int)other).getMax());
			different = true;
		}
		if( !Objects.equals(getMin(), ((Int)other).getMin())){
			setMin(((Int)other).getMin());
			different = true;
		}
		if(!Objects.equals(getVal(), ((Int)other).getVal())){
			setVal(((Int)other).getVal());
			changeEvents.add(ObservableEvent.VALCHANGED);
			different = true;
		}
		
		return super.updateWith(other, different, changeEvents);
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

	public void setUnit(Uri unit) {
		this.unit = unit;
	}

	public Uri getUnit() {
		return unit;
	}

	@Override
	public String encodeVal() {
		if (getVal() != null)
			return ((Integer) getVal()).toString();
		else
			return null;
	}

	@Override
	public void decodeVal(String value) {
		setVal(Integer.parseInt(value));
	}
	
	@Override
	public Obj cloneEmpty() {
		return new Int();
	}
	
	public Val getDiff(Val val) {
		Int result = null;
		if( val instanceof Int){
			
			result = new Int(this);
			result.setVal(new Integer(((Integer)getVal()).intValue() - ((Integer)((Int)val).getVal()).intValue()));
		}
		
		return result;
	}
	
	@Override
	public Contract getContract(){
		return contract;
	}

	@Override
	public int compareTo(Object o) {
		return ((Int)this.val).compareTo((Int)((Val)o).getVal());
	}
}
