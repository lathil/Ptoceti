package com.ptoceti.osgi.timeseries;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.ptoceti.osgi.timeseries.HistoryRecord;

import com.ptoceti.influxdb.Serie;
import com.ptoceti.influxdb.SerieWrapper;
import com.ptoceti.influxdb.TimeStampHelper;
import com.ptoceti.osgi.timeseries.impl.Activator;
import org.osgi.util.measurement.Measurement;


public class HistoryRecordSerie extends SerieWrapper implements Iterator<HistoryRecord>,
        Iterable<HistoryRecord> {

    protected TimeStampHelper timeStampHelper;

    protected static final String VALUEFIELD = "value";
    protected static final String NAMEFIELD = "name";
    protected static final String TIMEFIELD = "time";
    protected static final String UNITFIIELD = "unit";

    public HistoryRecordSerie(Serie serie) {

        super(serie);
        timeStampHelper = new TimeStampHelper();
        // TODO Auto-generated constructor stub
    }

    public HistoryRecord build(List<String> values) {
	
	HistoryRecord record = null;
	
	    try {

            record = new HistoryRecord();

            Date date = timeStampHelper.parseRfc3339((String) values.get(fields.get(TIMEFIELD)));
            record.setTimmestamp(date);

            record.setVal(new Measurement(Double.parseDouble(values.get(fields.get(VALUEFIELD)))));

            record.setName((String) values.get(fields.get(NAMEFIELD)));

        } catch (Exception ex) {
            Activator.getLogger().error("Couldnot create HistoryRollupRecords, ex" + ex);
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
