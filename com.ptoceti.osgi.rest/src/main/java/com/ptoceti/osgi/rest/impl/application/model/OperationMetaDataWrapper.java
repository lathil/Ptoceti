package com.ptoceti.osgi.rest.impl.application.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.osgi.service.dal.OperationMetadata;
import org.osgi.service.dal.PropertyMetadata;

import javax.xml.bind.annotation.XmlElement;
import java.util.Arrays;
import java.util.Map;

public class OperationMetaDataWrapper implements OperationMetadata {

    OperationMetadata wrapped;

    public OperationMetaDataWrapper(OperationMetadata wrapped) {
        this.wrapped = wrapped;
    }

    @JsonGetter
    @XmlElement
    @Override
    public Map getMetadata() {
        return wrapped.getMetadata();
    }

    @JsonGetter
    @XmlElement
    @Override
    public PropertyMatadataWrapper getReturnValueMetadata() {
        return new PropertyMatadataWrapper(wrapped.getReturnValueMetadata());
    }

    @JsonGetter
    @XmlElement
    @Override
    public PropertyMatadataWrapper[] getParametersMetadata() {
        return Arrays.stream(wrapped.getParametersMetadata()).map(propertyMetadata -> new PropertyMatadataWrapper(propertyMetadata)).toArray(size -> new PropertyMatadataWrapper[size]);
    }
}
