package com.ptoceti.osgi.obix.impl.back.converters;

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
