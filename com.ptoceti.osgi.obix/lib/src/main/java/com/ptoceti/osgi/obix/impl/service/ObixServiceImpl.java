package com.ptoceti.osgi.obix.impl.service;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : ObixServiceImpl.java
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

import com.ptoceti.osgi.obix.impl.front.ObixApplicationConfig;
import com.ptoceti.osgi.obix.impl.guice.GuiceContext;
import com.ptoceti.osgi.timeseries.TimeSeriesService;

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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.glassfish.jersey.servlet.ServletContainer;
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
public class ObixServiceImpl implements com.ptoceti.osgi.obix.service.ObixService, ManagedService {

	// a reference to the service registration for the Controller object.
	ServiceRegistration sReg = null;

	public static final String SERVICEPATH = "com.ptoceti.osgi.obixservice.servletpath";
	public static final String SERVICEPORT = "com.ptoceti.osgi.obixservice.servletport";
	
	public static final String DATABASEPATH = "com.ptoceti.osgi.obixservice.databasepath";
	public static final String NBEXECUTORPOOLTHREADS = "com.ptoceti.osgi.obixservice.nbexecutorpoolthreads";
	
	public static final String REALM = "obixapplication";


	// the http service listener
	private HttpServiceListener httpSrvLst;
	// The data service listener
	private DataDeviceListener dataDeviceLst;
	    // The time series listener
    private TimeSeriesListener timeSeriesLst;
	
	// the path under which the service is accessible.
	private String obixServletPath;

	
	private Integer oauthTokenExpiredPeriod;
	// the port used for the service rest
	private Integer obixServletPort;
	// the path to the database file
	private String databasePath;
	// falg to indicate if the database has benn initialised
	private boolean databaseInitialised = false;
	// indicate if the rest application has started
	private boolean restAppStarted = false;
	// the servlet container for the rest application
	private ServletContainer obixServlet;

	private ObixHttpHandler obixHttpHandler;
	
	private WireHandler wireHandler;
	
	private EventUpdateHandler eventUpdateHandler;
	
	private String httpServiceSymbolicName;
	
	private ExecutorService threadExecutor;

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
		threadExecutor = Executors.newFixedThreadPool(4);
		
		obixHttpHandler = new ObixHttpHandler();


        String[] clazzes = new String[]{ManagedService.class.getName(), com.ptoceti.osgi.obix.service.ObixService.class.getName()};
		// register the class as a managed service.
		Hashtable<String, String> properties = new Hashtable<String, String>();
        properties.put(Constants.SERVICE_PID, this.getClass().getName());
		sReg = Activator.bc.registerService(clazzes, this, properties);

		Activator.log(LogService.LOG_INFO, "Registered " + this.getClass().getName() +  ", Pid = " + (String) properties.get(Constants.SERVICE_PID));

		wireHandler = new WireHandler();
		
		// Create the event update Handler only once
		eventUpdateHandler = GuiceContext.Instance.getInjector().getInstance(EventUpdateHandler.class);
		
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

