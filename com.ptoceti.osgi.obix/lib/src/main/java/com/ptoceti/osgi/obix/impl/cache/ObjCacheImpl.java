package com.ptoceti.osgi.obix.impl.cache;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.Cache;
import com.google.inject.Inject;
import com.ptoceti.osgi.obix.cache.ObjCache;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.domain.ObjDomain;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Uri;

public class ObjCacheImpl extends AbstractCache implements ObjCache{

	ObjDomain objDomain;
	
	@Inject
	ObjCacheImpl(ObjDomain objDomain, Cache<String, Obj> cache){
		
		super( cache);
		this.objDomain = objDomain;
	}
	
	@Override
	public Obj getObixObj(final Uri href) throws DomainException {
		// TODO Auto-generated method stub
	
		Obj result = null;
		try {
			result = cache.get(href.getPath(), new Callable<Obj>(){

				@Override
				public Obj call() throws Exception {
					Obj obj =  objDomain.getObixObj(href);
					if( obj == null) throw new ObjNotFoundException("Obj at "  + href.getPath() + " not found");
					return obj;
				}
				
			});
		} catch (ExecutionException e) {
			
		}
		return result;
	}

	@Override
	public List<Obj> getObixObjsByContract(Contract contract)
			throws DomainException {
		return objDomain.getObixObjsByContract(contract);
	}

	@Override
	public Obj updateObixObjAt(Uri href, Obj updateObj) throws DomainException {
		Obj result = objDomain.updateObixObjAt(href, updateObj);
		cache.put(result.getHref().getPath(), result);
		return result;
	}

	@Override
	public Obj createUpdateObixObj(Obj updateObj) throws DomainException {
		Obj result = objDomain.createUpdateObixObj(updateObj);
		cache.put(result.getHref().getPath(), result);
		return result;
	}

	
	@Override
	public Obj createObixObj(Obj newObj) throws DomainException {
		Obj result = objDomain.createObixObj(newObj);
		cache.put(result.getHref().getPath(), result);
		return result;
	}

}
