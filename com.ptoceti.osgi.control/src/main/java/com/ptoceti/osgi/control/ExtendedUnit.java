package com.ptoceti.osgi.control;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Control
 * FILENAME : ExtendedUnit.java
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


import java.util.HashMap;

import org.osgi.util.measurement.Measurement;
import org.osgi.util.measurement.Unit;

/**
 * A unit composition around org.osgi.util.measurement.Unit that allows also for non SI unit.
 * 
 * 
 * @author LATHIL
 *
 */
public class ExtendedUnit {

	private Unit siUnit;
	
	private boolean isSI;
	
	private String nonSIName;
	
	private double scale;
	
	private double offset;
	
	public static final ExtendedUnit unity = new ExtendedUnit(Unit.unity, true, null);
	public static final ExtendedUnit A = new ExtendedUnit(Unit.A, true, null);
	public static final ExtendedUnit C = new ExtendedUnit(Unit.C, true,   null);
	public static final ExtendedUnit cd = new ExtendedUnit(Unit.cd, true,  null);
	public static final ExtendedUnit F = new ExtendedUnit(Unit.F, true,  null);
	public static final ExtendedUnit Gy = new ExtendedUnit(Unit.Gy, true,  null);
	public static final ExtendedUnit Hz = new ExtendedUnit(Unit.Hz, true,  null);
	public static final ExtendedUnit J = new ExtendedUnit(Unit.J, true,  null);
	public static final ExtendedUnit K = new ExtendedUnit(Unit.K, true,  null);
	public static final ExtendedUnit kat = new ExtendedUnit(Unit.kat, true,  null);
	public static final ExtendedUnit kg = new ExtendedUnit(Unit.kg, true,   null);
	public static final ExtendedUnit Lx = new ExtendedUnit(Unit.lx, true,  null);
	public static final ExtendedUnit m = new ExtendedUnit(Unit.m, true,   null);
	public static final ExtendedUnit m2 = new ExtendedUnit(Unit.m2, true,  null);
	public static final ExtendedUnit m3 = new ExtendedUnit(Unit.m3, true,   null);
	public static final ExtendedUnit m_s = new ExtendedUnit(Unit.m_s, true,   null);
	public static final ExtendedUnit m_s2 = new ExtendedUnit(Unit.m_s2, true,   null);
	public static final ExtendedUnit mol = new ExtendedUnit(Unit.mol, true,   null);
	public static final ExtendedUnit N = new ExtendedUnit(Unit.N, true,   null);
	public static final ExtendedUnit Ohm = new ExtendedUnit(Unit.Ohm, true,  null);
	public static final ExtendedUnit Pa = new ExtendedUnit(Unit.Pa, true,  null);
	public static final ExtendedUnit rad = new ExtendedUnit(Unit.rad, true,  null);
	public static final ExtendedUnit s = new ExtendedUnit(Unit.s, true,  null);
	public static final ExtendedUnit S = new ExtendedUnit(Unit.S, true,  null);
	public static final ExtendedUnit T = new ExtendedUnit(Unit.T, true,  null);
	public static final ExtendedUnit V = new ExtendedUnit(Unit.V, true,  null);
	public static final ExtendedUnit W = new ExtendedUnit(Unit.W, true,   null);
	public static final ExtendedUnit Wb = new ExtendedUnit(Unit.Wb, true,  null);
	
	public static final ExtendedUnit percent = new ExtendedUnit(Unit.unity, false, "percent");
	public static final ExtendedUnit celsius = new ExtendedUnit(Unit.K, false, "celsius", 1.0, -273.15);
	
	protected static HashMap<String, ExtendedUnit> units = new HashMap<String, ExtendedUnit>();
	
	static {
		units.put(ExtendedUnit.A.toString(), ExtendedUnit.A);
		units.put(ExtendedUnit.C.toString(), ExtendedUnit.C);
		units.put(ExtendedUnit.cd.toString(), ExtendedUnit.cd);
		units.put(ExtendedUnit.celsius.toString(), ExtendedUnit.celsius);
		units.put(ExtendedUnit.F.toString(), ExtendedUnit.F);
		units.put(ExtendedUnit.Gy.toString(), ExtendedUnit.Gy);
		units.put(ExtendedUnit.Hz.toString(), ExtendedUnit.Hz);
		units.put(ExtendedUnit.J.toString(), ExtendedUnit.J);
		units.put(ExtendedUnit.K.toString(), ExtendedUnit.K);
		units.put(ExtendedUnit.kat.toString(), ExtendedUnit.kat);
		units.put(ExtendedUnit.kg.toString(), ExtendedUnit.kg);
		units.put(ExtendedUnit.Lx.toString(), ExtendedUnit.Lx);
		units.put(ExtendedUnit.m.toString(), ExtendedUnit.m);
		units.put(ExtendedUnit.m2.toString(), ExtendedUnit.m2);
		units.put(ExtendedUnit.m3.toString(), ExtendedUnit.m3);
		units.put(ExtendedUnit.m_s.toString(), ExtendedUnit.m_s);
		units.put(ExtendedUnit.m_s2.toString(), ExtendedUnit.m_s2);
		units.put(ExtendedUnit.mol.toString(), ExtendedUnit.mol);
		units.put(ExtendedUnit.N.toString(), ExtendedUnit.N);
		units.put(ExtendedUnit.Ohm.toString(), ExtendedUnit.Ohm);
		units.put(ExtendedUnit.Pa.toString(), ExtendedUnit.Pa);
		units.put(ExtendedUnit.percent.toString(), ExtendedUnit.percent);
		units.put(ExtendedUnit.rad.toString(), ExtendedUnit.rad);
		units.put(ExtendedUnit.s.toString(), ExtendedUnit.s);
		units.put(ExtendedUnit.S.toString(), ExtendedUnit.S);
		units.put(ExtendedUnit.T.toString(), ExtendedUnit.T);
		units.put(ExtendedUnit.unity.toString(), ExtendedUnit.unity);
		units.put(ExtendedUnit.V.toString(), ExtendedUnit.V);
		units.put(ExtendedUnit.W.toString(), ExtendedUnit.W);
		units.put(ExtendedUnit.Wb.toString(), ExtendedUnit.Wb);

	}
	
