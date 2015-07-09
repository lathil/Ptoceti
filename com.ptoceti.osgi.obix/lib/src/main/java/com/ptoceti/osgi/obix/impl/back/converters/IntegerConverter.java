package com.ptoceti.osgi.obix.impl.back.converters;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : IntegerConverter.java
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

import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Int;
import com.ptoceti.osgi.obix.object.Status;
import com.ptoceti.osgi.obix.object.Val;

public class IntegerConverter implements OsgiObixConverter<Int>{

	@Override
	public Int toObix(Object in) {
		Int obixInt = new Int(null, ((Integer) in).intValue());
		obixInt.setStatus(Status.OK);
		return obixInt;
	}

	@Override
	public Val toBaseObix(Object in){
		return toObix(in);
	}
	
	@Override
	public Object fromObix(Int in) {
		return in.getVal() != null ? in.getVal() : null;
	}

	@Override
	public String getObixClassName() {
		return Int.class.getName();
	}

	@Override
	public String getOsgiClassName() {
		return Integer.class.getName();
	}

	@Override
	public Object fromBaseObix(Val in) {
		return in.getVal() != null ? in.getVal() : null;
	}

	@Override
	public Contract getObixContract() {
		return null;
	}

}
