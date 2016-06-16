package com.ptoceti.osgi.obix.custom.contract;

import java.io.Serializable;

import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.List;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Ref;

public class SearchOut extends Obj implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -327159805480987161L;
	public static final Contract contract = new Contract("ptoceti:SearchOut");
	
	public SearchOut() {
		this.setIs(contract);
		
		List itemList =  new List("results");
		itemList.setOf(new Contract("obix:ref"));
		
		this.addChildren(itemList);
	}
	
	public List getValuesList() {
		return (List)this.getChildren("results");
	}

	public void addValue(Ref ref) {
		getValuesList().addChildren(ref);
	}
}
