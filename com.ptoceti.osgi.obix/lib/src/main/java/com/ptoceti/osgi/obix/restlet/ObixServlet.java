package com.ptoceti.osgi.obix.restlet;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : ObixServlet.java
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

import java.io.IOException;
import java.util.Map;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

 
import org.restlet.engine.Engine;
import org.restlet.ext.servlet.ServletAdapter;

import org.restlet.routing.Router;
import org.restlet.routing.Template;
import org.restlet.routing.TemplateRoute;
import org.restlet.routing.Variable;
import org.restlet.Application;
import org.restlet.Context;

import com.ptoceti.osgi.obix.impl.converters.JSonConverter;
import com.ptoceti.osgi.obix.impl.converters.XMLConverter;
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


public class ObixServlet extends HttpServlet {


	static final long serialVersionUID = 0;
	
	private ServletAdapter adapter;
	
	private GuiceRouter root;
	
	private Application application;
	
	private GuiceFinderFactory guiceFinderFactory;


	/**
	 * Create a new ObixServiceImpl instance. Register the class instance as a ManagedService.
	 * The class will be recognised as such by the framework allowing it to pass on configuration data.
	 *
	 * @throws Exception
	 */
	public ObixServlet() {
		super();
		
		Context context = new Context();
		application = new Application();
		application.setContext(context);
		
		XMLConverter obixConverter = new XMLConverter();
		Engine.getInstance().getRegisteredConverters().add( obixConverter);
		
		JSonConverter jsonConverter = new JSonConverter();
		Engine.getInstance().getRegisteredConverters().add(jsonConverter);
		
		
		guiceFinderFactory = new GuiceFinderFactory();
		
		root = new GuiceRouter(context);
		root.setFinderFactory(guiceFinderFactory);
		
		OriginServerFilter corsFilter = new OriginServerFilter(root.getContext());
		corsFilter.setNext(root);
		
		application.setInboundRoot(corsFilter);
		
	}
	
	public void init() throws ServletException {
		
		adapter = new ServletAdapter(getServletContext());
		this.adapter.setNext(application);
		
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

	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	     
		this.adapter.service(req, res);
	}

	public Router getRoot() {
		return root;
	}
	
}
