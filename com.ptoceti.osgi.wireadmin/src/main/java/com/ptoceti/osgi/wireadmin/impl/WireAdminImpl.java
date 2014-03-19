
package com.ptoceti.osgi.wireadmin.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : Ptoceti
 * PROJECT : WireAdmin
 * FILENAME : WireAdminImpl.java
 * 
 * This file is part of the Ptoceti project. More information about
 * this project can be found here: http://www.ptoceti.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2013 Ptoceti
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

import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;

import org.osgi.service.log.LogService;
import org.osgi.service.cm.ManagedService;

import org.osgi.service.wireadmin.WireAdmin;
import org.osgi.service.wireadmin.WireConstants;
import org.osgi.service.wireadmin.WireAdminListener;
import org.osgi.service.wireadmin.WireAdminEvent;
import org.osgi.service.wireadmin.Consumer;
import org.osgi.service.wireadmin.Producer;
import org.osgi.service.wireadmin.Wire;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;



public class WireAdminImpl implements WireAdmin, ManagedService, ServiceListener {

	// A HashMap table with the consumer PID as key, and the corresponding service reference as value
	private Hashtable consumersTable;
	// A HashMap table with the producer PID as key, and the corresponding service reference as value
	private Hashtable producersTable;
	// A list of all the wires managed by the WireAdmin class
	private ArrayList wires;
	
	// The class responsable for responding to (un)registering event from producer services
	private ProducerServiceListener producerSerListener;
	// The list of services pid the producer listerer is registered to
	private ArrayList producerListenerList;
	// The class responsable for responding to (un)registering event from consumer services
	private ConsumerServiceListener consumerSerListener;
	// The list of services pid the consumer listener is registered to
	private ArrayList consumerListenerList;
	// a table of services that have subscribed to receive WireAdminEvents. ServiceReference is key, Service is value
	private Hashtable wireAdminEventListeners;
	
	// A count used for generating wire's PID.
	private int selfNamedWireCount = 0;
	
	// The object responsible for notifying Producers and Consumers that a new wire is created
	private WireDispatcher asyncWireDispatcher;
	// The object responsible for notifying WireAdminEventListener that a event is generated
	private EventDispatcher asyncEventDispatcher;
	
	private ServiceRegistration wireAdminImplReg;
	private ServiceReference wireAdminImplRef;
	
	public static String wireConfigProperty = "com.ptoceti.osgi.wireadmin.config.wires.file";
	private String configFile;
	
	/**
	 * WireAdminImpl's constructor.
	 *
	 * Initialise the two HashTables that will keep track of the Consumers and Producers services
	 * that are needed by the wires as they are created.
	 * Create an empty ArrayList that will keep track of all the wire instances.
	 * Create two ServicesListener object that will receive notification of Producer and Consumer
	 * services registering and unregistering.
	 * Finally, create an instance of the AsynchronousWireDispatcher that will call asynchronously instances
	 * of Producer and Consumer services with new wires lists.
	 *
	 *
	 */
	public WireAdminImpl(){
	
		consumersTable = new Hashtable();
		producersTable = new Hashtable();
		
		wires = new ArrayList();
		
		producerSerListener = new ProducerServiceListener();
		consumerSerListener = new ConsumerServiceListener();
		
		producerListenerList = new ArrayList();
		consumerListenerList = new ArrayList();
		
		wireAdminEventListeners = new Hashtable();
		
		asyncWireDispatcher = new WireDispatcher();
		asyncEventDispatcher = new EventDispatcher();
		
		String[] clazzes = new String[] {
				ManagedService.class.getName(),
				WireAdmin.class.getName()
		};
		Hashtable properties = new Hashtable();
		properties.put(Constants.SERVICE_PID, this.getClass().getName());
		properties.put(WireConstants.WIREADMIN_PID, this.getClass().getName());
		wireAdminImplReg = Activator.bc.registerService(clazzes, this, properties );
		wireAdminImplRef = wireAdminImplReg.getReference();
		
		Activator.log(LogService.LOG_INFO, "Registered " + this.getClass().getName()  + " as " + WireAdmin.class.getName());
		
		// Register this class to listen to REGISTER and UNREGISTER events from WireAdminListener services.
		String filter = "(objectclass=" + WireAdminListener.class.getName() + ")";
		try {
			// Register ...
			Activator.bc.addServiceListener( this, filter);
			// Get hold of services that implements WireAdlinListener that have already regiser in the framework.
			ServiceReference[] srRefs = Activator.bc.getServiceReferences( WireAdminListener.class.getName(), null);
			
			if( srRefs != null ) {
				synchronized( wireAdminEventListeners ){
					for( int i=0; i> srRefs.length; i++) {
						ServiceReference sRef = srRefs[i];
						wireAdminEventListeners.put( sRef, Activator.bc.getService(sRef));
					}
				}
			}
			
		} catch ( InvalidSyntaxException e ) {
			
		}
		
	}
	
