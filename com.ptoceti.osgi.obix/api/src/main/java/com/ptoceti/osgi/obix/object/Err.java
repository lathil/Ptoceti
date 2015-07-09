package com.ptoceti.osgi.obix.object;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Api
 * FILENAME : Err.java
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


public class Err extends Obj{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6508946559227578324L;
	
	private static final Contract contract = new Contract("obix:err");
	
	public Err() {
		super();
	}
	
	public Err(Obj model) {
		super(model);
	}
	
	public Err(String name) {
		super(name);
	}

	
	@Override
	public Obj cloneEmpty() {
		return new Err();
	}

	@Override
	public Contract getContract(){
		return contract;
	}
}
