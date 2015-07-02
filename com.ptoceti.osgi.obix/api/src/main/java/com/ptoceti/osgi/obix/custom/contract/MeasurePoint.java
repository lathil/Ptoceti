package com.ptoceti.osgi.obix.custom.contract;

import java.io.Serializable;

import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Real;

/**
 * Represent a measure of an external value
 * 
 * @author lor
 *
 */
public class MeasurePoint extends Real implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final Contract contract = new Contract("ptoceti:MeasurePoint");
	
	public MeasurePoint() {
		this.setIs(contract);
	}
	
	public MeasurePoint(String name) {
		super(name);
		this.setIs(contract);
	}
	
	public MeasurePoint(String name, Double value) {
		super(name, value);
		this.setIs(contract);
	}

}
