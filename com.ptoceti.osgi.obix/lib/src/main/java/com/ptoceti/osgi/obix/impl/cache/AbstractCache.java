package com.ptoceti.osgi.obix.impl.cache;

import com.google.common.cache.Cache;
import com.ptoceti.osgi.obix.object.Obj;

public abstract class AbstractCache {

	Cache<String, Obj> cache;
	
	AbstractCache( Cache<String, Obj> cache){
		this.cache = cache;
	}
	
	
}
