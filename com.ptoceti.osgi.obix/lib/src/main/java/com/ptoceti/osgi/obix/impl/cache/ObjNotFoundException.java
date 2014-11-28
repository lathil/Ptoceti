package com.ptoceti.osgi.obix.impl.cache;

public class ObjNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4993304381531402715L;

	public ObjNotFoundException(String message){
		super(message);
	}
	
	public ObjNotFoundException(String message, Throwable cause ){
		super(message, cause);
	}
	
	public ObjNotFoundException(Throwable cause ){
		super(cause);
	}
}
