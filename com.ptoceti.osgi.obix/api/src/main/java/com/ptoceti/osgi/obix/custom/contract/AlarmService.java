package com.ptoceti.osgi.obix.custom.contract;

import java.io.Serializable;

import com.ptoceti.osgi.obix.contract.Alarm;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Op;
import com.ptoceti.osgi.obix.object.Ref;

public class AlarmService extends Obj implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4045287588147806803L;
	/**
	 * 
	 */
	

	public static final Contract contract = new Contract("ptoceti:AlarmService");
	
	public AlarmService() {
		setIs(contract);
		
		Op make = new Op("make", Ref.contract, Alarm.contract);
		this.addChildren(make);
	}
	
	public AlarmService(String name) {
		super(name);
		
		setIs(contract);
		Op make = new Op("make", Ref.contract, Alarm.contract);
		this.addChildren(make);
	}
	
	public Op getMake(){
		return (Op)(this.getChildren("make"));
	}

}
