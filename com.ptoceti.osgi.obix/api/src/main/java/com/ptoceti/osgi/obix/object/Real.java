package com.ptoceti.osgi.obix.object;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Api
 * FILENAME : Real.java
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


public class Real extends Val {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9068900088523384313L;

	private static final Contract contract = new Contract("obix:real");
	
	protected Float min;
	protected Float max;
	protected Uri unit;
	protected Integer precision;

	public Real() {
		super();
	}
	
	public Real( Obj model){
		super(model);
	}
	
	public Real( Real model){
		super(model);
		if( model.min != null) min = new Float(model.min);
		if( model.max != null) min = new Float(model.max);
		if( model.unit != null) unit = new Uri(model.unit);
		if( model.precision != null) precision = new Integer(model.precision);
		if( model.val != null) val = new Double( ((Double)model.val).doubleValue());
	}

	public Real(String name) {
		super(name);
	}

	public Real(String name, Double value) {
		super(name, value);
	}
	
	public void setMin(Float min) {
		this.min = min;
	}

	public Float getMin() {
		return min;
	}

	public void setMax(Float max) {
		this.max = max;
	}

	public Float getMax() {
		return max;
	}

	public void setPrecision(Integer precision) {
		this.precision = precision;
	}

	public Integer getPrecision() {
		return precision;
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
			return ((Double) getVal()).toString();
		else
			return null;
	}

	@Override
	public void decodeVal(String value) {
		setVal(Double.parseDouble(value));
	}
	
	@Override
	public Obj cloneEmpty() {
		return new Real();
	}
	
	public Val getDiff(Val val) {
		Real result = null;
		if( val instanceof Real){
			
			result = new Real(this);
			result.setVal(new Double(((Double)getVal()).doubleValue() - ((Double)((Real)val).getVal()).doubleValue()));
		
		}
		
		return result;
	}
	
	@Override
	public Contract getContract(){
		return contract;
	}
}
