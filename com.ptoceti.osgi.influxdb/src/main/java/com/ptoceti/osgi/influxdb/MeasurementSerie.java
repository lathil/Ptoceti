package com.ptoceti.osgi.influxdb;

import java.util.Iterator;
import java.util.List;

public class MeasurementSerie extends SerieWrapper implements Iterator<MeasurementSerie.Measurement>,
	Iterable<MeasurementSerie.Measurement> {

    protected static final String SERIENAME = "measurements";
    protected static final String NAMEFIELD = "name";

    public MeasurementSerie(Serie serie) {
	super(serie);
    }

    @Override
    public Iterator<Measurement> iterator() {
	return this;
    }

    @Override
    public boolean hasNext() {
	return delegate.hasNext();
    }

    @Override
    public void remove() {
	delegate.remove();

    }

    @Override
    public MeasurementSerie.Measurement next() {
	return new Measurement(delegate.next());
    }

    public static String getSerieName() {
	return SERIENAME;
    }

    public class Measurement {
	private String name;

	Measurement(List<Object> values) {
	    setName((String) values.get(fields.get(NAMEFIELD)));
	}

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}
    }
}
