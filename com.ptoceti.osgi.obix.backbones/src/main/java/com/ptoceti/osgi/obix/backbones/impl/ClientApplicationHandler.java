package com.ptoceti.osgi.obix.backbones.impl;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix Backbones
 * FILENAME : ClientApplicationHandler.java
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

import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.Filter;

import org.eclipse.jetty.servlets.GzipFilter;
import org.ops4j.pax.web.extender.whiteboard.ExtenderConstants;
import org.ops4j.pax.web.extender.whiteboard.ResourceMapping;
import org.ops4j.pax.web.extender.whiteboard.runtime.DefaultFilterMapping;
import org.ops4j.pax.web.extender.whiteboard.runtime.DefaultResourceMapping;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.log.LogService;

public class ClientApplicationHandler  implements ManagedService {

	ServiceRegistration sReg = null;
	ServiceRegistration appSReg = null;
	
	public static final String CLIENTPATH = "com.ptoceti.osgi.obix.backbones.clientpath";
	public String clientPath = null;
	
	public ClientApplicationHandler() {
		
		String[] clazzes = new String[] { ManagedService.class.getName()};
		// register the class as a managed service.
		Hashtable properties = new Hashtable();
		properties.put(Constants.SERVICE_PID, this.getClass().getName());
		sReg = Activator.bc.registerService(clazzes, this, properties);

		Activator.log(LogService.LOG_INFO, "Registered "
				+ this.getClass().getName() +  ", Pid = "
				+ (String) properties.get(Constants.SERVICE_PID));
	}
	
	public void updated(Dictionary props) throws ConfigurationException {
		
		if (props != null) {
			
			clientPath = (String) props.get(CLIENTPATH);
			// If we have already registered the application , ...
			if( appSReg != null ) {
				//.unregister it ..
				appSReg.unregister();
				appSReg = null;
				Activator.log(LogService.LOG_INFO, "Uregister resource /obix under alias " + clientPath);
			}
			
			DefaultResourceMapping resourceMapping = new DefaultResourceMapping();
			resourceMapping.setAlias( clientPath );
			resourceMapping.setPath( "/resources" );
			appSReg = Activator.bc.registerService( ResourceMapping.class.getName(), resourceMapping, null );
			
			Activator.log(LogService.LOG_INFO, "Mapped /obix under alias " + clientPath);
			
			// the filter path must englobe all resources under the root application path
			String filterPath;
			if( clientPath.endsWith("/*")){
				filterPath =  clientPath;
			} else if (clientPath.endsWith("/")){
				filterPath = clientPath.concat("*");
			} else {
				filterPath = clientPath.concat("/*");
			}
			
			Dictionary<String, Object> filterProps = new Hashtable<String, Object>();
			String[] urls = {filterPath};
			
			filterProps.put("filter-name", "ObixBackbonesGzipFilter");
			filterProps.put(ExtenderConstants.PROPERTY_URL_PATTERNS, urls);
			filterProps.put("mimeTypes", "text/html,text/plain,text/xml,application/xhtml+xml,text/css,application/javascript,application/x-javascript,image/svg+xml");
			// add the gzip filter as osgi service. It will be picked up by Pax-Web Whiteboard.
			Activator.bc.registerService(Filter.class.getName(), new GzipFilter(), filterProps );
			
			Activator.log(LogService.LOG_INFO, "Registered GZip filter under " + filterPath);
			

		} else {
			

		}
		
	}

}
