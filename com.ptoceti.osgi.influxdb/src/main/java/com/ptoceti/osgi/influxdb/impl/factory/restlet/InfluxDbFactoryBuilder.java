package com.ptoceti.osgi.influxdb.impl.factory.restlet;

import java.net.URL;

public class InfluxDbFactoryBuilder {

    private InfluxDbResourceFactory factory = null;

    private InfluxDbFactoryBuilder(InfluxDbResourceFactory factory) {
	this.factory = factory;
    }

    public static InfluxDbFactoryBuilder build(URL target) {
	InfluxDbResourceFactory newFactory = new InfluxDbResourceFactory(target);
	return new InfluxDbFactoryBuilder(newFactory);
    }
    
    
    public InfluxDbResourceFactory getFactory(){
	return factory;
    }
    
    public InfluxDbFactoryBuilder dbName(String dbName){
	factory.setDbName(dbName);
	return this;
    }
}