	public static ExtendedUnit findUnit(String name){
		return units.get(name);
	}
	
	public ExtendedUnit(){
		
	}
	
	protected ExtendedUnit(Unit unit, boolean isSI, String nonSIName){
		this.setSiUnit(unit);
		this.setSI(isSI);
		this.nonSIName = nonSIName;
		this.offset = 0;
		this.scale = 1;
	}
	
	protected ExtendedUnit(Unit unit, boolean isSI, String nonSIName, double scale, double offset){
		this(unit, isSI,nonSIName);
		this.scale = scale;
		this.offset = offset;
	}
	
	ExtendedUnit div ( ExtendedUnit unit){
		
		if( unit.isSI() && isSI()){
			Measurement a = new Measurement(1,0,getSiUnit());
			Measurement b = new Measurement(1,0,unit.getSiUnit());
			return new ExtendedUnit( a.div(b).getUnit(), true, null);
		} else if( !unit.isSI() && !isSI() && (unit.getNonSIName() == getNonSIName())) {
			return this;
		} 
		
		throw new ArithmeticException("Cannot add " + this + " to " + unit );
	}

	ExtendedUnit mul( ExtendedUnit unit){
		
		if( unit.isSI() && isSI()){
			Measurement a = new Measurement(1,0,getSiUnit());
			Measurement b = new Measurement(1,0,unit.getSiUnit());
			return new ExtendedUnit( a.mul(b).getUnit(), true, null);
		} else if( !unit.isSI() && !isSI() && (unit.getNonSIName() == getNonSIName())) {
			return this;
		} 
		
		throw new ArithmeticException("Cannot add " + this + " to " + unit );
	}

	ExtendedUnit add( ExtendedUnit unit){
		
		if( unit.isSI() && isSI()){
			Measurement a = new Measurement(1,0,getSiUnit());
			Measurement b = new Measurement(1,0,unit.getSiUnit());
			return new ExtendedUnit( a.add(b).getUnit(), true, null);
		} else if( !unit.isSI() && !isSI() && (unit.getNonSIName() == getNonSIName())) {
			return this;
		} 
		
		throw new ArithmeticException("Cannot add " + this + " to " + unit );
	}
	
	ExtendedUnit sub( ExtendedUnit unit){
		
		if( unit.isSI() && isSI()){
			Measurement a = new Measurement(1,0,getSiUnit());
			Measurement b = new Measurement(1,0,unit.getSiUnit());
			return new ExtendedUnit( a.sub(b).getUnit(), true, null);
		} else if( !unit.isSI() && !isSI() && (unit.getNonSIName() == getNonSIName())) {
			return this;
		} 
		
		throw new ArithmeticException("Cannot add " + this + " to " + unit );
	}
	
	@Override
	public boolean equals( Object obj){
		if ( this == obj) return true;
		else if(!( obj instanceof ExtendedUnit)) return false;
		else if( isSI() != ((ExtendedUnit)obj).isSI()) return false;
		else if ( isSI() && ( getSiUnit().equals( ((ExtendedUnit)obj).getSiUnit()))) return true;
		else if ( !isSI() && ( getNonSIName() == ((ExtendedUnit)obj).getNonSIName())) return true;
		
		return false;
	}
	
	public Unit getSiUnit(){
		return siUnit;
	}
	
	public void setSiUnit(Unit siUnit) {
		this.siUnit = siUnit;
	}
	
	public double getScale(){
		return scale;
	}
	
	public void setScale(double scale){
		this.scale = scale;
	}
	
	public double getOffset(){
		return offset;
	}
	
	public void setOffset( double offset){
		this.offset = offset;
	}
	
	public String toString(){
		if( isSI()) return getSiUnit().toString();
		else return getNonSIName();
	}

	public String getNonSIName() {
		return nonSIName;
	}
	
	public void setNonSIName(String nonSIName){
		this.nonSIName = nonSIName;
	}

	public boolean isSI() {
		return isSI;
	}

	public void setSI(boolean isSI) {
		this.isSI = isSI;
	}

	
}
