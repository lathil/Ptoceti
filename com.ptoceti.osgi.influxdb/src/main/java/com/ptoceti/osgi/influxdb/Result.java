package com.ptoceti.osgi.influxdb;

import java.util.List;

public class Result {

    private List<Serie> series;
    private String error;
    
    public List<Serie> getSeries() {
	return series;
    }
    public void setSeries(List<Serie> series) {
	this.series = series;
    }
    public String getError() {
	return error;
    }
    public void setError(String error) {
	this.error = error;
    }
    
    
}
