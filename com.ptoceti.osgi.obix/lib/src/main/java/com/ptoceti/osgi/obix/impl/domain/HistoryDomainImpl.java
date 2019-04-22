package com.ptoceti.osgi.obix.impl.domain;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : HistoryDomainImpl.java
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


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.ptoceti.osgi.obix.contract.History;
import com.ptoceti.osgi.obix.contract.HistoryRecord;
import com.ptoceti.osgi.obix.contract.HistoryRollupRecord;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.domain.HistoryDomain;
import com.ptoceti.osgi.obix.impl.entity.EntityException;
import com.ptoceti.osgi.obix.impl.entity.EntityType;
import com.ptoceti.osgi.obix.impl.entity.ObjEntity;
import com.ptoceti.osgi.obix.impl.front.resources.HistoryServerResource;
import com.ptoceti.osgi.obix.impl.service.ObixTimeSeriesHandler;
import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Int;
import com.ptoceti.osgi.obix.object.Reltime;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.object.Val;
import com.ptoceti.osgi.timeseries.TimeSeriesService;

public class HistoryDomainImpl extends AbstractDomain implements  HistoryDomain{

	@Override
	public History make(Contract of,  String displayName) throws DomainException {

	// try to get hold of the TimeSeries service
	TimeSeriesService timesSerieService = ObixTimeSeriesHandler.getInstance().getTimeSeriesService();
	if (timesSerieService == null) {
	    // if we can't get the time series service, we can't create the
	    // history.
	    return null;
	}

		String timeStamp = (new Long (Calendar.getInstance().getTimeInMillis())).toString();
		
		History history = new History(timeStamp);
		
		Long initialTimeStamp = Long.valueOf(((Calendar.getInstance()).getTimeInMillis()));
		Abstime start = new Abstime("", new Date(initialTimeStamp));
		history.setStart(start);
		
		Abstime end = new Abstime("", new Date(initialTimeStamp));
		history.setEnd(end);
		
		history.setCount(0);
		
		history.setDisplayName(displayName);

        history.setHref(new Uri("uri", HistoryServerResource.baseuri.concat("/").concat(timeStamp).concat("/")));
		
		// create a hidden list that will contain all history records
		com.ptoceti.osgi.obix.object.List recordsList = new com.ptoceti.osgi.obix.object.List("historyrecords");
		recordsList.setOf(of);
		history.addChildren(recordsList);
		
		ObjEntity objEnt = new ObjEntity(history);
		try {
			objEnt.create();
	    String[] fieldNames = { "value" };
	    timesSerieService.setupMeasurement(history.getHref().getPath(), fieldNames);
		} catch(EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".make", ex);
		}
		
		return (History) objEnt.getObixObject();
	}

	@Override
	public void remove(String uri) throws DomainException {
		
		History obixObj = new History();
		obixObj.setHref(new Uri("href", uri));
		ObjEntity objEnt = new ObjEntity(obixObj);
		
		try {
			if( objEnt.fetchByHref()) {
				if( objEnt.getObixObject().containsContract(History.contract)) {
					objEnt.fetchChildrens();
					for( ObjEntity entity : (List<ObjEntity>) objEnt.getChilds()){
			if (entity.getObixObject().getName() != null
				&& entity.getObixObject().getName().equals("historyrecords")) {
							// we are on the records list, delete all childs
							entity.deleteChilds();

			    TimeSeriesService timesSerieService = ObixTimeSeriesHandler.getInstance()
				    .getTimeSeriesService();
			    if (timesSerieService != null) {
				timesSerieService.dropMeasurement(uri);
			    }

							break;
						}
					}
					// delete history and direct childs.
					objEnt.delete();
				}
			}
		} catch(EntityException ex) {
	    throw new DomainException("Exception in " + this.getClass().getName() + ".getHistory", ex);
		}
		
	}
	
	@Override
	public History retrieve(String uri) throws DomainException{
		return (History)getHistory(uri).getObixObject();
	}

	private ObjEntity getHistory( String uri) throws DomainException{

		History obixObj = new History();
		obixObj.setHref(new Uri("href", uri));
		ObjEntity objEnt = new ObjEntity(obixObj);
		
		try {
			if( objEnt.fetchByHref()) {
				if( objEnt.getObixObject().containsContract(History.contract)) {
					objEnt.fetchChildrens();
					for( ObjEntity entity : (List<ObjEntity>) objEnt.getChilds()){
						objEnt.getObixObject().addChildren(entity.getObixObject());
					}
					return objEnt;
				}
			}
		} catch(EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".getHistory", ex);
		}
		
		return null;
	}
	
	@Override
	public void addRecord(String uri, Val value) throws DomainException{
		
		ObjEntity historyEntity = getHistory(uri);
		
		try {
			
			ObjEntity recordsList = historyEntity.getChildByName("historyrecords");
			ObjEntity end = historyEntity.getChildByName("end");
			ObjEntity count = historyEntity.getChildByName("count");

			if( recordsList.getObjtype().equals(EntityType.List) ){
		TimeSeriesService timesSerieService = ObixTimeSeriesHandler.getInstance().getTimeSeriesService();
		if (timesSerieService != null) {
		    // simply add the sample as history record. creation date
		    // will be the timestamp
		    timesSerieService.saveMeasurementRecord(uri, value);
		    ((Int) count.getObixObject()).setVal(new Integer(((Integer) ((Int) count.getObixObject()).getVal())
			    .intValue() + 1));
				count.update();
				((Abstime)end.getObixObject()).setVal(new Date());
				end.update();
			}
	    }
			
		} catch(EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".addRecord", ex);
		}
		
		
	}

    public List<HistoryRollupRecord> getRollUprecords(String uri, Int limit, Abstime start, Abstime end,
	    Reltime roolUpDuration) throws DomainException {
		
		List<HistoryRollupRecord> result = new ArrayList<HistoryRollupRecord>();
		
		try {
	    TimeSeriesService timesSerieService = ObixTimeSeriesHandler.getInstance().getTimeSeriesService();
	    if (timesSerieService != null) {
		result = timesSerieService.loadMeasurementRollUpRecords(uri, (Date) start.getVal(),
			(Date) end.getVal(), ((Integer) limit.getVal()));
				}
			
	} catch (Exception ex) {
	    throw new DomainException("Exception in " + this.getClass().getName() + ".getRollUprecords", ex);
			}
		return result;
	}
	
	@Override
    public List<HistoryRecord> getRecords(String uri, Int limit, Abstime start, Abstime end) throws DomainException {
		
		List<HistoryRecord> result = new ArrayList<HistoryRecord>();
		
		try {
	    TimeSeriesService timesSerieService = ObixTimeSeriesHandler.getInstance().getTimeSeriesService();
	    if (timesSerieService != null) {
		result = timesSerieService.loadMeasurementRecords(uri, (Date) start.getVal(), (Date) end.getVal(),
			((Integer) limit.getVal()));
					 }
	} catch (Exception ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".getRecords", ex);
		}
		
		return result;
	}

}
