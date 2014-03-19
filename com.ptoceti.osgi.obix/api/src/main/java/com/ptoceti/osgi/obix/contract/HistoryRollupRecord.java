package com.ptoceti.osgi.obix.contract;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Api
 * FILENAME : HistoryRollupRecord.java
 * 
 * This file is part of the Ptoceti project. More information about
 * this project can be found here: http://www.ptoceti.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2013 - 2014 ptoceti
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


import java.io.Serializable;

import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Real;

public class HistoryRollupRecord extends Obj implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2177732596623383138L;

	public static final Contract contract = new Contract("obix:HistoryRollupRecord");
	
	public HistoryRollupRecord() {
		super();
		setIs(contract);
	}
	
	public void setStart(Abstime timeStamp){
		Abstime value = new Abstime(timeStamp);
		value.setName("start");
		addChildren(value);
	}
	
	public Abstime getStart() {
		return (Abstime)getChildren("start");
	}
	
	public void setEnd(Abstime timeStamp){
		Abstime value = new Abstime(timeStamp);
		value.setName("end");
		addChildren(value);
	}
	
	public Abstime getEnd() {
		return (Abstime)getChildren("end");
	}
	
	
	public void setMin(Real min){
		Real value = new Real(min);
		value.setName("min");
		addChildren(value);
	}
	
	public Real getMin() {
		return (Real)getChildren("min");
	}
	
	public void setMax(Real max){
		Real value = new Real(max);
		value.setName("max");
		addChildren(value);
	}
	
	public Real getMax() {
		return (Real)getChildren("max");
	}
	
	public void seAvg(Real avg){
		Real value = new Real(avg);
		value.setName("avg");
		addChildren(value);
	}
	
	public Real getAvg() {
		return (Real)getChildren("avg");
	}
	
	public void setSum(Real sum){
		Real value = new Real(sum);
		value.setName("sum");
		addChildren(value);
	}
	
	public Real getSum() {
		return (Real)getChildren("sum");
	}
}
