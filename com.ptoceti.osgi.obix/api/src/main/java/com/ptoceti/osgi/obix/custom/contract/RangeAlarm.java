package com.ptoceti.osgi.obix.custom.contract;

import java.io.Serializable;

import com.ptoceti.osgi.obix.contract.AckAlarm;
import com.ptoceti.osgi.obix.contract.PointAlarm;
import com.ptoceti.osgi.obix.contract.StatefulAlarm;
import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Val;

public class RangeAlarm extends AckAlarm implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -201648067493668424L;
	public static final Contract contract = new Contract(" ptoceti:RangeAlarm");
	
	public RangeAlarm(){
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
	
	public Val getMaxValue(){
		return (Val)(this.getChildren("maxValue"));
	}
	
	public void setMaxValue(Val maxValue){
		maxValue.setName("maxValue");
		this.addChildren(maxValue);
	}
	
	public Val getMinValue(){
		return (Val)(this.getChildren("minValue"));
	}
	
	public void setMinValue(Val minValue){
		minValue.setName("minValue");
		this.addChildren(minValue);
	}

}
