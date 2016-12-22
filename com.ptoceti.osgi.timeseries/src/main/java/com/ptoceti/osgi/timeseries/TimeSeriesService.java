package com.ptoceti.osgi.timeseries;

public interface  TimeSeriesService {

    void setupMeasurement(String measurementName, String[] fieldNames);
    
    void dropMeasurement(String measurementName );
}
