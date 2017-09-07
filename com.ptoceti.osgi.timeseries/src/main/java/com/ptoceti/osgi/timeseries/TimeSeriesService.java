package com.ptoceti.osgi.timeseries;

import java.util.ArrayList;
import java.util.Date;

import com.ptoceti.osgi.obix.contract.HistoryRecord;
import com.ptoceti.osgi.obix.contract.HistoryRollupRecord;
import com.ptoceti.osgi.obix.object.Val;

public interface  TimeSeriesService {

    void setupMeasurement(String measurementName, String[] fieldNames);
    
    void dropMeasurement(String measurementName );
    
    public void saveMeasurementRecord(String measurementName, Val record);
    
    public ArrayList<HistoryRecord> loadMeasurementRecords(String measurementName, Date start, Date end, Integer limit);
    
    public ArrayList<HistoryRollupRecord> loadMeasurementRollUpRecords(String measurementName, Date start, Date end, Integer limit);
    
    
}
