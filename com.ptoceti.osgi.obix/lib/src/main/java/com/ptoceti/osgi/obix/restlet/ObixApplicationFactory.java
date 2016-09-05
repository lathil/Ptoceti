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

import java.util.Map;

import org.osgi.service.log.LogService;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Reference;
import org.restlet.engine.Engine;
import org.restlet.ext.oauth.TokenVerifier;
import org.restlet.routing.Template;
import org.restlet.routing.TemplateRoute;
import org.restlet.routing.Variable;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.Role;
import org.restlet.security.RoleAuthorizer;

import com.ptoceti.osgi.obix.impl.front.converters.JSonConverter;
import com.ptoceti.osgi.obix.impl.front.converters.XMLConverter;
import com.ptoceti.osgi.obix.impl.guice.GuiceFinderFactory;
import com.ptoceti.osgi.obix.impl.guice.GuiceRouter;
import com.ptoceti.osgi.obix.impl.resources.server.AboutServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.AlarmAckServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.AlarmServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.AlarmServiceServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.BatchServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.HistoryQueryServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.HistoryRollupServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.HistoryServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.HistoryServiceServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.LobbyServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.ObjServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.RangeAlarmMaxServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.RangeAlarmMinServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.SearchServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.WatchAddServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.WatchDeleteServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.WatchPoolChangesServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.WatchPoolRefreshServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.WatchRemoveServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.WatchServerResource;
import com.ptoceti.osgi.obix.impl.resources.server.WatchServiceServerResource;
import com.ptoceti.osgi.obix.impl.service.Activator;
import com.ptoceti.osgi.obix.impl.service.ObixServiceImpl;
import com.ptoceti.osgi.obix.resources.RangeAlarmMaxResource;
import com.ptoceti.osgi.obix.resources.RangeAlarmMinResource;

/**
 * Base class to build the restlet application with routes, converters and filters.
 * 
 * 
 * 
 * @author lor
 *
 */
public class ObixApplicationFactory {
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
	
	public static final String ROLE_CLIENT = "client";
    public static final String ROLE_OWNER = "owner";
    
    public String oautLocalServerPath;
    Boolean doSecure = false;
	
    public ObixApplicationFactory( String oautLocalServerPath, Boolean doSecure) {
    	this.oautLocalServerPath = oautLocalServerPath;
    	this.doSecure = doSecure;
    }
    
	private void make() {
		
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
		
		// Cors filter for cross domain requests
		CorsFilter corsFilter = new CorsFilter(root.getContext());		
		corsFilter.setNext(root);
		// attach resources to root
		addRoutes();

		// if the rest resources have to be secured, add verification up front.
		if( doSecure.booleanValue()){
			// Create authentifier, will attempt to authentify every request
			ChallengeAuthenticator authenticator = createAuthenticator();		
			// authorizer will block request on resources based on role for current authentified user
			//RoleAuthorizer roleAuth = createRoleAuthorizer();
			authenticator.setNext(corsFilter);
			//roleAuth.setNext(corsFilter);
			application.setInboundRoot(authenticator);
		} else {
			// otherwie just the cors filter
			application.setInboundRoot(corsFilter);
		}
		
		Activator.log(LogService.LOG_INFO, "Restlet application initialised.");
		//Engine.setRestletLogLevel(Level.OFF);
		
	}
	
	public  Application getApplication() {
		if( application == null){
			make();
		}
		return application;
	}

	/**
	 * Bind all resources to routes. Override this if you need to.
	 */
	private void addRoutes(){
		
		root.attach(AboutServerResource.uri, AboutServerResource.class);
		root.attach(LobbyServerResource.uri, LobbyServerResource.class);
		root.attach(BatchServerResource.uri, BatchServerResource.class);
		
		root.attach(SearchServerResource.uri, SearchServerResource.class);
		
		root.attach(WatchServiceServerResource.uri, WatchServiceServerResource.class);
		
		root.attach(WatchAddServerResource.uri, WatchAddServerResource.class);
		root.attach(WatchDeleteServerResource.uri, WatchDeleteServerResource.class);
		root.attach(WatchPoolChangesServerResource.uri, WatchPoolChangesServerResource.class);
		root.attach(WatchPoolRefreshServerResource.uri, WatchPoolRefreshServerResource.class);
		root.attach(WatchRemoveServerResource.uri, WatchRemoveServerResource.class);
		root.attach(WatchServerResource.uri, WatchServerResource.class);
		
		root.attach(HistoryServiceServerResource.uri, HistoryServiceServerResource.class);
		root.attach(HistoryServerResource.uri, HistoryServerResource.class);
		root.attach(HistoryQueryServerResource.uri, HistoryQueryServerResource.class);
		root.attach(HistoryRollupServerResource.uri, HistoryRollupServerResource.class);
		
		root.attach(AlarmServerResource.uri, AlarmServerResource.class);
		root.attach(AlarmServiceServerResource.uri,AlarmServiceServerResource.class);
		root.attach(AlarmAckServerResource.uri, AlarmAckServerResource.class);
		root.attach(RangeAlarmMaxResource.uri, RangeAlarmMaxServerResource.class);
		root.attach(RangeAlarmMinResource.uri, RangeAlarmMinServerResource.class);
		
		// Last route. 
		TemplateRoute route = root.attach( ObjServerResource.uri + "{+href}", ObjServerResource.class);
		route.setMatchingMode(Template.MODE_STARTS_WITH);
		Map<String, Variable> variables = route.getTemplate().getVariables();
		variables.put("href",new Variable(Variable.TYPE_URI_PATH));
	}
	
	private ChallengeAuthenticator createAuthenticator() {
		// ChallengeAuthenticator extends Authenticator extends Filter extens Restlet
		ChallengeAuthenticator bearerAuthenticator = new ChallengeAuthenticator(application.getContext(),ChallengeScheme.HTTP_OAUTH_BEARER, ObixServiceImpl.REALM);
		// do not propose new challenge if authorisation fails.
		bearerAuthenticator.setRechallenging(false);
		//bearerAuthenticator.setVerifier( new TokenVerifier(new Reference( "http://localhost:8080" + oautLocalServerPath + Oauth2ApplicationFactory.TOKENAUTHENTICATORURI)));
		bearerAuthenticator.setVerifier( new TokenVerifier(new Reference( "riap://component" + oautLocalServerPath + Oauth2ApplicationFactory.TOKENAUTHENTICATORURI)));
	    return bearerAuthenticator;
	}
	
	private RoleAuthorizer createRoleAuthorizer(){
		// RoleAuthorizer extends Authorizer extends Filter extends Restlet
        RoleAuthorizer roleAuth = new RoleAuthorizer();
        roleAuth.getAuthorizedRoles().add(Role.get(application, ROLE_OWNER));
        roleAuth.getAuthorizedRoles().add(Role.get(application, ROLE_CLIENT));
        return roleAuth;
	}
}
