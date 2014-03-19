package com.ptoceti.osgi.obix.object;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Api
 * FILENAME : Abstime.java
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


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Abstime extends Val {

	private static final Contract contract = new Contract("obix:abstime");
	
	protected Abstime max;
	protected Abstime min;

	public Abstime() {
		super();
	}
	
	public Abstime( Obj model) {
		super(model);
	}
	
	public Abstime( Abstime model) {
		super(model);
		if( model.min != null) min = new Abstime(model.min);
		if( model.max != null) max = new Abstime(model.max);
		if( model.val != null) val = new Date(((Date)model.val).getTime());
	}

	public Abstime(String name) {
		super(name);
	}

	public Abstime(String name, Long millis) {
		super(name, new Date(millis.longValue()));
	}

	public Abstime(String name, Date millis) {
		super(name, millis);
	}
	
	public void setMax(Abstime max) {
		this.max = max;
	}

	public Abstime getMax() {
		return max;
	}

	public void setMin(Abstime min) {
		this.min = min;
	}

	public Abstime getMin() {
		return min;
	}

	@Override
	public String encodeVal() {

		if (this.getVal() != null) {
			
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
			String output = df.format((Date)this.getVal());

			int inset0 = 5;
			int inset1 = 5;

			String s0 = output.substring(0, output.length() - inset0);
			String s1 = output.substring(output.length() - inset1,
					output.length());
			String s2 = s1.substring(0, 3) + ":" + s1.substring(3, 5);

			String result = s0 + s2;


			return result;
		} else
			return null;
	}

	@Override
	public void decodeVal(String val) {

		if (val != null) {

			// this is zero time so we need to add that TZ indicator for
			if (val.endsWith("Z")) {
				val = val.substring(0, val.length() - 1) + "+00:00";
			} 

			// Get part of string after minutes.
			String afterMinPart = val.substring(val.lastIndexOf("T") + 9);
			boolean hasPointMillisSep = false;
			boolean hasCommaMillisSep = false;
			
			StringBuffer afterMinPartPattern = new StringBuffer();
			if( afterMinPart.startsWith(".")) {hasPointMillisSep = true; afterMinPartPattern.append("."); }
			if( afterMinPart.startsWith(",")) {hasCommaMillisSep = true; afterMinPartPattern.append(",");}
			
			int timeZonePart = -1;
			timeZonePart = afterMinPart.indexOf("+");
			if( timeZonePart < 0) timeZonePart = afterMinPart.indexOf("-");
			
			if( hasPointMillisSep || hasCommaMillisSep){			
				for( int i = 0; i < (timeZonePart < 0 ?  afterMinPart.length() - 1 : timeZonePart - 1) ; i++){
					afterMinPartPattern.append("S");
				}
			}
			
		    int hasTimeZoneStep = afterMinPart.indexOf(":", timeZonePart);
		    
		    if( hasTimeZoneStep > 0) afterMinPartPattern.append("XXX");
		    else if( timeZonePart > 0){
		    	if( (afterMinPart.length() - timeZonePart) == 4 ) afterMinPartPattern.append("XX");
		    	else if( (afterMinPart.length() - timeZonePart) == 2 ) afterMinPartPattern.append("X");
		    }
			
			// NOTE: SimpleDateFormat uses GMT[-+]hh:mm for the TZ which breaks
			// things a bit. Before we go on we have to repair this.
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss" + afterMinPartPattern.toString());

			Date parsedDate = null;

			try {
				parsedDate = df.parse(val);
				this.setVal(parsedDate);
			} catch (Exception e) {
				System.err.println("Error decoding val: " + val + ". " + e);
			}
		}
	}
	
	@Override
	public Obj cloneEmpty() {
		return new Abstime();
	}
	
	public Val getDiff(Val val) {
		Abstime result = null;
		if( val instanceof Abstime){
			
			result = new Abstime(this);
			result.setVal(new Date(((Date)getVal()).getTime() - ((Date)((Abstime)val).getVal()).getTime()));
		
		}
		
		return result;
	}
	
	@Override
	public Contract getContract(){
		return contract;
	}
}
