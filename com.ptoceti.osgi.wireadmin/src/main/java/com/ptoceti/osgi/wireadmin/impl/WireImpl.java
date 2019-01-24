

package com.ptoceti.osgi.wireadmin.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : WireAdmin
 * FILENAME : WireImpl.java
 * 
 * This file is part of the Ptoceti project. More information about
 * this project can be found here: http://www.ptoceti.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2013 - 2015 ptoceti
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.osgi.service.wireadmin.Wire;

import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.Filter;

import org.osgi.framework.InvalidSyntaxException;

import org.osgi.service.wireadmin.Consumer;
import org.osgi.service.wireadmin.Producer;
import org.osgi.service.wireadmin.Envelope;
import org.osgi.service.wireadmin.WireConstants;
import org.osgi.service.wireadmin.WireAdminEvent;

import java.util.Hashtable;
import java.util.Dictionary;
import java.util.Date;
import java.util.ArrayList;

/**
 *
 *
 *
 *
 */
 
public class WireImpl implements Wire {

	
	private Dictionary properties;
	private Object lastValue;
	private long lastUpdate;
	private Hashtable wireFilterValues;
	private boolean isValid;
	private String[] scope;
	private Filter wireFilter;
	
	private String producerPID = null;
	private ServiceReference producerSerRef = null;
	private Producer producerSer = null;
	private boolean producerIsComposite = false;
	private String[] producerScope =  null;
	
	private String consumerPID = null;
	private ServiceReference consumerSerRef = null;
	private Consumer consumerSer = null;
	private boolean consumerIsComposite = false;
	private String[] consumerScope = null;
	
	private WireAdminImpl wireAdmin = null;
	
	/**
	 *
	 *
	 *
	 *
	 */

	public WireImpl(Dictionary properties, WireAdminImpl wAdmin ) {
	
		producerPID = (String) properties.get( WireConstants.WIREADMIN_PRODUCER_PID);
		consumerPID = (String) properties.get( WireConstants.WIREADMIN_CONSUMER_PID);
		
		this.properties = properties;
		isValid = true;
		lastValue = null;
		lastUpdate = 0;
		scope = null;
		wireAdmin = wAdmin;
		
		wireFilterValues = new Hashtable();
				
	}
	
	protected void setProperties(Dictionary properties) {
		this.properties = properties;
	}
	
	protected String getProducerPID(){
		return producerPID;
	}
	
	protected String getConsumerPID(){
		return consumerPID;
	}
	
	protected Producer getProducer(){
		return producerSer;
	}
	
	protected Consumer getConsumer(){
		return consumerSer;
	}
	
	/**
	 * Set the wire's producer service.
	 *
	 * Bind the wire to a producer service. The wire get from the bundle context a Producer service
	 * from the provided ServiceReference. If the provided ServiceReference is null and the wire object
	 * already got a Producer service, it is understood as we must get rid of the Producer service. in
	 * this case, the Producer service is unregistered from the bundle context, and the internal references
	 * are set to null.
	 * 
	 * @param producerSRef The ServiceReference to a Producer service. Null if the internal Producer service is to
	 * be get rif of.
	 *
	 */
	protected void setProducer(ServiceReference producerSRef ){
		if( producerSRef != null ) {
			producerSerRef = producerSRef;
			producerSer = (Producer) Activator.bc.getService(producerSerRef);
			
			// if the Producer property WIREADMIN_PRODUCER_FLTERS is set, then the Producer filters
			// itself the data it produces.
			if( producerSRef.getProperty(WireConstants.WIREADMIN_PRODUCER_FILTERS ) == null ){
				// Otherwise, we use the filter set from the wire's properties.
				String wireFilterString = (String)properties.get(WireConstants.WIREADMIN_FILTER );
				if( wireFilterString != null ) {
					try {
						wireFilter = Activator.bc.createFilter(wireFilterString);
					} catch (InvalidSyntaxException e) {}
				}
			}
			
			if((producerSRef.getProperty(WireConstants.WIREADMIN_PRODUCER_COMPOSITE)) != null) {
				producerIsComposite = true;
				producerScope = (String[])producerSRef.getProperty(WireConstants.WIREADMIN_PRODUCER_SCOPE);
			}
			
			if( consumerSer != null ) {
				// calculated the scope of the wire if the consumer and producer service
				// are composite.
				if(producerIsComposite && consumerIsComposite) calculateScope();
				// send a event indicating that this wire has been connected.
				wireAdmin.sendEvent(WireAdminEvent.WIRE_CONNECTED, this, null);
			}
			
		} else {
			if( producerSer != null ) {
				Activator.bc.ungetService(producerSerRef);
				producerSer = null;
				producerSerRef = null;
				producerIsComposite = false;
				producerScope = null;
				// send a event indicating that this wire has been disconnected.
				wireAdmin.sendEvent(WireAdminEvent.WIRE_DISCONNECTED, this, null);
			}
		}
	}
	
