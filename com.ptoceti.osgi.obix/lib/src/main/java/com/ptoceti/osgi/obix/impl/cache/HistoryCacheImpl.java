package com.ptoceti.osgi.obix.impl.cache;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.Cache;
import com.google.inject.Inject;
import com.ptoceti.osgi.obix.cache.HistoryCache;
import com.ptoceti.osgi.obix.contract.History;
import com.ptoceti.osgi.obix.contract.HistoryRecord;
import com.ptoceti.osgi.obix.contract.HistoryRollupRecord;
import com.ptoceti.osgi.obix.contract.Watch;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.domain.HistoryDomain;
import com.ptoceti.osgi.obix.domain.ObjDomain;
import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Int;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Reltime;
import com.ptoceti.osgi.obix.object.Val;

public class HistoryCacheImpl extends ObjCacheImpl implements HistoryCache {

	HistoryDomain historyDomain;
	
	@Inject
	HistoryCacheImpl(HistoryDomain historyDomain, ObjDomain objDomain, Cache<String, Obj> cache){
		
		super(objDomain, cache);
		this.historyDomain = historyDomain;
	}
	
	@Override
	public History make(Contract of) throws DomainException {
		History result =  historyDomain.make(of);
		cache.put(result.getHref().getPath(), result);
		return result;
	}

	@Override
	public History retrieve(final String uri) throws DomainException {
		History result = null;
		try {
			result = (History) cache.get(uri, new Callable<History>(){

				@Override
				public History call() throws Exception {
					History obj =  historyDomain.retrieve(uri);
					if( obj == null) throw new ObjNotFoundException("Obj at "  + uri + " not found");
					return obj;
				}
				
			});
		} catch (ExecutionException e) {
			
		}
		return result;
	}

	@Override
	public void addRecord(String uri, Val value) throws DomainException {
		historyDomain.addRecord(uri, value);
	}

	@Override
	public List<HistoryRecord> getRecords(String uri, Int limit, Abstime from, Abstime to) throws DomainException {
		return historyDomain.getRecords(uri, limit, from, to);
	}

	@Override
	public List<HistoryRollupRecord> getRollUprecords(String uri, Int limit, Abstime from, Abstime to, Reltime roolUpDuration) throws DomainException {
		return historyDomain.getRollUprecords(uri, limit, from, to, roolUpDuration);
	}

}
