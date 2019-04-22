package com.ptoceti.osgi.obix.impl.domain;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.ptoceti.osgi.obix.contract.AckAlarm;
import com.ptoceti.osgi.obix.contract.Alarm;
import com.ptoceti.osgi.obix.contract.PointAlarm;
import com.ptoceti.osgi.obix.contract.StatefulAlarm;
import com.ptoceti.osgi.obix.domain.AlarmDomain;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.impl.entity.EntityException;
import com.ptoceti.osgi.obix.impl.entity.ObjEntity;
import com.ptoceti.osgi.obix.impl.front.resources.AlarmServerResource;
import com.ptoceti.osgi.obix.impl.front.resources.RangeAlarmMaxServerResource;
import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Bool;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Int;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Real;
import com.ptoceti.osgi.obix.object.Ref;
import com.ptoceti.osgi.obix.object.Status;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.object.Val;
import com.ptoceti.osgi.obix.custom.contract.RangeAlarm;
import com.ptoceti.osgi.obix.custom.contract.DigitAlarm;

public class AlarmDomainImpl extends AbstractDomain implements  AlarmDomain {

	@Override
	public Alarm retrieve(String uri) throws DomainException{
		return (Alarm)getAlarm(uri).getObixObject();
	}
	
	private ObjEntity getAlarm( String uri) throws DomainException{

		Alarm obixObj = new Alarm();
		obixObj.setHref(new Uri("href", uri));
		ObjEntity objEnt = new ObjEntity(obixObj);
		
		try {
			if( objEnt.fetchByHref()) {
				if( objEnt.getObixObject().containsContract(Alarm.contract)) {
					objEnt.fetchChildrens();
					for( ObjEntity entity : (List<ObjEntity>) objEnt.getChilds()){
						objEnt.getObixObject().addChildren(entity.getObixObject());
					}
					return objEnt;
				}
			}
		} catch(EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".getAlarm", ex);
		}
		
