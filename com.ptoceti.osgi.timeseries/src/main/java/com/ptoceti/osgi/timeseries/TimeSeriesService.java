package com.ptoceti.osgi.timeseries;

import java.util.ArrayList;
import java.util.Date;

import org.osgi.util.measurement.Measurement;

public interface TimeSeriesService {

    public boolean ping();

    void setupMeasurement(String measurementName, String[] fieldNames);

    void dropMeasurement(String measurementName);

    public void saveMeasurementRecord(String measurementName, Measurement record);

    public ArrayList<HistoryRecord> loadMeasurementRecords(String measurementName, Date start, Date end, Integer limit);

}
