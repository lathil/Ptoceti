package com.ptoceti.osgi.obix.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : EnveloppeHandler.java
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

import com.google.inject.Inject;
import com.ptoceti.osgi.obix.contract.History;
import com.ptoceti.osgi.obix.contract.Point;
import com.ptoceti.osgi.obix.custom.contract.MonitoredPoint;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.domain.HistoryDomain;
import com.ptoceti.osgi.obix.domain.ObjDomain;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Ref;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.object.Val;

public class EnveloppeHandler {

	@Inject
	private ObjDomain objDomain;
	
	@Inject
	private HistoryDomain historyDomain;
	
	public void consumeObject(Val obj, String href) {
		try {
			// if object is a Point, we wrapped inside of a MonitoredPoint
			if( obj.getIs().containsContract(Point.contract)){
				// extend path of value object
				obj.setHref(new Uri("", href + "/point"));
				// href is then the one of the MonitoredPoint
				Obj monitoredObj = objDomain.getObixObj(new Uri("",href));
				MonitoredPoint monitoredPoint = null;
				Val sample = (Val)obj.cloneEmpty();
				sample.setVal(obj.getVal());
				
				if( monitoredObj != null && monitoredObj.getIs().containsContract(MonitoredPoint.contract))
				{
					monitoredPoint = new MonitoredPoint(monitoredObj);
					// update db only if point value changed.
					if( !((Val)monitoredPoint.getPoint()).getVal().equals(obj.getVal())){
						
						Val point = (Val)monitoredPoint.getPoint();
						point.setVal(obj.getVal());
						objDomain.updateObixObjAt(obj.getHref(), point);
						historyDomain.addRecord(monitoredPoint.getHistoryRef().getHref().getPath(), sample);
					}
				} else {
					monitoredPoint = new MonitoredPoint();
					monitoredPoint.setHref(new Uri("",href));
					
					monitoredPoint.setPoint(obj);
					
					History history = historyDomain.make( obj.getContract());
					Ref historyRef = new Ref();
					historyRef.setHref(history.getHref());
					monitoredPoint.setHistoryRef(historyRef);
					
					objDomain.createObixObj(monitoredPoint);
					historyDomain.addRecord(monitoredPoint.getHistoryRef().getHref().getPath(), sample);
				}
				
				
			} else {
	
				objDomain.createUpdateObixObj(obj);
			}
			
			ObixDataHandler.getInstance().commitTransaction();
		} catch (Exception ex ) {
			ObixDataHandler.getInstance().rollbackTransaction();
		}
	}
}
