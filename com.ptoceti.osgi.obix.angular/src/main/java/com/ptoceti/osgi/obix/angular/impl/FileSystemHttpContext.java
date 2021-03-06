package com.ptoceti.osgi.obix.angular.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : FileSystemHttpContext.java
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


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.http.HttpContext;
import org.osgi.service.log.LogService;

public class FileSystemHttpContext implements HttpContext{

	
	public String getMimeType(String arg0) {
		return null;
	}

	
	public URL getResource(String filePath) {
		
		URL url = null;
		
		
		File file = new File(filePath);
		if( file.exists() && !file.isDirectory()) {
			try {
				url= file.toURI().toURL();
			} catch (MalformedURLException e) {
				Activator.log(LogService.LOG_ERROR, "Error creating url for file path: " + filePath);
			}
		}
		
		return url;
	}

	public boolean handleSecurity(HttpServletRequest arg0,
			HttpServletResponse arg1) throws IOException {
		
		return true;
	}

}
