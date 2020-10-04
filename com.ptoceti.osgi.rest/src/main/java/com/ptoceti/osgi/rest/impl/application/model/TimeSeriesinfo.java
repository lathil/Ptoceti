package com.ptoceti.osgi.rest.impl.application.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "timeseries")
public class TimeSeriesinfo {

    private boolean connected;

    public TimeSeriesinfo() {

    }

    public TimeSeriesinfo(boolean isConnected) {
        this.connected = isConnected;
    }

    @XmlAttribute
    public boolean isConnected() {
        return connected;
    }
}
