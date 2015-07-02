package com.ptoceti.osgi.control;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Control
 * FILENAME : Reference.java
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

/**
 * A command that represent a user settable value.
 * 
 * @author lor
 *
 */

public class Reference {
	
	private double value;
	private double min;
	private double max;
	private ExtendedUnit unit;
	
	public Reference(double value, ExtendedUnit unit){
		this.value = value;
		this.unit = (unit != null) ? unit : ExtendedUnit.unity;
		
		min = 0;
		max = Double.MAX_VALUE;
	}

	public double getValue(){
		return value;
	}
	
	public ExtendedUnit getUnit(){
		return unit;
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}
	
}
