package com.ptoceti.osgi.obix.impl.service;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : ObixService.java
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

import com.ptoceti.osgi.obix.restlet.AppOwnerManager;
import com.ptoceti.osgi.obix.restlet.Oauth2ApplicationFactory;
import com.ptoceti.osgi.obix.restlet.Oauth2Servlet;
import com.ptoceti.osgi.obix.restlet.ObixApplicationFactory;
import com.ptoceti.osgi.obix.restlet.ObixRestComponent;
import com.ptoceti.osgi.obix.restlet.ObixServlet;
import com.ptoceti.osgi.obix.service.ObixService;

import com.ptoceti.osgi.data.JdbcDevice;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.service.log.LogService;
import org.osgi.service.cm.ManagedService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.Constants;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.data.Protocol;
import org.restlet.ext.crypto.DigestUtils;
import org.restlet.ext.oauth.GrantType;
import org.restlet.ext.oauth.ResponseType;
import org.restlet.ext.oauth.internal.Client;
import org.restlet.ext.oauth.internal.Client.ClientType;
import org.restlet.ext.oauth.internal.ClientManager;
import org.restlet.ext.oauth.internal.TokenManager;
import org.restlet.ext.oauth.internal.memory.MemoryClientManager;
import org.restlet.ext.oauth.internal.memory.MemoryTokenManager;


/**
 * A ManagedService class providing Obix (Open Building Information eXchange)
 * metamodel and interface; The configuration will provide URI base path under
 * which to register the servlet that implement the web interface.
 * 
 * @author lor
 * 
 */
public class ObixServiceImpl  implements ObixService, ManagedService {

	// a reference to the service registration for the Controller object.
	ServiceRegistration sReg = null;

	public static final String SERVICEPATH = "com.ptoceti.osgi.obixservice.servletpath";
	public static final String SERVICEPORT = "com.ptoceti.osgi.obixservice.servletport";
	public static final String OAUTHPATH = "com.ptoceti.osgi.obixservice.oauthpath";
	public static final String OAUTHSECURE = "com.ptoceti.osgi.obixservice.oauthsecure";
	
	public static final String OAUTHOWNERNAME = "com.ptoceti.osgi.obixservice.oauth.owner.name";
	public static final String OAUTHOWNERSECRET = "com.ptoceti.osgi.obixservice.oauth.owner.secret";
	
	public static final String RESOURCEPATH = "com.ptoceti.osgi.obixservice.resourcepath";
	public static final String DATABASEPATH = "com.ptoceti.osgi.obixservice.databasepath";
	public static final String EXTERNALRESOURCEPATH = "com.ptoceti.osgi.obixservice.externalresourcepath";
	public static final String NBEXECUTORPOOLTHREADS = "com.ptoceti.osgi.obixservice.nbexecutorpoolthreads";
	
	public static final String REALM = "obixapplication";
	
	public static final String RESTLETSYMBOLICNAME = "org.restlet";

	// the http service listener
	private HttpServiceListener httpSrvLst;
	// The data service listener
	private DataDeviceListener dataDeviceLst;
	
	// the path under which the service is accessible.
	private String obixServletPath;
	// the base path for the Oauth2 application
	private String oauthServletPath;
	// indicate if resource muste be secure with oauth
	private Boolean oauthSecure;
	// the port used for the service rest
	private Integer obixServletPort;
	// the path under which the resouces are accessibles.
	private String obixResourcesPath;
	// the path to the resources themselves.
	private String obixExternalResourcesPath;
	// the path to the database file
	private String databasePath;
	// falg to indicate if the database has benn initialised
	private boolean databaseInitialised = false;
	// indicate if the restlet main bundle has started
	private boolean restletHasStarted = false;
	// indicate if the rest application has started
	private boolean restAppStarted = false;
	// Le service Rest Obix
	private ObixRestComponent obixRestService;
	
	private ObixServlet obixServlet;
	
	private Oauth2Servlet oauth2Servlet;
	
	private ObixHttpHandler obixHttpHandler;
	
	private WireHandler wireHandler;
	
	private RestletListener restletListener;
	
	private String httpServiceSymbolicName;
	
	private ExecutorService threadExecutor;
	