	/**
	 * Set the wire's consumer service.
	 *
	 * Bind the wire to a consumer service. The wire get from the bundle context a Consumer service
	 * from the provided ServiceReference. If the provided ServiceReference is null and the wire object
	 * already got a Consumer service, it is understood as we must get rid of the Consumer service. in
	 * this case, the Consumer service is unregistered from the bundle context, and the internal references
	 * are set to null.
	 * 
	 * @param consumerSRef The ServiceReference to a Consumer service. Null if the internal Consumer service is to
	 * be get rif of.
	 *
	 */
	protected void setConsumer(ServiceReference consumerSRef ){
		if( consumerSRef != null ) {
			consumerSerRef = consumerSRef;
			consumerSer = (Consumer) Activator.bc.getService(consumerSerRef);
			
			if((consumerSRef.getProperty(WireConstants.WIREADMIN_CONSUMER_COMPOSITE)) != null) {
				consumerIsComposite = true;
				consumerScope = (String[])consumerSRef.getProperty(WireConstants.WIREADMIN_CONSUMER_SCOPE);
			}
			
			if( producerSer != null ) {
//				 calculated the scope of the wire if the consumer and producer service
				// are composite.
				if(producerIsComposite && consumerIsComposite) calculateScope();
				// send a event indicating that this wire has been connected.
				wireAdmin.sendEvent(WireAdminEvent.WIRE_CONNECTED, this, null);
			}
			
		} else {
			if( consumerSer != null ) {
				Activator.bc.ungetService(consumerSerRef);
				consumerSer = null;
				consumerSerRef = null;
				consumerIsComposite = false;
				consumerScope = null;
				// send a event indicating that this wire has been disconnected.
				wireAdmin.sendEvent(WireAdminEvent.WIRE_DISCONNECTED, this, null);
			}
		}
	}
	
	/**
	 * Mark the wir as invalid
	 * 
	 * Called by the WireAdminImpl when invoked on the delete() method. Unregister the Producer
	 * and Consumer services, and mark the wire as invalid.
	 *
	 */
	protected void invalidate(){
	
		if(( producerSer != null ) || (consumerSer != null )) {
			if( producerSer != null ){
				Activator.bc.ungetService(producerSerRef);
				producerSer = null;
				producerSerRef = null;
			}
			if( consumerSer != null ){
				Activator.bc.ungetService(consumerSerRef);
				consumerSer = null;
				consumerSerRef = null;
			}
			
			// send a event indicating that this wire has been disconnected.
			wireAdmin.sendEvent(WireAdminEvent.WIRE_DISCONNECTED, this, null);
		}
		
		isValid = false;
	}

	/**
	 * Return the state of this wire.
	 *
	 * @return True is the wire is valid (still in the WireAdmin's Wire list). False if the wire
	 * has been deleted.
	 */
	public boolean isValid(){
		return isValid;
	}
	
	/**
	 * Return the connection state of this wire.
	 *
	 * @return true if the wire is connected to live Consumer and producer services, false otherwise.
	 */
	public boolean isConnected() {
	
		if(( producerSer != null) && (consumerSer != null )) return true;
		else return false;
	}
	
