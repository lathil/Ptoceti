package com.ptoceti.osgi.rest.impl.application.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Dictionary;
import java.util.List;

@XmlRootElement(name = "wire")
public class WireInfo {

    private boolean connected;

    private Sample lastValue;

    private String consumerPid;

    private String producerPid;

    private List<String> scopes;

    public WireInfo() {

    }

    public WireInfo(boolean connected, Sample lastValue, String consumerPid, String producerPid, List<String> scopes) {
        this.connected = connected;
        this.lastValue = lastValue;
        this.consumerPid = consumerPid;
        this.producerPid = producerPid;
        this.scopes = scopes;
    }

    @XmlAttribute
    public boolean isConnected() {
        return connected;
    }

    @XmlElement
    public Sample getLastValue() {
        return lastValue;
    }

    @XmlAttribute
    public String getConsumerPid() {
        return consumerPid;
    }

    @XmlAttribute
    public String getProducerPid() {
        return producerPid;
    }

    @XmlElementWrapper(name = "scopes")
    @XmlElement(name = "scope")
    public List<String> getScopes() {
        return scopes;
    }
}
