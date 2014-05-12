package com.ptoceti.osgi.obix.object;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Api
 * FILENAME : Ref.java
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



public class Ref extends Obj {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7465212287362622984L;
	
	private static final Contract contract = new Contract("obix:ref");
	
	public Ref() {super();}
	
	public Ref(Obj model) {
		super(model);
	}
	
	public Ref(Ref model){
		super(model);
	}
	
	public Ref(String name) {
		super(name);
	}
	
	public Ref(String name, Uri href) {
		super(name);
		setHref(href);
	}

	
	@Override
	public Obj cloneEmpty() {
		return new Ref();
	}
	
	@Override
	public Contract getContract(){
		return contract;
	}
}
