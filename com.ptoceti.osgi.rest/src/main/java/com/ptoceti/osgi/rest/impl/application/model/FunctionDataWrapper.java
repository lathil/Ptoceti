package com.ptoceti.osgi.rest.impl.application.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.FunctionData;
import org.osgi.service.dal.functions.data.AlarmData;
import org.osgi.service.dal.functions.data.BooleanData;
import org.osgi.service.dal.functions.data.KeypadData;
import org.osgi.service.dal.functions.data.LevelData;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.HashMap;
import java.util.Map;

public class FunctionDataWrapper extends FunctionData {

    enum FunctionDataType {

        FUNCTION_DATA_LEVEL(LevelData.class.getName()),
        FUNCTION_DATA_KEYPAD(KeypadData.class.getName()),
        FUNCTION_DATA_BOOLEAN(BooleanData.class.getName()),
        FUNCTION_DATA_ALARM(AlarmData.class.getName());

        private String type;

        FunctionDataType(String type) {
            this.type = type;
        }

        public static FunctionDataType getByType(String type) {
            for (FunctionDataType e : FunctionDataType.values()) {
                if (type.equals(e.type)) return e;
            }
            return null;
        }
    }


    long timestamp;
    Map metadata;
    Map fields;

    FunctionData innerFunctionData;

    public FunctionDataWrapper() {
        super(new HashMap<String, Object>() {
            {
                put(FunctionData.FIELD_METADATA, new HashMap());
            }
        });
    }

    public FunctionDataWrapper(FunctionData functionData) {
        super(functionData.getTimestamp(), functionData.getMetadata());
        this.metadata = functionData.getMetadata();
        this.timestamp = functionData.getTimestamp();
        this.innerFunctionData = functionData;

        if (functionData instanceof LevelData) {
            fields = new HashMap();
            fields.put(LevelData.FIELD_LEVEL, ((LevelData) functionData).getLevel());
            fields.put(LevelData.FIELD_UNIT, ((LevelData) functionData).getUnit());
        } else if (functionData instanceof KeypadData) {
            fields = new HashMap();
            fields.put(KeypadData.FIELD_KEY_CODE, ((KeypadData) functionData).getKeyCode());
            fields.put(KeypadData.FIELD_KEY_NAME, ((KeypadData) functionData).getKeyName());
            fields.put(KeypadData.FIELD_TYPE, ((KeypadData) functionData).getType());
            fields.put(KeypadData.FIELD_SUB_TYPE, ((KeypadData) functionData).getSubType());
        } else if (functionData instanceof BooleanData) {
            fields = new HashMap();
            fields.put(BooleanData.FIELD_VALUE, ((BooleanData) functionData).getValue());
        } else if (functionData instanceof AlarmData) {
            fields = new HashMap();
            fields.put(AlarmData.FIELD_TYPE, ((AlarmData) functionData).getType());
            fields.put(AlarmData.FIELD_SEVERITY, ((AlarmData) functionData).getSeverity());
        }
    }

    @JsonGetter
    @XmlAttribute
    public long getTimestamp() {
        return timestamp;
    }

    @JsonGetter
    @XmlElement
    public Map getMetadata() {
        return metadata;
    }

    @JsonGetter
    @XmlElement
    public Map getFields() {
        return fields;
    }

    @JsonGetter
    @XmlAttribute
    public FunctionDataType getType() {
        return FunctionDataType.getByType(this.innerFunctionData.getClass().getName());
    }

}
