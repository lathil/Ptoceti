package com.ptoceti.osgi.obix.contract;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Api
 * FILENAME : HistoryQueryOut.java
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
import com.ptoceti.osgi.obix.object.Int;
import com.ptoceti.osgi.obix.object.List;
import com.ptoceti.osgi.obix.object.Obj;

public class HistoryQueryOut extends Obj implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5979283686214316850L;

	public static final Contract contract = new Contract("obix:HistoryQueryOut");
	
	public HistoryQueryOut() {
		setIs(contract);
		
		List data = new List("data");
		data.setOf(HistoryRecord.contract);
		
		addChildren(data);
	}
	
	public void setCount(int count) {
		Int value = new Int("count", count);
		addChildren(value);
	}
	
	public Int getCount(){
		Obj count = getChildren("count");
		if( count != null && count instanceof Int){
			return (Int)count;
		} else return null;
	}
	
	public void addToDataList(HistoryRecord record) {
		List data = (List)getChildren("data");
		if ( data != null) {
			data.getChildrens().add(record);
		}
	}
	
	public List getDataList(){
		List data = (List)getChildren("data");
		return data;
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
}