	/**
	 * Receives notification that a service has had a life cycle change.
	 *
	 * We are interested in receiving notification of WireAdminListener services that register
	 * an unregister to and from the framework. This allows us to build dynamically a list of
	 * services to which to send WireAdminEvent(s) as they are produces by thi bundle.
	 *
	 * @param event The ServiceEvent describing the type of life cycle change.
	 */
	public void serviceChanged( ServiceEvent event ) {
		
		ServiceReference sr = event.getServiceReference();
		switch(event.getType()) {
			case ServiceEvent.REGISTERED: {
					synchronized( wireAdminEventListeners ){
						if( !wireAdminEventListeners.containsKey( sr)) {
							wireAdminEventListeners.put( sr, Activator.bc.getService( sr ));
						}
					}
				}
				break;
			case ServiceEvent.UNREGISTERING: {
					synchronized( wireAdminEventListeners ){
						if( wireAdminEventListeners.containsKey( sr )) {
							Activator.bc.ungetService( sr );
							wireAdminEventListeners.remove( sr );
						}
					}
				}
				break;
			}
	}
	
	/**
	 * ManagedServiceFactory Interface method
	 *
	 * @param The dictionary containing all the configuration.
	 */
	public void updated(Dictionary properties) {
		
		if( properties == null ){
			
			while(! wires.isEmpty()) {
				WireImpl wire;
				synchronized(wires){				
					wire = (WireImpl)wires.get(0);
				}
				this.deleteWire(wire);
			}
		} else {
			
		  for( Enumeration e = properties.keys();e.hasMoreElements();){
			  String key = (String) e.nextElement();
			  if( key.equals(wireConfigProperty)){
				  Object wireProps = properties.get(key);
				  if(wireProps instanceof String){
					  configFile = (String) wireProps;
				  }
			  }
		  }
		
		  try {
			  if( configFile != null) {
				  URL configUrl = null;
				  
				  if( configFile.startsWith("file:")){
					  String configFilePath = configFile.substring("file:".length());
					  
					  File file = new File(configFilePath);
						if( file.exists() && !file.isDirectory()) {
							try {
								configUrl= file.toURI().toURL();
							} catch (MalformedURLException e) {
								Activator.log(LogService.LOG_ERROR, "Error creating url for file path: " + configFile);
							}
						}
				  } else {
					  configUrl = Activator.getResourceStream(configFile);
				  }
				  
				  if( configUrl != null){
					  InputStream configFileStream = configUrl.openStream();
					  // Create an xml file wire configuration reader, and pass it the wireadministrator as delegate
					  WireConfig wConfig = new WireConfig(this);
					  // , tell it to read the file. This in turn will call back the wireadministrator to create the wires.
					  wConfig.parse(configFileStream);
					  configFileStream.close();
				  }
			  }
		  } catch (java.io.IOException e) {
			  Activator.log(LogService.LOG_INFO, "Configuration file: " + configFile + " could not be found.");
		  }
		}
	}
	
