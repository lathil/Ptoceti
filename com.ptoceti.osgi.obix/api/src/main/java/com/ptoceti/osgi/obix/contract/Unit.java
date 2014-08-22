package com.ptoceti.osgi.obix.contract;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Api
 * FILENAME : Unit.java
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


import java.io.Serializable;

import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Real;
import com.ptoceti.osgi.obix.object.Str;
import com.ptoceti.osgi.obix.object.Uri;



public class Unit extends Obj implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5941629735105808952L;
	public static final Contract contract = new Contract("obix:Unit");
	
	public static final Unit unity(){
		return new Unit( Dimension.dim_null, "unity", "", 0.0, 0.0);
	}
	public static final Unit m() {
		return new Unit( Dimension.m,"meter","m",0.0,1.0);
	}
	
	public static final Unit s() {
		return new Unit( Dimension.s,"second","s",0.0,1.0);
	}
	
	public static final Unit kg() {
		return new Unit( Dimension.kg,"kilo","kg",0.0,1.0);
	}
	
	public static final Unit K() {
		return new Unit( Dimension.K,"Kelvin","K",0.0,1.0);
	}
	
	public static final Unit A() {
		return new Unit( Dimension.A,"Ampere","A",0.0,1.0);
	}
	
	public static final Unit mol() {
		return new Unit( Dimension.mol,"mole","mol",0.0,1.0);
	}
	
	public static final Unit cd() {
		return new Unit( Dimension.cd,"candella","cd",0.0,1.0);
	}
	
	public static final Unit ms() {
		return new Unit( Dimension.m_s,"ms","ms",0.0,1.0);
	}
	
	public static final Unit ms2() {
		return new Unit( Dimension.m_s2,"ms2","ms2",0.0,1.0);
	}
	
	public static final Unit m2() {
		return new Unit( Dimension.m2,"m2","m2",0.0,1.0);
	}
	
	public static final Unit m3(){
		return new Unit( Dimension.m3,"m3","m3", 0.0,1.0);
	}
	
	public static final Unit Hz(){
		return new Unit( Dimension.Hz,"Hertz","Hz",0.0,1.0);
	}
	
	public static final Unit N(){
		return new Unit( Dimension.N,"Newton","N",0.0,1.0);
	}
	
	public static final Unit Pa(){
		return new Unit( Dimension.Pa,"pascal","Pa",0.0,1.0);
	}
	
	public static final Unit J(){
		return new Unit( Dimension.J,"Joule","J",0.0,1.0);
	}
	
	public static final Unit W(){
		return new Unit( Dimension.W,"Watt","W",0.0,1.0);
	}
	
	public static final Unit C(){
		return new Unit( Dimension.C,"Coulon","C",0.0,1.0);
	}
	
	public static final Unit V(){
		return new Unit( Dimension.V,"Volt", "V", 0.0,1.0);
	}
	
	public static final Unit F(){
		return new Unit( Dimension.F, "Farad","F", 0.0,1.0);
	}
	
	public static final Unit Ohm(){
		return new Unit( Dimension.Ohm, "Ohm","Ohm",0.0,1.0);
	}
	
	public static final Unit S(){
		return new Unit( Dimension.S,"Siemens","S",0.0,1.0);
	}
	
	public static final Unit Wb(){
		return new Unit( Dimension.Wb,"Weber","Wb",0.0,1.0);
	}
	
	public static final Unit T(){
		return new Unit( Dimension.T,"Tesla","T",0.0,1.0);
	}
	
	public static final Unit lx(){
		return new Unit( Dimension.lx,"lux","lx",0.0,1.0);
	}
	
	public static final Unit Gy(){
		return new Unit( Dimension.Gy,"Gray","Gy",0.0,1.0);
	}
	
	public static final Unit kat(){
		return new Unit( Dimension.kat,"katal","kat",0.0,1.0);
	}
	
	public static final Unit percent(){
		return new Unit( Dimension.dim_null,"percent","%",0.0,1.0);
	}
	
	public static final Unit celsius(){
		return new Unit( Dimension.K,"celsius","\u2103",-273.15,1.0);
	}
	
	public Unit() {
		setIs(contract);
	}
	
	public Unit(String name) {
		setIs(contract);
		setName(name);
	}
	
	public Unit(Dimension dimension, String display, String symbol, Double offset, Double scale) {
		
		setIs(contract);
		setHref( new Uri("unit", "obix:Unit" + "/" + display));
		setName("unit");
		setDisplay(display);
		
		this.addChildren(dimension);
		this.addChildren(new Real("offset",offset));
		this.addChildren(new Real("scale",scale));
		this.addChildren(new Str("symbol",symbol));
		
	}
	
	public static Unit getUnit(Uri unitUri){
		
		Unit unit = null;
		
		if( Unit.m().getHref().getPath().equals(unitUri.getPath())) return Unit.m();
		if( Unit.s().getHref().getPath().equals(unitUri.getPath())) return Unit.s();
		if( Unit.kg().getHref().getPath().equals(unitUri.getPath())) return Unit.kg();
		if( Unit.K().getHref().getPath().equals(unitUri.getPath())) return Unit.K();
		if( Unit.A().getHref().getPath().equals(unitUri.getPath())) return Unit.A();
		if( Unit.mol().getHref().getPath().equals(unitUri.getPath())) return Unit.mol();
		if( Unit.cd().getHref().getPath().equals(unitUri.getPath())) return Unit.cd();
		if( Unit.ms().getHref().getPath().equals(unitUri.getPath())) return Unit.ms();
		if( Unit.ms2().getHref().getPath().equals(unitUri.getPath())) return Unit.ms2();
		if( Unit.m2().getHref().getPath().equals(unitUri.getPath())) return Unit.m2();
		if( Unit.m3().getHref().getPath().equals(unitUri.getPath())) return Unit.m3();
		if( Unit.Hz().getHref().getPath().equals(unitUri.getPath())) return Unit.Hz();
		if( Unit.N().getHref().getPath().equals(unitUri.getPath())) return Unit.N();
		if( Unit.Pa().getHref().getPath().equals(unitUri.getPath())) return Unit.Pa();
		if( Unit.J().getHref().getPath().equals(unitUri.getPath())) return Unit.J();
		if( Unit.W().getHref().getPath().equals(unitUri.getPath())) return Unit.W();
		if( Unit.C().getHref().getPath().equals(unitUri.getPath())) return Unit.C();
		if( Unit.V().getHref().getPath().equals(unitUri.getPath())) return Unit.V();
		if( Unit.F().getHref().getPath().equals(unitUri.getPath())) return Unit.F();
		if( Unit.Ohm().getHref().getPath().equals(unitUri.getPath())) return Unit.Ohm();
		if( Unit.S().getHref().getPath().equals(unitUri.getPath())) return Unit.S();
		if( Unit.Wb().getHref().getPath().equals(unitUri.getPath())) return Unit.Wb();
		if( Unit.T().getHref().getPath().equals(unitUri.getPath())) return Unit.T();
		if( Unit.lx().getHref().getPath().equals(unitUri.getPath())) return Unit.lx();
		if( Unit.Gy().getHref().getPath().equals(unitUri.getPath())) return Unit.Gy();
		if( Unit.kat().getHref().getPath().equals(unitUri.getPath())) return Unit.kat();
		
		if( Unit.percent().getHref().getPath().equals(unitUri.getPath())) return Unit.percent();
		if( Unit.celsius().getHref().getPath().equals(unitUri.getPath())) return Unit.celsius();
		
		return unit;
	}

	public Dimension getDimension(){
		return (Dimension)this.getChildren("dimension");
	}
	
	public void setDimension( Dimension dimension) {
		this.replace("dimension", dimension);
	}
	
	public Real getOffset(){
		return (Real)this.getChildren("offset");
	}
	
	public void setOffset(Real offset){
		this.replace("offset", offset);
	}
	
	public Real getScale(){
		return (Real)this.getChildren("scale");
	}
	
	public void setScale(Real scale) {
		this.replace("scale", scale);
	}
	
	public Str getSymbol(){
		return (Str)this.getChildren("symbol");
	}
	
	public void setSymbol(Str symbol) {
		this.replace("symbol", symbol);
	}
}
