package com.ptoceti.osgi.rest.impl.application.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.osgi.service.metatype.AttributeDefinition;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class AttributeDefinitionWrapper {

    enum Type {
        STRING(1),
        LONG(2),
        INTEGER(3),
        SHORT(4),
        CHARACTER(5),
        BYTE(6),
        DOUBLE(7),
        FLOAT(8),
        /**
         * @deprecated
         */
        BIGINTEGER(9),
        /**
         * @deprecated
         */
        BIGDECIMAL(10),
        BOOLEAN(11),
        PASSWORD(12);

        int code;

        private Type(int code) {
            this.code = code;
        }

        public static String getNameByCode(int code) {
            for (Type e : Type.values()) {
                if (code == e.code) return e.name();
            }
            return null;
        }
    }


    private AttributeDefinition attributeDefinition;

    public AttributeDefinitionWrapper(AttributeDefinition attributeDefinitio) {
        this.attributeDefinition = attributeDefinitio;
    }

    @JsonGetter
    @XmlAttribute
    public String getName() {
        return this.attributeDefinition.getName();
    }

    @JsonGetter
    @XmlAttribute
    public String getId() {
        return this.attributeDefinition.getID();
    }

    @JsonGetter
    @XmlAttribute
    public String getDescription() {
        return this.attributeDefinition.getDescription();
    }

    @JsonGetter
    @XmlAttribute
    public int getCardinality() {
        return this.attributeDefinition.getCardinality();
    }

    @JsonGetter
    @XmlAttribute
    public String getType() {
        int status = this.attributeDefinition.getType();
        return AttributeDefinitionWrapper.Type.getNameByCode(status);
    }

    @JsonGetter
    @XmlElementWrapper(name = "optionValues")
    @XmlElement(name = "optionValues")
    public String[] getOptionValues() {
        return this.attributeDefinition.getOptionValues();
    }

    @JsonGetter
    @XmlElementWrapper(name = "optionLabels")
    @XmlElement(name = "optionLabels")
    public String[] getOptionLabels() {
        return this.attributeDefinition.getOptionLabels();
    }


    @JsonGetter
    @XmlElementWrapper(name = "defaultValues")
    @XmlElement(name = "defaultValue")
    public String[] getDefaultValue() {
        return this.attributeDefinition.getDefaultValue();
    }
}
