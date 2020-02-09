package com.ptoceti.osgi.rest.impl.application.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.ptoceti.osgi.rest.impl.Activator;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dal.Device;
import org.osgi.service.dal.Function;
import org.osgi.service.dal.OperationMetadata;
import org.osgi.service.dal.PropertyMetadata;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemWrapper implements Function {

    private ServiceReference sRef;
    private Function wrappedFunction;

    public ItemWrapper(ServiceReference deviceSref) {
        sRef = deviceSref;
        wrappedFunction = (Function) Activator.getBundleContext().getService(deviceSref);
    }

    @JsonGetter
    @XmlAttribute
    public String getUid() {
        return (String) getServiceProperty(Function.SERVICE_UID);
    }

    @JsonGetter
    @XmlAttribute
    public String getType() {
        return (String) getServiceProperty(Function.SERVICE_TYPE);
    }

    @JsonGetter
    @XmlAttribute
    public String getVersion() {
        return (String) getServiceProperty(Function.SERVICE_VERSION);
    }

    @JsonGetter
    @XmlAttribute
    public String[] getOperationNames() {
        return (String[]) getServiceProperty(Function.SERVICE_OPERATION_NAMES);
    }

    @JsonGetter
    @XmlAttribute
    public String[] getPropertyNames() {
        return (String[]) getServiceProperty(Function.SERVICE_PROPERTY_NAMES);
    }

    @JsonGetter
    @XmlAttribute
    public String getDescription() {
        return (String) getServiceProperty(Function.SERVICE_DESCRIPTION);
    }

    @JsonGetter
    @XmlAttribute
    public String getDeviceUid() {
        return (String) getServiceProperty(Function.SERVICE_DEVICE_UID);
    }

    @JsonGetter
    @XmlAttribute
    public Map<String, String> getProperties() {
        Map<String, String> result = new HashMap<>();

        List<String> defaultKeys = Arrays.asList(Function.SERVICE_UID, Function.SERVICE_TYPE, Function.SERVICE_VERSION,
                Function.SERVICE_OPERATION_NAMES,
                Function.SERVICE_PROPERTY_NAMES,
                Function.SERVICE_DESCRIPTION,
                Function.SERVICE_DEVICE_UID);

        for (String key : sRef.getPropertyKeys()) {
            if (!defaultKeys.contains(key)) {
                result.put(key, sRef.getProperty(key).toString());
            }
        }
        ;
        return result;
    }

    @JsonGetter
    @XmlElement
    public Map<String, PropertyMatadataWrapper> getPropertiesMetadata() {
        Map<String, PropertyMatadataWrapper> propertyMetadataMap = new HashMap<>();
        for (String name : getPropertyNames()) {
            PropertyMetadata meta = getPropertyMetadata(name);
            if (meta != null) {
                propertyMetadataMap.put(name, new PropertyMatadataWrapper(meta));
            }
        }
        return propertyMetadataMap;
    }

    @JsonGetter
    @XmlElement
    public Map<String, OperationMetaDataWrapper> getOperationsMetadata() {
        Map<String, OperationMetaDataWrapper> operationsMetadataMap = new HashMap<>();
        for (String name : getOperationNames()) {
            OperationMetadata meta = getOperationMetadata(name);
            if (meta != null) {
                operationsMetadataMap.put(name, new OperationMetaDataWrapper(meta));
            }
        }
        return operationsMetadataMap;
    }

    @Override
    public PropertyMetadata getPropertyMetadata(String s) {
        return wrappedFunction.getPropertyMetadata(s);
    }

    @Override
    public OperationMetadata getOperationMetadata(String s) {
        return wrappedFunction.getOperationMetadata(s);
    }

    @Override
    public Object getServiceProperty(String s) {
        return wrappedFunction.getServiceProperty(s);
    }

    @Override
    public String[] getServicePropertyKeys() {
        return wrappedFunction.getServicePropertyKeys();
    }
}
