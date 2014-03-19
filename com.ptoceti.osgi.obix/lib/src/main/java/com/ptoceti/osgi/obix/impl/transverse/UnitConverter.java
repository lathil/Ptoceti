package com.ptoceti.osgi.obix.impl.transverse;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : UnitConverter.java
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


import com.ptoceti.osgi.obix.contract.Unit;


public class UnitConverter {

public static final Unit mapFromOsgi( org.osgi.util.measurement.Unit unit){
		
		Unit result = null;
		
		try {
		if( unit.equals( org.osgi.util.measurement.Unit.m)){
			result = Unit.m();
		} else if( unit.equals( org.osgi.util.measurement.Unit.s)){
			result = Unit.s();
		} else if( unit.equals( org.osgi.util.measurement.Unit.kg)){
			result = Unit.kg();
		} else if( unit.equals( org.osgi.util.measurement.Unit.K)){
			result = Unit.K();
		} else if( unit.equals( org.osgi.util.measurement.Unit.A)){
			result = Unit.m();
		} else if( unit.equals( org.osgi.util.measurement.Unit.mol)){
			result = Unit.mol();
		} else if( unit.equals( org.osgi.util.measurement.Unit.cd)){
			result = Unit.cd();
		} else if( unit.equals( org.osgi.util.measurement.Unit.m_s)){
			result = Unit.ms();
		} else if( unit.equals( org.osgi.util.measurement.Unit.m_s2)){
			result = Unit.ms2();
		} else if( unit.equals( org.osgi.util.measurement.Unit.m2)){
			result = Unit.m2();
		} else if( unit.equals( org.osgi.util.measurement.Unit.m3)){
			result = Unit.m3();
		} else if( unit.equals( org.osgi.util.measurement.Unit.Hz)){
			result = Unit.Hz();
		} else if( unit.equals( org.osgi.util.measurement.Unit.N)){
			result = Unit.N();
		} else if( unit.equals( org.osgi.util.measurement.Unit.Pa)){
			result = Unit.Pa();
		} else if( unit.equals( org.osgi.util.measurement.Unit.J)){
			result = Unit.J();
		} else if( unit.equals( org.osgi.util.measurement.Unit.W)){
			result = Unit.W();
		} else if( unit.equals( org.osgi.util.measurement.Unit.C)){
			result = Unit.C();
		} else if( unit.equals( org.osgi.util.measurement.Unit.V)){
			result = Unit.V();
		} else if( unit.equals( org.osgi.util.measurement.Unit.F)){
			result = Unit.F();
		} else if( unit.equals( org.osgi.util.measurement.Unit.Ohm)){
			result = Unit.Ohm();
		} else if( unit.equals( org.osgi.util.measurement.Unit.S)){
			result = Unit.S();
		} else if( unit.equals( org.osgi.util.measurement.Unit.Wb)){
			result = Unit.Wb();
		} else if( unit.equals( org.osgi.util.measurement.Unit.T)){
			result = Unit.T();
		} else if( unit.equals( org.osgi.util.measurement.Unit.lx)){
			result = Unit.lx();
		} else if( unit.equals( org.osgi.util.measurement.Unit.Gy)){
			result = Unit.Gy();
		} else if( unit.equals( org.osgi.util.measurement.Unit.kat)){
			result = Unit.kat();
		}
		
		} catch (Exception e) {
			
		}
		
		return result;
		
	}
}
