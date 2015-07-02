package com.ptoceti.osgi.obix.impl.back.converters;


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
