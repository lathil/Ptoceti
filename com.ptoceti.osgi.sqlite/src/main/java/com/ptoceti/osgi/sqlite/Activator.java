 package com.ptoceti.osgi.sqlite;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : SQLite
 * FILENAME : Activator.java
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


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.log.LogService;
import org.sqlite.OSInfo;



/**
 * Activator class implement the BundleActivator interface. This class load the bundle in the framework.
 * Task is to register the SQLite Driver as a Driver service into the framework
 *
 * @author Laurent Thil
 * @version 1.0
 */

public class Activator implements BundleActivator{

	/**
	 *  a reference to this service bundle context.
	 */
	static BundleContext bc = null;
	/**
	 * a reference to the logging service.
	 */
	static LogService logSer;
	/**
	 * the name of the logging service in the osgi framework.
	 */
	static private final String logServiceName = org.osgi.service.log.LogService.class.getName();
	
	/**
	 * The service instance
	 */
	private static SQLiteDataSourceFactory sqliDataSourceFactory;
	
	/**
	 * The driver instance
	 */
	private static SQLiteJDBC sqliteJDBC;
	
	/**
	 * Activator creator. Just create a base ObixServiceImpl object instance.
	 *
	 */
	public Activator()
	{
		
	}
	
	/**
	 * Called by the framework for initialisation when the Activator class is loaded.
	 * The method first get a service reference on the osgi logging service, used for
	 * logging whithin the bundle.
	 *
	 * If the method cannot get a reference to the logging service, a NullPointerException is thrown.
	 * @param context the bundle context
	 * @throws BundleException
	 */
	public void start(final BundleContext context) throws BundleException {
		
		Activator.bc = context;
		
		// we construct a listener to detect if the log service appear or disapear.
		String filter = "(objectclass=" + logServiceName + ")";
		ServiceListener logServiceListener = new LogServiceListener();
		try {
			bc.addServiceListener( logServiceListener, filter);
			// in case the service is already registered, we send a REGISTERED event to its listener.
			ServiceReference srLog = bc.getServiceReference( logServiceName );
			if( srLog != null ) {
				logServiceListener.serviceChanged(new ServiceEvent( ServiceEvent.REGISTERED, srLog ));
			}
		} catch ( InvalidSyntaxException e ) {
			throw new BundleException("Error in filter string while registering LogServiceListener." + e.toString());
		}
			
		try {
			// load libraies if needed befor registering the driver.
			loadLibrairies(context);
			
			sqliteJDBC = new SQLiteJDBC();
			
			String[] dataSourceFactoryClazzes = new String[] {DataSourceFactory.class.getName()};
			Hashtable<String, String> dataSourcesProperties = new Hashtable<String, String>();
			dataSourcesProperties.put( DataSourceFactory.OSGI_JDBC_DRIVER_CLASS, com.ptoceti.osgi.sqlite.SQLiteJDBC.class.getName());
			//dataSourcesProperties.put( DataSourceFactory.OSGI_JDBC_DRIVER_NAME, );
			dataSourcesProperties.put( DataSourceFactory.OSGI_JDBC_DRIVER_VERSION, String.valueOf(sqliteJDBC.getMajorVersion()).concat(".").concat(String.valueOf(sqliteJDBC.getMinorVersion())));
			// register the data source factory
			sqliDataSourceFactory = new SQLiteDataSourceFactory();
			Activator.bc.registerService(dataSourceFactoryClazzes, sqliDataSourceFactory, dataSourcesProperties );
			
		} catch( Exception e) {
			throw new BundleException( e.toString() );
		}
			
		log(LogService.LOG_INFO, "Starting version " + bc.getBundle().getHeaders().get("Bundle-Version"));
		
		
			
	}
	
