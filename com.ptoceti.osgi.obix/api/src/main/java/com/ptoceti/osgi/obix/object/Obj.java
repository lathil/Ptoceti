package com.ptoceti.osgi.obix.object;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Api
 * FILENAME : Obj.java
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


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


public class Obj  implements Serializable {

	private static final Contract contract = new Contract("obix:obj");
	/**
	 * 
	 */
	private static final long serialVersionUID = -850217714498587205L;
	protected String name;
	protected Uri href;
	protected Contract is;
	protected Boolean isNull = Boolean.FALSE;
	protected Uri icon;
	protected String displayName;
	protected String display;
	protected Boolean writable = Boolean.FALSE;
	protected Status status;
	protected ArrayList<Obj> childrens;
	
	private long updateTimeStamp;
	
	public Obj() {
		setChildrens(new ArrayList<Obj>());
		updateTimeStamp = (new Date()).getTime();
	}
	
	public Obj(Obj model) {
		
		if( model != null){
			if( model.name != null) name = new String(model.name);
			if( model.href != null) href = new Uri(model.getHref().getName(), model.getHref().getPath());
			if( model.is != null) is = model.is;
			if( model.isNull != null) isNull = new Boolean(model.isNull);
			if( model.icon != null) icon = new Uri(model.getIcon().getName(), model.getIcon().getPath());
			if( model.display != null) display = new String( model.display);
			if( model.displayName != null) displayName = new String(model.displayName);
			if( model.writable != null) writable = new Boolean( model.writable);
			if( model.status != null) status= model.status;
			if( model.childrens != null) childrens = model.childrens;
		}
	}
	
	public Obj(String name){
		setChildrens(new ArrayList<Obj>());
		setName(name);
	}
	
	public void addChildren( Obj child) {
		if( has(child.getName())){
			replace( child.getName(), child);
		} else {
			getChildrens().add(child);
		}
	}
	
	public boolean has (String name ){
		Iterator<Obj> childIter = childrens.iterator();
		boolean found = false;
		if( name != null && name.length() > 0) {
			while( childIter.hasNext()) {
				Obj nextObj = childIter.next();
				if( (nextObj.getName() != null )&& nextObj.getName().equals(name)) {
					found = true;
					break;
				}
			}
		}
		return found;
	}
	
	public void replace( String name, Obj child) {
		Iterator<Obj> childIter = childrens.iterator();
		while( childIter.hasNext()) {
			Obj nextObj = childIter.next();
			if( nextObj.getName().equals(name)) {
				int index = childrens.indexOf(nextObj);
				// be sure child obj got final name
				child.setName(name);
				childrens.set(index, child);
				break;
			}
		}
	}
	
	public Obj getChildren( String name) {
		
		Obj result = null;
		Iterator<Obj> childIter = childrens.iterator();
		while( childIter.hasNext()) {
			Obj nextObj = childIter.next();
			if( nextObj.getName().equals(name)) {
				result = nextObj;
				break;
			}
		}
		return result;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public void setIs(Contract is) {
		this.is = is;
	}
	public Contract getIs() {
		return is;
	}
	
	public void setIsNull(Boolean isNull) {
		this.isNull = isNull;
	}
	public Boolean getIsNull() {
		return isNull;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplay(String display) {
		this.display = display;
	}
	public String getDisplay() {
		return display;
	}
	public void setWritable(Boolean writable) {
		this.writable = writable;
	}
	public Boolean getWritable() {
		return writable;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public Status getStatus() {
		return status;
	}

	public void setHref(Uri href) {
		this.href = href;
	}

	public Uri getHref() {
		return href;
	}

	public void setIcon(Uri icon) {
		this.icon = icon;
	}

	public Uri getIcon() {
		return icon;
	}

	public ArrayList<Obj> getChildrens() {
		return childrens;
	}

	public void setChildrens(ArrayList<Obj> arrayList) {
		this.childrens = arrayList;
	}

	public boolean containsContract( Contract in) {
		if( this.getIs() == null || this.getIs().getUris().length == 0 ){
			if( in == null || in.getUris().length == 0) return true; else return false;
		}
		
		if( this.getIs().containsContract(in)) return true;
		
		return false;
	}
	
	public Obj cloneEmpty() {
		return new Obj();
	}
	
	public Contract getContract(){
		return contract;
	}
	
	public long getUpdateTimeStamp(){
		return updateTimeStamp;
	}
	
	public void setUpdateTimeStamp(long timeStamp){
		updateTimeStamp = timeStamp;
	}
}
