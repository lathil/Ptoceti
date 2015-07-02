package com.ptoceti.osgi.obix.impl;

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
