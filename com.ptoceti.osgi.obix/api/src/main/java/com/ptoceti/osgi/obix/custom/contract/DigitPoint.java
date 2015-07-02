package com.ptoceti.osgi.obix.custom.contract;

import java.io.Serializable;

import com.ptoceti.osgi.obix.object.Bool;
import com.ptoceti.osgi.obix.object.Contract;

/**
 * A Digital point for boolean inputs
 * 
 * @author lor
 *
 */
public class DigitPoint extends Bool implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7545156905451046438L;

	public static final Contract contract = new Contract("ptoceti:DigitPoint");
	
	public DigitPoint() {
		this.setIs(contract);
		this.setWritable(Boolean.FALSE);
	}
	
	public DigitPoint(String name) {
		super(name);
		this.setIs(contract);
		this.setWritable(Boolean.FALSE);
	}
	
	public DigitPoint(String name, Boolean value) {
		super(name, value);
		this.setIs(contract);
		this.setWritable(Boolean.FALSE);
	}
}
