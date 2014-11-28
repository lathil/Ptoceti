package com.ptoceti.osgi.obix.cache;

import java.util.List;

import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Uri;

public interface ObjCache {

	/**
	 * Get Obix oject specified by given uri
	 * 
	 * @param href
	 * @return
	 * @throws DomainException
	 */
	public Obj getObixObj(Uri href) throws DomainException;
	
	/**
	 * Get all Obix ojects that responds to specified contract
	 * 
	 * @param contract
	 * @return
	 * @throws DomainException
	 */
	public List<Obj> getObixObjsByContract(Contract contract) throws DomainException;
	
	/**
	 * Update an Obix oject at the uri specified
	 * 
	 * @param href
	 * @param updatePbj
	 * @return
	 * @throws DomainException
	 */
	public Obj updateObixObjAt(Uri href, Obj updatePbj) throws DomainException;
	
	/**
	 * Update an existing Obix object or if it does not exists, create it.
	 * 
	 * @param updateObj
	 * @return
	 * @throws DomainException
	 */
	public Obj createUpdateObixObj(Obj updateObj) throws DomainException;
	
	/**
	 * Create a new Obix object
	 * 
	 * @param newObj
	 * @return
	 * @throws DomainException
	 */
	public Obj createObixObj(Obj newObj) throws DomainException;
}
