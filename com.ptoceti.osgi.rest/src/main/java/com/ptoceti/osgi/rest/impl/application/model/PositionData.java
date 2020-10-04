package com.ptoceti.osgi.rest.impl.application.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "position")
public class PositionData {

    private MeasurementData altitude;
    private MeasurementData longitude;
    private MeasurementData latitude;
    private MeasurementData speed;
    private MeasurementData track;

    public PositionData() {

    }

    public PositionData(MeasurementData altitude, MeasurementData longitude, MeasurementData latitude, MeasurementData speed, MeasurementData track) {
        this.altitude = altitude;
        this.longitude = longitude;
        this.latitude = latitude;
        this.speed = speed;
        this.track = track;

    }

    public PositionData(org.osgi.util.position.Position position) {

        this.altitude = new MeasurementData(position.getAltitude());
        this.longitude = new MeasurementData(position.getLongitude());
        this.latitude = new MeasurementData(position.getLatitude());
        this.speed = new MeasurementData(position.getSpeed());
        this.track = new MeasurementData(position.getTrack());
    }


    @XmlElement
    public MeasurementData getAltitude() {
        return altitude;
    }

    @XmlElement
    public MeasurementData getLongitude() {
        return longitude;
    }

    @XmlElement
    public MeasurementData getLatitude() {
        return latitude;
    }

    @XmlElement
    public MeasurementData getSpeed() {
        return speed;
    }

    @XmlElement
    public MeasurementData getTrack() {
        return track;
    }
}
