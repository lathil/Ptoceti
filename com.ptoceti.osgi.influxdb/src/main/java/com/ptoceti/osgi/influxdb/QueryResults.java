package com.ptoceti.osgi.influxdb;

import java.util.List;

public class QueryResults {
    
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
