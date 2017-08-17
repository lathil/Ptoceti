package com.ptoceti.osgi.obix.custom.contract;

import java.io.Serializable;

import com.ptoceti.osgi.obix.contract.History;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Op;
import com.ptoceti.osgi.obix.object.Ref;

public class HistoryService extends Obj implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5692719973806387925L;

	public static final Contract contract = new Contract("ptoceti:HistoryService");
	
	public HistoryService() {
		setIs(contract);
		
		Op make = new Op("make", Ref.contract, History.contract);
		this.addChildren(make);
	}
	
	public HistoryService(String name) {
		super(name);
		
		setIs(contract);
		Op make = new Op("make", Ref.contract, History.contract);
		this.addChildren(make);
	}
	
	public Op getMake(){
		return (Op)(this.getChildren("make"));
	}

}
