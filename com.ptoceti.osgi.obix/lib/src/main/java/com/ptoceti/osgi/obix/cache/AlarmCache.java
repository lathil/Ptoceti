package com.ptoceti.osgi.obix.cache;

import com.ptoceti.osgi.obix.contract.Alarm;
import com.ptoceti.osgi.obix.contract.History;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Ref;
import com.ptoceti.osgi.obix.object.Val;

public interface AlarmCache {

	/**
	 * Create a Alarm that will monitor an Obix Object, ideally something that extends Val
	 * 
	 * @param ref the ref to the object to be monito
	 * @return the Alarm
	 * @throws DomainException on creating the alarm
	 */
	Alarm make(Ref ref) throws DomainException;
	
	Alarm make(Ref ref, Contract of, String displayName) throws DomainException;
	
	Alarm retrieve(String uri) throws DomainException;
	
	boolean delete(String uri) throws DomainException;
	
	Alarm ack( String uri, String ackUser) throws DomainException;
	
	/**
	 * Add an alarm observer to the observable object
	 * 
	 * @param uri uri of the alarm object
	 * @param observable the object to be observed by the history object
	 * @throws DomainException on accessing the alarm
	 */
	void addAlarmObserver(String uri, Obj observable) throws DomainException;
	
	/**
	 * Update the state of an alarm with the object new values
	 * 
	 * @param uri the arlarm uri	
	 * @param obj the updated obj
	 * @throws DomainException on accessing the alarm
	 */
	void updateAlarmState(String uri, Val obj) throws DomainException;
	
	 /**
	  * Set the max limit for an alarm
	  * 
	  * @param uri the alarm uri
	  * @param obj the max value
	  * 
	  * @throws DomainException on accessing the alarm
	  */
	void setMax(String uri, Val obj) throws DomainException;
	
	 /**
	  * Set the min limit for an alarm
	  * 
	  * @param uri the alarm uri
	  * @param obj the min value
	  * 
	  * @throws DomainException on accessing the alarm
	  */
	void setMin(String uri, Val obj) throws DomainException;
}
