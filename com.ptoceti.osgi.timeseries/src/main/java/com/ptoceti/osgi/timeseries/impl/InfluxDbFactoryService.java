package com.ptoceti.osgi.timeseries.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.log.LogService;

import com.ptoceti.influxdb.factory.InfluxDbFactoryBuilder;
import com.ptoceti.influxdb.factory.InfluxDbResourceFactory;
import com.ptoceti.osgi.timeseries.TimeSeriesService;

public class InfluxDbFactoryService implements ManagedService {
    
    public static final String INFLUXDBURL = "com.ptoceti.osgi.influxdb.url";
    public static final String INFLUXDBNAME = "com.ptoceti.osgi.influxdb.dbname";
    public static final String TIMESERIESIMMEDIATEPOLICYDURATION = "com.ptoceti.osgi.timeseries.policy.immediate.duration";
    public static final String TIMESERIESSHORTPOLICYDURATION = "com.ptoceti.osgi.timeseries.policy.short.duration";
    public static final String TIMESERIESMEDIUMPOLICYDURATION = "com.ptoceti.osgi.timeseries.policy.medium.duration";
    public static final String TIMESERIESLONGPOLICYDURATION = "com.ptoceti.osgi.timeseries.policy.long.duration";
    public static final String TIMESERIESIMMEDIATEAGGREGATEDURATION = "com.ptoceti.osgi.timeseries.aggregate.immediate.duration";
    public static final String TIMESERIESSHORTAGGREGATEDURATION = "com.ptoceti.osgi.timeseries.aggregate.short.duration";
    public static final String TIMESERIESMEDIUMAGGREGATEDURATION = "com.ptoceti.osgi.timeseries.aggregate.medium.duration";
    
    
    protected TimeSeriesServiceImpl timeSeriesService = null;
    // a reference to the service registration for the Controller object.
    protected ServiceRegistration sReg = null;

    protected synchronized void start() {

	String[] clazzes = new String[] { ManagedService.class.getName(), InfluxDbFactoryService.class.getName() };
	// register the class as a managed service.
	Hashtable<String, String> properties = new Hashtable<String, String>();
	properties.put(Constants.SERVICE_PID, InfluxDbFactoryService.class.getName());
	sReg = Activator.bc.registerService(clazzes, this, properties);

	Activator.log(LogService.LOG_INFO,"Registered " + this.getClass().getName() + ", Pid = "
				+ (String) properties.get(Constants.SERVICE_PID));

    }

    /**
     * Uregistered the class from the service registration system.
     * 
     * 
     */
    protected void stop() {
	// Unregister the factory first ..
	sReg.unregister();

	Activator.log(LogService.LOG_INFO, "Unregistered " + this.getClass().getName());
    }

  
    public void updated(Dictionary properties) throws ConfigurationException {
	// TODO Auto-generated method stub
	if (properties != null) {
	    
	    String url = (String) properties.get(INFLUXDBURL);
	    String dbname = (String) properties.get(INFLUXDBNAME);
	    
	    
	    try {
		InfluxDbResourceFactory influxDbFactory = InfluxDbFactoryBuilder.build(new URL(url)).dbName(dbname).getFactory();
		timeSeriesService = new TimeSeriesServiceImpl(influxDbFactory, properties);
		
	    } catch (MalformedURLException e) {
		Activator.log(LogService.LOG_ERROR, "Couldn't create factory. Url malformed: " + url );
	    }
	}
    }

}
