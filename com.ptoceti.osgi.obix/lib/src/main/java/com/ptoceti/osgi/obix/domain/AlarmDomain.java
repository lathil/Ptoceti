package com.ptoceti.osgi.obix.domain;

import com.ptoceti.osgi.obix.contract.Alarm;
import com.ptoceti.osgi.obix.impl.proxy.jdbc.JdbcConnection;
import com.ptoceti.osgi.obix.impl.proxy.jdbc.JdbcConnection.ConnectionType;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Ref;
import com.ptoceti.osgi.obix.object.Val;

public interface AlarmDomain extends BaseDomain {

	/**
	 * Create a hitory element
	 * 
	 * @param of
	 * @return
	 * @throws DomainException
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	Alarm make(Ref ref, Contract of, String displayName) throws DomainException;
	
	/**
	 * Remove a hitory element
	 * 
	 * @param of
	 * @return
	 * @throws DomainException
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	void remove(String uri) throws DomainException;
	
	/**
	 * Return a history and its configuration froml the specified urlHistoryDomain
	 * 
	 * @param uri
	 * @return
	 * @throws DomainException
	 */
	@JdbcConnection(type = ConnectionType.RX)
	Alarm retrieve(String uri) throws DomainException;
	
	/**
	 * Trigger the alarm state on the alarm with triggering value
	 * @param uri
	 * @param val
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	Alarm setAlarm( String uri, Val val) throws DomainException ;
	
	/**
	 * Clear the alarm stats on the alarm
	 * 
	 * @param uri
	 * @param val
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	Alarm clearAlarm(String uri, Val val) throws DomainException ;
	
	/**
	 * Acknowledge the alarm
	 * 
	 * @param uri the alarm uri
	 * @param ackUser the user id, if provided.
	 * @return
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	Alarm ack(String uri,  String ackUser) throws DomainException;
	
	/**
	 * Set max value for the alarm
	 * 
	 * @param uri the alarm uri
	 * @param obj the max value
	 * @return
	 * @throws DomainException
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	Val setMax(String uri, Val obj) throws DomainException;
	
	/**
	 * Set the min value for the alarm
	 * 
	 * @param uri the alarm uri
	 * @param obj the min value
	 * @return
	 * @throws DomainException
	 */
	@JdbcConnection(type = ConnectionType.RWX)
	Val setMin(String uri, Val obj) throws DomainException;
	
}
