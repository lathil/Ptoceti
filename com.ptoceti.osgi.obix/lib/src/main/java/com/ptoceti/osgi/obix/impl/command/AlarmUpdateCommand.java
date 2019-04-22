package com.ptoceti.osgi.obix.impl.command;


import com.ptoceti.osgi.obix.cache.AlarmCache;
import com.ptoceti.osgi.obix.cache.ObjCache;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.object.Val;

import javax.inject.Inject;

public class AlarmUpdateCommand {

	private ObjCache objCache;
	private AlarmCache alarmCache;
	
	@Inject
	public AlarmUpdateCommand(ObjCache cache, AlarmCache alarmCache) {
		this.objCache = cache;
		this.alarmCache = alarmCache;
	}
	
	public void execute(String alarmUri, Val obj) throws DomainException{
		alarmCache.updateAlarmState(alarmUri, obj);
	}
	
}
