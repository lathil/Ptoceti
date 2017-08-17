package com.ptoceti.osgi.obix.impl.back.converters;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : ReferenceConverter.java
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



import com.ptoceti.osgi.control.Reference;
import com.ptoceti.osgi.obix.contract.Unit;
import com.ptoceti.osgi.obix.custom.contract.ReferencePoint;
import com.ptoceti.osgi.obix.impl.transverse.UnitConverter;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Real;
import com.ptoceti.osgi.obix.object.Status;
import com.ptoceti.osgi.obix.object.Val;

public class ReferenceConverter implements OsgiObixConverter<ReferencePoint>{

	@Override
	public ReferencePoint toObix(Object in) {
		ReferencePoint obixReference = new ReferencePoint(null, ((Reference) in).getValue());
		Unit unit = UnitConverter.mapFromOsgi(((Reference) in).getUnit());
		obixReference.setUnit(unit.getHref());
		obixReference.setStatus(Status.OK);
		obixReference.setMax(Double.valueOf(((Reference) in).getMax()));
		obixReference.setMin(Double.valueOf(((Reference) in).getMin()));
		return obixReference;
	}
	
	@Override
	public Val toBaseObix(Object in){
		Real obixReference = new Real(null, ((Reference) in).getValue());
		Unit unit = UnitConverter.mapFromOsgi(((Reference) in).getUnit());
		obixReference.setUnit(unit.getHref());
		obixReference.setStatus(Status.OK);
		obixReference.setMax(Double.valueOf(((Reference) in).getMax()));
		obixReference.setMin(Double.valueOf(((Reference) in).getMin()));
		obixReference.setIs(ReferencePoint.contract);
		obixReference.setWritable(Boolean.TRUE);
		return obixReference;
	}

	@Override
	public Object fromObix(ReferencePoint in) {
		Reference result = new Reference( (double) in.getVal(),  UnitConverter.mapFromObix( Unit.getUnit(in.getUnit())));
		return result;
	}

	@Override
	public String getObixClassName() {
		return ReferencePoint.class.getName();
	}

	@Override
	public String getOsgiClassName() {
		return Reference.class.getName();
	}
	
	@Override
	public Contract getObixContract() {
		return ReferencePoint.contract;
	}

	@Override
	public Object fromBaseObix(Val in) {
		Reference result = new Reference( (double) in.getVal(),  UnitConverter.mapFromObix( Unit.getUnit(((Real)in).getUnit())));
		return result;
	}

}