		return null;
	}

	@Override
	public Alarm make(Ref ref, Contract of, String displayName) throws DomainException {
		
		Alarm alarm = null;
		
		String timeStamp = (new Long (Calendar.getInstance().getTimeInMillis())).toString();
        Uri href = new Uri("uri", AlarmServerResource.baseuri.concat("/").concat(timeStamp).concat("/"));
		
		if( of.containsContract(Int.contract)){
			alarm = new RangeAlarm();
			Int max = new Int("max", 0);
            max.setHref(new Uri("uri", AlarmServerResource.baseuri.concat("/").concat(timeStamp).concat("/").concat(RangeAlarmMaxServerResource.baseuri)));
			((RangeAlarm)alarm).setMaxValue(max);
			Int min = new Int("min", 0);
            min.setHref(new Uri("uri", AlarmServerResource.baseuri.concat("/").concat(timeStamp).concat("/").concat(RangeAlarmMaxServerResource.baseuri)));
			((RangeAlarm)alarm).setMinValue(min);
		} else if (of.containsContract(Real.contract)){
			alarm = new RangeAlarm();
			Real max = new Real("max", 0.0);
            max.setHref(new Uri("uri", AlarmServerResource.baseuri.concat("/").concat(timeStamp).concat("/").concat(RangeAlarmMaxServerResource.baseuri)));
			((RangeAlarm)alarm).setMaxValue(max);
			Real min = new Real("min", 0.0);
            min.setHref(new Uri("uri", AlarmServerResource.baseuri.concat("/").concat(timeStamp).concat("/").concat(RangeAlarmMaxServerResource.baseuri)));
			((RangeAlarm)alarm).setMinValue(min);
		} else if (of.containsContract(Bool.contract)){
			alarm = new DigitAlarm();
			((DigitAlarm)alarm).setAlarmLevel(new Bool("level",false));
		} else {
			return null;
		}
		
		alarm.setDisplayName(displayName);
		alarm.setSource(ref);
		alarm.setHref(href);
		
		// create a hidden list that will contain all alarm records
		com.ptoceti.osgi.obix.object.List recordsList = new com.ptoceti.osgi.obix.object.List("alarmrecords");
		recordsList.setOf(of);
		alarm.addChildren(recordsList);
		
		ObjEntity objEnt = new ObjEntity(alarm);
		try {
			objEnt.create();
		} catch(EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".make", ex);
		}
		
		return (Alarm) objEnt.getObixObject();
	}

	@Override
	public void remove(String uri) throws DomainException {
		Alarm obixObj = new Alarm();
		obixObj.setHref(new Uri("href", uri));
		ObjEntity objEnt = new ObjEntity(obixObj);
		
		try {
			if( objEnt.fetchByHref()) {
				if( objEnt.getObixObject().containsContract(Alarm.contract)) {
					objEnt.fetchChildrens();
					for( ObjEntity entity : (List<ObjEntity>) objEnt.getChilds()){
						if( entity.getObixObject().getName() != null && entity.getObixObject().getName().equals("alarmrecords")) {
							// we are on the records list, delete all childs
							entity.deleteChilds();
							break;
						}
					}
					// delete history and direct childs.
					objEnt.delete();
				}
			}
		} catch(EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".remove", ex);
		}
	}
	
	@Override
	public
	Alarm setAlarm( String uri, Val val) throws DomainException  {
		
		ObjEntity alarmEntity = getAlarm(uri);
		
		ObjEntity timestampEntity = alarmEntity.getChildByName("timestamp");
		ObjEntity ackTimestampEntity = alarmEntity.getChildByName("ackTimestamp");
		
		try {
			// base alarm
			if( timestampEntity != null){
				((Abstime)timestampEntity.getObixObject()).setVal(new Date());
				timestampEntity.update();
			} else {
				Abstime value = new Abstime("timestamp", new Date());
				alarmEntity.addChildren(value);
			}
			// if point alarm
			if( alarmEntity.getObixObject().containsContract(PointAlarm.contract)){
				ObjEntity valueEntity = alarmEntity.getChildByName("alarmValue");
				if( valueEntity != null){
					((Val)valueEntity.getObixObject()).setVal(val.getVal());
					valueEntity.update();
				} else {
					Obj value = val.clone();
					value.setName("alarmValue");
					alarmEntity.addChildren(value);
				}
			}
			
			boolean isAck = alarmEntity.getObixObject().containsContract(AckAlarm.contract);
			boolean iStateF = alarmEntity.getObixObject().containsContract(StatefulAlarm.contract);
			
			ObjEntity normalTimeStampEntity;
			if( iStateF){
				normalTimeStampEntity = alarmEntity.getChildByName("normalTimestamp");
				if( normalTimeStampEntity != null) {
					// if there is a normal time stamps, remove it
					alarmEntity.deleteChildByName("normalTimestamp");
				}
			}
			
			if(isAck &&  iStateF){
				alarmEntity.deleteChildByName("ackTimestamp");
				alarmEntity.getObixObject().setStatus(Status.UNACKEDALARM);
			} else if( isAck ){
				alarmEntity.deleteChildByName("ackTimestamp");
				alarmEntity.getObixObject().setStatus(Status.UNACKED);
			} else if( iStateF){
				alarmEntity.getObixObject().setStatus(Status.ALARM);
			} 
			
			alarmEntity.update();
			alarmEntity = getAlarm(uri);
			
			return (Alarm)alarmEntity.getObixObject();
		
		} catch (EntityException | CloneNotSupportedException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".setAlarm", ex);
		}
		
	}
	
	@Override
	public
	Alarm clearAlarm(String uri, Val val) throws DomainException {
		ObjEntity alarmEntity = getAlarm(uri);
		
		ObjEntity timestampEntity = alarmEntity.getChildByName("timestamp");
		
		// if statefull alarm
		ObjEntity normalTimeStampEntity = alarmEntity.getChildByName("normalTimestamp");
		// if ackAlarm
		ObjEntity ackTimestampEntity = alarmEntity.getChildByName("ackTimestamp");
		ObjEntity ackUserEntity = alarmEntity.getChildByName("ackUser");
		
		try {
			
			boolean isAck = alarmEntity.getObixObject().containsContract(AckAlarm.contract);
			boolean iStateF = alarmEntity.getObixObject().containsContract(StatefulAlarm.contract);
			
			if( iStateF) {
				// if statefull alarm
				if( normalTimeStampEntity != null){
					((Abstime)normalTimeStampEntity.getObixObject()).setVal(new Date());
					normalTimeStampEntity.update();
				} else {
					Abstime value = new Abstime("normalTimestamp", new Date());
					alarmEntity.addChildren(value);
				}
			}
			
			if(isAck &&  iStateF){
				if( ackTimestampEntity == null) {
					alarmEntity.getObixObject().setStatus(Status.UNACKED);
				} else {
					alarmEntity.getObixObject().setStatus(Status.OK);
				}
				
			} else if( isAck ){
				if( ackTimestampEntity == null){
					// the alarm has not yet been acknowledge
					alarmEntity.getObixObject().setStatus(Status.UNACKED);
				}
			} else {
				alarmEntity.getObixObject().setStatus(Status.OK);
			}
			
			
			alarmEntity.update();
			alarmEntity = getAlarm(uri);
			
			return (Alarm)alarmEntity.getObixObject();
			
		} catch (EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".clearAlarm", ex);
		}
	}
	
	@Override
	public Alarm ack(String uri,  String ackUser) throws DomainException {
		ObjEntity alarmEntity = getAlarm(uri);
		
		try {
			
			if(alarmEntity.getObixObject().containsContract(AckAlarm.contract)){
				if( alarmEntity.getObixObject().getStatus().equals(Status.UNACKEDALARM) || alarmEntity.getObixObject().getStatus().equals(Status.UNACKED)){
					
					ObjEntity ackTimestampEntity = alarmEntity.getChildByName("ackTimestamp");
					if( ackTimestampEntity == null){
						Abstime value = new Abstime("ackTimestamp", new Date());
						alarmEntity.addChildren(value);
					} else {
						((Abstime)ackTimestampEntity.getObixObject()).setVal(new Date());
						ackTimestampEntity.update();
					}
				
					if( alarmEntity.getObixObject().getStatus().equals(Status.UNACKEDALARM)){
						alarmEntity.getObixObject().setStatus(Status.ALARM);
					} else {
						alarmEntity.getObixObject().setStatus(Status.OK);
					}
					
					alarmEntity.update();
					alarmEntity = getAlarm(uri);
				}
			}
			
			
		
		} catch (EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".ack", ex);
		}
		
		return (Alarm)alarmEntity.getObixObject();
	}
	
	@Override
	public Val setMax(String uri, Val obj) throws DomainException {
		ObjEntity alarmEntity = getAlarm(uri);
		
		try {
			Val result = null;
			
			if(alarmEntity.getObixObject().containsContract(RangeAlarm.contract)){
				ObjEntity maxValueEntity = alarmEntity.getChildByName("maxValue");
				if( maxValueEntity == null){
					alarmEntity.addChildren(obj);
				} else {
					((Val)maxValueEntity.getObixObject()).setVal(obj.getVal());
					maxValueEntity.update();
				}
				result = (Val)maxValueEntity.getObixObject();
			}
			
			return result;
			
		} catch (EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".setMax", ex);
		}
		
	}


	@Override
	public Val setMin(String uri, Val obj) throws DomainException {
		ObjEntity alarmEntity = getAlarm(uri);
		
		try {
			
			Val result = null;
			
			if(alarmEntity.getObixObject().containsContract(RangeAlarm.contract)){
				ObjEntity minValueEntity = alarmEntity.getChildByName("minValue");
				if( minValueEntity == null){
					alarmEntity.addChildren(obj);
				} else {
					((Val)minValueEntity.getObixObject()).setVal(obj.getVal());
					minValueEntity.update();
				}
				
				result = (Val)minValueEntity.getObixObject();
			}
			
			return result;
			
		} catch (EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".setMin", ex);
		}
		
	}
}
