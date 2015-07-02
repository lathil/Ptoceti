package com.ptoceti.osgi.obix.impl.back.converters;

import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Status;
import com.ptoceti.osgi.obix.object.Str;
import com.ptoceti.osgi.obix.object.Val;

public class StringConverter implements OsgiObixConverter<Str>{

	@Override
	public Str toObix(Object in) {
		Str obixStr = new Str(null, (String) in);
		obixStr.setStatus(Status.OK);
		return obixStr;
	}

	@Override
	public Val toBaseObix(Object in){
		return toObix(in);
	}
	
	@Override
	public Object fromObix(Str in) {
		return in.getVal() != null ? in.getVal() : null;
	}

	@Override
	public String getObixClassName() {
		return Str.class.getName();
	}

	@Override
	public String getOsgiClassName() {
		return String.class.getName();
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
