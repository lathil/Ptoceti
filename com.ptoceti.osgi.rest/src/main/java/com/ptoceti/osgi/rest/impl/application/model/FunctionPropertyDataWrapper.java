package com.ptoceti.osgi.rest.impl.application.model;

import com.fasterxml.jackson.annotation.JsonGetter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class FunctionPropertyDataWrapper {

    private String propertyName;
    private FunctionDataWrapper propertyData;

    public FunctionPropertyDataWrapper(String propertyNam, FunctionDataWrapper propertyData) {
        this.propertyData = propertyData;
        this.setPropertyName(propertyNam);
    }

    @JsonGetter
    @XmlElement
    public FunctionDataWrapper getPropertyData() {
        return propertyData;
    }

    @JsonGetter
    @XmlElement
    public void setPropertyData(FunctionDataWrapper propertyData) {
        this.propertyData = propertyData;
    }

    @JsonGetter
    @XmlAttribute
    public String getPropertyName() {
        return propertyName;
    }

    @JsonGetter
    @XmlAttribute
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
}
