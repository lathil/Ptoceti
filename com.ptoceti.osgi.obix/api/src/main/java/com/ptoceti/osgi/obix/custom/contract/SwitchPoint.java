package com.ptoceti.osgi.obix.custom.contract;

import java.io.Serializable;

import com.ptoceti.osgi.obix.object.Bool;
import com.ptoceti.osgi.obix.object.Contract;

/**
 * A point that represents a digital output
 * 
 * @author lor
 *
 */
public class SwitchPoint extends Bool implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6849793493486595821L;

	public static final Contract contract = new Contract("ptoceti:SwitchPoint");
	
	public SwitchPoint() {
		this.setIs(contract);
		this.setWritable(Boolean.TRUE);
	}
	
	public SwitchPoint(String name) {
		super(name);
		this.setIs(contract);
		this.setWritable(Boolean.TRUE);
	}

	public SwitchPoint(String name, Boolean value) {
		super(name, value);
		this.setIs(contract);
		this.setWritable(Boolean.TRUE);
	}
}