	private ClientManager clientManager;
	
	private AppOwnerManager ownerManager;
	
	private Component component;

	// Default creator. Don't do nothing at this point.
	public ObixServiceImpl() {
	}

	/**
	 * Start registration of the service and setup listener to other services.
	 * Do that in a separate synchronised method because the service is a
	 * managed one and in this way, the call to the synchronized updated()
	 * method by the Configuration manager (presumely an asynchronous call) will
	 * be block till all registrations work is done.
	 * 
	 */

	protected synchronized void start() {

		// create a default pool of 2 threads.
		threadExecutor = Executors.newFixedThreadPool(2);
		
		obixHttpHandler = new ObixHttpHandler();
		
		// create the client manager that create id for oauth authorisation
		clientManager = new MemoryClientManager();
		// creat the owner manager that jeep track of owners ids. 
		ownerManager  = new AppOwnerManager();
		
		String[] clazzes = new String[] { ManagedService.class.getName(),ObixService.class.getName()};
		// register the class as a managed service.
		Hashtable<String, String> properties = new Hashtable<String, String>();
		properties.put(Constants.SERVICE_PID, ObixService.class.getName());
		sReg = Activator.bc.registerService(clazzes, this, properties);

		Activator.log(LogService.LOG_INFO, "Registered " + this.getClass().getName() +  ", Pid = " + (String) properties.get(Constants.SERVICE_PID));
		wireHandler = new WireHandler();
		
		// We need to get a reference to a data service. We need to get this
		// reference dynamically by constructing
		// a listener that will detect when the dataservice appear and disapear.
		String dataServiceFilter = "(objectclass=" + JdbcDevice.class.getName() + ")";
		try {
			dataDeviceLst = new DataDeviceListener(this);
			Activator.bc.addServiceListener(dataDeviceLst, dataServiceFilter);
			// in case the service is already registered, we send a REGISTER
			// event to the listener.
			ServiceReference srDataSrv[] = Activator.bc.getServiceReferences(JdbcDevice.class.getName(), null);
			if (srDataSrv != null) {
				dataDeviceLst.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, srDataSrv[0]));
			}
		} catch (InvalidSyntaxException e) {
			// We know there shouldn't be an exception here since we made the
			// filter string.
		}

		String servletfilter = "(objectclass=" + HttpService.class.getName() + ")";
		try {
			httpSrvLst = new HttpServiceListener();
			Activator.bc.addServiceListener(httpSrvLst, servletfilter);
			// In case the HttpService is already register, we force an event to
			// ourselves.
			ServiceReference servletSer[] = Activator.bc.getServiceReferences(HttpService.class.getName(), null);
			if (servletSer != null) {
				httpSrvLst.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, servletSer[0]));
			}
		} catch (InvalidSyntaxException e) {
			// The shouldn't be any exception comming here.
		}
		
		// register an event listener to the restlet bundle to detect when it has finished initialising.
		restletListener = new RestletListener();
		Activator.bc.addBundleListener(restletListener);
		
		for (final Bundle bundle : Activator.bc.getBundles()) {
			if( bundle.getSymbolicName().equals(RESTLETSYMBOLICNAME) && bundle.getState() == Bundle.ACTIVE){
				restletListener.bundleChanged(new BundleEvent(BundleEvent.STARTED, bundle));
				break;
			}
		}
	}
	
	/**
	 * Get the instance of the wire handler. Might not be started.
	 * 
	 * @return WireHandler the instance.
	 */
	protected WireHandler getWireHandler(){
		return wireHandler;
	}
	
	/**
	 * Get the instance of the executor service. Might not be instanciated
	 * 
	 * @return
	 */
	protected ExecutorService getExecutorService(){
		return threadExecutor;
	}

	/**
	 * Uregistered the class from the service registration system.
	 * 
	 * 
	 */
	protected void stop() {
		// Unregister the factory first ..
		sReg.unregister();
		
		stopRestService();

		Activator.log(LogService.LOG_INFO, "Unregistered " + this.getClass().getName());
		Activator.log(LogService.LOG_INFO, "Unregistered " + wireHandler.getClass().getName());
	}

	/*
	 * updated method from the Managed interface. Called by the Configuration
	 * Admin service when a configuration set is found and that this service
	 * should be instanciated.
	 */
	public synchronized void updated(Dictionary props) {

		if (props != null) {

			// we record the dictionary parameters. It may contains information
			// for the http servlet.
			obixServletPath = (String) props.get(SERVICEPATH);
			oauthServletPath = (String) props.get(OAUTHPATH);
			obixResourcesPath = (String) props.get(RESOURCEPATH);
			databasePath = (String) props.get(DATABASEPATH);
			obixExternalResourcesPath = (String) props.get(EXTERNALRESOURCEPATH);
			
			Object doOauthsecure = props.get(OAUTHSECURE);
			oauthSecure = doOauthsecure instanceof Boolean ? (Boolean) doOauthsecure: Boolean.parseBoolean(doOauthsecure != null ? doOauthsecure.toString(): "false");
		
			String ownerName = (String)props.get(OAUTHOWNERNAME);
			String ownerSecret = (String)props.get(OAUTHOWNERSECRET);
			if( ownerName != null && ownerName.length() > 0 && ownerSecret != null && ownerSecret.length() > 0){
				
				String md5;
				try {
					md5 = DigestUtils.toMd5(ownerSecret, "UTF-8");
					ownerManager.addOwner(ownerName, new String(md5));
				} catch (UnsupportedEncodingException e) {
					Activator.log(LogService.LOG_ERROR, "Error creating user: " + e );
				}
		       
		       
			}
			
			Object nbThreads = props.get(NBEXECUTORPOOLTHREADS);
			if( nbThreads != null && nbThreads instanceof Integer) {
				try {
				if( threadExecutor != null){
						threadExecutor.shutdown();
						threadExecutor.awaitTermination(10, TimeUnit.SECONDS);
					}
					threadExecutor = Executors.newFixedThreadPool(Integer.parseInt(nbThreads.toString()));
				}catch (InterruptedException e) {
					Activator.log(LogService.LOG_ERROR, "Error creating thread pool: " + e );
				}
			}
				
			
			Object port = props.get(SERVICEPORT);
			obixServletPort = port instanceof Integer ? (Integer) port : Integer.parseInt(port.toString());

			startRestService();
			startDatabase();

		} else {
			stopRestService();
		}
	}

	protected void startDatabase() {

		if (ObixDataHandler.getInstance().getDataDevice() != null && (!databaseInitialised)) {
			
			List<URL> urls = new ArrayList<URL>();

			Enumeration<?> pathEnums;
			
			pathEnums = Activator.bc.getBundle().getEntryPaths("/sql/");
		
			if (pathEnums != null) {
				while (pathEnums.hasMoreElements()) {
					String path = (String) pathEnums.nextElement();
					if (path.endsWith(".sql")) {

						URL url = Activator.bc.getBundle().getEntry(path);
						if (url != null) {
							urls.add(url);
						}
					}
				}
			}

			if (urls.size() > 0 && databasePath != null) {

				URL[] scriptsUrls = new URL[urls.size()];
				urls.toArray(scriptsUrls);

				StringBuffer sbf = new StringBuffer();
				
				for (int i = 0; i < scriptsUrls.length; i++) {

					URL url = scriptsUrls[i];
					InputStream stream;
					
					try {
						stream = url.openStream();
						sbf.append(readInputStreamAsString(stream));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				ObixDataHandler.getInstance().getDataDevice().setupDatabase(databasePath,
						sbf.toString());
				
				databaseInitialised = true;
			}
			
			wireHandler.setRespondToUpdates(true);
		}
	}
	
	private String readInputStreamAsString(InputStream in) throws IOException {

		BufferedInputStream bis = new BufferedInputStream(in);
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		int result = bis.read();
		while (result != -1) {
			byte b = (byte) result;
			buf.write(b);
			result = bis.read();
		}
		return buf.toString();
	}

	public class DataDeviceListener implements ServiceListener {
		private ObixServiceImpl serviceImpl = null;

		public DataDeviceListener(ObixServiceImpl serviceImpl) {
			this.serviceImpl = serviceImpl;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.osgi.framework.ServiceListener#serviceChanged(org.osgi.framework
		 * .ServiceEvent)
		 */
		public void serviceChanged(ServiceEvent event) {
			ServiceReference sr = event.getServiceReference();
			switch (event.getType()) {
			case ServiceEvent.REGISTERED: {
				ObixDataHandler.getInstance().setDataDevice((JdbcDevice) Activator.bc.getService(sr));
				Activator.log(LogService.LOG_INFO, "Getting instance of service: "
								+ JdbcDevice.class.getName() + ","
								+ Constants.SERVICE_PID + "="
								+ (String) sr.getProperty(Constants.SERVICE_PID));
				startDatabase();
			}
				break;
			case ServiceEvent.UNREGISTERING: {
				Activator.log(LogService.LOG_INFO, "Releasing service: " + JdbcDevice.class.getName() + ","
						+ Constants.SERVICE_PID + "="
						+ (String) sr.getProperty(Constants.SERVICE_PID));

				// httpService.unregister(obixServletPath);
				ObixDataHandler.getInstance().setDataDevice(null);
				databaseInitialised = false;
			}
				break;
			}
		}
	}

	protected synchronized void startRestService() {
		if ((obixServletPath != null)
				&& (obixHttpHandler.getHttpService() != null)
				&& (this.obixResourcesPath != null)
				&& restletHasStarted && !restAppStarted) {

			try {
				HttpContext defaultContext = obixHttpHandler.getHttpService().createDefaultHttpContext();
				//Hashtable<Object, Object> initParams = new Hashtable<Object, Object>();

				if (!obixServletPath.startsWith("/")) obixServletPath = "/" + obixServletPath;
				if (!oauthServletPath.startsWith("/")) oauthServletPath = "/" + oauthServletPath;
				
				//do this to serve through jetty connector directly
				/**
				if( obixRestService == null){
					obixRestService = new ObixRestComponent();
				}
				obixRestService.start(obixServletPath, obixServletPort);
				**/
				
				component = new Component();
			    component.getServers().add(Protocol.RIAP);
			    component.getClients().add(Protocol.RIAP);
			     
				// create oauth servlet
				if( oauth2Servlet == null){
					Oauth2ApplicationFactory factory = new Oauth2ApplicationFactory(clientManager, ownerManager);
					
					Application  application = factory.getApplication();
					component.getInternalRouter().attach(oauthServletPath, application);
					oauth2Servlet = new Oauth2Servlet(application);
				}
				obixHttpHandler.getHttpService().registerServlet( oauthServletPath, oauth2Servlet, null, defaultContext);
				Activator.log(LogService.LOG_INFO, "Registered oauth2 servlet under alias: " + oauthServletPath );
				
				// create obix servlet
				if( obixServlet == null){
					ObixApplicationFactory factory = new ObixApplicationFactory( oauthServletPath, oauthSecure );
					obixServlet = new ObixServlet(factory.getApplication());
				}
				obixHttpHandler.getHttpService().registerServlet( obixServletPath, obixServlet, null, defaultContext);
				Activator.log(LogService.LOG_INFO, "Registered obix servlet under alias: " + obixServletPath + " ,port = " + obixServletPort);
				
				
				if (!obixResourcesPath.startsWith("/")) obixResourcesPath = "/" + obixResourcesPath;
				
				if( obixExternalResourcesPath.startsWith("file:")) {
					
					FileSystemHttpContext fsHttpContext = new FileSystemHttpContext();
					obixHttpHandler.getHttpService().registerResources( obixResourcesPath, obixExternalResourcesPath.substring("file:".length()), fsHttpContext);
				} else obixHttpHandler.getHttpService().registerResources( obixResourcesPath, obixExternalResourcesPath, defaultContext);
				
				Activator.log(LogService.LOG_INFO, "Registered resources under alias: " + obixResourcesPath);
				Activator.log(LogService.LOG_INFO, "Registered external resources under alias: " + obixExternalResourcesPath);

				// flag that we have started the rest front
				restAppStarted = true;
				

			} catch (NamespaceException ne) {
				Activator.log(LogService.LOG_ERROR, "Error starting rest service: " + ne.toString());
			} catch (Exception e) {
				Activator.log(LogService.LOG_ERROR, "Error starting rest service: " +  e.toString());
			}

		}
	}

	protected synchronized void stopRestService() {
		
		// stop obix rest application
		if ((obixServletPath != null) && (obixHttpHandler.getHttpService() != null) && restAppStarted) {
			try {
				obixHttpHandler.getHttpService().unregister(obixServletPath);
				obixHttpHandler.getHttpService().unregister(oauthServletPath);
				obixHttpHandler.getHttpService().unregister(obixResourcesPath);
				// flag that we have stoppped the front
				restAppStarted = false;
			} catch (IllegalArgumentException ne) {
			} catch (Exception e) {
				Activator.log(LogService.LOG_ERROR, "Error starting obix rest service: " +  e.toString());
			}
		}
		obixServletPath = null;
		
		// stop oauth2 service application 
		if ((oauthServletPath != null) && (obixHttpHandler.getHttpService() != null)) {
			try {
				obixHttpHandler.getHttpService().unregister(oauthServletPath);
			} catch (IllegalArgumentException ne) {
			} catch (Exception e) {
				Activator.log(LogService.LOG_ERROR, "Error starting oauth2 service: " +  e.toString());
			}
		}
		oauthServletPath = null;
	}

	protected String getHttpServiceSymbolicName() {
		return httpServiceSymbolicName;
	}

	protected void setHttpServiceSymbolicName(String httpServiceSymbolicName) {
		this.httpServiceSymbolicName = httpServiceSymbolicName;
	}
	
	public String createOauthPublicClientID(String redirectURI){
		
		Map<String, Object> properties = new Hashtable<String, Object>();
		properties.put(Client.PROPERTY_SUPPORTED_FLOWS, new Object[] { GrantType.password, GrantType.refresh_token});
		String[] redirectURIs = new String[] { redirectURI };
		Client client = clientManager.createClient(ClientType.PUBLIC, redirectURIs, properties);
		
		return client.getClientId();
	}
	
	public boolean existsOauthClient(String id){
		return clientManager.findById(id) != null;
	}

	/**
	 * Listener that listen to the Http service register and unregister events.
	 * 
	 * @author lor
	 *
	 */
	public class HttpServiceListener implements ServiceListener {

		public HttpServiceListener() {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.osgi.framework.ServiceListener#serviceChanged(org.osgi.framework
		 * .ServiceEvent)
		 */
		public void serviceChanged(ServiceEvent event) {
			ServiceReference sr = event.getServiceReference();
			switch (event.getType()) {
			case ServiceEvent.REGISTERED: {
				obixHttpHandler.setHttpService((HttpService) Activator.bc.getService(sr));
				startRestService();
				Activator.log(LogService.LOG_INFO, "Getting instance of service: "
								+ HttpService.class.getName() + ","
								+ Constants.SERVICE_PID + "="
								+ (String) sr.getProperty(Constants.SERVICE_PID) + " from "
								+ sr.getBundle().getSymbolicName());
				
				// keep track of HttpService name for filling About resource info
				setHttpServiceSymbolicName(sr.getBundle().getSymbolicName());
				
			}
				break;
			case ServiceEvent.UNREGISTERING: {
				Activator.log(LogService.LOG_INFO, "Releasing service: "
						+ HttpService.class.getName() + ","
						+ Constants.SERVICE_PID + "="
						+ (String) sr.getProperty(Constants.SERVICE_PID));

				// httpService.unregister(obixServletPath);
				obixHttpHandler.setHttpService(null);
				
				setHttpServiceSymbolicName(null);
			}
				break;
			}
		}
	}
	
	/**
	 * 
	 * Listener that detect when the restlet main bundle has started. Necessary as this one re-init list of services and converters ...
	 * @author lor
	 *
	 */
	public class RestletListener implements BundleListener {
		@Override
		public void bundleChanged(BundleEvent event) {
			 if( event.getBundle().getSymbolicName().equals(RESTLETSYMBOLICNAME) ){
				 if( event.getType() == BundleEvent.STARTED){
					 restletHasStarted = true;
					 Activator.log(LogService.LOG_INFO, "Restlet started event detected.");
					 startRestService();
				 } else {
					 restletHasStarted = false;
				 }
			 }
		}
		
	}
}
