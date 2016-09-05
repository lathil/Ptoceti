package com.ptoceti.osgi.obix.contract;

import java.io.Serializable;

import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Val;

public class PointAlarm extends Alarm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5909637690910022020L;
	public static final Contract contract = new Contract("obix:PointAlarm");
	
	public PointAlarm(){
		setIs(contract);
	}
	
	public Val getAlarmValue(){
		return (Val)(this.getChildren("alarmValue"));
	}
	
	public void setAlarmValue(Val alarmValue){
		alarmValue.setName("alarmValue");
		this.addChildren(alarmValue);
	}
}
