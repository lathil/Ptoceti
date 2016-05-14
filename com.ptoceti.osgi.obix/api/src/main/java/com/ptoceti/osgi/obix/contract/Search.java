package com.ptoceti.osgi.obix.contract;

import java.io.Serializable;

import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Op;
import com.ptoceti.osgi.obix.object.Ref;

public class Search extends Op implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6629405059646297095L;

	public Search() {
		super( Ref.contract, SearchOut.contract);
	}
	
	public Search(String name) {
		this( name , Ref.contract, SearchOut.contract);
	}
	
	public Search(String name, Contract in, Contract out) {
		super(name, in, out);
		
	}

	public Ref getQuery() {
		return (Ref)this.getChildren("query");
	}
	
	public void setQuery(Ref query){
		
		if(query.getName() == null) query.setName("query");
		this.addChildren(query);
	}
	
	public SearchOut getResult() {
		return (SearchOut)this.getChildren("result");
	}
	
	public void setResult(SearchOut result){
		if(result.getName()== null) result.setName("result");
		this.addChildren(result);
	}

}