	/**
	 * Return the flavors of the Consumer service this wire is wire is connect to.
	 *
	 * @return Class[] a list of datatypes the consumer service can return.
	 *
	 */
	public Class[] getFlavors(){
	
		Class[] flavors = null;
		
		if( isConnected()) {
			flavors = (Class[]) consumerSerRef.getProperty(WireConstants.WIREADMIN_CONSUMER_FLAVORS);
		}
		
		return flavors;
	}
	
	/**
	 * Update the value
	 *
	 * Called by the Producer service to notify connected Consumer services of an updated value.
	 * The wire notify the connected Consumer service of the updated value.
	 *
	 * If the Producer connected to this wire does not perform filtering itself, then this wire
	 * will perform filtering if it has the property WIREADMIN_FILTER set. If not then the updated
	 * value shall be passed on directly to the Consumer service through the Consumer.Updated() method.
	 *
	 * If the value is an Enveloppe and the scope name is not permitted, then the value shall not be
	 * passed on to the Consumer service. Not yet done.
	 *
	 * @param value The updated value to be passed on to the connected Sonsumer service.
	 *
	 */
	 
	public void update(Object value){
	
		// check first that the wire is connected
		if( isConnected()) {
			// note the time this value has arrived
			long update = new Date().getTime();
			// we only do filtering if the prodicer is not a composite
			if( producerIsComposite == false ) {
				// the first time a value arrives, we just pass it on. On the second time, we filter
				// the value if the wire performs its own filtring.
				if(( lastValue != null ) && ( wireFilter != null )) {
					
					// we buit up the matching criteria for the filter from the las and current values.
					wireFilterValues.put(WireConstants.WIREVALUE_CURRENT, value );
					wireFilterValues.put(WireConstants.WIREVALUE_PREVIOUS, lastValue);
					wireFilterValues.put(WireConstants.WIREVALUE_ELAPSED, new Long(update - lastUpdate));
					// cwe can calculate the diffs if value of type Number, otherwise default to 0.
					if( value instanceof Number ) {
						wireFilterValues.put(WireConstants.WIREVALUE_DELTA_RELATIVE, new Float(((Number)value).floatValue()  - ((Number)lastValue).floatValue()));
						wireFilterValues.put(WireConstants.WIREVALUE_DELTA_ABSOLUTE, new Float((((Number)value).floatValue()  - ((Number)lastValue).floatValue())  / ((Number)value).floatValue()));
					} else {
						wireFilterValues.put(WireConstants.WIREVALUE_DELTA_RELATIVE, new Float(0));
						wireFilterValues.put(WireConstants.WIREVALUE_DELTA_ABSOLUTE, new Float(0));
					}
					
					// check if criterias matches the filter ..
					if( ! wireFilter.match( wireFilterValues )) {
						// .. they don't so we do not pass the value and quit.
						return;
					}
				}
			}
			
			try {
				// pass the value to the Consumer service ..
				consumerSer.updated( this, value);
				// and remember them for next time.
				lastValue = value;
				lastUpdate = update;
				// send a event indicating that the consumer was updated.
				wireAdmin.sendEvent(WireAdminEvent.WIRE_TRACE, this, null);
			} catch (Exception e ) {
				// send a event indicating that the consumer throwed an exception.
				wireAdmin.sendEvent(WireAdminEvent.CONSUMER_EXCEPTION, this, e);
			}
		}
	}
	
	/**
	 * Poll for an updated value
	 *
	 * Called by a Consumer service to get an updated value from the Producer service connected
	 * to this wire;
	 * The pool method should return null if the wire is not connected to a producer or the value
	 * returned by the producer is not an instance of one of the types retuned by getFlavor().
	 *
	 * if the wire has a scope, then the method must return an array of Enveloppe objects that are
	 * within the scope of the wire. Not yet done.
	 *
	 * @return A value whose type should be one the types returned by getFlavor(), or null if the 
	 * wire is not connected.
	 */
	 
