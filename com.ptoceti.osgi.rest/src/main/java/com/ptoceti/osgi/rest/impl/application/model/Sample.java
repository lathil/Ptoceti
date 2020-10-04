package com.ptoceti.osgi.rest.impl.application.model;

import com.sun.xml.txw2.annotation.XmlElement;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "sample")
public class Sample {

    private MeasurementData measurement;
    private PositionData position;

    public Sample() {

    }

    public Sample(MeasurementData measurement, PositionData position) {
        this.measurement = measurement;
        this.position = position;
    }

    public Sample(MeasurementData measurement) {
        this.measurement = measurement;
    }

    public Sample(PositionData position) {
        this.position = position;
    }

    @XmlElement
    public MeasurementData getMeasurement() {
        return measurement;
    }

    @XmlElement
    public PositionData getPosition() {
        return position;
    }
}
