package com.ptoceti.osgi.obix.impl.cache;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : ObjNotFoundException.java
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

public class ObjNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4993304381531402715L;

	public ObjNotFoundException(String message){
		super(message);
	}
	
	public ObjNotFoundException(String message, Throwable cause ){
		super(message, cause);
	}
	
	public ObjNotFoundException(Throwable cause ){
		super(cause);
	}
}
