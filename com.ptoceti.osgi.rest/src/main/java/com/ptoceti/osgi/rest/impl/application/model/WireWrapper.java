package com.ptoceti.osgi.rest.impl.application.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.osgi.service.wireadmin.Wire;
import org.osgi.service.wireadmin.WireConstants;

import javax.xml.bind.annotation.*;
import java.util.Dictionary;

@XmlRootElement(name = "Wire")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class WireWrapper implements Wire {

    private Wire wire;

    public WireWrapper() {
    }

    public WireWrapper(Wire wire) {
        this.wire = wire;
    }

    @JsonGetter
    @XmlAttribute
    @Override
    public boolean isValid() {
        return this.wire.isValid();
    }

    @JsonGetter
    @XmlAttribute
    @Override
    public boolean isConnected() {
        return this.wire.isConnected();
    }

    @JsonGetter
    @XmlElement
    @Override
    public Class[] getFlavors() {
        return this.wire.getFlavors();
    }

    @Override
    public void update(Object o) {
    }

    @JsonGetter
    @XmlElement
    @Override
    public Object getLastValue() {
        return this.wire.getLastValue();
    }

    @JsonIgnore
    @Override
    public Dictionary getProperties() {
        return this.wire.getProperties();
    }

    @JsonGetter
    @XmlElement
    @Override
    public String[] getScope() {
        return this.wire.getScope();
    }

    @JsonGetter
    @XmlAttribute
    public String getPid() {
        return (String) this.wire.getProperties().get(WireConstants.WIREADMIN_PID);
    }


    @JsonGetter
    @XmlAttribute
    public String getConsumerPid() {
        return (String) this.wire.getProperties().get(WireConstants.WIREADMIN_CONSUMER_PID);
    }

    @JsonGetter
    @XmlAttribute
    public String getProducerPid() {
        return (String) this.wire.getProperties().get(WireConstants.WIREADMIN_PRODUCER_PID);
    }

    @JsonIgnore
    @Override
    public boolean hasScope(String s) {
        return false;
    }

    @Override
    public Object poll() {
        return null;
    }

}