	/**
	 * Release all resources: wires, Consumers an Producers services, asynchronous caller.
	 *
	 *
	 */
	 
	public void releaseAll() {
	
		synchronized( wires ) {
			for( int i = 0; i < wires.size(); i++) {
				WireImpl wire = (WireImpl) wires.get(i);
				wire.invalidate();
			}
		}
		
		for (Enumeration e = consumersTable.elements();e.hasMoreElements();){
			ServiceReference sRef = (ServiceReference) e.nextElement();
			Consumer consumer = (Consumer)Activator.bc.getService(sRef);
			consumer.producersConnected( null );
			Activator.bc.ungetService(sRef);
		}
		
		for (Enumeration e = producersTable.elements();e.hasMoreElements();){
			ServiceReference sRef = (ServiceReference) e.nextElement();
			Producer producer = (Producer)Activator.bc.getService(sRef);
			producer.consumersConnected( null);
			Activator.bc.ungetService(sRef);
		}
		
		// stop the asynchronous caller last. It will first call every Consumer and Producer
		// service with null list of wires.
		asyncWireDispatcher.stop();
	}
	
	
	/**
	 * Create a wire object.
	 *
	 * The WireAdminImpl object manage the creation of wires object. It add the wire to its internal list and
	 * try to connect it to the indicated Consumer and Producer services.
	 * To manage the connection of the wire to Producer and Consumer services, it registers two of its internal
	 * class as event listerner for these services. This allow to connect the wire automaticaly when the services
	 * are registering to the framework, and to disconnect the wire whe theses services are unregistering.
	 * If whe the wire is created, the Producer and Consumer services have not yet registered, the wire is left
	 * unconnected, but valid. If these services have already registered, the wire is immediately connected.
	 *
	 * When the wire is being connected, the Producer and Consumer receive an updated list of wire objects.
	 * When the wire is being creates, its properties collection is completed with the PID's of the Consumer and 
	 * Producer ends, and with a unique wire PID generated by the WireAdminImpl.
	 *
	 * @param producerPID The persistent identifier for the producer service.
	 * @param consumerPID The persistent identifier for the consumer service.
	 * @param properties The wire's properties as (key, values) tuples.
	 * @return The create wire object. Return null if failed.
	 *
	 */
	 
