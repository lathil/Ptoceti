package com.ptoceti.osgi.wireadmin.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : WireAdmin
 * FILENAME : DisconnectedWire.java
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


import java.util.Dictionary;

import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

/**
 * A specification for a wire for which we do not have consumer and producers pid. However we have classne and properties that allows us to buid a filter
 * 
 * @author LATHIL
 *
 */
public class DisconnectedWire {

	private Dictionary properties;
	private Filter wireFilter;
	
	private String producerPID = null;
	
	private String consumerPID = null;

	private Filter consumerFilter = null;
	private Filter producerFilter = null;
	
	
	public DisconnectedWire(String producerPID, String producerFilter, String consumerPID, String consumerFilter, Dictionary properties) {
				
		this.setProducerPID(producerPID);
		StringBuffer producerBuffFilter = new StringBuffer();
		
		if( producerFilter != null && producerFilter.length() > 0) {
			String trimmedProducerFilter = producerFilter.trim();
			if( !trimmedProducerFilter.startsWith("(")) trimmedProducerFilter = "(" + trimmedProducerFilter;
			if( !trimmedProducerFilter.endsWith(")")) trimmedProducerFilter = trimmedProducerFilter + ")";
			
			producerBuffFilter.append(trimmedProducerFilter);
			
			try {
				this.setProducerFilter(Activator.bc.createFilter(producerBuffFilter.toString()));
			} catch (InvalidSyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	
		
		this.setConsumerPID(consumerPID);
		StringBuffer consumerBuffFilter = new StringBuffer();
		
		if( consumerFilter != null && consumerFilter.length() > 0) {
			String trimmedConsumerFilter = consumerFilter.trim();
			if( !trimmedConsumerFilter.startsWith("(")) trimmedConsumerFilter = "(" + trimmedConsumerFilter;
			if( !trimmedConsumerFilter.endsWith(")")) trimmedConsumerFilter = trimmedConsumerFilter + ")";
			
			consumerBuffFilter.append(trimmedConsumerFilter);
			
			try {
				this.setConsumerFilter(Activator.bc.createFilter(consumerBuffFilter.toString()));
			} catch (InvalidSyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		this.setProperties(properties);
	}
	
	public boolean producerMatches(ServiceReference sReg) {
		if( sReg != null && producerFilter != null && getProducerFilter().match(sReg)) {
			setProducerPID(sReg.getProperty(Constants.SERVICE_PID).toString());
			return true;
		}
		
		return false;
	}
	
	public boolean consumerMatches(ServiceReference sReg) {
		if( sReg != null && consumerFilter != null && getConsumerFilter().match(sReg)) {
			setConsumerPID(sReg.getProperty(Constants.SERVICE_PID).toString());
			return true;
		}
		
		return false;
	}
	
	public boolean isComplete() {
		return getConsumerPID() != null && getProducerPID() != null;
	}

	public String getProducerPID() {
		return producerPID;
	}

	protected void setProducerPID(String producerPID) {
		this.producerPID = producerPID;
	}

	public String getConsumerPID() {
		return consumerPID;
	}

	protected void setConsumerPID(String consumerPID) {
		this.consumerPID = consumerPID;
	}

	public Dictionary getProperties() {
		return properties;
	}

	protected void setProperties(Dictionary properties) {
		this.properties = properties;
	}

	public Filter getConsumerFilter() {
		return consumerFilter;
	}

	private void setConsumerFilter(Filter consumerFilter) {
		this.consumerFilter = consumerFilter;
	}

	public Filter getProducerFilter() {
		return producerFilter;
	}

	private void setProducerFilter(Filter producerFilter) {
		this.producerFilter = producerFilter;
	}


}
