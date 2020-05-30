package com.ptoceti.osgi.obix.object;

import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ptoceti.osgi.obix.observable.ObservableEvent;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Api
 * FILENAME : Reltime.java
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


public class Reltime extends Val {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4398992355838294392L;

	// private static final String xsduration =
	// "-?P(?=\\d+|T)(\\d+Y)?(\\d+M)?(\\d+D)?(T(?=\\d+)(\\d+H)?(\\d+M)?(\\d+S)?)?";

	public static final Contract contract = new Contract("obix:reltime");
	
	protected Reltime max;
	protected Reltime min;

	public Reltime() {
		super();
		setVal("P7D");
	}
	
	public Reltime( Obj model){
		super(model);
		setVal("P7D");
	}
	
	public Reltime( Reltime model){
		super(model);
		if( model.min != null) min = new Reltime(model.min);
		if( model.max != null) min = new Reltime(model.max);
		if( model.val != null) val = new String(model.val.toString());
	}

	public Reltime(String name) {
		super(name);
	}

	public Reltime(String name, Long value) {
		super(name, value);
	}
	
	@Override
	public Obj clone() throws CloneNotSupportedException {
		Reltime clone = (Reltime)super.clone();
		
		clone.setMax(this.getMax() != null ? new Reltime(this.getMax()) : null);
		clone.setMin(this.getMin() != null ? new Reltime(this.getMin()) : null);
		clone.setVal(this.getVal() != null ? new Long( ((Long)this.getVal()).longValue()) : null);
		
		return clone;
	}
	
	@Override
	public synchronized boolean updateWith(Obj other){
		boolean different = false;
		
		ArrayList<ObservableEvent> changeEvents = new ArrayList<ObservableEvent>();
		
		if( !Objects.equals(getMax(), ((Reltime)other).getMax())){
			setMax(((Reltime)other).getMax());
			different = true;
		}
		if( !Objects.equals(getMin(), ((Reltime)other).getMin())){
			setMin(((Reltime)other).getMin());
			different = true;
		}
		if(!Objects.equals(getVal(), ((Reltime)other).getVal())){
			setVal(((Reltime)other).getVal());
			changeEvents.add(ObservableEvent.VALCHANGED);
			different = true;
		}
		
		
		return super.updateWith(other, different, changeEvents);
	}
	
	public void setMax(Reltime max) {
		this.max = max;
	}

	public Reltime getMax() {
		return max;
	}

	public void setMin(Reltime min) {
		this.min = min;
	}

	public Reltime getMin() {
		return min;
	}

	@Override
	public String encodeVal() {
		if( val != null) return val.toString();
		else return "";
	}

	/**
	 * The time interval is specified in the following form "PnYnMnDTnHnMnSn" P
	 * indicates the period (required) nY indicates the number of years nM
	 * indicates the number of months nD indicates the number of days
	 */

	@Override
	public void decodeVal(String value) {

		Pattern iso8601Duration = Pattern.compile("-?P(?=\\d+|T)(\\d+Y)?(\\d+M)?(\\d+D)?(T(?=\\d+)(\\d+H)?(\\d+M)?(\\d+S)?)?");
		Matcher matcher = iso8601Duration.matcher(value);
		if( matcher.matches()){
			setVal(value);
		}
	}
	
	@Override
	public Obj cloneEmpty() {
		return new Reltime();
	}
	
	public Val getDiff(Val val) {
		Reltime result = null;
		if( val instanceof Abstime){
			
			result = new Reltime(this);
			result.setVal(new Long(((Long)getVal()).longValue() - ((Long)((Reltime)val).getVal()).longValue()));
		
		}
		
		return result;
	}
	
	@Override
	public Contract getContract(){
		return contract;
	}
	
	@Override
	public int compareTo(Object o) {
		return ((Long)this.val).compareTo((Long)((Val)o).getVal());
	}
}
