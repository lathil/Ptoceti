package com.ptoceti.osgi.obix.contract;

import java.io.Serializable;

import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Ref;

public class Alarm extends Obj implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8815225627070640826L;
	public static final Contract contract = new Contract("obix:Alarm");
	
	public Alarm() {
		setIs(contract);
	}
	
	public Ref getSource(){
		return (Ref)(this.getChildren("source"));
	}
	
	public void setSource(Ref source){
		source.setName("source");
		this.addChildren(source);
	}
	
	public Abstime getTimestamp(){
		return (Abstime)(this.getChildren("timestamp"));
	}
	
	public void setTimestamp(Abstime timeStamp){
		timeStamp.setName("timestamp");
		addChildren(timeStamp);
	}
}
