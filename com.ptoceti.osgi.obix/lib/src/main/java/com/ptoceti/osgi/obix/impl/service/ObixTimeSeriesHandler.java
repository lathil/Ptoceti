package com.ptoceti.osgi.obix.impl.service;

import com.ptoceti.osgi.timeseries.TimeSeriesService;


public class ObixTimeSeriesHandler {

    private TimeSeriesService timeSeriesService;

    private static final ObixTimeSeriesHandler instance = new ObixTimeSeriesHandler();
    
    private ObixTimeSeriesHandler(){
	
    }
    
    public TimeSeriesService getTimeSeriesService() {
	return timeSeriesService;
    }

    public void setTimeSeriesService(TimeSeriesService timeSeriesService) {
	this.timeSeriesService = timeSeriesService;
    }

    public static ObixTimeSeriesHandler getInstance() {
	return instance;
    }

	
}
