package com.ptoceti.osgi.rest.impl.microprofile;

import com.ptoceti.osgi.rest.impl.Activator;
import io.smallrye.config.SecuritySupport;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BundleConfigProviderResolver extends ConfigProviderResolver {

    private Map<ClassLoader, Config> configs = new HashMap();

    @Override
    public Config getConfig() {
        return this.configs.get(Activator.class.getClassLoader());
    }

    @Override
    public Config getConfig(ClassLoader classLoader) {

        Config config = this.configs.get(classLoader);
        return config;
    }

    @Override
    public ConfigBuilder getBuilder() {
        return null;
    }

    @Override
    public void registerConfig(Config config, ClassLoader classLoader) {
        synchronized (this) {
            this.configs.put(classLoader != null ? classLoader : Activator.class.getClassLoader(), config);
        }
    }

    @Override
    public void releaseConfig(Config config) {
        synchronized (this) {
            Iterator it = configs.entrySet().iterator();

            Map.Entry entry;
            do {
                if (!it.hasNext()) {
                    return;
                }

                entry = (Map.Entry) it.next();
            } while (entry.getValue() != config);
            it.remove();
        }
    }
}
