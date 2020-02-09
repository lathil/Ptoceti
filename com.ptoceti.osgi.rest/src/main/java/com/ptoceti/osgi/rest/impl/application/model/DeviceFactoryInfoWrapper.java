package com.ptoceti.osgi.rest.impl.application.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.ptoceti.osgi.deviceadmin.DeviceFactoryInfo;

import javax.xml.bind.annotation.XmlAttribute;

public class DeviceFactoryInfoWrapper extends DeviceFactoryInfo {

    private DeviceFactoryInfo devicefactoryInfo;

    public DeviceFactoryInfoWrapper(DeviceFactoryInfo devicefactoryInfo) {
        this.devicefactoryInfo = devicefactoryInfo;
    }

    @JsonGetter
    @XmlAttribute
    public String getDescription() {
        return this.devicefactoryInfo.getDescription();
    }

    @JsonGetter
    @XmlAttribute
    public String getPid() {
        return this.devicefactoryInfo.getPid();
    }

    @JsonGetter
    @XmlAttribute
    public boolean isFactory() {
        return this.devicefactoryInfo.isFactory();
    }

    @JsonGetter
    @XmlAttribute
    public Type getType() {
        return this.devicefactoryInfo.getType();
    }

}
