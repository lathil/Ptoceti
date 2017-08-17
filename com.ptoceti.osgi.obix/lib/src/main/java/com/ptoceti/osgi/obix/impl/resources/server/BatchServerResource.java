package com.ptoceti.osgi.obix.impl.resources.server;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : BatchServerResource.java
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

import org.restlet.resource.Post;

import com.google.inject.Inject;
import com.ptoceti.osgi.obix.contract.BatchIn;
import com.ptoceti.osgi.obix.contract.BatchOut;
import com.ptoceti.osgi.obix.contract.Invoke;
import com.ptoceti.osgi.obix.contract.Read;
import com.ptoceti.osgi.obix.contract.Write;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.domain.ObjDomain;
import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Bool;
import com.ptoceti.osgi.obix.object.Enum;
import com.ptoceti.osgi.obix.object.Int;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Real;
import com.ptoceti.osgi.obix.object.Reltime;
import com.ptoceti.osgi.obix.object.Str;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.resources.BatchResource;
import com.ptoceti.osgi.obix.resources.ResourceException;



public class BatchServerResource extends AbstractServerResource implements BatchResource{

	private ObjDomain objDomain;
	
	@Inject
	public BatchServerResource(ObjDomain domain) {
		objDomain = domain;
	}
	
	@Post("xml|json")
	public BatchOut batch(BatchIn batchIn) throws ResourceException {
		BatchOut batchOut = new BatchOut();
		
		try {
			ArrayList<Obj> inObj = batchIn.getChildrens();
			for( Obj obj : inObj) {
				if( obj.containsContract(Read.contract)){
					Obj readObj = objDomain.getObixObj((Uri)obj);
					if( readObj != null)  batchOut.addObj(readObj);		
				} else if ( obj.containsContract(Write.contract)) {
					Obj currentObj = objDomain.getObixObj((Uri)obj);
					Obj updateObj = ((Write)obj).getIn();
					
					if( updateObj.getDisplay() != null && !updateObj.getDisplay().isEmpty()) currentObj.setDisplay(updateObj.getDisplay());
					if( updateObj.getDisplayName() != null && !updateObj.getDisplayName().isEmpty()) currentObj.setDisplayName(updateObj.getDisplayName());
					if( updateObj.getIcon() != null) currentObj.setIcon(updateObj.getIcon());
					
					if( updateObj.getStatus() != null ) currentObj.setStatus(updateObj.getStatus());
					if( updateObj.getWritable() != null ) currentObj.setWritable(updateObj.getWritable());
					
					if (updateObj instanceof Abstime) {
						if( ((Abstime)updateObj).getVal()!= null && !((Abstime)updateObj).getVal().toString().isEmpty()) {
							((Abstime)currentObj).setVal(((Abstime)updateObj).getVal());
						}
					} else if (updateObj instanceof Bool) {
						if( ((Bool)updateObj).getVal()!= null && !((Bool)updateObj).getVal().toString().isEmpty()) {
							((Bool)currentObj).setVal(((Bool)updateObj).getVal());
						}
					} else if (updateObj instanceof Enum) {
						if( ((Abstime)updateObj).getVal()!= null && !((Abstime)updateObj).getVal().toString().isEmpty()) {
							((Abstime)currentObj).setVal(((Abstime)updateObj).getVal());
						}
					} else if (updateObj instanceof Int) {
						if( ((Int)updateObj).getVal()!= null && !((Int)updateObj).getVal().toString().isEmpty()) {
							((Int)currentObj).setVal(((Int)updateObj).getVal());
						}
					} else if (updateObj instanceof Real) {
						if( ((Real)updateObj).getVal()!= null && !((Real)updateObj).getVal().toString().isEmpty()) {
							((Real)currentObj).setVal(((Real)updateObj).getVal());
						}
					} else if (updateObj instanceof Reltime) {
						if( ((Reltime)updateObj).getVal()!= null && !((Reltime)updateObj).getVal().toString().isEmpty()) {
							((Reltime)currentObj).setVal(((Reltime)updateObj).getVal());
						}
					} else if (updateObj instanceof Str) {
						if( ((Str)updateObj).getVal()!= null && !((Str)updateObj).getVal().toString().isEmpty()) {
							((Str)currentObj).setVal(((Str)updateObj).getVal());
						}
					} else if (updateObj instanceof Uri) {
						if( ((Uri)updateObj).getVal()!= null && !((Uri)updateObj).getVal().toString().isEmpty()) {
							((Uri)currentObj).setVal(((Uri)updateObj).getVal());
						}
					}
					
					Obj writeObj = objDomain.updateObixObjAt((Uri)obj, currentObj);
					if( writeObj != null) batchOut.addObj(writeObj);
				} else if ( obj.containsContract(Invoke.contract)) {
					
				}
			}
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".batch", ex);
		}
		return batchOut;
	}

}
