

package com.ptoceti.osgi.wireadmin.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : WireAdmin
 * FILENAME : WireDispatcher.java
 * 
 * This file is part of the Ptoceti project. More information about
 * this project can be found here: http://www.ptoceti.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2013 - 2014 ptoceti
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

import org.osgi.service.wireadmin.Consumer;
import org.osgi.service.wireadmin.Producer;
import org.osgi.service.wireadmin.Wire;

import org.osgi.service.log.LogService;

import java.util.Vector;

public class WireDispatcher implements Runnable {
	
	private boolean mustStop;
	private Thread myThread;
	private Vector dispatchingList;
	
	public WireDispatcher(){
	
		mustStop = false;
		dispatchingList = new Vector();
		
		myThread = new Thread( this );
		myThread.start();
	}
	
	public void run(){
		
		while(mustStop == false){
			
			try {
				DispatcherDetails dispDt = getFirstDispatcherDetailsFromList();
				Object target = dispDt.getTarget();
				Wire[] wires = dispDt.getWires();
				if( wires != null)
				{
					if( dispDt.isProducer ) {
						((Producer) target ).consumersConnected( dispDt.getWires());
					} else {
						((Consumer) target ).producersConnected( dispDt.getWires());
					}
				}
				else
				{
					if( target instanceof Producer) {
						Activator.log(LogService.LOG_INFO, "WireDispacther: Error calling consumerConnected :" + ((Producer)target).toString() );
					} else if ( target instanceof Consumer) {
						Activator.log(LogService.LOG_INFO, "WireDispacther: Error calling producerConnected :" + ((Consumer)target).toString() );
					}
				}
			} catch (InterruptedException e) {
			}
		}
	}
		
	public void stop(){
		mustStop = true;
		myThread.interrupt();
	}
	
	public synchronized DispatcherDetails getFirstDispatcherDetailsFromList()
	 throws InterruptedException {
		
		DispatcherDetails result = null;
		if (!(dispatchingList.size() > 0)) wait();
		
		result = (DispatcherDetails) dispatchingList.firstElement();
		dispatchingList.removeElement( result );
		return result;
	}
	

	/**
	 * Add the list of wireas to pass on to a producer. 
	 * 
	 * @producer The producer service
	 * @wires[] The list of wires
	 *
	 */
	public synchronized void addProducerWires( Producer producer, Wire wires[]){
		DispatcherDetails dispDt =  new DispatcherDetails(true, (Object) producer, wires );
		dispatchingList.add( dispDt );
		notify();
	}
	
	/**
	 * Add the list of wireas to pass on to a consumer
	 * 
	 * @consumer The consumer service
	 * @wires[] The list of wires
	 *
	 */
	public synchronized void addConsumerWires( Consumer consumer, Wire wires[]){
		DispatcherDetails dispDt =  new DispatcherDetails(false,(Object) consumer, wires );
		dispatchingList.add( dispDt );
		notify();
	}

	private class DispatcherDetails {
		
		private Object target;
		private Wire wires[];
		private boolean isProducer;
		
		/**
		 * A DispatcherDetails record.
		 * 
		 * @isProducer A boolean to indicate whether the target is to be taken as a consumer or a producer.
		 * This is important because services could implement botha consumer and a consumer
		 * @target The Consumer or Producer or both service.
		 * @wires[] The list of wires
		 *
		 */
		public DispatcherDetails( boolean isProducer, Object target, Wire wires[]) {
			this.target = target;
			this.wires = wires;
			this.isProducer = isProducer;
			
		}
		
		public boolean isProducer()
		{
			return isProducer;
		}
		
		public Object getTarget(){
			return target;
		}
		
		public Wire[] getWires(){
			return wires;
		}
	}
}