	public Wire createWire(String producerPID, String consumerPID, Dictionary properties) {
	
		// Create a new wire with the provided parameters..
		
		// Create a new property table for this wire
		Hashtable props = new Hashtable();
		// If the property table given by the method is not null, ..
		if( properties != null ){
			// .. then we are copying all the (key,values) tuples over.
			for ( Enumeration e = properties.keys(); e.hasMoreElements(); ) {
				String key = (String)e.nextElement();
				props.put( key, properties.get(key));
			}
		}
		// add the new constants to the property table
		props.put( WireConstants.WIREADMIN_PRODUCER_PID, producerPID);
		props.put( WireConstants.WIREADMIN_CONSUMER_PID, consumerPID);
		// add a PID constants specific to this wire.
		props.put( WireConstants.WIREADMIN_PID, getNewWirePID());
		// Create the wire and assign the new properties
		WireImpl wire = new WireImpl( props, this );
		// Everything is fine so far, add the wire to the internal list,
		wires.add( wire );
		// send a event indicating that a wire has benn created.
		sendEvent(WireAdminEvent.WIRE_CREATED, wire, null);
		
		if(! producerListenerList.contains(producerPID)) {
			producerListenerList.add(producerPID);
			
			try { // set ourselved as listener to the register / unregister event from this service
				Activator.bc.addServiceListener( producerSerListener, this.getProducerPidFilter() );
			} catch ( InvalidSyntaxException e ) {
				Activator.log(LogService.LOG_INFO, "Error in filter string while registering LogServiceListener." + e.toString());
				return(null);
			}
		}
		
		// if we do not have yet a reference to the producer service, ..
		if( ! producersTable.containsKey(producerPID)){
			// in case the service is already registered, we send a REGISTERED event to its listener.
			ServiceReference[] producerSerRef = null;
			try {
				producerSerRef = Activator.bc.getServiceReferences( Producer.class.getName(), "(" + Constants.SERVICE_PID + "=" + producerPID + ")"  );
			} catch ( InvalidSyntaxException e) {}
			if( producerSerRef != null && producerSerRef.length >0 ) {
				producerSerListener.serviceChanged(new ServiceEvent( ServiceEvent.REGISTERED, producerSerRef[0] ));
			} 
		} else {
			// the producer service is already up and running, we have a service reference to it.
			ServiceReference producerServiceRef= (ServiceReference) producersTable.get( producerPID );
			wire.setProducer( producerServiceRef );
		}
		
		if(! consumerListenerList.contains(consumerPID)) {
			consumerListenerList.add(consumerPID);
			
			try { // set ourselved as listener to the register / unregister event from this service
				Activator.bc.addServiceListener( consumerSerListener, this.getConsumerPidFilter() );
			} catch ( InvalidSyntaxException e ) {
				Activator.log(LogService.LOG_INFO, "Error in filter string while registering LogServiceListener." + e.toString());
				return(null);
			}
		}
		
		// if we do not have yet a reference to the consumer service, ..
		if( ! consumersTable.containsKey(consumerPID)){
			// in case the service is already registered, we send a REGISTERED event to its listener.
			ServiceReference[] consumerSerRef = null;
			try {
				consumerSerRef = Activator.bc.getServiceReferences( Consumer.class.getName(), "(" + Constants.SERVICE_PID + "=" + consumerPID + ")"  );
			} catch ( InvalidSyntaxException e) {}
			if( consumerSerRef != null && consumerSerRef.length > 0) {
				consumerSerListener.serviceChanged(new ServiceEvent( ServiceEvent.REGISTERED, consumerSerRef[0] ));
			} 
		} else {
			// the consumer service is already up and running, we have a service reference to it.
			ServiceReference consumerServiceRef= (ServiceReference) consumersTable.get( consumerPID );
			wire.setConsumer( consumerServiceRef );
		}
	
		Activator.log(LogService.LOG_INFO,
				"Created wire PID: " + wire.getProperties().get( WireConstants.WIREADMIN_PID) + ", "
				+ "ConsumerPID: " + wire.getConsumerPID() + ", "
				+ "ProducerPID: " + wire.getProducerPID());
		// and return the result.
		return wire;
	}
	

	/**
	 * Delete a wire object.
	 * 
	 * The WireAminImpl object keep a list of all the wires it has created. A wire represents a connection between
	 * a Producer and a Consumer service. The wire created are not created if the Consumer and the Producer
	 * services have not yet registered to the framework.
	 * When deleting a wire object, the WireAdminImpl service first removes the wire from it internal list. Second,
	 * it check whether the wire was connected to a Consumer or a Producer service. If it was, the WireAdminImpl
	 * object recompute the wire list for each of the Consumer and Producer services, and assign them via the
	 * AsynchronousWireDispatcher object. If there is no wires left for the Consumer and : or the Producer service, the wire
	 * list will be emty.
	 * After being deleted from the list, the wire object is marked as invalid.
	 *
	 * @param wire The Wire object to be deleted.
	 */
	public void deleteWire(Wire wire){
	
		synchronized( wires ) {
			if( wires.contains( wire )){
				WireImpl wi = (WireImpl) wire;
				// remove the wire from the list
				wires.remove( wire );
				
				// if the wire was connected, recalculate the wire list for both the Consumer and the Producer services.
				if( wi.isConnected()) {
					asyncWireDispatcher.addConsumerWires(wi.getConsumer(), getConsumerWires(wi.getConsumerPID()));
					asyncWireDispatcher.addProducerWires(wi.getProducer(), getProducerWires(wi.getProducerPID()));
				}
				
				// tell the wire to unregister its Consumer and Producer services.
				wi.invalidate();
				
				// send a event indicating that a wire has been deleted.
				sendEvent(WireAdminEvent.WIRE_DELETED, wire, null);
			}
		}
	}
	
