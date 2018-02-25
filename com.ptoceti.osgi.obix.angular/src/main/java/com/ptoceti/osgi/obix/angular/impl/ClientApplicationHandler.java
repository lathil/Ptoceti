package com.ptoceti.osgi.obix.angular.impl;

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

import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.ServletException;


import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.service.log.LogService;

import com.ptoceti.osgi.obix.service.ObixService;

/**
 * Simple handler / managed service that register the spa application under a resource servlet once 
 * the http service is available.
 * 
 * 
 * @author lor
 *
 */
public class ClientApplicationHandler  implements ManagedService {

	ServiceRegistration sReg = null;
	ServiceRegistration filterSReg = null;
	
	// The http service registration
	private HttpService httpService = null;
	// The obix service registration
	private ObixService obixService = null;
	// the configuration properties
	private Dictionary configProps = null;
	// flag whether we have done the initialisation
	private boolean isInitialized = false;
	// the alias under which the resources is registered
	private String registeredAlias = null;

	
	public static final String CLIENTPATH = "com.ptoceti.osgi.obix.angular.clientpath";
	public static final String GZIPRESOURCES = "com.ptoceti.osgi.obix.angular.gzip";
	public static final String EXTERNALRESOURCEPATH = "com.ptoceti.osgi.obix.angular.externalresourcepath";
	
	public String clientPath = null;
	public String externalResourcePath = null;
	public Boolean doGzip = false;
	
	// the client id obtained from oauth server for this application;
	protected String oauthClientId = null;
	
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
	
	
	/**
	 * Called by the configuration manager when a set of configuration properties is found.
	 * 
	 */
	public synchronized void updated(Dictionary props) throws ConfigurationException {
		
		if (props != null) {
			if( isInitialized && httpService != null ){
				unregisterResources();
			}
			configProps = props;
			if( !isInitialized && httpService != null && obixService != null){
				registerResources();
			}
		} else {
			unregisterResources();
		}
	}
	
	protected String getOauthPublicClientID() {
		return oauthClientId;
	}
	
	protected boolean existsOauthClient(String id){
		return obixService.existsOauthClient(id);
	}

	public synchronized void setObixService(ObixService obixService) {
		this.obixService = obixService;
		if( !isInitialized && configProps != null && httpService != null){
			registerResources();
		}
	}
	
	public ObixService getObixService() {
		return obixService;
	}
	
	public synchronized void setHttpService(HttpService httpService) {
		this.httpService = httpService;
		if( !isInitialized && configProps != null && obixService != null){
			registerResources();
		}
	}

	public HttpService getHttpService() {
		return httpService;
	}

	/**
	 * Unregister the spas static resources.
	 * 
	 */
	protected void unregisterResources(){
		if( registeredAlias != null){
			httpService.unregister(registeredAlias);
			Activator.log(LogService.LOG_INFO, "Uregister resource /obix under alias " + registeredAlias);
			registeredAlias = null;
		}
		if( filterSReg != null){
			filterSReg.unregister();
			filterSReg = null;
		}
		isInitialized = false;
	}
	
	/**
	 * Register spa resources under a resource servlet.
	 * 
	 */
	protected void registerResources() {
		
		clientPath = (String) configProps.get(CLIENTPATH);
		Object zip = configProps.get(GZIPRESOURCES);
		if(configProps.get(EXTERNALRESOURCEPATH) != null){
			externalResourcePath = (String)configProps.get(EXTERNALRESOURCEPATH);
		}
		
		doGzip = zip instanceof Boolean ? (Boolean) zip: Boolean.parseBoolean(zip != null ? zip.toString(): "false");
		
		if( clientPath != null && clientPath.length() > 0) {
			try {
				registeredAlias = clientPath;
				
				if( externalResourcePath.startsWith("file:")){
					FileSystemHttpContext fileHttpContexte =  new FileSystemHttpContext();
					httpService.registerServlet(registeredAlias, new ResourceServlet(externalResourcePath.substring("file:".length()), doGzip.booleanValue(), this ), null, fileHttpContexte);
				} else {
					httpService.registerServlet(registeredAlias, new ResourceServlet("/resources", doGzip.booleanValue(), this ), null, null);
				}
				
			
				Activator.log(LogService.LOG_INFO, "Mapped /obix under alias " + registeredAlias);
				
				oauthClientId = obixService.createOauthPublicClientID(registeredAlias);
				
				// Remember initialisation was done.
				isInitialized = true;
			} catch (NamespaceException | ServletException e) {
				Activator.log(LogService.LOG_ERROR, "Error while registering resources:  " + e.toString());
			}
		}
	}
}
