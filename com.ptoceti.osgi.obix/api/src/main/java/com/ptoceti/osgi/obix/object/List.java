package com.ptoceti.osgi.obix.object;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Api
 * FILENAME : List.java
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


public class List extends Obj{
	
	private static final Contract contract = new Contract("obix:list");
	
	protected Contract of;
	
	public List () {super();};
	
	
	public List(Obj model) {
		super(model);
	}
	
	public List(String name) {
		super(name);
	}
	
	public List(String name, int min, int max) {
		super(name);
		
		setMin(min);
		setMax(max);
	}
	
	protected int min;
	protected int max;

	
	public void setMin(int min) {
		this.min = min;
	}
	public int getMin() {
		return min;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public int getMax() {
		return max;
	}
	public void setOf(Contract of) {
		this.of = of;
	}
	public Contract getOf() {
		return of;
	}
	
	@Override
	public Obj cloneEmpty() {
		return new List();
	}
	
	@Override
	public Contract getContract(){
		return contract;
	}
}
