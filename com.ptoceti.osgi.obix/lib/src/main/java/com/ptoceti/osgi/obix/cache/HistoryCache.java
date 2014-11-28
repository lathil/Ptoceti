package com.ptoceti.osgi.obix.cache;

import java.util.List;

import com.ptoceti.osgi.obix.contract.History;
import com.ptoceti.osgi.obix.contract.HistoryRecord;
import com.ptoceti.osgi.obix.contract.HistoryRollupRecord;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Int;
import com.ptoceti.osgi.obix.object.Reltime;
import com.ptoceti.osgi.obix.object.Val;

public interface HistoryCache {

	/**
	 * Create a hitory element
	 * 
	 * @param of
	 * @return
	 * @throws DomainException
	 */
	History make(Contract of) throws DomainException;
	
	/**
	 * Return a history and its configuration froml the specified url
	 * 
	 * @param uri
	 * @return
	 * @throws DomainException
	 */
	History retrieve(String uri) throws DomainException;
	
	/**
	 * Add a value record to the history specified by the uri
	 * 
	 * @param uri
	 * @param value
	 * @throws DomainException
	 */
	void addRecord(String uri, Val value) throws DomainException;
	
	/**
	 * Get records from a history
	 * @param uri
	 * @param limit
	 * @param from
	 * @param to
	 * @return
	 * @throws DomainException
	 */
	List<HistoryRecord> getRecords(String uri, Int limit, Abstime from, Abstime to) throws DomainException;
	
	/**
	 * Get rollup record from a history
	 * @param uri
	 * @param limit
	 * @param from
	 * @param to
	 * @param roolUpDuration
	 * @return
	 * @throws DomainException
	 */
	List<HistoryRollupRecord> getRollUprecords(String uri, Int limit, Abstime from, Abstime to, Reltime roolUpDuration) throws DomainException;
	
}
