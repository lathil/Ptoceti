package com.ptoceti.osgi.obix.impl.back.converters;

import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Val;

public interface OsgiObixConverter<T extends Val> {

	T toObix(Object in);
	Object fromObix(T in);
	
	Val toBaseObix(Object in);
	Object fromBaseObix(Val in);
	
	String getObixClassName();
	Contract getObixContract();
	String getOsgiClassName();
}
