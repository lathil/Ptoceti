package com.ptoceti.osgi.obix.contract;

import java.io.Serializable;

import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Str;

public class AlarmAckOut extends Obj implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7740960402459010406L;
	public static final Contract contract = new Contract("obix:AlarmAckOut");

	public AlarmAckOut() {
		this.setIs(contract);
	}
	
	public Alarm getAlarm(){
		return (Alarm)(this.getChildren("alarm"));
	}
	
	public void setAlarm(Alarm alarm){
		alarm.setName("alarm");
		this.addChildren(alarm);
	}
}
