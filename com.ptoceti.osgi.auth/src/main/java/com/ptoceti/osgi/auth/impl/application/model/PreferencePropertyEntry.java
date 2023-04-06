package com.ptoceti.osgi.auth.impl.application.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class PreferencePropertyEntry {

    private String key;
    private String value;

    public PreferencePropertyEntry(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @XmlAttribute
    public String getKey() {
        return key;
    }

    @XmlAttribute
    public String getValue() {
        return value;
    }

}
