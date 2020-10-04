package com.ptoceti.osgi.rest.impl.application.model;

import com.ptoceti.influxdb.MeasurementSerie;
import com.ptoceti.osgi.control.Measure;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "measurement")
public class MeasurementData {

    private double value;
    private double error;
    private long time;
    private String unit;
    private String name;

    public MeasurementData() {

    }

    public MeasurementData(double value, double error, long time, String unit, String name) {
        this.value = value;
        this.error = error;
        this.time = time;
        this.unit = unit;
        this.name = name;
    }

    public MeasurementData(Measure measure) {
        this.value = measure.getValue();
        this.error = measure.getError();
        this.name = measure.toString();
        this.time = measure.getTime();
        this.unit = measure.getUnit().toString();
    }

    public MeasurementData(org.osgi.util.measurement.Measurement measure) {

        this.value = measure.getValue();
        this.error = measure.getError();
        this.name = measure.toString();
        this.time = measure.getTime();
        this.unit = measure.getUnit().toString();

    }

    @XmlAttribute
    public double getValue() {
        return value;
    }

    @XmlAttribute
    public double getError() {
        return error;
    }

    @XmlAttribute
    public long getTime() {
        return time;
    }

    @XmlAttribute
    public String getUnit() {
        return unit;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }
}
