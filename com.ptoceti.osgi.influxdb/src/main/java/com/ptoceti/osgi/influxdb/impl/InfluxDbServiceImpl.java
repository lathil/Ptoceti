package com.ptoceti.osgi.influxdb.impl;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.log.LogService;

import com.ptoceti.osgi.influxdb.InfluxDbService;
import com.ptoceti.osgi.influxdb.Point;
//import com.ptoceti.osgi.influxdb.impl.client.resources.WriteResource;

public class InfluxDbServiceImpl implements ManagedService, InfluxDbService {

    ServiceRegistration sReg = null;
    // the resource factory to handle calls to influxdb
    private InfluxDbResourceFactory resourceFactory =  null;
    
    public static final String INFLUXDBURL = "com.ptoceti.osgi.influxdb.url";
    public static final String INFLUXDBNAME = "com.ptoceti.osgi.influxdb.dbname";

    public InfluxDbServiceImpl() {
	String[] clazzes = new String[] { ManagedService.class.getName() };
	// register the class as a managed service.
	Hashtable properties = new Hashtable();
	properties.put(Constants.SERVICE_PID, this.getClass().getName());
	sReg = Activator.bc.registerService(clazzes, this, properties);

	Activator.log(LogService.LOG_INFO,"Registered " + this.getClass().getName() + ", Pid = " + (String) properties.get(Constants.SERVICE_PID));

    }

    @Override
    public void updated(Dictionary properties) throws ConfigurationException {
	if (properties != null) {
	   if( resourceFactory == null){
	       resourceFactory = new InfluxDbResourceFactory(properties);
	   } else {
	       resourceFactory.resetConfig(properties);
	   }
	} else {

	}

    }
    
    public void writePoint(Point point){
	
	//WriteResource wResource = resourceFactory.getWriteResource();
	//wResource.write(point);
	
    }

}