		String timeSeriesServiceFilter = "(objectclass=" + TimeSeriesService.class.getName() + ")";
		try {
			timeSeriesLst = new TimeSeriesListener(this);
			Activator.bc.addServiceListener(timeSeriesLst, timeSeriesServiceFilter);
			// in case the service is already registered, we send a REGISTER
			// event to the listener.
			ServiceReference timeSerDataSrv[] = Activator.bc.getServiceReferences(TimeSeriesService.class.getName(),
					null);
			if (timeSerDataSrv != null) {
				timeSeriesLst.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, timeSerDataSrv[0]));
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

	}
	
	/**
	 * Get the instance of the wire handler. Might not be started.
	 * 
	 * @return WireHandler the instance.
	 */
	public WireHandler getWireHandler(){
		return wireHandler;
	}
	
	/**
	 * Get the instance of the event update Handler.
	 * 
	 * @return EventUpdateHandler the instance
	 */
	public EventUpdateHandler getEventUpdateHandler(){
		return eventUpdateHandler;
	}
	
	/**
	 * Get the instance of the executor service. Might not be instanciated
	 * 
	 * @return ExecutorService the service executor service
	 */
	public ExecutorService getExecutorService(){
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
			databasePath = (String) props.get(DATABASEPATH);


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

			startDatabase();
			stopRestService();
			startRestService();

		} else {
			stopRestService();
		}
	}

	protected void startDatabase() {

		if (ObixDataHandler.getInstance().getDataDevice() != null && (!databaseInitialised)) {
			
			List<URL> urls = new ArrayList<URL>();

			Enumeration<?> pathEnums;
			
			// we want to get the database initialisation script for the correct jdbc driver.
			String driverName = ObixDataHandler.getInstance().getDataDevice().getDriverName().toLowerCase();
			pathEnums = Activator.bc.getBundle().getEntryPaths("/sql/" + driverName.replace(".", "/"));
		
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


	protected synchronized void startRestService() {
		if ((obixServletPath != null)
				&& (obixHttpHandler.getHttpService() != null)) {

			try {
				HttpContext defaultContext = obixHttpHandler.getHttpService().createDefaultHttpContext();
				//Hashtable<Object, Object> initParams = new Hashtable<Object, Object>();

				if (!obixServletPath.startsWith("/")) obixServletPath = "/" + obixServletPath;
				
				//do this to serve through jetty connector directly
				/**
				if( obixRestService == null){
					obixRestService = new ObixRestComponent();
				}
				obixRestService.start(obixServletPath, obixServletPort);
				**/


				// create obix servlet
				if( obixServlet == null){

					Dictionary<String, String> ObixApplicationServletParams = new Hashtable<>();
					ObixApplicationServletParams.put("javax.ws.rs.Application", ObixApplicationConfig.class.getName());

					// TODO - temporary workaround
					// This is a workaround related to issue JERSEY-2093; grizzly (1.9.5) needs to have the correct context
					// classloader set
					ClassLoader myClassLoader = getClass().getClassLoader();
					ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();

					try {
						Thread.currentThread().setContextClassLoader(myClassLoader);

						obixHttpHandler.getHttpService().registerServlet(obixServletPath, new ServletContainer(), ObixApplicationServletParams, defaultContext);
						Activator.log(LogService.LOG_INFO, "Registered obix servlet under alias: " + obixServletPath + " ,port = " + obixServletPort);

					} finally {
						Thread.currentThread().setContextClassLoader(originalContextClassLoader);
					}
				}

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
		
		if(restAppStarted) {
			// stop obix rest application
			if ((obixServletPath != null) && (obixHttpHandler.getHttpService() != null)) {
				try {
					obixHttpHandler.getHttpService().unregister(obixServletPath);
					if(obixServlet != null){

						obixServlet.destroy();
						obixServlet = null;
					}
				} catch (IllegalArgumentException ne) {
				} catch (Exception e) {
					Activator.log(LogService.LOG_ERROR, "Error starting obix rest service: " +  e.toString());
				}
			}


			// flag that we have stoppped the front
			restAppStarted = false;
		}
		
	}

	protected String getHttpServiceSymbolicName() {
		return httpServiceSymbolicName;
	}

	protected void setHttpServiceSymbolicName(String httpServiceSymbolicName) {
		this.httpServiceSymbolicName = httpServiceSymbolicName;
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
		Activator.log(LogService.LOG_INFO, "Getting instance of service: " + JdbcDevice.class.getName() + ","
			+ Constants.SERVICE_PID + "=" + (String) sr.getProperty(Constants.SERVICE_PID));
		startDatabase();
	    }
		break;
	    case ServiceEvent.UNREGISTERING: {
		Activator.log(LogService.LOG_INFO, "Releasing service: " + JdbcDevice.class.getName() + ","
			+ Constants.SERVICE_PID + "=" + (String) sr.getProperty(Constants.SERVICE_PID));

		// httpService.unregister(obixServletPath);
		ObixDataHandler.getInstance().setDataDevice(null);
		databaseInitialised = false;
	    }
		break;
	    }
	}
    }

    /**
     * Listener that listen to the TimeSeries service register and unregister
     * events.
     * 
     * @author LATHIL
     * 
     */
    public class TimeSeriesListener implements ServiceListener {

	private ObixServiceImpl serviceImpl = null;

	public TimeSeriesListener(ObixServiceImpl serviceImpl) {
	    this.serviceImpl = serviceImpl;
	}

	public void serviceChanged(ServiceEvent event) {
	    ServiceReference sr = event.getServiceReference();
	    switch (event.getType()) {
	    case ServiceEvent.REGISTERED: {
		ObixTimeSeriesHandler.getInstance().setTimeSeriesService(
			(TimeSeriesService) Activator.bc.getService(sr));
		Activator.log(LogService.LOG_INFO, "Getting instance of service: " + TimeSeriesService.class.getName()
			+ "," + Constants.SERVICE_PID + "=" + (String) sr.getProperty(Constants.SERVICE_PID));
	    }
		break;
	    case ServiceEvent.UNREGISTERING: {
		Activator.log(LogService.LOG_INFO, "Releasing service: " + TimeSeriesService.class.getName() + ","
			+ Constants.SERVICE_PID + "=" + (String) sr.getProperty(Constants.SERVICE_PID));

		// httpService.unregister(obixServletPath);
		ObixTimeSeriesHandler.getInstance().setTimeSeriesService(null);
	    }
		break;
	    }
	}

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
		Activator.log(LogService.LOG_INFO, "Getting instance of service: " + HttpService.class.getName() + ","
			+ Constants.SERVICE_PID + "=" + (String) sr.getProperty(Constants.SERVICE_PID) + " from "
			+ sr.getBundle().getSymbolicName());

		// keep track of HttpService name for filling About resource
		// info
		setHttpServiceSymbolicName(sr.getBundle().getSymbolicName());

	    }
		break;
	    case ServiceEvent.UNREGISTERING: {
		Activator.log(LogService.LOG_INFO, "Releasing service: " + HttpService.class.getName() + ","
			+ Constants.SERVICE_PID + "=" + (String) sr.getProperty(Constants.SERVICE_PID));

		// httpService.unregister(obixServletPath);
		obixHttpHandler.setHttpService(null);

		setHttpServiceSymbolicName(null);
	    }
		break;
	    }
	}
    }


}

