package com.ptoceti.osgi.timeseries.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.osgi.service.log.LogService;

import com.ptoceti.influxdb.Serie;
import com.ptoceti.influxdb.SerieWrapper;
import com.ptoceti.influxdb.TimeStampHelper;
import com.ptoceti.osgi.obix.contract.HistoryRecord;
import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Val;
import com.ptoceti.osgi.obix.object.ValHelper;

public class HistoryRecordSerie extends SerieWrapper implements Iterator<HistoryRecord>,
	Iterable<HistoryRecord> {

    protected static final String VALUEFIELD = "value";
    protected static final String NAMEFIELD = "name";
    protected static final String TIMEFIELD = "time";
    protected static final String CONTRACTFIELD = "contract";

    public HistoryRecordSerie(Serie serie) {
	super(serie);
	// TODO Auto-generated constructor stub
    }

    public HistoryRecord build(List<String> values) {
	
	HistoryRecord record = null;
	
	    try {
		
		record = new HistoryRecord();
		
		Date date = TimeStampHelper.parseRfc3339((String) values.get(fields.get(TIMEFIELD)));
		Abstime timestamp = new Abstime("time", date.getTime());
		record.setTimeStamp(timestamp);

		Val val = ValHelper.buildFromContract((String) values.get(fields.get(CONTRACTFIELD)));
		val.decodeVal( values.get(fields.get(VALUEFIELD)).toString());
		record.setValue(val);

		record.setName((String) values.get(fields.get(NAMEFIELD)));

	    } catch (Exception ex) {
		Activator.log(LogService.LOG_ERROR, "Couldnot create HistoryRollupRecords, ex" + ex);
	    }
	    
	    
	return record;
    }

    public Iterator<HistoryRecord> iterator() {
	return this;
    }

    @Override
    public boolean hasNext() {
	return delegate.hasNext();
    }

    @Override
    public HistoryRecord next() {
	return build(delegate.next());
    }

    @Override
    public void remove() {
	delegate.remove();
    }

}
