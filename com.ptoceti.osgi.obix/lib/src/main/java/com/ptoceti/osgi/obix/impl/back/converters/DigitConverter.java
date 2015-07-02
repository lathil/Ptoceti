package com.ptoceti.osgi.obix.impl.back.converters;

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
