package com.ptoceti.osgi.obix.impl;

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

import com.ptoceti.osgi.obix.impl.guice.GuiceContext;
import com.ptoceti.osgi.obix.restlet.ObixServlet;
import com.ptoceti.osgi.obix.restlet.ObixRestComponent;

import com.ptoceti.osgi.data.JdbcDevice;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Dictionary;

import javax.servlet.ServletException;

import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.service.log.LogService;
import org.osgi.service.cm.ManagedService;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.Constants;


/**
 * A ManagedService class providing Obix (Open Building Information eXchange)
 * metamodel and interface; The configuration will provide URI base path under
 * which to register the servlet that implement the web interface.
 * 
 * @author lor
 * 
 */
public class ObixService  implements ManagedService {

	// a reference to the service registration for the Controller object.
	ServiceRegistration sReg = null;

	public static final String SERVICEPATH = "com.ptoceti.osgi.obixservice.servletpath";
	public static final String SERVICEPORT = "com.ptoceti.osgi.obixservice.servletport";
	public static final String RESOURCEPATH = "com.ptoceti.osgi.obixservice.resourcepath";
	public static final String DATABASEPATH = "com.ptoceti.osgi.obixservice.databasepath";
	public static final String EXTERNALRESOURCEPATH = "com.ptoceti.osgi.obixservice.externalresourcepath";

	// the http service listener
	private HttpServiceListener httpSrvLst;
	// The data service listener
	private DataDeviceListener dataDeviceLst;
	
	// the path under which the service is accessible.
	private String obixServletPath;
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

	// Le service Rest Obix
	private ObixRestComponent obixRestService;
	
	private ObixHttpHandler obixHttpHandler;
	
	private WireHandler wireHandler;
	
	private String httpServiceSymbolicName;

	// Default creator. Don't do nothing at this point.
	public ObixService() {

		obixRestService = new ObixRestComponent();
	}

	/**
	 * Start registration of the service and setup listener to other services.
	 * Do that in a separate synchronised method because the service is a
	 * managed one and in this way, the call to the synchronized updated()
	 * method by the Configuration manager (presumely an asynchronous call) will
	 * be block till all registrations work is done.
	 * 
	 */

	public synchronized void start() {

		obixHttpHandler = new ObixHttpHandler();
		
		String[] clazzes = new String[] { ManagedService.class.getName()};
		// register the class as a managed service.
		Hashtable properties = new Hashtable();
		properties.put(Constants.SERVICE_PID, this.getClass().getName());
		sReg = Activator.bc.registerService(clazzes, this, properties);

		Activator.log(LogService.LOG_INFO, "Registered "
				+ this.getClass().getName() +  ", Pid = "
				+ (String) properties.get(Constants.SERVICE_PID));

		
		wireHandler = new WireHandler();
		
		
		// We need to get a reference to a data service. We need to get this
		// reference dynamically by constructing
		// a listener that will detect when the dataservice appear and disapear.
		String dataServiceFilter = "(objectclass=" + JdbcDevice.class.getName()
				+ ")";
		try {
			dataDeviceLst = new DataDeviceListener(this);
			Activator.bc.addServiceListener(dataDeviceLst, dataServiceFilter);
			// in case the service is already registered, we send a REGISTER
			// event to the listener.
			ServiceReference srDataSrv[] = Activator.bc.getServiceReferences(
					JdbcDevice.class.getName(), null);
			if (srDataSrv != null) {
				dataDeviceLst.serviceChanged(new ServiceEvent(
						ServiceEvent.REGISTERED, srDataSrv[0]));
			}
		} catch (InvalidSyntaxException e) {
			// We know there shouldn't be an exception here since we made the
			// filter string.
		}

		String servletfilter = "(objectclass=" + HttpService.class.getName()
				+ ")";
		try {
			httpSrvLst = new HttpServiceListener();
			Activator.bc.addServiceListener(httpSrvLst, servletfilter);
			// In case the HttpService is already register, we force an event to
			// ourselves.
			ServiceReference servletSer[] = Activator.bc.getServiceReferences(
					HttpService.class.getName(), null);
			if (servletSer != null) {
				httpSrvLst.serviceChanged(new ServiceEvent(
						ServiceEvent.REGISTERED, servletSer[0]));
			}
		} catch (InvalidSyntaxException e) {
			// The shouldn't be any exception comming here.
		}

	}

