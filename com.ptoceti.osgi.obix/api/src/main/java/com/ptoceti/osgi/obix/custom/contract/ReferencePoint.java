package com.ptoceti.osgi.obix.custom.contract;

import java.io.Serializable;

import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Real;

/**
 * A point that represents a setting
 * 
 * @author lor
 *
 */
public class ReferencePoint extends Real implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final Contract contract = new Contract("ptoceti:ReferencePoint");
	
	public ReferencePoint() {
		this.setIs(contract);
	}
	
	public ReferencePoint(String name) {
		super(name);
		this.setIs(contract);
	}
	
	public ReferencePoint(String name, Double value) {
		super(name, value);
		this.setIs(contract);
	}

}
