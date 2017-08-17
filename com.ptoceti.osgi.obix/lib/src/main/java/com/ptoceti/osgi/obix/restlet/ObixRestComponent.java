package com.ptoceti.osgi.obix.restlet;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : ObixRestComponent.java
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


import org.osgi.service.log.LogService;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;


import com.ptoceti.osgi.obix.impl.service.Activator;


/**
 * A Restlet Container that wrap up all resources and serves through the local http server.
 * 
 * @author LATHIL
 *
 */
public class ObixRestComponent {

	/**
	 * Jetty parameter key name for type of connector
	 */
	private static final String JETTY_HTTP_CONNECTOR_TYPE = "type";
	/**
	 * Jetty parameter value for connector type non blocking io
	 */
	private static final String JETTY_HTTP_CONNECTOR_TYPE_NIO = "1";
	/**
	 * Jetty parameter value for connector type blocking /  non blocking io
	 */
	@SuppressWarnings("unused")
	private static final String JETTY_HTTP_CONNECTOR_TYPE_BNIO = "2";
	/**
	 * Jetty parameter value for connector type blocking io
	 */
	@SuppressWarnings("unused")
	private static final String JETTY_HTTP_CONNECTOR_TYPE_BIO = "3";
	
	/**
	 * Jetty connector parameters for max threads service request.
	 */
	@SuppressWarnings("unused")
	private static final String MAX_THREADS = "maxThreads";
	
	/**
	 * Main component that wrapp the application, server connector and filters
	 */
	private Component component;
	
	
	private Application obixApplication;
	
	
	
	/**
	 * Create Restlet main Application, giving it guice rooter, other routes and a cors filter 
	 */
	public ObixRestComponent(String oautLocalServerPath){
	
		ObixApplicationFactory factory = new ObixApplicationFactory(oautLocalServerPath, false);
		obixApplication = factory.getApplication();

	}
	
	/**
	 * Serves the application though a http server connector. Expect to find the restlet jetty extension on the class path. Configure for NIO http socket.
	 * 
	 * @param path the path under which the rest root application is served
	 * @param port the local port that the http connector must bind to.
	 * @throws Exception
	 */
	public void start(String path, Integer port) throws Exception{
		if( component == null) {
			component = new Component();
		} else {
			component.getDefaultHost().detach(obixApplication);
		}
		
		Server server = component.getServers().add(Protocol.HTTP, port.intValue());
		server.getContext().getParameters().add(JETTY_HTTP_CONNECTOR_TYPE, JETTY_HTTP_CONNECTOR_TYPE_NIO);
		server.getContext().getParameters().add(MAX_THREADS, Integer.toString(5));
		component.getDefaultHost().attach(path, obixApplication);
		
		component.start();
		Activator.log(LogService.LOG_INFO, "Restlet application started.");
	}
	
	/**
	 * Stop the main restlet component
	 * @throws Exception
	 */
	public void stop() throws Exception{
		if( component != null){
			 component.stop();
		}
	}
	
	
}
