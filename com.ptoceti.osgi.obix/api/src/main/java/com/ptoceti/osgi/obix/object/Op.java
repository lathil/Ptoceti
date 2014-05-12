package com.ptoceti.osgi.obix.object;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Api
 * FILENAME : Op.java
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



public class Op extends Obj {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3427772146596982538L;
	
	public static final Contract contract = new Contract("obix:Op");
	
	public Op() {
		setIs(contract);
	}
	
	public Op( String name) {
		this();
		setName(name);
	}
	
	public Op (Contract in, Contract out) {
		this();
		setIn(in);
		setOut(out);
	}
	
	public Op(String name, Contract in, Contract out) {
		this(name);
		setIn(in);
		setOut(out);
	}
	
	protected Contract in;
	protected Contract out;

	
	public void setIn(Contract in) {
		this.in = in;
	}
	public Contract getIn() {
		return in;
	}
	public void setOut(Contract out) {
		this.out = out;
	}
	public Contract getOut() {
		return out;
	}
	
	@Override
	public Obj cloneEmpty() {
		return new Op();
	}

}
