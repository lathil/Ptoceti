package com.ptoceti.osgi.obix.contract;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Api
 * FILENAME : About.java
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

import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Str;
import com.ptoceti.osgi.obix.object.Uri;


public class About extends Obj implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8333440200130895417L;
	public static final Contract contract = new Contract("obix:About");
	
	public About(){	
		this("about");
	}
	
	public About( String name) {
		
		super(name);
		setIs(contract);
		
		this.addChildren(new Abstime("serverBootTime",new Long(0)));
		this.addChildren(new Abstime("serverTime",new Long(0)));
	}
	
	public void setServerName(String serverName){
		this.addChildren(new Str("serverName",serverName));
	}
	
	public Str getServerName(){	
		return (Str)this.getChildren("serverName");
	}
	
	
	public void setProductName(String productName){
		this.addChildren(new Str("productName",productName));
	}
	
	public Str getProductName(){
		return (Str)this.getChildren("productName");
	}
	
	public void setProductVersion(String productVersion){
		this.addChildren(new Str("productVersion",productVersion));
	}
	
	public Str getProductVersion(){
		return (Str)this.getChildren("productVersion");
	}
	
	
	public Abstime getServerBootTime(){
		return (Abstime)this.getChildren("serverBootTime");
	}
	
	public void setServerBootTime(Abstime serverBootTime ) {
		serverBootTime.setName("serverBootTime");
		this.replace("serverBootTime", serverBootTime);
	}
	
	public Abstime getServerTime(){
		return (Abstime)this.getChildren("serverTime");
	}
	
	public void setServerTime(Abstime serverTime) {
		serverTime.setName("serverTime");
		this.replace("serverTime", serverTime);
	}
	
	public void setProductUrl(String productUrl){
		this.addChildren(new Uri("productUrl", productUrl));
	}
	public Uri getProductUrl(){
		return (Uri)this.getChildren("productUrl");
	}
	
	public void setVendorUrl(String vendorUrl){
		this.addChildren(new Uri("vendorUrl", vendorUrl));
	}
	public Uri getVendorUrl(){
		return (Uri)this.getChildren("vendorUrl");
	}
	
	public void setVendorName(String vendorName){
		this.addChildren(new Str("vendorName",vendorName));
	}
	public Str getVendorName(){
		return (Str)this.getChildren("vendorName");
	}
	
	public void setObixVersion(String obixVersion){
		this.addChildren(new Str("obixVersion",obixVersion));
	}
	public Str getObixVersion(){
		return (Str)this.getChildren("obixVersion");
	}
}

