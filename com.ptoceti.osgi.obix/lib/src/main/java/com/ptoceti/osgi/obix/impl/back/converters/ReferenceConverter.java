package com.ptoceti.osgi.obix.impl.back.converters;



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
