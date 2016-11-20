package com.ptoceti.osgi.influxdb.impl.client.resources;

import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import com.ptoceti.osgi.influxdb.Batch;
import com.ptoceti.osgi.influxdb.Point;


public interface WriteResource {
    
    static String  path = "write";
    
    @Post()
    void write(Point point) throws InfluxDbApiNotFoundException;
    
    @Post()
    void write(Batch batch) throws InfluxDbApiNotFoundException;

}
