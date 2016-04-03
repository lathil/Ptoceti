package com.ptoceti.osgi.obix.impl.command;

import com.google.inject.Inject;
import com.ptoceti.osgi.obix.cache.HistoryCache;
import com.ptoceti.osgi.obix.cache.ObjCache;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Val;

public class AddHistoryRecordCommand {

	private ObjCache objCache;
	
	private HistoryCache historyCache;
	
	@Inject
	public AddHistoryRecordCommand(ObjCache cache, HistoryCache historyCache) {
		this.objCache = cache;
		this.historyCache = historyCache;
	}
	
	public void execute(String historyUri, Val recordObj) throws DomainException{
		historyCache.addRecord(historyUri, recordObj);
	}
}
