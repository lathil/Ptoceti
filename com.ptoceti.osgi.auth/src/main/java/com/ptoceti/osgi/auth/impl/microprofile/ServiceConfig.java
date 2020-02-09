package com.ptoceti.osgi.auth.impl.microprofile;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigSource;

import java.io.Serializable;
import java.util.*;

public class ServiceConfig implements Config, Serializable {

    protected ArrayList<ConfigSource> configSources = new ArrayList<ConfigSource>();

    Comparator<ConfigSource> sourcesComparator = new Comparator<ConfigSource>() {
        @Override
        public int compare(ConfigSource o1, ConfigSource o2) {
            int res = o2.getOrdinal() - o1.getOrdinal();

            return res == 0 ? o2.getName().compareTo(o1.getName()) : res;
        }
    };

    public ServiceConfig() {
    }

    public ServiceConfig(ConfigSource configSource) {
        addConfigSource(configSource);
    }

    @Override
    public <T> T getValue(String s, Class<T> aClass) {

        Iterator<ConfigSource> iter = configSources.iterator();
        configSources.forEach(configSource -> {
                    String value = configSource.getValue(s);
                    if (value != null) {

                    }
                }

        );
        return null;
    }


    @Override
    public <T> Optional<T> getOptionalValue(String s, Class<T> aClass) {
        return Optional.empty();
    }

    @Override
    public Iterable<String> getPropertyNames() {
        Set<String> sources = new HashSet();
        for (ConfigSource source : configSources) {
            sources.addAll(source.getProperties().keySet());
        }
        return sources;
    }

    protected void addConfigSource(ConfigSource source) {
        synchronized (this) {
            configSources.add(source);
            configSources.sort(sourcesComparator);
        }
    }

    @Override
    public Iterable<ConfigSource> getConfigSources() {
        ArrayList<ConfigSource> copy = new ArrayList<ConfigSource>();
        synchronized (this) {
            copy.addAll(configSources);
        }
        return copy;
    }

}
