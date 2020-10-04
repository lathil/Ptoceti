package com.ptoceti.osgi.control;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Control
 * FILENAME : Measure.java
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


/**
 * A rework of org.osgi.util.measurement.Measurement that allows to take ExtendedUnit and so also non SI unit.
 * 
 * @author LATHIL
 *
 */
public class Measure {

	private double				value;
	private double				error;
	private long					time;
	private StatusCode					status;
	private ExtendedUnit			unit;
	private transient volatile String	name;
	private transient volatile int		hashCode;
	
	public Measure(){
		
	}
	
	public Measure(double value, double error, ExtendedUnit unit, long time) {
		this.value = value;
		this.error = Math.abs(error);
		this.unit = (unit != null) ? unit : ExtendedUnit.unity;
		this.time = time;
		this.status = StatusCode.OK;
	}
	
	public Measure(double value, double error, ExtendedUnit unit) {
		this(value, error, unit, 0l);
	}
	
	public Measure(double value, ExtendedUnit unit) {
		this(value, 0.0d, unit, 0l);
	}
	
	public Measure(double value) {
		this(value, 0.0d, null, 0l);
	}
	
	public Measure mul(Measure m) {
		double mvalue = m.getValue();
		return new Measure(getValue() * mvalue, Math.abs(getValue()) * m.getError()
				+ getError() * Math.abs(mvalue), unit.mul(m.unit), getTime());
	}
	
	public Measure mul(double d, ExtendedUnit u) {
		return new Measure(getValue() * d, getError() * Math.abs(d), unit.mul(u),
				getTime());
	}
	
	public Measure mul(double d) {
		return new Measure(getValue() * d, getError() * Math.abs(d), unit, getTime());
	}
	
	public Measure div(Measure m) {
		double mvalue = m.getValue();
		return new Measure(getValue() / mvalue,
				(Math.abs(getValue()) * m.getError() + getError() * Math.abs(mvalue))
						/ (mvalue * mvalue), unit.div(m.unit), getTime());
	}
	
	public Measure div(double d, ExtendedUnit u) {
		return new Measure(getValue() / d, getError() / Math.abs(d), unit.div(u),
				getTime());
	}
	
	public Measure div(double d) {
		return new Measure(getValue() / d, getError() / Math.abs(d), unit, getTime());
	}
	
	public Measure add(Measure m) {
		return new Measure(getValue() + m.getValue(), getError() + m.getError(), unit
				.add(m.unit), getTime());
	}
	
	public Measure add(double d, ExtendedUnit u) {
		return new Measure(getValue() + d, getError(), unit.add(u), getTime());
	}
	
	public Measure add(double d) {
		return new Measure(getValue() + d, getError(), unit, getTime());
	}
	
	public Measure sub(Measure m) {
		return new Measure(getValue() - m.getValue(), getError() + m.getError(), unit
				.sub(m.unit), getTime());
	}
	
	public Measure sub(double d, ExtendedUnit u) {
		return new Measure(getValue() - d, getError(), unit.sub(u), getTime());
	}
	
	public Measure sub(double d) {
		return new Measure(getValue() - d, getError(), unit, getTime());
	}
	
	public String toString() {
		String result = getName();
		if (result == null) {
			StringBuffer sb = new StringBuffer();
			sb.append(getValue());
			if (getError() != 0.0d) {
				sb.append(" +/- ");
				sb.append(getError());
			}
			String u = unit.toString();
			if (u.length() > 0) {
				sb.append(" ");
				sb.append(u);
			}
			result = sb.toString();
		}
		return result;
	}
	
	public int compareTo(Object obj) {
		if (this == obj) {
			return 0;
		}
		Measure that = (Measure) obj;
		if (!unit.equals(that.unit)) {
			throw new ArithmeticException("Cannot compare " + this + " and "
					+ that);
		}
		int result = Double.compare(getValue(), that.getValue());
		if (result == 0) {
			return 0;
		}
		if (result < 0) {
			if (Double.compare(getValue() + getError(), that.getValue() - that.getError()) >= 0) {
				return 0;
			}
			return -1;
		}
		if (Double.compare(getValue() - getError(), that.getValue() + that.getError()) <= 0) {
			return 0;
		}
		return 1;
	}
	
	public int hashCode() {
		int h = hashCode;
		if (h == 0) {
			long bits = Double.doubleToLongBits(getValue());
			h = 31 * 17 + ((int) (bits ^ (bits >>> 32)));
			bits = Double.doubleToLongBits(getError());
			h = 31 * h + ((int) (bits ^ (bits >>> 32)));
			h = 31 * h + unit.hashCode();
			hashCode = h;
		}
		return h;
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Measure)) {
			return false;
		}
		Measure that = (Measure) obj;
		return (Double.compare(getValue(), that.getValue()) == 0)
				&& (Double.compare(getError(), that.getError()) == 0)
				&& unit.equals(that.unit);
	}

	public ExtendedUnit getUnit(){
		return unit;
	}
	
	public void setUnit(ExtendedUnit unit){
		this.unit = unit;
	}
	
	
	public double getValue() {
		return value;
	}
	
	public void setValue(double value){
		this.value = value;
	}

	public double getError() {
		return error;
	}
	
	public void setError(double error){
		this.error = error;
	}

	public long getTime() {
		return time;
	}
	
	public void setTime(long time){
		this.time = time;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}

	public StatusCode getStatus() {
		return status;
	}
	
	public void setStatus(StatusCode status){
		this.status = status;
	}
}
