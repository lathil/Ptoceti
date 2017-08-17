package com.ptoceti.osgi.obix.impl.service;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : BaseObixHandler.java
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

import java.util.regex.PatternSyntaxException;

import org.osgi.service.log.LogService;

public abstract class BaseObixHandler {

	
	
	protected String mapScopeToHref(String scope) {

		String result = null;
		try {
			// Remove white space
			String resultNoWhites = scope.replaceAll("\t\n\f\r", "");
			// Replace any '.' by '/'.
			String resultNoDots = resultNoWhites.replaceAll("[.]", "/");
			result = resultNoDots;
		} catch (PatternSyntaxException e) {
			Activator.log(LogService.LOG_DEBUG, "Error while mapping Pid to Href: " + e.toString());
		} catch (Exception e) {
			Activator.log(LogService.LOG_DEBUG, "Error while mapping Pid to Href: " + e.toString());
		}

		return result;
	}
	
	protected String mapHrefToScope(String href){
		String result = null;
		try {
			// Remove white space
			String resultNoName = href.replaceAll("\t\n\f\r", "");
			// Replace any '/' by '.'.
			String resultNoDots = resultNoName.replaceAll("[//]", ".");
			result = resultNoDots;
		} catch (PatternSyntaxException e) {
			Activator.log(LogService.LOG_DEBUG, "Error while mapping Pid to Href: " + e.toString());
		} catch (Exception e) {
			Activator.log(LogService.LOG_DEBUG, "Error while mapping Pid to Href: " + e.toString());
		}
		
		return result;
	}
}
