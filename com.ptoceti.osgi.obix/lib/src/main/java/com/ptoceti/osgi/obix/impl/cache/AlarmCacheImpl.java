package com.ptoceti.osgi.obix.impl.cache;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.osgi.service.wireadmin.Envelope;

import com.google.common.cache.Cache;
import com.ptoceti.osgi.obix.cache.AlarmCache;
import com.ptoceti.osgi.obix.contract.Alarm;
import com.ptoceti.osgi.obix.custom.contract.DigitAlarm;
import com.ptoceti.osgi.obix.custom.contract.RangeAlarm;
import com.ptoceti.osgi.obix.domain.AlarmDomain;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.domain.ObjDomain;
import com.ptoceti.osgi.obix.impl.command.AlarmUpdateCommand;
import com.ptoceti.osgi.obix.impl.guice.GuiceContext;
import com.ptoceti.osgi.obix.impl.observer.AlarmObserver;
import com.ptoceti.osgi.obix.impl.service.Activator;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Ref;
import com.ptoceti.osgi.obix.object.Status;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.object.Val;

import javax.inject.Inject;

public class AlarmCacheImpl extends ObjCacheImpl implements AlarmCache {

	AlarmDomain alarmDomain;
	@Inject
	AlarmCacheImpl(AlarmDomain alarmDomain, ObjDomain objDomain, Cache<String, Obj> cache) {
		super(objDomain, cache);
		this.alarmDomain = alarmDomain;
	}

	
	@Override
	public Alarm make(Ref ref) throws DomainException {
		
		Alarm alarm;
		
		// get the object for which the alarm will be created
		Obj obj = getObixObj(ref.getHref());
		// check if there is already a alarm for this object
		Obj alarmRef = obj.getChildren("alarm");
		
		if( alarmRef == null ){
			// create a alarm object of type same as referenced object
			alarm = make(ref, obj.getContract(), obj.getDisplayName() + ".alarm");
			if( alarm != null){
				// create a observer for the alarm object ..
				AlarmObserver observer = new AlarmObserver( alarm.getHref().getPath(), GuiceContext.Instance.getInjector().getInstance(AlarmUpdateCommand.class));
				// .. and set it
				alarm.setObserver(observer);
				// create a reference to alarm object ..
				alarmRef = new Ref("alarm", alarm.getHref());
				// .. and add it to the object
				alarmRef.setIs(alarm.getIs());
				objDomain.addChildObject(ref.getHref(), alarmRef);
				obj.addChildren(alarmRef);
				// finally add alarm observer to observable object
				obj.addObserver(observer);
			}
		} else {
			alarm = retrieve(alarmRef.getHref().getPath());
		}
		
		return alarm;
	}

	@Override
	public Alarm make(Ref ref, Contract of, String displayName) throws DomainException {
		Alarm result =  alarmDomain.make(ref, of, displayName);
		if( result != null){
			cache.put(result.getHref().getPath(), result);
		}
		return result;
	}
	
	@Override
	public Alarm retrieve(final String uri) throws DomainException {
		Alarm result = null;
		try {
			result = (Alarm) cache.get(uri, new Callable<Alarm>(){

				@Override
				public Alarm call() throws Exception {
					Alarm obj =  alarmDomain.retrieve(uri);
					if( obj == null) throw new ObjNotFoundException("Obj at "  + uri + " not found");
					return obj;
				}
				
			});
		} catch (ExecutionException e) {
			
		}
		return result;
	}

	@Override
	public boolean delete(String uri) throws DomainException {
		// get hold of alarm ...
		Alarm alarm = retrieve(uri);
		// ... and object that has a reference to it
		Obj refObj = objDomain.getObixObjWithRefTo(new Uri("ref", uri));
		if( refObj != null && alarm != null)	{
			// if both found, get counterpart in cache ...
			Obj observableObj = getObixObj(refObj.getHref());
			// ... and remove reference to observer
			observableObj.removeObserver(alarm.getObserver());
			// remove reference to ref object in persistant store ..
			objDomain.deleteChildObject(observableObj.getHref(), "alarm");
			// .. and reference in cache
			observableObj.removeChildren("alarm");
			
			// remove from persistent storage ...
			alarmDomain.remove(uri);
			// .. and from cache
			cache.invalidate(uri);
			return true;
		}
		
		return false;
	}
	
	@Override
	public void addAlarmObserver(String uri, Obj observable) throws DomainException {
		
		Alarm alarm = retrieve(uri);
		if( alarm != null){
			// create a observer for the alarm object .. and add a comman object for injecting history records
			AlarmObserver observer = new AlarmObserver( alarm.getHref().getPath(), GuiceContext.Instance.getInjector().getInstance(AlarmUpdateCommand.class));
			// .. and set it
			alarm.setObserver(observer);
			// finally add history observer to observable object
			observable.addObserver(observer);
		}
	}
	
	@Override
	public void updateAlarmState(String uri, Val val) throws DomainException {
		// get hold of the alarm before everything
		Alarm alarm = retrieve(uri);
		
		
		if( alarm != null){
			Alarm updatedAlarm = null;
			boolean valueOutOfBounds = false;
			if( alarm.containsContract(RangeAlarm.contract)){
				
				Val maxValue = (Val)alarm.getChildren("maxValue");
				Val minValue = (Val)alarm.getChildren("minValue");
				
				if( maxValue != null && (( val.compareTo(maxValue))  > 0 )){
					valueOutOfBounds = true;
				}
				
				if( minValue != null && (( val.compareTo(minValue))  < 0 )){
					valueOutOfBounds = true;
				}
				
				
			} else if( alarm.containsContract(DigitAlarm.contract)){
				if(val.compareTo(alarm.getChildren("alarmLevel")) != 0   ){
					valueOutOfBounds = true;
				}
			}
			
			if( valueOutOfBounds){
				// need to record value that changed the alarm state and set state to alarm if not already so
				if( alarm.getStatus() == null || (!alarm.getStatus().equals(Status.ALARM) && !alarm.getStatus().equals(Status.UNACKEDALARM))){
					updatedAlarm = alarmDomain.setAlarm( uri, val);
				}
				
			} else {
				if( alarm.getStatus() != null && (alarm.getStatus().equals(Status.ALARM) || alarm.getStatus().equals(Status.UNACKEDALARM))){
					updatedAlarm = alarmDomain.clearAlarm(uri, val);
				}
			}
			
			if( updatedAlarm != null){
				updatedAlarm.setObserver(alarm.getObserver());
				alarm.setObserver(null);
				cache.put(updatedAlarm.getHref().getPath(), updatedAlarm);

			}
		}
		
	}


	@Override
	public Alarm ack(String uri, String ackUser) throws DomainException {
		
		Alarm alarm = retrieve(uri);
		if( alarm != null) {
			Alarm updatedAlarm = alarmDomain.ack(uri, ackUser);
			if( updatedAlarm != null){
				updatedAlarm.setObserver(alarm.getObserver());
				alarm.setObserver(null);
				cache.put(updatedAlarm.getHref().getPath(), updatedAlarm);
				alarm = updatedAlarm;
			}
		}
				
		return alarm;
	}


	@Override
	public void setMax(String uri, Val obj) throws DomainException {
	
		Val max = alarmDomain.setMax(uri, obj);
		
		if( max != null){
			Alarm alarm = retrieve(uri);
			alarm.addChildren(max);
		}
		
	}


	@Override
	public void setMin(String uri, Val obj) throws DomainException {
		Val min = alarmDomain.setMin(uri, obj);
		
		if( min != null){
			Alarm alarm = retrieve(uri);
			alarm.addChildren(min);
		}
	}

}
