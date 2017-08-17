package com.ptoceti.osgi.obix.impl.resources.server;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : HistoryRollupServerResource.java
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


import java.util.List;

import org.restlet.resource.Post;

import com.google.inject.Inject;

import com.ptoceti.osgi.obix.cache.HistoryCache;
import com.ptoceti.osgi.obix.contract.HistoryRollupIn;
import com.ptoceti.osgi.obix.contract.HistoryRollupOut;
import com.ptoceti.osgi.obix.contract.HistoryRollupRecord;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.resources.HistoryResource;
import com.ptoceti.osgi.obix.resources.HistoryRollupResource;
import com.ptoceti.osgi.obix.resources.ResourceException;

public class HistoryRollupServerResource extends AbstractServerResource implements HistoryRollupResource {

	private HistoryCache cache;
	
	@Inject
	public HistoryRollupServerResource(HistoryCache cache) {
		this.cache = cache;
	}
	
	@Post
	public HistoryRollupOut rollupHistory(HistoryRollupIn in) throws ResourceException{
		
		String historyUri = HistoryResource.baseuri.concat("/").concat((String)getRequest().getAttributes().get(HistoryResource.HISTORY_URI)).concat("/");

		HistoryRollupOut result = new HistoryRollupOut();
		
		try {
			
			List<HistoryRollupRecord> records = cache.getRollUprecords(historyUri, in.getLimit(), in.getStart(), in.getEnd(), in.getInterval());
			if( records.size() > 0) {
				
				result.setStart(records.get(0).getStart());
				result.setEnd(records.get(records.size() - 1).getEnd());
				result.setCount(records.size());
				
				for( HistoryRollupRecord record : records){
					result.getDataList().addChildren(record);
				}
			}
			
		}  catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".queryHistory", ex);
		}
		
		return result;
	}

}
