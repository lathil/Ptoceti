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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;

import com.ptoceti.osgi.obix.observable.IObserver;
import com.ptoceti.osgi.obix.observable.Observable;
import com.ptoceti.osgi.obix.observable.ObservableEvent;


public class Obj extends Observable<Obj >implements Serializable, Cloneable {

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
	
	
	private IObserver<? super Obj> observer;
	
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
	
	public void removeChildren( String name ){
		for( int i = 0; i < childrens.size(); i ++){
			Obj nextChild = childrens.get(i);
			if( nextChild.getName() != null && (nextChild.getName().equals(name))){
				childrens.remove(i);
				break;
			}
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
	
	public synchronized boolean updateWith(Obj other){
		return updateWith(other, false);
	}
	
	protected boolean updateWith(Obj other, boolean hasChanged){
		ArrayList<ObservableEvent> changeEvents = new ArrayList<ObservableEvent>();
		return updateWith(other, hasChanged, changeEvents);
	}

	protected boolean updateWith(Obj other, boolean hasChanged, ArrayList<ObservableEvent> changeEvents){
		
		boolean different = hasChanged;
		
		if( !Objects.equals(getDisplayName(), other.getDisplayName())){
			if( other.getDisplayName() != null){
				setDisplayName(other.getDisplayName());
				different = true;
			}
		}
		if( !Objects.equals(getIcon(), other.getIcon())){
			if( other.getIcon() != null){
				setIcon(other.getIcon());
				different = true;
			}
		}
		if( !Objects.equals(getStatus(), other.getStatus())){
			if( other.getStatus() != null){
				setStatus(other.getStatus());
				changeEvents.add(ObservableEvent.STATUSCHANGED);
				different = true;
			}
		}
		if( !Objects.equals(getIsNull(), other.getIsNull())){
			if( other.getIsNull() != null){
				setIsNull(other.getIsNull());
				different = true;
			}
		}
		if( !Objects.equals(getWritable(), other.getWritable())){
			if( other.getWritable() != null){
				setWritable(other.getWritable());
				different = true;
			}
		}
		
		for(Obj otherChild: other.getChildrens()){
			Obj thisChild = getChildren(otherChild.getName());
			if( thisChild!= null){
				if (thisChild.updateWith(otherChild)) different = true;
			}
		}
		
		for(ObservableEvent obsEvent : changeEvents){
			notifyObservers(this, obsEvent);
		}
		
		return different;
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
	

	public Obj clone() throws CloneNotSupportedException {
		
		Obj clone = (Obj)super.clone();
		
		clone.setDisplay(this.getDisplay() != null ? new String(this.getDisplay()) : null);
		clone.setDisplayName(this.getDisplayName() != null ? new String (this.getDisplayName()) : null);
		clone.setName(this.getName() != null ? new String(this.getName()) : null);
		clone.setIsNull(this.isNull != null ? new Boolean(this.isNull) : null);
		clone.setWritable(this.writable != null ? new Boolean(this.writable) : null);
		clone.setStatus(this.getStatus() != null ? this.getStatus() : null);
		clone.setHref(this.getHref() != null ? this.getHref().clone() : null);
		clone.setIcon(this.getIcon() != null ?this.getIcon().clone() : null);		
		clone.setIs(this.getIs() != null ? this.getIs() .clone(): null);
		
		if( this.getChildrens().size() > 0){
			for( Obj child : this.getChildrens()){
				clone.addChildren(child.clone());
			}
		}
		
		return clone;
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

	public IObserver<? super Obj> getObserver() {
		return observer;
	}

	public void setObserver(IObserver<? super Obj> observer) {
		this.observer = observer;
	}
}
