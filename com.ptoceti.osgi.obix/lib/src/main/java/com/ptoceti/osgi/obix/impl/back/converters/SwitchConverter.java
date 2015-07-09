package com.ptoceti.osgi.obix.impl.back.converters;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : SwitchConverter.java
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
