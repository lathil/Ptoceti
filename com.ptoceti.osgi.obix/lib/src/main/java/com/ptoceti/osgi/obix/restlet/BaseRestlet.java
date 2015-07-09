package com.ptoceti.osgi.obix.restlet;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : BaseRestlet.java
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import org.osgi.service.log.LogService;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.engine.Engine;
import org.restlet.routing.Template;
import org.restlet.routing.TemplateRoute;
import org.restlet.routing.Variable;

import com.ptoceti.osgi.obix.impl.Activator;
import com.ptoceti.osgi.obix.impl.front.converters.JSonConverter;
import com.ptoceti.osgi.obix.impl.front.converters.XMLConverter;
import com.ptoceti.osgi.obix.impl.guice.GuiceFinderFactory;
import com.ptoceti.osgi.obix.impl.guice.GuiceRouter;
import com.ptoceti.osgi.obix.impl.resources.server.AboutServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.BatchServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.HistoryQueryServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.HistoryRollupServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.HistoryServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.LobbyServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.ObjServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.WatchAddServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.WatchDeleteServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.WatchPoolChangesServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.WatchPoolRefreshServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.WatchRemoveServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.WatchServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.WatchServiceServerResource;

/**
 * Base class to build the restlet application with routes, converters and filters.
 * 
 * 
 * 
 * @author lor
 *
 */
public class BaseRestlet {
	/**
	 * Guice rooter for dependencies injection inside the resources
	 */
	protected GuiceRouter root;
	/**
	 * Guice factory
	 */
	protected GuiceFinderFactory guiceFinderFactory;
	/**
	 * The application that dispatches requests to router
	 */
	protected Application application;
	
	BaseRestlet() {
		
		XMLConverter obixConverter = new XMLConverter();
		Engine.getInstance().getRegisteredConverters().add( obixConverter);
		
		JSonConverter jsonConverter = new JSonConverter();
		Engine.getInstance().getRegisteredConverters().add(jsonConverter);
		
		application = new Application();
		Context context = new Context();
		application.setContext(context);
		
		guiceFinderFactory = new GuiceFinderFactory();
		
		root = new GuiceRouter(context);
		root.setFinderFactory(guiceFinderFactory);
		
		addRoutes();
		
		CorsFilter corsFilter = new CorsFilter(root.getContext());
		corsFilter.setNext(root);

		//Logger logger = Logger.getLogger("org.restlet");
		application.setInboundRoot(corsFilter);
		
		Activator.log(LogService.LOG_INFO, "Restlet application initialised.");
		//Engine.setRestletLogLevel(Level.OFF);
		
	}
	
	Application getApplication() {
		return application;
	}

	/**
	 * Bind all resources to routes. Override this if you need to.
	 */
	public void addRoutes(){
		
		root.attach(AboutServerResource.uri, AboutServerResource.class);
		root.attach(LobbyServerResource.uri, LobbyServerResource.class);
		root.attach(BatchServerResource.uri, BatchServerResource.class);
		
		root.attach(WatchServiceServerResource.uri, WatchServiceServerResource.class);
		
		root.attach(WatchAddServerResource.uri, WatchAddServerResource.class);
		root.attach(WatchDeleteServerResource.uri, WatchDeleteServerResource.class);
		root.attach(WatchPoolChangesServerResource.uri, WatchPoolChangesServerResource.class);
		root.attach(WatchPoolRefreshServerResource.uri, WatchPoolRefreshServerResource.class);
		root.attach(WatchRemoveServerResource.uri, WatchRemoveServerResource.class);
		root.attach(WatchServerResource.uri, WatchServerResource.class);
		
		root.attach(HistoryServerResource.uri, HistoryServerResource.class);
		root.attach(HistoryQueryServerResource.uri, HistoryQueryServerResource.class);
		root.attach(HistoryRollupServerResource.uri, HistoryRollupServerResource.class);
		
		// Last route. 
		TemplateRoute route = root.attach( ObjServerResource.uri + "{+href}", ObjServerResource.class);
		route.setMatchingMode(Template.MODE_STARTS_WITH);
		Map<String, Variable> variables = route.getTemplate().getVariables();
		variables.put("href",new Variable(Variable.TYPE_URI_PATH));
	}
}
