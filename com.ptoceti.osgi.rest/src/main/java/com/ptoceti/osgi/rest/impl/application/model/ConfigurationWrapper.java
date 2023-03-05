package com.ptoceti.osgi.rest.impl.application.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.osgi.service.cm.Configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class ConfigurationWrapper {

    Configuration configuration;

    private String pid;
    private String factoryPid;
    private String bundleLocation;
    private List<ConfigurationPropertyEntry> properties;

    public ConfigurationWrapper() {
    }

    public ConfigurationWrapper(Configuration configuration) {
        this.setPid(configuration.getPid());
        this.setFactoryPid(configuration.getFactoryPid());
        this.setBundleLocation(configuration.getBundleLocation());

        List<ConfigurationPropertyEntry> properties = new ArrayList<ConfigurationPropertyEntry>();
        for (Iterator<String> it = configuration.getProperties().keys().asIterator(); it.hasNext(); ) {
            String key = it.next();
            Object value = configuration.getProperties().get(key);
            ConfigurationPropertyEntry entry = new ConfigurationPropertyEntry(key, (value instanceof String) ? (String) value : String.valueOf(value));
            properties.add(entry);
        }

        this.setProperties(properties);
    }

    @JsonGetter
    @XmlAttribute
    public String getPid() {
        return this.pid;
    }

    @JsonGetter
    @XmlElementWrapper(name = "properties")
    public List<ConfigurationPropertyEntry> getProperties() {
        return this.properties;
    }

    @JsonGetter
    @XmlAttribute
    public String getFactoryPid() {
        return this.factoryPid;
    }

    @JsonGetter
    @XmlAttribute
    public String getBundleLocation() {
        return this.bundleLocation;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setFactoryPid(String factoryPid) {
        this.factoryPid = factoryPid;
    }

    public void setBundleLocation(String bundleLocation) {
        this.bundleLocation = bundleLocation;
    }

    public void setProperties(List<ConfigurationPropertyEntry> properties) {
        this.properties = properties;
    }
}
