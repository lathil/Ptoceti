package com.ptoceti.osgi.modbusdevice.impl.functions;

import org.osgi.service.dal.Function;
import org.osgi.service.dal.FunctionData;
import org.osgi.service.dal.PropertyMetadata;

import java.util.HashMap;
import java.util.Map;

public class PropertyMetadataImpl implements PropertyMetadata {


    Map<String, Object> defaultMetadata;
    Map<String, Map<String, Object>> metadata;

    FunctionData defaultMinValue;
    Map<String, FunctionData> minValue;
    FunctionData defaultMaxValue;
    Map<String, FunctionData> maxValue;

    FunctionData defaultStep;
    Map<String, FunctionData> step;
    FunctionData[] defaultEnumValue;
    Map<String, FunctionData[]> enumValue;

    public PropertyMetadataImpl(Map<String, Object> metaData, FunctionData minValue, FunctionData maxValue, FunctionData step, FunctionData[] enumValue) {
        defaultMetadata = metaData;
        defaultMinValue = minValue;
        defaultMaxValue = maxValue;
        defaultStep = step;
        defaultEnumValue = enumValue;

    }

    @Override
    public Map getMetadata(String s) {
        if (s != null) {
            if (metadata.containsKey(s)) {
                return metadata.get(s);
            }
            return null;
        }
        return defaultMetadata;
    }

    @Override
    public FunctionData getStep(String s) {
        if (s != null) {
            if (step.containsKey(s)) {
                return step.get(s);
            }
            return null;
        }
        return defaultStep;
    }

    @Override
    public FunctionData[] getEnumValues(String s) {
        if (s != null) {
            if (enumValue.containsKey(s)) {
                return enumValue.get(s);
            }
            return null;
        }
        return defaultEnumValue;
    }

    @Override
    public FunctionData getMinValue(String s) {
        if (s != null) {
            if (minValue.containsKey(s)) {
                return minValue.get(s);
            }
            return null;
        }
        return defaultMinValue;
    }

    @Override
    public FunctionData getMaxValue(String s) {
        if (s != null) {
            if (maxValue.containsKey(s)) {
                return maxValue.get(s);
            }
            return null;
        }
        return defaultMaxValue;
    }
}
