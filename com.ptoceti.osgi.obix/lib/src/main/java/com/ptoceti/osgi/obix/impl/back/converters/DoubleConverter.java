package com.ptoceti.osgi.obix.impl.back.converters;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : DoubleConverter.java
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
import com.ptoceti.osgi.obix.object.Real;
import com.ptoceti.osgi.obix.object.Status;
import com.ptoceti.osgi.obix.object.Val;

public class DoubleConverter implements OsgiObixConverter<Real>{

	@Override
	public Real toObix(Object in) {
		Real obixReal = new Real(null, ((Double) in).doubleValue());
		obixReal.setStatus(Status.OK);
		return obixReal;
	}

	@Override
	public Val toBaseObix(Object in){
		return toObix(in);
	}
	
	@Override
	public Object fromObix(Real in) {
		return in.getVal() != null ? in.getVal() : null;
	}

	@Override
	public String getObixClassName() {
		return Real.class.getName();
	}

	@Override
	public String getOsgiClassName() {
		return Double.class.getName();
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
