package com.ptoceti.osgi.obix.impl.back.converters;

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
