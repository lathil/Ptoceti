package com.ptoceti.osgi.rest.impl.application.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class ConfigurationPropertyEntry {

    private String key;

    private String value;

    public ConfigurationPropertyEntry() {
    }

    public ConfigurationPropertyEntry(String key, String value) {
        this.setKey(key);
        this.setValue(value);
    }

    @XmlAttribute
    public String getValue() {
        return value;
    }

    @XmlAttribute
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
