package com.ptoceti.osgi.obix.contract;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Api
 * FILENAME : Dimension.java
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


import java.io.Serializable;

import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Int;

public class Dimension extends Obj implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6253786571814789250L;

	public static final Contract contract = new Contract("obix:Dimension");
	
	public static final Dimension m = new Dimension(0,0,0,0,0,0,1);// Meters
	public static final Dimension s = new Dimension(0,0,0,0,0,1,0);// Seconds
	public static final Dimension kg = new Dimension(0,0,0,0,1,0,0);// Kilos
	public static final Dimension K = new Dimension(0,0,0,1,0,0,0);// Kelvin
	public static final Dimension A = new Dimension(0,0,1,0,0,0,0);// Ampere
	public static final Dimension mol = new Dimension(0,1,0,0,0,0,0);// Mole
	public static final Dimension cd = new Dimension(1,0,0,0,0,0,0);// Candela
	
	public static final Dimension dim_null = new Dimension(0,0,0,0,0,0,0);
	
	/* SI Derived Units */
	/** The speed unit meter per second (m/s) */
	public static final Dimension m_s = new Dimension(0,0,0,0,0,-1,1);
	/** The acceleration unit meter per second squared (m/s<sup>2</sup>) */
	public static final Dimension m_s2 = new Dimension(0,0,0,0,0,-2,1);
	/** The area unit square meter(m<sup>2</sup>) */
	public static final Dimension m2 = new Dimension(0,0,0,0,0,0,2);
	/** The volume unit cubic meter (m<sup>3</sup>) */
	public static final Dimension m3 = new Dimension(0,0,0,0,0,0,3);
	/** The frequency unit hertz (Hz). <p>hertz is expressed in SI units as 1/s */
	public static final Dimension Hz = new Dimension(0,0,0,0,0,-1,0);
	/** The force unit newton (N). <p>N is expressed in SI units as m&#183;kg/s<sup>2</sup> */
	public static final Dimension N = new Dimension(0,0,0,0,0,-2,1);
	/** The pressure unit pascal (Pa). <p>Pa is equal to N/m<sup>2</sup> or is expressed in SI units as kg/m&#183;s<sup>2</sup> */
	public static final Dimension Pa = new Dimension(0,0,0,0,0,-2,-1);
	/** The energy unit joule (J). <p>joule is equal to N&#183;m or is expressed in SI units as m<sup>2</sup>&#183;kg/s<sup>2</sup>*/
	public static final Dimension J = new Dimension(0,0,0,0,1,-2,2);
	/** The power unit watt (W).<p>watt is equal to J/s or is expressed in SI units as m<sup>2</sup>&#183;kg/s<sup>3</sup> */
	public static final Dimension W = new Dimension(0,0,0,0,1,-3,2);
	/** The electric charge unit coulomb (C). <p>coulomb is expressed in SI units as s&#183;A */
	public static final Dimension C = new Dimension(0,0,1,0,0,1,0);
	/** The electric potential difference unit volt (V).<p>volt is equal to W/A or is expressed in SI units as m<sup>2</sup>&#183;kg/s<sup>3</sup>&#183;A */
	public static final Dimension V = new Dimension(0,0,-1,0,1,-3,2);
	/** The capacitance unit farad (F).<p>farad is equal to C/V or is expressed in SI units as s<sup>4</sup>&#183;A<sup>2</sup>/m<sup>2</sup>&#183;kg */
	public static final Dimension F = new Dimension(0,0,2,0,-1,4,-2);
	/** The electric resistance unit ohm. <p>ohm is equal to V/A or is expressed in SI units as m<sup>2</sup>&#183;kg/s<sup>3</sup>&#183;A<sup>2</sup> */
	public static final Dimension Ohm = new Dimension(0,0,-2,0,1,-3,2);
	/** The electric conductance unit siemens (S). <p>siemens is equal to A/V or is expressed in SI units as s<sup>3</sup>&#183;A<sup>2</sup>/m<sup>2</sup>&#183;kg */
	public static final Dimension S = new Dimension(0,0,2,0,-1,3,-2);
	/** The magnetic flux unit weber (Wb). <p>weber is equal to V&#183;s or is expressed in SI units as m<sup>2</sup>&#183;kg/s<sup>2</sup>&#183;A */
	public static final Dimension Wb = new Dimension(0,0,1,0,1,-2,2);
	/** The magnetic flux density unit tesla (T).<p>tesla is equal to Wb/m<sup>2</sup> or is expressed in SI units as kg/s<sup>2</sup>&#183;A */
	public static final Dimension T = new Dimension(0,0,-1,0,1,-2,0);
	/** The illuminance unit lux (lx).<p>lux is expressed in SI units as cd/m<sup>2</sup> */
	public static final Dimension lx = new Dimension(1,0,0,0,0,0,-2);
	/** The absorbed dose unit gray (Gy).<p>Gy is equal to J/kg or is expressed in SI units as m<sup>2</sup>/s<sup>2</sup> */
	public static final Dimension Gy = new Dimension(0,0,0,0,0,-2,2);
	/** The catalytic activity unit katal (kat).<p>katal is expressed in SI units as mol/s */
	public static final Dimension kat = new Dimension(0,1,0,0,0,-1,0);
	
	public Dimension () {
		setIs(contract);
	}
	
	public Dimension ( String name ) {
		setIs(contract);
		super.setName(name);
	}
	
	public Dimension(Integer cd, Integer mol, Integer A, Integer K, Integer kg, Integer s, Integer m) {
		
		setIs(contract);
		setName("dimension");
		
		this.addChildren(new Int("kg",kg));
		this.addChildren(new Int("m",m));
		this.addChildren(new Int("sec",s));
		this.addChildren(new Int("K",K));
		this.addChildren(new Int("A",A));
		this.addChildren(new Int("mol",mol));
		this.addChildren(new Int("cd",cd));
	}
	
	public Int getKg(){
		return (Int)this.getChildren("kg");
	}
	
	public void setKg( Int kg) {
		this.replace("kg", kg);
	}
	
	public Int getM(){
		return (Int)this.getChildren("m");
	}
	
	public void setM(Int m){
		this.replace("m", m);
	}
	
	public Int getSec(){
		return (Int)this.getChildren("sec");
	}
	
	public void setSec(Int sec) {
		this.replace("sec", sec);
	}
	
	public Int getK(){
		return (Int)this.getChildren("K");
	}
	
	public void setK(Int K) {
		this.replace("K", K);
	}
	
	public Int A(){
		return (Int)this.getChildren("A");
	}
	
	public void setA(Int A) {
		this.replace("A", A);
	}
	
	public Int getMol(){
		return (Int)this.getChildren("mol");
	}
	
	public void setMol(Int mol) {
		this.replace("mol", mol);
	}

	public Int getCd(){
		return (Int)this.getChildren("cd");
	}
	
	public void setCd(Int cd) {
		this.replace("cd", cd);
	}
	
}