	/**
	 * Uregistered the class from the service registration system.
	 * 
	 * 
	 */
	public void stop() {
		// Unregister the factory first ..
		sReg.unregister();
		
		stopServlet();

		Activator.log(LogService.LOG_INFO, "Unregistered "
				+ this.getClass().getName());
		Activator.log(LogService.LOG_INFO, "Unregistered "
				+ wireHandler.getClass().getName());
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
			obixResourcesPath = (String) props.get(RESOURCEPATH);
			databasePath = (String) props.get(DATABASEPATH);
			obixExternalResourcesPath = (String) props.get(EXTERNALRESOURCEPATH);
			
			Object port = props.get(SERVICEPORT);
			obixServletPort = port instanceof Integer ? (Integer) port : Integer.parseInt(port.toString());

			startServlet();
			startDatabase();

		} else {
			stopServlet();
		}
	}

	public void startDatabase() {

		if (ObixDataHandler.getInstance().getDataDevice() != null && (!databaseInitialised)) {
			
			ArrayList urls = new ArrayList();

			Enumeration pathEnums;
			
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
		private ObixService serviceImpl = null;

		public DataDeviceListener(ObixService serviceImpl) {
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
				ObixDataHandler.getInstance().setDataDevice((JdbcDevice) Activator.bc
						.getService(sr));
				Activator.log(LogService.LOG_INFO,
						"Getting instance of service: "
								+ JdbcDevice.class.getName()
								+ ","
								+ Constants.SERVICE_PID
								+ "="
								+ (String) sr
										.getProperty(Constants.SERVICE_PID));
				startDatabase();
			}
				break;
			case ServiceEvent.UNREGISTERING: {
				Activator.log(LogService.LOG_INFO, "Releasing service: "
						+ JdbcDevice.class.getName() + ","
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

	protected synchronized void startServlet() {
		if ((obixServletPath != null)
				&& (obixHttpHandler.getHttpService() != null)
				&& (this.obixResourcesPath != null)) {

			try {
				HttpContext defaultContext = obixHttpHandler.getHttpService().createDefaultHttpContext();
				Hashtable initParams = new Hashtable();

				if (!obixServletPath.startsWith("/")) obixServletPath = "/" + obixServletPath;
				obixRestService.start(obixServletPath, obixServletPort);
				
				
				//obixHttpHandler.getHttpService().registerServlet( obixServletPath, obixRestService, null, defaultContext);
				
				if (!obixResourcesPath.startsWith("/")) obixResourcesPath = "/" + obixResourcesPath;
				
				if( obixExternalResourcesPath.startsWith("file:")) {
					
					FileSystemHttpContext fsHttpContext = new FileSystemHttpContext();
					obixHttpHandler.getHttpService().registerResources(
							obixResourcesPath, obixExternalResourcesPath.substring("file:".length()), fsHttpContext);
				} else obixHttpHandler.getHttpService().registerResources(
						obixResourcesPath, obixExternalResourcesPath, defaultContext);

				Activator.log(LogService.LOG_INFO,
						"Registered servlet under alias: " + obixServletPath + " ,port = " + obixServletPort);
				Activator.log(LogService.LOG_INFO,
						"Registered resources under alias: " + obixResourcesPath);
				Activator.log(LogService.LOG_INFO,
						"Registered external resources under alias: " + obixExternalResourcesPath);

			} catch (NamespaceException ne) {
				Activator.log(LogService.LOG_ERROR, "Error stating rest service: " + ne.toString());
			} catch (Exception e) {
				Activator.log(LogService.LOG_ERROR, "Error stating rest service: " +  e.toString());
			}

		}
	}

	protected synchronized void stopServlet() {
		if ((obixServletPath != null)
				&& (obixHttpHandler.getHttpService() != null)) {
			try {
				obixRestService.stop();
				obixHttpHandler.getHttpService().unregister(obixServletPath);
				obixHttpHandler.getHttpService().unregister(obixResourcesPath);
			} catch (IllegalArgumentException ne) {
			} catch (Exception e) {
				Activator.log(LogService.LOG_ERROR, "Error stating rest service: " +  e.toString());
			}
		}
		// We discard the initialisation parametres dictionary.
		obixServletPath = null;
	}

	public String getHttpServiceSymbolicName() {
		return httpServiceSymbolicName;
	}

	public void setHttpServiceSymbolicName(String httpServiceSymbolicName) {
		this.httpServiceSymbolicName = httpServiceSymbolicName;
	}

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
				obixHttpHandler.setHttpService((HttpService) Activator.bc
						.getService(sr));
				startServlet();
				Activator.log(LogService.LOG_INFO,
						"Getting instance of service: "
								+ HttpService.class.getName()
								+ ","
								+ Constants.SERVICE_PID
								+ "="
								+ (String) sr.getProperty(Constants.SERVICE_PID)
								+ " from "
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
}