	public Object poll(){
	
		Object result = null;
		
		// check that the wire is connected
		if( isConnected()) {
			try {
				// pool the producer for a new value object.
				Object value = producerSer.polled( this );
				//get a collection of flavors indiating the objects that we understand
				Class flavors[] = getFlavors();
				boolean isInstance = false;
				//chack that the return object is within the flavors
				for(int i = 0; i < flavors.length; i++){
					if( flavors[i].isInstance(value)) {
						isInstance = true;
						break;
					}
				}
				//if object within the flavors, ..
				if( isInstance ) {
					lastValue = value;
					lastUpdate = new Date().getTime();
					// .. return it
					result = lastValue;
				}
				
				// send a event indicating that the producer was polled.
				wireAdmin.sendEvent(WireAdminEvent.WIRE_TRACE, this, null);

			} catch ( Exception e ) {
				// send a event indicating that the producer throwed an exception.
				wireAdmin.sendEvent(WireAdminEvent.PRODUCER_EXCEPTION, this, e);
				return null;
			}
			
		}
		
		return result;
	}
	
	/**
	 * Return the last value sent throught this wire object.
	 *
	 * Return the last value passe through the update() method or returned by the pool method. If
	 * the wire object perform filtering, this is the last value passed on by the Producer service
	 * (not the result of the filtering process).
	 *
	 * @return The last value sent through this wire object.
	 */
	public Object getLastValue(){
		return lastValue;
	}
	
	/**
	 * Return the wire set of properties.
	 *
	 *
	 * @return The Wire's Property object containing the properties as set of key/values.
	 */
	public Dictionary getProperties(){
		return properties;
	}
	
	/**
	 * Return the calculated scope of this wire object. The calculated scope is the intersection of the
	 * wire's Producer and Consumer service's scope. If either the Producer or the Consumer service's 
	 * scope is null, then the wire object scope is null.
	 *
	 * @return the wire's scope, intersection of the wire Producer and Consumer scope.
	 */
	public String[] getScope(){
		return scope;
	}
	
	/**
	 * Calculate the scope of the wire in the case the consumer and producer attached service are composite.
	 * Care must be taken if one of the service scope is or contains the wildchar "*", indicating that it will
	 * accept all scope from the othet side.
	 * Otherwise the scope is calculated as the intersection of the producer and consumer scopes.
	 * 
	 */
	private void calculateScope(){
	
		scope = null;
		
		if( consumerScope != null && producerScope != null){
			
			int i, j;
			boolean producerHasScopeAll = false, consumerHasScopeAll = false;
			
			for ( i = 0;i < consumerScope.length;i++){
				if( consumerScope[i].equals("*")){
					consumerHasScopeAll = true;
				}
			}
			
			for( j = 0;j < producerScope.length;j++){
				if( producerScope[j].equals("*")){
					producerHasScopeAll = true;
				}
			}
			
			if( consumerHasScopeAll && !producerHasScopeAll){
				scope = new String[producerScope.length];
				for(i=0;i<scope.length;i++){
					scope[i] = producerScope[i];
				}
			} else if( !consumerHasScopeAll && producerHasScopeAll) {
				scope = new String[consumerScope.length];
				for(i=0;i<scope.length;i++){
					scope[i] = consumerScope[i];
				}
			} else if( consumerHasScopeAll && producerHasScopeAll) {
				scope = new String[]{"*" };
				
			} else {
			
				ArrayList scopeArray = new ArrayList();
				
				for( i = 0; i < consumerScope.length ; i++){
					for( j =0; j < producerScope.length ; j++){
						if( producerScope[j].equals( consumerScope[i])){
							scopeArray.add( producerScope[j]);
						}
					}
				}
				
				scope = new String[scopeArray.size()];
				for( i = 0; i<scope.length;i++){
					scope[i] = (String)scopeArray.get(i);
				}
			}
		} 
			
	}
	
	/**
	 * Check whether the given name is within this wire's scope
	 *
	 * @param name The scope name.
	 * @return True, if the scope name is contained within the wire scopes. False otherwise.
	 */
	public boolean hasScope(String name){
		if( scope != null ) {
			for( int i= 0; i < scope.length; i++) {
				if( scope[i].equals( name) || scope[i].equals("*"))
					return true;
			}
		}
		
		return false;
	}

}
