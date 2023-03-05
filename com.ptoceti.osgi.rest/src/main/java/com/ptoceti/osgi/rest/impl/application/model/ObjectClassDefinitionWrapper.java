package com.ptoceti.osgi.rest.impl.application.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class ObjectClassDefinitionWrapper {

    private ObjectClassDefinition objectClassDefinition;

    public ObjectClassDefinitionWrapper(ObjectClassDefinition objectClassDefinition) {
        this.objectClassDefinition = objectClassDefinition;
    }

    @JsonGetter
    @XmlAttribute
    public String getName() {
        return objectClassDefinition.getName();
    }

    @JsonGetter
    @XmlAttribute
    public String getId() {
        return objectClassDefinition.getID();
    }

    @JsonGetter
    @XmlAttribute
    public String getDescription() {
        return objectClassDefinition.getDescription();
    }


    @XmlElementWrapper(name = "attributesDefinitions")
    public List<AttributeDefinitionWrapper> getAttributeDefinitions() {

        ArrayList<AttributeDefinitionWrapper> attributeDefinitionWrappers = new ArrayList<>();
        AttributeDefinition[] attributeDefinitions = objectClassDefinition.getAttributeDefinitions(ObjectClassDefinition.ALL);
        for (AttributeDefinition attributeDefinition : attributeDefinitions) {
            attributeDefinitionWrappers.add(new AttributeDefinitionWrapper(attributeDefinition));
        }

        return attributeDefinitionWrappers;
    }
}
