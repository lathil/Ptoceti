package com.ptoceti.osgi.obix.contract;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Api
 * FILENAME : Lobby.java
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

import com.ptoceti.osgi.obix.custom.contract.AlarmService;
import com.ptoceti.osgi.obix.custom.contract.HistoryService;
import com.ptoceti.osgi.obix.custom.contract.SearchOut;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Ref;
import com.ptoceti.osgi.obix.object.Op;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Uri;

public class Lobby extends Obj implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7377785791839734162L;
	public static final Contract contract = new Contract("obix:Lobby");
	
	public Lobby() {
		this.setIs(contract);
		
		Op batch = new Op("batch", BatchIn.contract, BatchOut.contract);
        //batch.setHref(new Uri("uri", HistoryQueryResource.baseuri));
		this.addChildren(batch);
		
		Op search = new Op("search", Ref.contract, SearchOut.contract);
        //search.setHref(new Uri("uri", HistoryQueryResource.baseuri));
		this.addChildren(search);
	}
	
	public Lobby(String name) {
		super(name);
		this.setIs(contract);
	}

	public void setAbout(Uri uri) {
		Ref ref = new Ref("about", uri);
		ref.setIs(About.contract);
		this.addChildren(ref);
	}
	
	public Ref getAbout() {
		return (Ref)this.getChildren("about");
	}
	
	public void setWatchService(Uri uri) {
		Ref ref = new Ref("watchService", uri);
		ref.setIs(WatchService.contract);
		this.addChildren(ref);
	}
	
	public void setHistoryService(Uri uri) {
		Ref ref = new Ref("historyService", uri);
		ref.setIs(HistoryService.contract);
		this.addChildren(ref);
	}
	
	public void setAlarmService(Uri uri) {
		Ref ref = new Ref("alarmService", uri);
		ref.setIs(AlarmService.contract);
		this.addChildren(ref);
	}
	
	public Ref getWatchService() {
		return (Ref) this.getChildren("watchService");
	}
	
	public Op getBatch() {
		return (Op)this.getChildren("batch");
	}
	
	public Op getSearch() {
		return (Op)this.getChildren("search");
	}
	
}
