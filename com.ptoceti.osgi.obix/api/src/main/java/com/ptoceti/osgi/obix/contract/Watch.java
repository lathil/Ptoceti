package com.ptoceti.osgi.obix.contract;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Api
 * FILENAME : Watch.java
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


import java.io.Serializable;

import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Op;
import com.ptoceti.osgi.obix.object.Reltime;
import com.ptoceti.osgi.obix.object.Uri;


public class Watch extends Obj implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9071667180292671493L;
	public static final Contract contract = new Contract("obix:Watch");
	
	public Watch() {
		setIs(contract);
		
		Reltime rltm = new Reltime("lease");
		addChildren(rltm);
		
		Op watchAdd = new Op("add", WatchIn.contract, WatchOut.contract);
        //watchAdd.setHref(new Uri("uri", WatchAddResource.baseuri));
		addChildren(watchAdd);
		
		Op watchRemove = new Op("remove", WatchIn.contract, Nil.contract);
        //watchRemove.setHref(new Uri("uri",WatchRemoveResource.baseuri));
		addChildren(watchRemove);
		
		Op watchDelete = new Op("delete", Nil.contract, Nil.contract);
        //watchDelete.setHref(new Uri("uri",WatchDeleteResource.baseuri));
		addChildren(watchDelete);
		
		Op poolChanges = new Op("poolChanges", Nil.contract,WatchOut.contract);
        //poolChanges.setHref(new Uri("uri",WatchPoolChangesResource.baseuri));
		addChildren(poolChanges);
		
		Op poolRefresh = new Op("poolRefresh", Nil.contract, WatchOut.contract);
        //poolRefresh.setHref(new Uri("uri",WatchPoolRefreshResource.baseuri));
		addChildren(poolRefresh);
	}
	
	public Watch(String name) {
		this();
		setName(name);
	}
	
	public Reltime getLease() {
		return (Reltime)this.getChildren("lease");
	}
	
	public void setLease(Reltime time) {
		this.replace("lease", time);
	}
	
	public Op getAdd(){
		return (Op)(this.getChildren("add"));
	}
	
	public void setAdd(Op op) {
		this.replace("add", op);
	}
	
	public Op getRemove(){
		return (Op)(this.getChildren("remove"));
	}
	
	public void setRemove(Op op) {
		this.replace("remove", op);
	}
	
	public Op getDelete(){
		return (Op)(this.getChildren("delete"));
	}
	
	public void setDelete(Op op) {
		this.replace("delete", op);
	}
	
	public Op getPoolChanges(){
		return (Op)(this.getChildren("poolChanges"));
	}
	
	public void setPoolChanges(Op op) {
		this.replace("poolChanges", op);
	}
	
	public Op getPoolRefresh(){
		return (Op)(this.getChildren("poolRefresh"));
	}
	
	public void setPoolrefresh(Op op) {
		this.replace("poolRefresh", op);
	}

}
