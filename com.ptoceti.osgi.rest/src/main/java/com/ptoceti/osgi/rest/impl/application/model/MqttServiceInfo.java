package com.ptoceti.osgi.rest.impl.application.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "mqttservice")
public class MqttServiceInfo {

    private boolean connected;
    private String pid;

    public MqttServiceInfo() {

    }

    public MqttServiceInfo(String pid, boolean connected) {
        this.pid = pid;
        this.connected = connected;
    }

    @XmlAttribute
    public boolean isConnected() {
        return connected;
    }

    @XmlAttribute
    public String getPid() {
        return pid;
    }
}
