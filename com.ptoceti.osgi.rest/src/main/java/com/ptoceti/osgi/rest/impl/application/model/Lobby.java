package com.ptoceti.osgi.rest.impl.application.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "lobby")
public class Lobby {

    private String deviceServiceUrl;

    @XmlAttribute
    public String getDeviceServiceUrl() {
        return deviceServiceUrl;
    }

    public void setDeviceServiceUrl(String deviceServiceUrl) {
        this.deviceServiceUrl = deviceServiceUrl;
    }
}