	/**
	 * Update the properties of a wire object.
	 *
	 * The properties of the wire is updated from a new set of properties. The existing WIREADMIN_PRODUCER_PID,
	 * WIREADMIN_CONSUMER_PID and WIREADMIN_PID values are kept from the existing properties.
	 * If the wire is connected to a Producer or Consumer services, they will be called throught consumerConnected
	 * and producerConnected respectively.
	 *
	 * @param wire The Wire instance to update.
	 * @param prperties The new properties for the given wire.
	 */
	public void updateWire(Wire wire, Dictionary properties){
	
		synchronized( wires ) {
			if( wires.contains(wire)){
				WireImpl wi = (WireImpl) wire;
				
				// Clone the properties
				Hashtable props = new Hashtable();
				// If the property table given by the method is not null, ..
				if( properties != null ){
					// .. then we are copying all the (key,values) tuples over.
					for ( Enumeration e = properties.keys(); e.hasMoreElements(); ) {
						String key = (String)e.nextElement();
						props.put( key, properties.get(key));
					}
				}
				
				// copy over the PID properties from the old property collection
				Dictionary wiProps = wi.getProperties();
				props.put( WireConstants.WIREADMIN_PRODUCER_PID, wiProps.get(WireConstants.WIREADMIN_PRODUCER_PID ));
				props.put( WireConstants.WIREADMIN_CONSUMER_PID, wiProps.get(WireConstants.WIREADMIN_CONSUMER_PID ));
				props.put( WireConstants.WIREADMIN_PID, wiProps.get(WireConstants.WIREADMIN_PID ));
				
				// set the wire new properties
				wi.setProperties( props );
				// if the Consumer or Producer services are connected, update their wires lists.
				if( wi.isConnected()){
					asyncWireDispatcher.addConsumerWires(wi.getConsumer(), getConsumerWires(wi.getConsumerPID()));
					asyncWireDispatcher.addProducerWires(wi.getProducer(), getProducerWires(wi.getProducerPID()));
				}
				
				// send a event indicating that a wire has been updated.
				sendEvent(WireAdminEvent.WIRE_UPDATED , wire, null);

			}
		}
	}
	
	/**
	 * Return a list of wires that matches the given filter
	 *
	 * The internal list of wires is matched against a filter. The wires that match the 
	 * filter are added to a new wire array that is return as result.
	 * The filter is built from a string that express RFC1960 syntax. The matching is
	 * done on each wire properties dictionnary.
	 * If the provided filter string is null, the array return contains all wires.
	 * If the no wires are matched on the filter criteria, null is returned.
	 *
	 * @param filter An RFC1960 filter string.
	 * @throws org.osgi.framework.InvaalidSyntaxException if the filter has an invalid syntax.
	 * @return An array of wires, null if no wires are matched
	 */
	public Wire[] getWires(String filter) throws InvalidSyntaxException {
	
		Wire[] wireArray = null;
		
		synchronized( wires ) {
			// if filter is null
			if( filter == null ) {
				// return all wires in the list
				wireArray = (Wire[]) wires.toArray(new Wire[wires.size()]);
			} else {
				Filter wireFilter = Activator.bc.createFilter(filter);
				ArrayList result = new ArrayList();
				// try to match each wire in the list afainst the filter
				for( int i = 0; i < wires.size(); i++ ){
					WireImpl wire = (WireImpl)wires.get(i);
					if( wireFilter.match(wire.getProperties())){
						// if matches, add it to the new list
						result.add( wire );
					}
				}
				// return results.
				if( result.size() > 0 ) {
					wireArray = (Wire[]) result.toArray(new Wire[result.size()]);
				}
			}
		}
		
		return wireArray;
	}

