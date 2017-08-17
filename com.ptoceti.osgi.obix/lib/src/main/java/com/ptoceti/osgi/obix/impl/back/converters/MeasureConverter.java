package com.ptoceti.osgi.obix.impl.back.converters;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : MeasureConverter.java
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


import com.ptoceti.osgi.control.Measure;
import com.ptoceti.osgi.control.StatusCode;
import com.ptoceti.osgi.obix.contract.Unit;
import com.ptoceti.osgi.obix.custom.contract.MeasurePoint;
import com.ptoceti.osgi.obix.impl.transverse.UnitConverter;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Real;
import com.ptoceti.osgi.obix.object.Status;
import com.ptoceti.osgi.obix.object.Val;

public class MeasureConverter implements OsgiObixConverter<MeasurePoint>{

	@Override
	public MeasurePoint toObix(Object in) {
		MeasurePoint obixMeasure = new MeasurePoint(null, ((Measure) in).getValue());
		Unit unit = UnitConverter.mapFromOsgi(((Measure) in).getUnit());
		obixMeasure.setUnit(unit.getHref());
		StatusCode statusCode = ((Measure) in).getStatus();
		obixMeasure.setStatus(statusCode == StatusCode.OK ? Status.OK : Status.FAULT);
		return obixMeasure;
	}

	@Override
	public Val toBaseObix(Object in){
		Real obixMeasure = new Real(null, ((Measure) in).getValue());
		Unit unit = UnitConverter.mapFromOsgi(((Measure) in).getUnit());
		obixMeasure.setUnit(unit.getHref());
		StatusCode statusCode = ((Measure) in).getStatus();
		obixMeasure.setStatus(statusCode == StatusCode.OK ? Status.OK : Status.FAULT);
		obixMeasure.setIs(MeasurePoint.contract);
		obixMeasure.setWritable(Boolean.FALSE);
		return obixMeasure;
	}
	
	@Override
	public Object fromObix(MeasurePoint in) {
		Measure result = new Measure( (double) in.getVal(),  UnitConverter.mapFromObix( Unit.getUnit(in.getUnit())));
		return result;
	}

	@Override
	public String getObixClassName() {
		return MeasurePoint.class.getName();
	}
	
	@Override
	public Contract getObixContract() {
		return MeasurePoint.contract;
	}

	@Override
	public String getOsgiClassName() {
		return Measure.class.getName();
	}

	@Override
	public Object fromBaseObix(Val in) {
		Measure result = new Measure( (double) in.getVal(),  UnitConverter.mapFromObix( Unit.getUnit(((Real)in).getUnit())));
		return result;
	}

}
