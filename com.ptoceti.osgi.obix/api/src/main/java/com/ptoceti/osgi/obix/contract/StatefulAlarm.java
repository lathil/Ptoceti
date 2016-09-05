package com.ptoceti.osgi.obix.contract;

import java.io.Serializable;

import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Contract;

public class StatefulAlarm extends Alarm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7394150681526199763L;
	
	public static final Contract contract = new Contract("obix:StatefulAlarm");
	
	public StatefulAlarm() {
		setIs(contract);
	}
	
	public Abstime getNormalTimestamp(){
		return (Abstime)(this.getChildren("normalTimestamp"));
	}
	
	public void setNormalTimestamp(Abstime normalTimeStamp){
		normalTimeStamp.setName("normalTimeStamp");
		this.addChildren(normalTimeStamp);
	}
}