	private String getNewWirePID(){
	
		String wirePID = Wire.class.getName() + ":" + ( new Integer( selfNamedWireCount)).toString();
		selfNamedWireCount++;
		return wirePID;
	}
	
	/**
	 * Parse the internal list of wires and extract from it an array of wires that are
	 * connected to a given Consumer service.
	 *
	 * @param consumerPID The persistant identificator for the wire's consumer service
	 * @return Wire[] An array of wires that have an consumer service with the given PID
	 */
	private Wire[] getConsumerWires(String consumerPID) {
	
		Wire[] result = null;
		ArrayList wiresArray = new ArrayList();
		
		if( consumersTable.containsKey( consumerPID )){
			
			synchronized( wires ) {
				for( int i = 0; i < wires.size(); i++ ){
					WireImpl w = (WireImpl) wires.get(i);
					// if the PID of the service matches that of the wire producer PID
					if(( w.getConsumerPID().equals( consumerPID )) && (w.isConnected())){
						wiresArray.add(w);
					}
				}
			}
		}
		
		result = new Wire[wiresArray.size()];
		for(int i = 0; i < wiresArray.size(); result[i] = (Wire)wiresArray.get(i++));
		return result;
	}
	
	/**
	 * Parse the internal list of wires and extract from it an array of wires that are
	 * connected to a given Producer service.
	 *
	 * @param producerPID The persistant identificator for the wire's producer service
	 * @return Wire[] An array of wires that have an producer service with the given PID
	 */
	private Wire[] getProducerWires(String producerPID) {
	
		Wire[] result;
		ArrayList wiresArray = new ArrayList();
		
		if( producersTable.containsKey( producerPID )){
			
			synchronized( wires ) {
				for( int i = 0; i < wires.size(); i++ ){
					WireImpl w = (WireImpl) wires.get(i);
					// if the PID of the service matches that of the wire producer PID
					if(( w.getProducerPID().equals( producerPID ))&&(w.isConnected())){
						wiresArray.add(w);
					}
				}
			}
		}
		
		result = new Wire[wiresArray.size()];
		for(int i = 0; i < wiresArray.size(); result[i] = (Wire)wiresArray.get(i++));
		return result;
	}
	
	/**
	 * Send a WireAdminEvent to listening WireAdminListener objects.
	 *
	 */
	public void sendEvent( int eventType, Wire wire, Throwable exception ) {
	
		if(( eventType != 0 ) && ( wire != null )) {
			WireAdminEvent event = new WireAdminEvent(this.wireAdminImplRef, eventType, wire, exception );
			asyncEventDispatcher.sendEvent( event );
		}
	}
	
	private String getProducerPidFilter() {
		
		String result = "";
		
		for(int i=0; i < producerListenerList.size(); i++ ) {
			result = result + "(" + Constants.SERVICE_PID + "=" + (String)producerListenerList.get(i) + ")";
		}
		
		if( producerListenerList.size() > 1) {
			result = "(|" + result + ")";
		}
		
		return result;
	}
	
	private String getConsumerPidFilter() {
	
		String result = "";
		
		for(int i=0; i < consumerListenerList.size(); i++ ) {
			result = result + "(" + Constants.SERVICE_PID + "=" + (String)consumerListenerList.get(i) + ")";
		}
		
		if( consumerListenerList.size() > 1) {
			result = "(|" + result + ")";
		}
		
		return result;
	}
	/**
	 * This class is registered as a listener to register / register events from producer services.
	 * Whe such an event arise, the class will update the internal lookup table with the service
	 * reference ( matching the service PID ), and update the wires that are waiting for such a service
	 * with the service object.
	 *
	 */
	private class ProducerServiceListener implements ServiceListener {
		
