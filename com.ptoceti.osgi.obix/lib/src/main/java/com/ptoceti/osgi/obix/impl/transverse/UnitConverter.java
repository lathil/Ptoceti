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


import com.ptoceti.osgi.control.ExtendedUnit;
import com.ptoceti.osgi.obix.contract.Unit;


public class UnitConverter {
	
public static final ExtendedUnit mapFromObix( Unit unit ){
	ExtendedUnit result = ExtendedUnit.findUnit(unit.getName());
	
	return result;
}

public static final Unit mapFromOsgi( ExtendedUnit unit){
		
		Unit result = null;
		
		try {
		if( unit.equals( ExtendedUnit.unity)){
			result = Unit.unity();
		} else if( unit.equals( ExtendedUnit.m)){
			result = Unit.m();
		} else if( unit.equals( ExtendedUnit.s)){
			result = Unit.s();
		} else if( unit.equals( ExtendedUnit.kg)){
			result = Unit.kg();
		} else if( unit.equals( ExtendedUnit.K)){
			result = Unit.K();
		} else if( unit.equals( ExtendedUnit.A)){
			result = Unit.m();
		} else if( unit.equals( ExtendedUnit.mol)){
			result = Unit.mol();
		} else if( unit.equals( ExtendedUnit.cd)){
			result = Unit.cd();
		} else if( unit.equals( ExtendedUnit.m_s)){
			result = Unit.ms();
		} else if( unit.equals( ExtendedUnit.m_s2)){
			result = Unit.ms2();
		} else if( unit.equals( ExtendedUnit.m2)){
			result = Unit.m2();
		} else if( unit.equals( ExtendedUnit.m3)){
			result = Unit.m3();
		} else if( unit.equals( ExtendedUnit.Hz)){
			result = Unit.Hz();
		} else if( unit.equals( ExtendedUnit.N)){
			result = Unit.N();
		} else if( unit.equals( ExtendedUnit.Pa)){
			result = Unit.Pa();
		} else if( unit.equals( ExtendedUnit.J)){
			result = Unit.J();
		} else if( unit.equals( ExtendedUnit.W)){
			result = Unit.W();
		} else if( unit.equals( ExtendedUnit.C)){
			result = Unit.C();
		} else if( unit.equals( ExtendedUnit.V)){
			result = Unit.V();
		} else if( unit.equals( ExtendedUnit.F)){
			result = Unit.F();
		} else if( unit.equals( ExtendedUnit.Ohm)){
			result = Unit.Ohm();
		} else if( unit.equals( ExtendedUnit.S)){
			result = Unit.S();
		} else if( unit.equals( ExtendedUnit.Wb)){
			result = Unit.Wb();
		} else if( unit.equals( ExtendedUnit.T)){
			result = Unit.T();
		} else if( unit.equals( ExtendedUnit.Lx)){
			result = Unit.lx();
		} else if( unit.equals( ExtendedUnit.Gy)){
			result = Unit.Gy();
		} else if( unit.equals( ExtendedUnit.kat)){
			result = Unit.kat();
		} else if( unit.equals( ExtendedUnit.percent)){
			result = Unit.percent();
		} else if( unit.equals( ExtendedUnit.celsius)){
			result = Unit.celsius();
		}
		
		} catch (Exception e) {
			
		}
		
		return result;
		
	}
}
