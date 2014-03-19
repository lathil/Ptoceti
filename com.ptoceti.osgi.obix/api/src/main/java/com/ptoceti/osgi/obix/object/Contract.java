package com.ptoceti.osgi.obix.object;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Api
 * FILENAME : Contract.java
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


import java.util.ArrayList;

public class Contract {

	protected Uri[] uris;
	
	public Contract() {
		uris = new Uri[0];
	}

	public Contract(String contractName) {
		uris = new Uri[1];
		uris[0] = new Uri("uri", contractName);	
	}
	
	public void setUris(Uri[] uris) {
		this.uris = new Uri[uris.length];
		for(int i = 0;i < uris.length; i++) this.uris[i] = uris[i];
	}

	public Uri[] getUris() {
		Uri[] result = new Uri[uris.length];
		for(int i = 0;i < uris.length; i++) result[i] = uris[i];
		return result;
	}
	
	public boolean containsContract( Contract in ) {
		
		boolean found = false;
		
		Uri[] urisIn = in.getUris();
		for( int i = 0; i < urisIn.length; i ++){
			Uri uriIn = urisIn[i];
			found = false;
			for( int j = 0; j < uris.length; j ++) {
				if( uriIn.getVal().toString().equals( uris[i].getVal().toString() )){
					found = true;
					break;
				}
			}
			if( ! found ) break;
		}
		return found;
	}
	
}
