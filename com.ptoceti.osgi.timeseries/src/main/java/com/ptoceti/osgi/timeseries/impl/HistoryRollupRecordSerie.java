package com.ptoceti.osgi.timeseries.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.osgi.service.log.LogService;

import com.ptoceti.influxdb.Serie;
import com.ptoceti.influxdb.SerieWrapper;
import com.ptoceti.influxdb.TimeStampHelper;
import com.ptoceti.osgi.obix.contract.HistoryRollupRecord;
import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Real;

public class HistoryRollupRecordSerie extends SerieWrapper implements Iterator<HistoryRollupRecord>,
	Iterable<HistoryRollupRecord> {

    protected static final String AVGFIELD = "avg";
    protected static final String MAXFIELD = "max";
    protected static final String MINFIELD = "min";
    protected static final String NAMEFIELD = "name";
    protected static final String TIMEFIELD = "time";
    protected static final String CONTRACTFIELD = "contract";
    
    protected TimeStampHelper timeStampHelper;

    public HistoryRollupRecordSerie(Serie serie) {
    	super(serie);
    	timeStampHelper = new TimeStampHelper();
    }

    public HistoryRollupRecord build(List<String> values) {

	HistoryRollupRecord record = null;

	try {

	    record = new HistoryRollupRecord();
	    // start is lowerbound
	    Date date = timeStampHelper.parseRfc3339((String) values.get(fields.get(TIMEFIELD)));
	    Abstime startTime = new Abstime("time", date.getTime());
	    record.setStart(startTime);

	    // end is upper bound
	    Abstime endTime = new Abstime("time", date.getTime());
	    record.setEnd(endTime);

	    Real avg = new Real();
	    avg.decodeVal(values.get(fields.get(AVGFIELD)));
	    record.setAvg(avg);

	    Real min = new Real();
	    min.decodeVal(values.get(fields.get(MINFIELD)));
	    record.setMin(min);

	    Real max = new Real();
	    max.decodeVal(values.get(fields.get(MAXFIELD)));
	    record.setMax(max);

	    Real sum = new Real();
	    sum.setVal(new Double(0));
	    record.setSum(sum);

	    record.setName("historyrolluprecord-" + record.getStart().encodeVal());

	} catch (Exception ex) {
	    Activator.log(LogService.LOG_ERROR, "Couldnot create HistoryRollupRecords, ex" + ex);
	}

	return record;
    }

    @Override
    public Iterator<HistoryRollupRecord> iterator() {
	return this;
    }

    @Override
    public boolean hasNext() {
	return delegate.hasNext();
    }

    @Override
    public HistoryRollupRecord next() {
	return build(delegate.next());
    }

    @Override
    public void remove() {
	delegate.remove();

    }

}
