package com.ptoceti.osgi.obix.contract;

import java.io.Serializable;

import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Str;

public class AlarmAckIn extends Obj implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 26928255562009584L;
	public static final Contract contract = new Contract("obix:AlarmAckIn");

	public AlarmAckIn() {
		this.setIs(contract);
	}
	
	public Str getAckuser(){
		if( this.getChildren("ackUser") != null){
			return (Str)(this.getChildren("ackUser"));
		}
		return null;
	}
}
