package com.ptoceti.osgi.obix.impl.back.converters;

import com.ptoceti.osgi.control.Switch;
import com.ptoceti.osgi.obix.custom.contract.SwitchPoint;
import com.ptoceti.osgi.obix.object.Bool;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Status;
import com.ptoceti.osgi.obix.object.Val;

public class SwitchConverter  implements OsgiObixConverter<SwitchPoint>{

	@Override
	public SwitchPoint toObix(Object in) {
		SwitchPoint obixSwitch = new SwitchPoint(null, ((Switch) in).getState());
		obixSwitch.setStatus(Status.OK);
		return obixSwitch;
	}

	@Override
	public Val toBaseObix(Object in){
		Bool obixSwitch = new Bool(null, ((Switch) in).getState());
		obixSwitch.setStatus(Status.OK);
		obixSwitch.setIs(SwitchPoint.contract);
		return obixSwitch;
	}
	
	@Override
	public Object fromObix(SwitchPoint in) {
		return new Switch( in.getVal() != null ? ((Boolean)in.getVal()).booleanValue(): false);
	}

	@Override
	public String getObixClassName() {
		return SwitchPoint.class.getName();
	}

	@Override
	public String getOsgiClassName() {
		return Switch.class.getName();
	}

	@Override
	public Contract getObixContract() {
		return SwitchPoint.contract;
	}

	@Override
	public Object fromBaseObix(Val in) {
		return new Switch( in.getVal() != null ? ((Boolean)in.getVal()).booleanValue(): false);
	}
}
