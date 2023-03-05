package com.ptoceti.osgi.rest.impl.application.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.osgi.service.dal.FunctionData;
import org.osgi.service.dal.PropertyMetadata;

import javax.xml.bind.annotation.XmlElement;
import java.util.Arrays;
import java.util.Map;

public class PropertyMatadataWrapper implements PropertyMetadata {

    PropertyMetadata wrapped;

    PropertyMatadataWrapper(PropertyMetadata wrapped) {
        this.wrapped = wrapped;
    }


    @JsonGetter
    @XmlElement
    public Map getMetaData() {
        return getMetadata(null);
    }

    @Override
    public Map getMetadata(String s) {
        return wrapped.getMetadata(s);
    }

    @JsonGetter
    @XmlElement
    public FunctionDataWrapper getStep() {
        return getStep(null);
    }

    @Override
    public FunctionDataWrapper getStep(String s) {
        FunctionData functionData = wrapped.getStep(s);
        if (functionData != null) {
            return new FunctionDataWrapper(functionData);
        }
        return null;
    }

    @JsonGetter
    @XmlElement
    public FunctionDataWrapper[] getEnumValues() {
        return getEnumValues(null);
    }

    @Override
    public FunctionDataWrapper[] getEnumValues(String s) {
        FunctionData[] functionDatas = wrapped.getEnumValues(s);
        if (functionDatas != null) {
            return Arrays.stream(functionDatas).map(item -> new FunctionDataWrapper(item)).toArray(size -> new FunctionDataWrapper[size]);
        }
        return null;
    }


    @JsonGetter
    @XmlElement
    public FunctionDataWrapper getMinValue() {
        return getMinValue(null);
    }

    @Override
    public FunctionDataWrapper getMinValue(String s) {
        FunctionData functionData = wrapped.getMinValue(s);
        if (functionData != null) {
            return new FunctionDataWrapper(functionData);
        }
        return null;
    }

    @JsonGetter
    @XmlElement
    public FunctionDataWrapper getMaxValue() {
        return getMaxValue(null);
    }

    @Override
    public FunctionDataWrapper getMaxValue(String s) {
        FunctionData functionData = wrapped.getMaxValue(s);
        if (functionData != null) {
            return new FunctionDataWrapper(functionData);
        }
        return null;
    }
}