		public void serviceChanged( ServiceEvent event ) {
			
				ServiceReference producerServiceRef = event.getServiceReference();
				String producerPID = (String) producerServiceRef.getProperty(Constants.SERVICE_PID);
				switch(event.getType()) {
					case ServiceEvent.REGISTERED: {
						// We keep the service reference with its PID in the local lookup table
						producersTable.put( producerPID, producerServiceRef );
						Activator.log(LogService.LOG_INFO, "Producer service " + producerPID + " registered.");
						// and we give the service object to every wire that needs it as producer
						for( int i = 0; i < wires.size(); i++ ){
							WireImpl wire = (WireImpl) wires.get(i);
							// if the PID of the service matches that of the wire producer PID
							if( wire.getProducerPID().equals( producerPID )){
								wire.setProducer( producerServiceRef );
								// if the wire is now connected, update the list of its producers and consumers services
								if( wire.isConnected()) {
									asyncWireDispatcher.addConsumerWires(wire.getConsumer(), getConsumerWires(wire.getConsumerPID()));
									asyncWireDispatcher.addProducerWires(wire.getProducer(), getProducerWires(wire.getProducerPID()));
								}
							}
						}
					}
					break;
					case ServiceEvent.UNREGISTERING: {
						// we need to set, for each wire that use the service as producer, their reference to null.
						Activator.log(LogService.LOG_INFO, "Producer service " + producerPID + " unregistered.");
						for( int i = 0; i < wires.size(); i++ ){
							WireImpl wire = (WireImpl) wires.get(i);
							// if the PID of the service matches that of the wire producer PID
							if( wire.getProducerPID().equals( producerPID )){
								wire.setProducer( null );
								asyncWireDispatcher.addConsumerWires(wire.getConsumer(), getConsumerWires(wire.getConsumerPID()));
							}
						}
						// then, remove the producer reference from the lookup table.
						producersTable.remove(producerPID);
					}
					break;
				}
		}
	}
	
	/**
	 * This class is registered as a listener to register / register events from consumer services.
	 * Whe such an event arise, the class will update the internal lookup table with the service
	 * reference ( matching the service PID ), and update the wires that are waiting for such a service
	 * with the service object.
	 *
	 */
	private class ConsumerServiceListener implements ServiceListener {
		
		public void serviceChanged( ServiceEvent event ) {
			
				ServiceReference consumerServiceRef = event.getServiceReference();
				String consumerPID = (String) consumerServiceRef.getProperty(Constants.SERVICE_PID);
				switch(event.getType()) {
					case ServiceEvent.REGISTERED: {
						// We keep the service reference with its PID in the local lookup table
						consumersTable.put( consumerPID, consumerServiceRef );
						Activator.log(LogService.LOG_INFO, "Consumer service " + consumerPID + " registered.");
						// and we give the service object to every wire that needs it as producer
						for( int i = 0; i < wires.size(); i++ ){
							WireImpl wire = (WireImpl) wires.get(i);
							// if the PID of the service matches that of the wire producer PID
							if( wire.getConsumerPID().equals( consumerPID )){
								wire.setConsumer( consumerServiceRef );
								// if the wire is now connected, update the list of its producers and consumers services
								if( wire.isConnected()) {
									asyncWireDispatcher.addConsumerWires(wire.getConsumer(), getConsumerWires(wire.getConsumerPID()));
									asyncWireDispatcher.addProducerWires(wire.getProducer(), getProducerWires(wire.getProducerPID()));
								}
							}
						}
					}
					break;
					case ServiceEvent.UNREGISTERING: {
						// we need to set, for each wire that use the service as producer, their reference to null.
						Activator.log(LogService.LOG_INFO, "Consumer service " + consumerPID + " unregistered.");
						for( int i = 0; i < wires.size(); i++ ){
							WireImpl wire = (WireImpl) wires.get(i);
							// if the PID of the service matches that of the wire producer PID
							if( wire.getConsumerPID().equals( consumerPID )){
								wire.setConsumer( null );
								asyncWireDispatcher.addProducerWires(wire.getProducer(), getProducerWires(wire.getProducerPID()));
							}
						}
						// then, remove the producer reference from the lookup table.
						consumersTable.remove(consumerPID);
					}
					break;
				}
		}
	}
	