	/**
	 * Xerial sqlite-jdbc only taken account of sqlite libraries packed in it own jar. No easy way to add additional through osgi natural
	 * native libray mecanisme.
	 * 
	 * Provide here way to add libraries included in resource foldar
	 * @param context the bundle context
	 */
	private void loadLibrairies(final BundleContext context){
		// get library folder name from os + arch
		String libraryResourcePath = OSInfo.getNativeLibFolderPathForCurrentOS();
		
		String sqliteNativeLibraryName = System.mapLibraryName("sqlitejdbc");
        if (sqliteNativeLibraryName != null && sqliteNativeLibraryName.endsWith("dylib")) {
        	sqliteNativeLibraryName = sqliteNativeLibraryName.replace("dylib", "jnilib");
        }
        
        // check if the resource folder exist in the bundle
        URL libUrl = context.getBundle().getEntry("/" + libraryResourcePath + "/" + sqliteNativeLibraryName);
        if( libUrl != null) {
        	
        	String fullPath = libUrl.toExternalForm();
        	log(LogService.LOG_INFO, "Bundle sqlite libray dected for os/arch: " + fullPath); 
        	 
        	// extract the file name.
        	String targetFileName = "sqlite-" + getName() + "-" + getVersion() + "-" + OSInfo.getArchName() + "-" + sqliteNativeLibraryName;
        	// temporary library folder
            String tempFolder = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath();
            
            Boolean loaded = false;
            
            try {
   	            File extractedLibFile = new File(tempFolder, targetFileName);
	            if( extractedLibFile.exists()){
	            	
	            	 String md5sum1 = md5sum(libUrl.openStream());
	                 String md5sum2 = md5sum(new FileInputStream(extractedLibFile));
	
	                 if (md5sum1.equals(md5sum2)) {
	                     loaded = true;
	                 }
	                 else {
	                     // remove old native library file
	                     boolean deletionSucceeded = extractedLibFile.delete();
	                     if (!deletionSucceeded) {
	                         throw new IOException("failed to remove existing native library file: " + extractedLibFile.getAbsolutePath());
	                     }
	                 }
	            }
	        	
	            // extract file from bundle resource to temp folder
	            if( !loaded){
	            	FileOutputStream writer = new FileOutputStream(extractedLibFile);
	            	InputStream reader = libUrl.openStream();
	            	 byte[] buffer = new byte[1024];
	                 int bytesRead = 0;
	                 while ((bytesRead = reader.read(buffer)) != -1) {
	                     writer.write(buffer, 0, bytesRead);
	                 }
	
	                 writer.close();
	                 reader.close();
	                 
	                 if (!System.getProperty("os.name").contains("Windows")) {
	                     try {
	                         Runtime.getRuntime().exec(new String[] { "chmod", "755", extractedLibFile.getAbsolutePath() })
	                                 .waitFor();
	                     }
	                     catch (Throwable e) {}
	                 }
	            }
	        	
	            // indicate to xserial jdbc to load this library
	        	System.setProperty("org.sqlite.lib.path", tempFolder);
	            System.setProperty("org.sqlite.lib.name", targetFileName);
	            
	            log(LogService.LOG_INFO, "Recorded sqlite library at: " + tempFolder + "/" + targetFileName); 
	            
            } catch (IOException ex) {
            	 log(LogService.LOG_ERROR, "Error while attempting to extract bundle's local sqlite library. ex: " + ex.toString()); 
            }
        	
        }
	}
	
	/**
	 * Getter return a bundle property
	 * 
	 * @param propertyName the name of the prperty
	 * @return Sting the property value
	 */
	public static String getProperty(final String propertyName){
		return (String)bc.getProperty(propertyName);
	}
	
	/**
	 * Getter return a property from the bundle manifest
	 * 
	 * @param propertyName the name of the property
	 * @return the property value
	 */
	public static String getManifestProperty(final String propertyName){
		return (String)bc.getBundle().getHeaders().get(propertyName);
	}
	
	/**
	 * Called by the framework to stop the service
	 *
	 * @param context the bundle context
	 * @throws BundleException if exception occurs while stopping the service
	 */
	public void stop( final BundleContext context ) throws BundleException {
	
		log(LogService.LOG_INFO, "Stopping");
		Activator.bc = null;
	}
		
	/**
	 * Class method for logging to the logservice. This method can be accessed from every class
	 * in the bundle by simply invoking Activator.log(..).
	 *
	 * @param logLevel : the level to use when togging this message.
	 * @param message : the message to log.
	 */
	static public void log(final int logLevel, final String message ) {
		if( logSer != null )
			logSer.log( logLevel, message );
	}
	
	/**
	 * Internel listener class that receives framework event when the log service is registered
	 * in the the framework and when it is being removed from it. The framework is a dynamic place
	 * and it is important to note when services appear and disappear.
	 * This inner class update the outer class reference to the log service in concordance.
	 *
	 */
	private class LogServiceListener implements ServiceListener {
		
		/**
		 * Unique method of the ServiceListener interface.
		 * 
		 * @param event the service event
		 *
		 */
		public void serviceChanged( final ServiceEvent event ) {
			
				ServiceReference sr = event.getServiceReference();
				switch(event.getType()) {
					case ServiceEvent.REGISTERED: {
						logSer = (LogService) bc.getService(sr);
					}
					break;
					case ServiceEvent.UNREGISTERING: {
						logSer = null;
					}
					break;
				}
		}
	}
	
	protected String getVersion() {
		return Activator.getManifestProperty(org.osgi.framework.Constants.BUNDLE_VERSION);
	}
	
	protected String getName(){
		return getManifestProperty(org.osgi.framework.Constants.BUNDLE_NAME);
	}
	
	 /**
     * Computes the MD5 value of the input stream.
     * @param input InputStream.
     * @return Encrypted string for the InputStream.
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    static String md5sum(InputStream input) throws IOException {
        BufferedInputStream in = new BufferedInputStream(input);

        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            DigestInputStream digestInputStream = new DigestInputStream(in, digest);
            for (; digestInputStream.read() >= 0;) {

            }
            ByteArrayOutputStream md5out = new ByteArrayOutputStream();
            md5out.write(digest.digest());
            return md5out.toString();
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm is not available: " + e);
        }
        finally {
            in.close();
        }
    }

	protected static SQLiteJDBC getSqliteJDBC() {
		return sqliteJDBC;
	}
}
