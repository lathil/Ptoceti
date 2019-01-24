package com.ptoceti.osgi.obix.domain;

import com.ptoceti.osgi.obix.contract.Alarm;
import com.ptoceti.osgi.obix.impl.proxy.jdbc.JdbcConnection;
import com.ptoceti.osgi.obix.impl.proxy.jdbc.JdbcConnection.ConnectionType;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Ref;
import com.ptoceti.osgi.obix.object.Val;

public interface AlarmDomain extends BaseDomain {

	/**
	 * Create a history element
	 *
	 * @param ref reference to the object the alarm must monitor
	 * @param of input contract
	 * @param displayName the name for the alarm
	 * @return Alarm the new alarm
	 * @throws DomainException on accessing persisted alarm
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	Alarm make(Ref ref, Contract of, String displayName) throws DomainException;
	
	/**
	 * Remove a history element
	 * 
	 * @param uri uri of the alarm
	 * @throws DomainException on accessing persisted alarm
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	void remove(String uri) throws DomainException;
	
	/**
	 * Return a history and its configuration froml the specified urlHistoryDomain
	 * 
	 * @param uri uri of the alarm
	 * @return Alarm fetched alarm
	 * @throws DomainException on accessing persisted alarm
	 */
	@JdbcConnection(type = ConnectionType.RX)
	Alarm retrieve(String uri) throws DomainException;
	
	/**
	 * Trigger the alarm state on the alarm with triggering value
	 * @param uri uri of the alarm
	 * @param val value for the trigger
	 * @return Alarm configured alarm
	 * @throws DomainException on accessing persisted alarm
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	Alarm setAlarm( String uri, Val val) throws DomainException ;
	
	/**
	 * Clear the alarm stats on the alarm
	 * 
	 * @param uri uri of the alarm
	 * @param val reset value
	 * @return Alarm cleared alarm
	 * @throws DomainException on accessing persisted alarm
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	Alarm clearAlarm(String uri, Val val) throws DomainException ;
	
	/**
	 * Acknowledge the alarm
	 * 
	 * @param uri the alarm uri
	 * @param ackUser the user id, if provided.
	 * @return Alarm acknowledged alarm
	 * @throws DomainException on accessing persisted alarm
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	Alarm ack(String uri,  String ackUser) throws DomainException;
	
	/**
	 * Set max value for the alarm
	 * 
	 * @param uri the alarm uri
	 * @param obj the max value
	 * @return Val returned max value
	 * @throws DomainException on accessing persisted alarm
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	Val setMax(String uri, Val obj) throws DomainException;
	
	/**
	 * Set the min value for the alarm
	 * 
	 * @param uri the alarm uri
	 * @param obj the min value
	 * @return Val returned min value
	 * @throws DomainException on accessing persisted alarm
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	Val setMin(String uri, Val obj) throws DomainException;
	
}
