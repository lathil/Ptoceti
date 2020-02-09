package com.ptoceti.osgi.modbusdevice.impl.functions;

import org.osgi.service.dal.OperationMetadata;
import org.osgi.service.dal.PropertyMetadata;

import java.util.Map;

public class OperationMetaDataImpl implements OperationMetadata {

    Map<String, Object> metadata;

    public OperationMetaDataImpl(Map<String, Object> operationMetadata) {
        metadata = operationMetadata;
    }

    @Override
    public Map getMetadata() {
        return null;
    }

    @Override
    public PropertyMetadata getReturnValueMetadata() {
        return null;
    }

    @Override
    public PropertyMetadata[] getParametersMetadata() {
        return new PropertyMetadata[0];
    }
}
