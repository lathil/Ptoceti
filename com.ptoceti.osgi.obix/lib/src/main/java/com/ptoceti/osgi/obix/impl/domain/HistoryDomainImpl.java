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
import com.ptoceti.osgi.obix.impl.entity.ListEntity;
import com.ptoceti.osgi.obix.impl.entity.ObjEntity;
import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Int;
import com.ptoceti.osgi.obix.object.Real;
import com.ptoceti.osgi.obix.object.Reltime;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.object.Val;
import com.ptoceti.osgi.obix.resources.HistoryResource;

public class HistoryDomainImpl extends AbstractDomain implements  HistoryDomain{

	@Override
	public History make(Contract of,  String displayName) throws DomainException {
		String timeStamp = (new Long (Calendar.getInstance().getTimeInMillis())).toString();
		
		History history = new History(timeStamp);
		
		Long initialTimeStamp = Long.valueOf(((Calendar.getInstance()).getTimeInMillis()));
		Abstime start = new Abstime("", new Date(initialTimeStamp));
		history.setStart(start);
		
		Abstime end = new Abstime("", new Date(initialTimeStamp));
		history.setEnd(end);
		
		history.setCount(0);
		
		history.setDisplayName(displayName);
		
		history.setHref(new Uri("uri",HistoryResource.baseuri.concat("/").concat(timeStamp).concat("/")));
		
		// create a hidden list that will contain all history records
		com.ptoceti.osgi.obix.object.List recordsList = new com.ptoceti.osgi.obix.object.List("historyrecords");
		recordsList.setOf(of);
		history.addChildren(recordsList);
		
		ObjEntity objEnt = new ObjEntity(history);
		try {
			objEnt.create();
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
						if( entity.getObixObject().getName() != null && entity.getObixObject().getName().equals("historyrecords")) {
							// we are on the records list, delete all childs
							entity.deleteChilds();
							break;
						}
					}
					// delete history and direct childs.
					objEnt.delete();
				}
			}
		} catch(EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".remove", ex);
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
				// simply add the sample as history record. creation date will be the timestamp
				
				Val clone = (Val)value.clone();
				
				clone.setIs(null);
				clone.setHref(null);
				
				recordsList.addChildren(clone);
				
				((Int)count.getObixObject()).setVal( new Integer(((Integer)((Int)count.getObixObject()).getVal()).intValue() + 1 ));
				count.update();
				((Abstime)end.getObixObject()).setVal(new Date());
				end.update();
			}
			
		} catch(EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".addRecord", ex);
		} catch (CloneNotSupportedException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".addRecord", ex);
		}
		
		
	}

	public List<HistoryRollupRecord> getRollUprecords(String uri, Int limit, Abstime from, Abstime to, Reltime roolUpDuration) throws DomainException{
		
		List<HistoryRollupRecord> result = new ArrayList<HistoryRollupRecord>();
		ObjEntity historyEntity = getHistory(uri);
		
		List<ObjEntity> childList = historyEntity.getChilds();
		
		long recordsLimit = ((Integer)limit.getVal()).longValue();
		try {
			
			ListEntity recordsList = null;
			for(int i = 0; i < childList.size(); i++){
				ObjEntity child = (ObjEntity)childList.get(i);
				if( child.getObjtype().equals(EntityType.List) && child.getObixObject().getName().equals("historyrecords")){
					recordsList = (ListEntity) child;
					break;
				}
			}
			
			if( recordsList != null && recordsLimit > 0){
				
				long start = ((Date)from.getVal()).getTime();
				long end = ((Date)to.getVal()).getTime();
				long periode = ((Long)roolUpDuration.getVal()).longValue();
				long nextEndPeriode = start + periode;
				if( nextEndPeriode > end) nextEndPeriode = end;
				do {
					recordsList.fetchChildrensFilterByTimeStamp( start, nextEndPeriode );
					
					double min = 0;
					double max = 0;
					double avg = 0;
					double sum = 0;
					
					List<ObjEntity> childObjEntity = recordsList.getChilds();
					
					int count = childObjEntity.size();
					boolean hasSample = false;
					
					for( int i = 0; i < count; i++){
						Object val = ((Val)((ObjEntity)childObjEntity.get(i)).getObixObject()).getVal();
						double prim = 0;
						if(val instanceof Integer || val instanceof Double) {
							if( val instanceof Integer ) prim = ((Integer)val).doubleValue();
							else if( val instanceof Double) prim = ((Double)val).doubleValue();
							if( i == 0){
								min = prim;
								max = prim;
										
							} else {
								if( prim < min) min = prim;
								if( prim > max) max = prim;
							}
							avg += prim / (double)count;
							sum += prim;
							
							hasSample = true;
						}
					}
					
					if( hasSample) {
						HistoryRollupRecord record = new HistoryRollupRecord();
						
						record.setMax(new Real("", new Double(max)));
						record.setMin(new Real("", new Double(min)));
						record.setSum(new Real("", new Double(sum)));
						record.seAvg(new Real("", new Double(avg)));
						
						record.setStart(new Abstime("", new Long(start)));
						record.setEnd(new Abstime("", new Long(nextEndPeriode)));
						
						record.setName("historyrolluprecord-" + record.getStart().encodeVal()); 
						
						result.add(record);
					}
					
					
					if( nextEndPeriode < end){
						start = nextEndPeriode + 1;
						nextEndPeriode = nextEndPeriode + periode;
						if( nextEndPeriode > end) nextEndPeriode = end;
					}
				} while (nextEndPeriode < end && result.size() < recordsLimit );
			}
			
			
		} catch(EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".getRecords", ex);
		}
		
		return result;
	}
	
	@Override
	public List<HistoryRecord> getRecords(String uri, Int limit, Abstime from, Abstime to) throws DomainException{
		
		List<HistoryRecord> result = new ArrayList<HistoryRecord>();
		ObjEntity historyEntity = getHistory(uri);
		
		List<ObjEntity> childList = historyEntity.getChilds();
		try {
			for(int i = 0; i < childList.size(); i++){
				ObjEntity child = (ObjEntity)childList.get(i);
				if( child.getObjtype().equals(EntityType.List) && child.getObixObject().getName().equals("historyrecords")){
					
					 child.fetchChildrensFilterByTimeStamp( ((Date)from.getVal()).getTime(), ((Date)to.getVal()).getTime());
					 
					 List<ObjEntity> childObjEntity = child.getChilds();
					 float step = ( childObjEntity.size() < ((Integer)limit.getVal()).intValue() ? ((Integer)limit.getVal()).floatValue() : ((float)childObjEntity.size() / ((Integer)limit.getVal()).floatValue()));
					 float nextStep = 0;
					 for( int j = 0; i < childObjEntity.size(); j = (int)((float)i + nextStep)){
						 
						 HistoryRecord record = new HistoryRecord();
						 record.setValue( ((ObjEntity)childObjEntity.get(j)).getObixObject());
						 record.setTimeStamp(new Abstime("",((ObjEntity)childObjEntity.get(j)).getCreationDate()));
						 
						 nextStep = nextStep + step;
					 }
						 
					 break;
				}
			}
		} catch(EntityException ex) {
			throw new DomainException("Exception in " + this.getClass().getName() + ".getRecords", ex);
		}
		
		return result;
	}

}