	/**
	 * Dispacth asynchronously WireAdminEvent(s) to WireAdminListener(s) services.
	 *
	 * The event are send in the chronoligical order they are generated and passed on to this class
	 * for being broadcasted to any service register as WireAdminListener. Ie, when a WireAdminEvent
	 * must be sent, it is first pusched on in a stack. Then events at the bottom of the stack are
	 * destacked and sent to any known WireAdminListener interested in it. The process is repeated
	 * as long as they are events in the stack.
	 *
	 * The class make use of the WireAdminListener list that is tracked by the outer class WireAdminImpl.
	 * This list is constantly upated by the WireAdminImpl with the actual list of WireAdminListener(s).
	 *
	 * A Thread object call asynchronously any WireAdminListener's wireAdminEvent() method.
	 *
	 */
	
	private class EventDispatcher implements Runnable {
	
		private boolean mustStop;
		private Thread myThread;
		
		private ArrayList eventList;
		
		/**
		 * Constructor method. create and initialise the thread, create a empty WireAdminEvent stack.
		 *
		 */
		public EventDispatcher() {
		
			mustStop = false;
			eventList = new ArrayList();
			myThread = new Thread( this );
			myThread.start();
		}
		
		/**
		 * Runnable interface's run() method. Executed by the thread to parse the event stack and send the
		 * events to the WireAdminListener(s).
		 *
		 *
		 */
		public void run() {
			while( mustStop == false ) {
				
				WireAdminEvent nextEvent = null;
				
				// Get the next WireAdminEvent from the stack. 
				synchronized( eventList ) {
					try {
						// If there is no event to be sent, block the execution of the thread till receiving notification.
						while( eventList.size() == 0 ) eventList.wait();
					} catch ( InterruptedException e ) {}
					nextEvent = (WireAdminEvent)eventList.get(0);
					eventList.remove(0);
				}
				
				// If we got an event from the stack, we can proceed to send it.
				if( nextEvent != null ) {
					synchronized( wireAdminEventListeners ) {
						Enumeration sRefKeys = wireAdminEventListeners.keys();
						// We review every WireAdminListener that we know.
						while( sRefKeys.hasMoreElements()) {
							ServiceReference waListenerRef = (ServiceReference) sRefKeys.nextElement();
							WireAdminListener waListener = (WireAdminListener)wireAdminEventListeners.get( waListenerRef );
							// We got a WireAdminListener instance ..
							if( waListener != null ) {
								// we get what kind of WireAdminEvent it is interested in ..
								Integer waLEvents = (Integer) waListenerRef.getProperty(WireConstants.WIREADMIN_EVENTS);
								if( waLEvents != null ) {
									// .. if our WireAdminEvent is the one it is interested in ..
									if(( waLEvents.intValue() & nextEvent.getType()) != 0 ) {
										// ... then we send it to him.
										waListener.wireAdminEvent( nextEvent );
									}
								}
							}
						}
					}
				}
			}
		}
		
		/**
		 * Stop the thread.
		 */ 
		public void stop(){
			mustStop = true;
		}
		
		/**
		 * Push a WireAdminEvent to be dispatched to the WireAdminListener(s) into the stack.
		 *
		 * @param wEvent The WireAdminEvent to be sent.
		 */
		public void sendEvent( WireAdminEvent wEvent ) {
			
			synchronized( eventList ) {
				eventList.add( wEvent );
				eventList.notify();
			}
		}

	}
}
