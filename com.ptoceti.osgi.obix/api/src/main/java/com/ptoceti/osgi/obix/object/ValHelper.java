package com.ptoceti.osgi.obix.object;

public class ValHelper {

    public static Val buildFromContract(String contract){
	
	Val result = null;
	Contract contractIn  = new Contract(contract);
	
	if( Abstime.contract.containsContract(contractIn)){
	    result =  new Abstime();
	} else if ( Bool.contract.containsContract(contractIn)){
	    result = new Bool();
	} else if ( Int.contract.containsContract(contractIn)){
	    result = new Int();
	} else if ( Real.contract.containsContract(contractIn)){
	    result = new Real();
	} else if ( Reltime.contract.containsContract(contractIn)){
	    result = new Reltime();
	} else if ( Str.contract.containsContract(contractIn)){
	    result = new Str();
	} else if ( Uri.contract.containsContract(contractIn)){
	    result = new Uri();
	}
	
	return result;
    }
}
