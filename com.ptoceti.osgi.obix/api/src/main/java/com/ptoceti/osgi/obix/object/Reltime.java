package com.ptoceti.osgi.obix.object;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Api
 * FILENAME : Reltime.java
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


import java.util.Date;

public class Reltime extends Val {

	// private static final String xsduration =
	// "-?P(?=\\d+|T)(\\d+Y)?(\\d+M)?(\\d+D)?(T(?=\\d+)(\\d+H)?(\\d+M)?(\\d+S)?)?";

	private static final Contract contract = new Contract("obix:reltime");
	
	protected Reltime max;
	protected Reltime min;

	public Reltime() {
		super();
	}
	
	public Reltime( Obj model){
		super(model);
	}
	
	public Reltime( Reltime model){
		super(model);
		if( model.min != null) min = new Reltime(model.min);
		if( model.max != null) min = new Reltime(model.max);
		if( model.val != null) val = new Long( ((Long)model.val).longValue());
	}

	public Reltime(String name) {
		super(name);
	}

	public Reltime(String name, Long value) {
		super(name, value);
	}
	
	public void setMax(Reltime max) {
		this.max = max;
	}

	public Reltime getMax() {
		return max;
	}

	public void setMin(Reltime min) {
		this.min = min;
	}

	public Reltime getMin() {
		return min;
	}

	@Override
	public String encodeVal() {

		String result = null;

		if (getVal() != null) {
			StringBuffer sbduration = new StringBuffer();
			
			if (((Long) getVal()).longValue() < 0)
				sbduration.append("-");
			

			long duration = Math.abs(((Long) getVal()).longValue());
			long resthours = duration % (24 * 3600 * 1000);
			long nbdays = (duration - resthours) / (24 * 3600 * 1000);
			long restmins = resthours % (3600 * 1000);
			long nbhours = (resthours - restmins) / ( 3600 * 1000);
			long restsec = restmins % (60 * 1000);
			long nbmin = (restmins - restsec) / (60 * 1000);
			long nbsec = restsec / 1000;

			sbduration.append("P");
			if (nbdays > 0)
				sbduration.append(Long.toString(nbdays) + "D");			
			sbduration.append("T");
			if (nbhours > 0)
				sbduration.append(Long.toString(nbhours) + "H");
			if (nbmin > 0)
				sbduration.append(Long.toString(nbmin) + "M");
			if (nbsec > 0)
				sbduration.append(Long.toString(nbsec) + "S");
			
			result = sbduration.toString();
		} 
		
		return result;

	}

	/**
	 * The time interval is specified in the following form "PnYnMnDTnHnMnSn" P
	 * indicates the period (required) nY indicates the number of years nM
	 * indicates the number of months nD indicates the number of days
	 */

	@Override
	public void decodeVal(String value) {

		int index = 0;
		boolean neg = false;
		if (value.startsWith("-")) {
			neg = true;
			index++;
		}

		long duration = 0;

		int pIndex = value.indexOf("P", index);
		int yIndex = value.indexOf("Y", pIndex);
		if( yIndex < 0) yIndex = pIndex;
		int MIndex = value.indexOf("M", yIndex);
		if( MIndex < 0) MIndex = yIndex;
		int dIndex = value.indexOf("D", MIndex);
		if( dIndex < 0) dIndex = MIndex;
		
		int tIndex = value.indexOf("T", dIndex); 
		int hIndex = value.indexOf("H", tIndex);
		if( hIndex < 0) hIndex = tIndex;
		int mIndex = value.indexOf("M", hIndex);
		if( mIndex < 0) mIndex = hIndex;
		int sIndex = value.indexOf("S", mIndex);
		if( sIndex < 0) sIndex = mIndex;
		
		// never uses year or month, start at days
		
		if (dIndex > MIndex) {
			int nbDays = Integer.parseInt(value.substring(MIndex + 1, dIndex));
			duration = +nbDays * 24 * 3600 * 1000;
		}
		
		if (hIndex > tIndex) {
			int nbHours = Integer.parseInt(value.substring(tIndex + 1, hIndex));
			duration = +nbHours * 3600 * 1000;
		}

		if (mIndex > hIndex) {
			int nbMin = Integer.parseInt(value.substring(hIndex + 1, mIndex));
			duration = +nbMin * 60 * 1000;
		}

		if (sIndex > mIndex) {
			int nbSec = Integer.parseInt(value.substring(mIndex + 1, sIndex));
			duration = +nbSec * 1000;
		}

		if (neg && (duration > 0))
			duration = duration * -1;

		setVal(new Long(duration));
	}
	
	@Override
	public Obj cloneEmpty() {
		return new Reltime();
	}
	
	public Val getDiff(Val val) {
		Reltime result = null;
		if( val instanceof Abstime){
			
			result = new Reltime(this);
			result.setVal(new Long(((Long)getVal()).longValue() - ((Long)((Reltime)val).getVal()).longValue()));
		
		}
		
		return result;
	}
	
	@Override
	public Contract getContract(){
		return contract;
	}
}
