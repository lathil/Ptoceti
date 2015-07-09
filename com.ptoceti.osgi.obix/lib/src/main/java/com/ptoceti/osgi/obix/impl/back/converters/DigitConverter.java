package com.ptoceti.osgi.obix.impl.back.converters;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : DigitConverter.java
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

import com.ptoceti.osgi.obix.custom.contract.DigitPoint;
import com.ptoceti.osgi.obix.object.Bool;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Status;
import com.ptoceti.osgi.obix.object.Val;
import com.ptoceti.osgi.control.Digit;

public class DigitConverter implements OsgiObixConverter<DigitPoint>{

	@Override
	public DigitPoint toObix(Object in) {
		DigitPoint obixState = new DigitPoint(null, ((Digit) in).getState());
		obixState.setStatus(Status.OK);
		return obixState;
	}
	
	@Override
	public Val toBaseObix(Object in){
		Bool obixState = new Bool(null, ((Digit) in).getState());
		obixState.setStatus(Status.OK);
		obixState.setIs(DigitPoint.contract);
		return obixState;
	}

	@Override
	public Object fromObix(DigitPoint in) {
		return new Digit( in.getVal() != null ? ((Boolean)in.getVal()).booleanValue(): false);
	}
	
	@Override
	public Object fromBaseObix(Val in){
		return new Digit( in.getVal() != null ? ((Boolean)in.getVal()).booleanValue(): false);
	}

	@Override
	public String getObixClassName() {
		return DigitPoint.class.getName();
	}

	@Override
	public String getOsgiClassName() {
		return Digit.class.getName();
	}

	@Override
	public Contract getObixContract() {
		return DigitPoint.contract;
	}

}
