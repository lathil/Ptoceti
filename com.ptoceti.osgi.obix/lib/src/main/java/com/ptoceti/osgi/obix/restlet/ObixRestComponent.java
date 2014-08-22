package com.ptoceti.osgi.obix.restlet;

import java.util.Map;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.engine.Engine;
import org.restlet.routing.Template;
import org.restlet.routing.TemplateRoute;
import org.restlet.routing.Variable;

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
	/**
	 * The application that dispatches requests to router
	 */
	private Application application;
	/**
	 * Guice rooter for dependencies injection inside the resources
	 */
	private GuiceRouter root;
	/**
	 * Guice factory
	 */
	private GuiceFinderFactory guiceFinderFactory;
	
	/**
	 * Create Restlet main Application, giving it guice rooter, other routes and a cors filter 
	 */
	public ObixRestComponent(){
	
		application = new Application();
		Context context = new Context();
		application.setContext(context);
		
		XMLConverter obixConverter = new XMLConverter();
		Engine.getInstance().getRegisteredConverters().add( obixConverter);
		
		JSonConverter jsonConverter = new JSonConverter();
		Engine.getInstance().getRegisteredConverters().add(jsonConverter);
		
		guiceFinderFactory = new GuiceFinderFactory();
		
		root = new GuiceRouter(context);
		root.setFinderFactory(guiceFinderFactory);
		
		addRoutes();
		
		OriginServerFilter corsFilter = new OriginServerFilter(root.getContext());
		corsFilter.setNext(root);
		
		application.setInboundRoot(corsFilter);
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
			component.getDefaultHost().detach(application);
		}
		
		Server server = component.getServers().add(Protocol.HTTP, port.intValue());
		server.getContext().getParameters().add(JETTY_HTTP_CONNECTOR_TYPE, JETTY_HTTP_CONNECTOR_TYPE_NIO);
		server.getContext().getParameters().add(MAX_THREADS, Integer.toString(5));
		component.getDefaultHost().attach(path, application);
		
		component.start();
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
