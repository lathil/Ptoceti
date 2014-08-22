package com.ptoceti.osgi.control;


public enum StatusCode {

	OK(0,"Ok"),
	FAULT(1,"Fault");
	
	private int code;
	private String name;
	
	StatusCode(int code, String name){
		this.code = code;
		this.name = name;
	}
	
	public int getCode(){
		return code;
	}
	
	public String getName(){
		return name;
	}
	
	public static StatusCode getStatusFromId( int id){
		
		StatusCode result = null;
		for( StatusCode status : StatusCode.values()){
			if( status.getCode() == id) {result = status; break;}
		}
		return result;
	}
}
