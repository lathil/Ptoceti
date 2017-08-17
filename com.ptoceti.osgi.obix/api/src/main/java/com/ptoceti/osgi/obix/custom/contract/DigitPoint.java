package com.ptoceti.osgi.obix.custom.contract;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Api
 * FILENAME : DigitPoint.java
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

import com.ptoceti.osgi.obix.object.Bool;
import com.ptoceti.osgi.obix.object.Contract;

/**
 * A Digital point for boolean inputs
 * 
 * @author lor
 *
 */
public class DigitPoint extends Bool implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7545156905451046438L;

	public static final Contract contract = new Contract("ptoceti:DigitPoint");
	
	public DigitPoint() {
		this.setIs(contract);
		this.setWritable(Boolean.FALSE);
	}
	
	public DigitPoint(String name) {
		super(name);
		this.setIs(contract);
		this.setWritable(Boolean.FALSE);
	}
	
	public DigitPoint(String name, Boolean value) {
		super(name, value);
		this.setIs(contract);
		this.setWritable(Boolean.FALSE);
	}
}
