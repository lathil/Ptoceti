package com.ptoceti.osgi.auth.impl.microprofile;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.HashMap;
import java.util.Map;

public class ServicePropertiesConfigSource implements ConfigSource {

    public static final String NAME = "ServiceConfigSource";
    private static Map<String, String> properties = new HashMap<>();

    public ServicePropertiesConfigSource(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public String getValue(String name) {
        return properties.get(name);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
