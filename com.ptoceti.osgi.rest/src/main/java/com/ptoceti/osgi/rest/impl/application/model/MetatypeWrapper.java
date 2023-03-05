package com.ptoceti.osgi.rest.impl.application.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.osgi.service.metatype.ObjectClassDefinition;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class MetatypeWrapper {

    private ObjectClassDefinition ocd;
    private String pid;
    private String factoryPid;

    public MetatypeWrapper(String pid, String factoryPid, ObjectClassDefinition ocd) {
        this.pid = pid;
        this.factoryPid = factoryPid;
        this.ocd = ocd;
    }

    @JsonGetter
    @XmlAttribute
    public String getPid() {
        return pid;
    }

    @JsonGetter
    @XmlAttribute
    public String getFactoryPid() {
        return factoryPid;
    }

    @JsonGetter
    @XmlElement
    public ObjectClassDefinitionWrapper getObjectClassDefinition() {
        return new ObjectClassDefinitionWrapper(ocd);
    }
}
