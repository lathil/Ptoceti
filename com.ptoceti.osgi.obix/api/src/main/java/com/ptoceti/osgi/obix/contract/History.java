package com.ptoceti.osgi.obix.contract;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Api
 * FILENAME : History.java
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
import java.util.Calendar;

import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Int;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Op;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.resources.HistoryQueryResource;

public class History extends Obj implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7762519492124253871L;

	public static final Contract contract = new Contract("obix:History");
	
	public History(){
		setIs(contract);
		
		Int value = new Int("count", 0);
		addChildren(value);
		
		long now = Calendar.getInstance().getTimeInMillis();
		
		Abstime start = new Abstime("start",now);
		addChildren(start);
		
		Abstime end = new Abstime("end", now);
		addChildren(end);
		
		
		Op query = new Op("query", HistoryFilter.contract, HistoryQueryOut.contract);
		query.setHref(new Uri("uri", HistoryQueryResource.baseuri));
		this.addChildren(query);
		
		Op rollup = new Op("rollup", HistoryRollupIn.contract, HistoryRollupOut.contract);
		this.addChildren(rollup);
		
		//Feed feed = new Feed("feed", HistoryFilter.contract, HistoryRecord.contract);
		
	}
	
	public History(String name){
		this();
		setName(name);
	}
	
	public void setCount(int count) {
		Int value = new Int("count", count);
		replace("count", value);
	}
	
	public Int getCount(){
		Obj count = getChildren("count");
		if( count != null && count instanceof Int){
			return (Int)count;
		} else return null;
	}
	
	public void setStart(Abstime timeStamp){
		replace("start", timeStamp);
	}
	
	public Abstime getStart() {
		return (Abstime)getChildren("start");
	}
	
	public void setEnd(Abstime timeStamp){
		replace("end", timeStamp);
	}
	
	public Abstime getEnd() {
		return (Abstime)getChildren("end");
	}

	public Op getQuery() {
		return (Op)getChildren("query");
	}
	
	public Op getRollup(){
		return (Op)getChildren("rollup");
	}
}
