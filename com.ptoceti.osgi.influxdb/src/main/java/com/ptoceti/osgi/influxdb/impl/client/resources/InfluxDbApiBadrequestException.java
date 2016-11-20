package com.ptoceti.osgi.influxdb.impl.client.resources;

import java.util.List;

import org.restlet.resource.Status;

import com.ptoceti.osgi.influxdb.Result;

@Status(value = 400, serialize = true)
public class InfluxDbApiBadrequestException  extends RuntimeException{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List<Result> results;
    private String error;
    
    
    public String getError() {
	return error;
    }
    public void setError(String error) {
	this.error = error;
    }
    public List<Result> getResults() {
	return results;
    }
    public void setResults(List<Result> results) {
	this.results = results;
    }
}
