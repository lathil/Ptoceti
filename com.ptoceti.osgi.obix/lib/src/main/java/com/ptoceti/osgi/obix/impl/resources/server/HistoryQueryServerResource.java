package com.ptoceti.osgi.obix.impl.resources.server;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : HistoryQueryServerResource.java
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


import java.util.Date;
import java.util.List;

import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.google.inject.Inject;
import com.ptoceti.osgi.obix.contract.HistoryFilter;
import com.ptoceti.osgi.obix.contract.HistoryQueryOut;
import com.ptoceti.osgi.obix.contract.HistoryRecord;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.domain.HistoryDomain;
import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.resources.HistoryQueryResource;
import com.ptoceti.osgi.obix.resources.HistoryResource;
import com.ptoceti.osgi.obix.resources.ResourceException;

public class HistoryQueryServerResource extends AbstractServerResource implements HistoryQueryResource{

	private HistoryDomain historyDomain;
	
	@Inject
	public HistoryQueryServerResource(HistoryDomain domain) {
		historyDomain = domain;
	}
	
	@Post
	public HistoryQueryOut queryHistory(HistoryFilter in) throws ResourceException {
		
		String historyUri = HistoryResource.baseuri.concat("/").concat((String)getRequest().getAttributes().get(HistoryResource.HISTORY_URI)).concat("/");

		HistoryQueryOut result = new HistoryQueryOut();
		
		try {
			List<HistoryRecord> records = historyDomain.getRecords(historyUri, in.getLimit(), in.getStart(), in.getEnd());
			
			Abstime start = null;
			Abstime end = null;
			
			for( HistoryRecord record : records) {
				
				if(start == null && end == null){
					start = record.getTimeStamp();
					end = record.getTimeStamp();
				} else {
					
					if( ((Date)record.getTimeStamp().getVal()).compareTo( (Date)start.getVal() )  < 0){
						start = record.getTimeStamp();
					} else if( ((Date)record.getTimeStamp().getVal()).compareTo( (Date)end.getVal() )  > 0){
						end = record.getTimeStamp();
					}
				}
				
				
				result.addToDataList(record);
			}
			
			result.setCount(records.size());
			result.setStart(start);
			result.setEnd(end);
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".queryHistory", ex);
		}
		
		return result;
	}

}
