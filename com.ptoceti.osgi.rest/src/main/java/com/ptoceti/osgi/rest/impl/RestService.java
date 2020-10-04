package com.ptoceti.osgi.rest.impl;

import com.ptoceti.osgi.rest.impl.application.RootApplication;
import com.ptoceti.osgi.rest.impl.guice.ApplicationModule;
import com.ptoceti.osgi.rest.impl.microprofile.ServiceConfig;
import com.ptoceti.osgi.rest.impl.microprofile.ServicePropertiesConfigSource;
import com.ptoceti.osgi.rest.impl.resteasy.servlet.ExtendedGuiceRestEasyServletContextListener;
import com.ptoceti.osgi.rest.impl.resteasy.servlet.ExtendedHttpServletDispatcher;
import io.smallrye.config.SmallRyeConfigProviderResolver;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import org.jboss.resteasy.microprofile.config.ResteasyConfigProvider;

import com.google.inject.Module;

import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import javax.servlet.Servlet;
import javax.servlet.ServletContextListener;
import java.util.*;


public class RestService implements ManagedService {

    public static final String PATH_PREFIX = "com.ptoceti.osgi.rest.api.path.pattern";

    ServiceRegistration restServiceReg = null;
    ServiceRegistration restEasyServletReg = null;
    ServiceRegistration restEasyContextServletListenerReg = null;

    //BundleConfigProviderResolver bundleConfigProviderResolver = null;
    ServiceConfig serviceConfig = null;
    Config config = null;

    String lastPathPrefix = null;

    public RestService() {

        // create a ConfigProviderResolver
        SmallRyeConfigProviderResolver configServiceProvider = new SmallRyeConfigProviderResolver();
        ConfigProviderResolver instance = null;
        try {
            instance = ConfigProviderResolver.instance();
        } catch (Exception ex) {

        }
        if (instance == null) {
            ConfigProviderResolver.setInstance(configServiceProvider);
        }


        String[] clazzes = new String[]{
                ManagedService.class.getName(),
                RestService.class.getName()
        };
        Hashtable<String, Object> properties = new Hashtable<String, Object>();
        properties.put(Constants.SERVICE_PID, this.getClass().getName());
        restServiceReg = Activator.bc.registerService(clazzes, this, properties);

        Activator.getLogger().info("Registered " + this.getClass().getName() + ", Pid = " + (String) properties.get(Constants.SERVICE_PID));


    }

    @Override
    public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
        if (properties != null && !properties.isEmpty()) {
            String pathPrefix = (String) properties.get(PATH_PREFIX);
            if (pathPrefix != null && !pathPrefix.isBlank()) {

                if (lastPathPrefix != null && lastPathPrefix.equals(pathPrefix)) {
                    // no changes, just return.
                    return;
                }

                // clear previous
                stop();

                List<? extends Module> applicationModules = Arrays.asList(new ApplicationModule());
                ExtendedGuiceRestEasyServletContextListener contextListener = new ExtendedGuiceRestEasyServletContextListener(applicationModules);

                Hashtable servletContextlistenerProps = new Hashtable();
                servletContextlistenerProps.put("osgi.http.whiteboard.listener", "true");
                servletContextlistenerProps.put("osgi.http.whiteboard.context.select", "(osgi.http.whiteboard.context.name=default)");

                Activator.getLogger().info("Registered  servlet context listener " + ExtendedGuiceRestEasyServletContextListener.class.getName() + " to Whiteboard service");
                restEasyContextServletListenerReg = Activator.bc.registerService(ServletContextListener.class.getName(), contextListener, servletContextlistenerProps);


                Hashtable props = new Hashtable();
                props.put("osgi.http.whiteboard.servlet.pattern", pathPrefix + "/*");
                props.put("osgi.http.whiteboard.servlet.name", "rest");

                //props.put("osgi.http.whiteboard.context.name","rest");
                //props.put("osgi.http.whiteboard.context.path","rest");

                props.put("servlet.init.javax.ws.rs.Application", RootApplication.class.getName());
                //props.put("servlet.init.resteasy.servlet.context.deployment","true");
                props.put("servlet.init.resteasy.servlet.mapping.prefix", pathPrefix);
                // for resteasy microprofile config use
                props.put("resteasy.servlet.mapping.prefix", pathPrefix);
                props.put("javax.ws.rs.Application", RootApplication.class.getName());

                ServicePropertiesConfigSource serviceConfigSource = new ServicePropertiesConfigSource(props);

                ConfigProviderResolver resolver = ConfigProviderResolver.instance();
                ConfigBuilder builder = resolver.getBuilder();
                Config newConfig = builder.addDefaultSources().withSources(serviceConfigSource).build();

                if (config != null) {
                    resolver.releaseConfig(config);
                }
                config = newConfig;
                resolver.registerConfig(config, ResteasyConfigProvider.class.getClassLoader());

                Activator.getLogger().info("Registered  servlet " + ExtendedHttpServletDispatcher.class.getName() + " to Whiteboard service with path: " + pathPrefix);
                restEasyServletReg = Activator.bc.registerService(Servlet.class.getName(), new ExtendedHttpServletDispatcher(), props);
                lastPathPrefix = pathPrefix;
            }
        } else {
            stop();
        }


    }

    public void stop() {
        if (restEasyServletReg != null) {
            this.restEasyServletReg.unregister();
        }
        if (restEasyContextServletListenerReg != null) {
            this.restEasyContextServletListenerReg.unregister();
        }

        lastPathPrefix = null;
    }
}
