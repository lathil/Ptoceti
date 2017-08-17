package com.ptoceti.osgi.obix.custom.contract;

import java.io.Serializable;

import com.ptoceti.osgi.obix.contract.AckAlarm;
import com.ptoceti.osgi.obix.contract.PointAlarm;
import com.ptoceti.osgi.obix.contract.StatefulAlarm;
import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Bool;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Val;

public class DigitAlarm extends AckAlarm implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3756187866600049957L;
	/**
	 * 
	 */
	
	public static final Contract contract = new Contract("ptoceti:DigitAlarm");

	
	public DigitAlarm(){
		
		super();
		getIs().addUri(contract.getUris());
		getIs().addUri(StatefulAlarm.contract.getUris());
		getIs().addUri(PointAlarm.contract.getUris());
	}
	
	public Abstime getNormalTimestamp(){
		return (Abstime)(this.getChildren("normalTimestamp"));
	}
	
	public void setNormalTimestamp(Abstime normalTimeStamp){
		normalTimeStamp.setName("normalTimeStamp");
		this.addChildren(normalTimeStamp);
	}
	
	public Val getAlarmValue(){
		return (Val)(this.getChildren("alarmValue"));
	}
	
	public void setAlarmValue(Val alarmValue){
		alarmValue.setName("alarmValue");
		this.addChildren(alarmValue);
	}
	
	public Bool getAlarmLevel(){
		return (Bool)(this.getChildren("alarmLevel"));
	}
	
	public void setAlarmLevel(Bool alarmLevel){
		alarmLevel.setName("alarmLevel");
		this.addChildren(alarmLevel);
	}
	
	
}
