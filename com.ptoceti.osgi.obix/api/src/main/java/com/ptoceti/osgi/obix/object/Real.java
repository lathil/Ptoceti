package com.ptoceti.osgi.obix.object;

import java.util.Objects;

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


public class Real extends Val {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9068900088523384313L;

	private static final Contract contract = new Contract("obix:real");
	
	protected Double min;
	protected Double max;
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
		if( model.min != null) min = new Double(model.min);
		if( model.max != null) min = new Double(model.max);
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
	
	@Override
	public Obj clone() throws CloneNotSupportedException {
		Real clone = (Real)super.clone();
		
		clone.setMax(this.getMax() != null ? new Double(this.getMax()) : null);
		clone.setMin(this.getMin() != null ? new Double(this.getMin()) : null);
		clone.setUnit(this.getUnit() != null ? this.getUnit().clone() : null);
		clone.setPrecision(this.getPrecision() != null ? new Integer(this.getPrecision()) : null);
		clone.setVal(this.getVal() != null ?   new Double( ((Double)this.getVal()).doubleValue()) : null);
		
		return clone;
	}
	
	@Override
	public boolean updateWith(Obj other){
		boolean different = false;
		
		if( !Objects.equals(getMax(), ((Real)other).getMax())){
			setMax(((Real)other).getMax());
			different = true;
		}
		if( !Objects.equals(getMin(), ((Real)other).getMin())){
			setMin(((Real)other).getMin());
			different = true;
		}
		if( !Objects.equals(getPrecision(), ((Real)other).getPrecision())){
			setPrecision(((Real)other).getPrecision());
			different = true;
		}
		if(!Objects.equals(getVal(), ((Real)other).getVal())){
			setVal(((Real)other).getVal());
			different = true;
		}
		
		return super.updateWith(other, different);
	}
	
	public void setMin(Double min) {
		this.min = min;
	}

	public Double getMin() {
		return min;
	}

	public void setMax(Double max) {
		this.max = max;
	}

	public Double getMax() {
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
