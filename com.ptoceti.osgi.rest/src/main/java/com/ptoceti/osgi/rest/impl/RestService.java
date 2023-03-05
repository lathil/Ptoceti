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
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletContextListener;
import java.util.*;


public class RestService implements ManagedService {

    public static final String PATH_PREFIX = "com.ptoceti.osgi.rest.api.path.pattern";

    ServiceRegistration servletContextHelperReg = null;
    ServiceRegistration restServiceReg = null;
    ServiceRegistration restEasyServletReg = null;
    ServiceRegistration restEasyContextServletListenerReg = null;

    ServiceRegistration swaggerServiceReg = null;
    ServiceRegistration swaggerFilterReg = null;

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

                // Register specific servlet context helper and servlet context
                /**
                 RestServletContext servletContext = new RestServletContext(Activator.bc.getBundle());
                 Hashtable servletContextProps = new Hashtable();
                 servletContextProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "rest");
                 servletContextProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, pathPrefix);
                 servletContextHelperReg = Activator.bc.registerService(ServletContextHelper.class, servletContext, servletContextProps);
                 **/

                List<? extends Module> applicationModules = Arrays.asList(new ApplicationModule());
                ExtendedGuiceRestEasyServletContextListener contextListener = new ExtendedGuiceRestEasyServletContextListener(applicationModules);

                Hashtable servletContextlistenerProps = new Hashtable();
                servletContextlistenerProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
                servletContextlistenerProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=default)");
                Activator.getLogger().info("Registered  servlet context listener " + ExtendedGuiceRestEasyServletContextListener.class.getName() + " to Whiteboard service");
                restEasyContextServletListenerReg = Activator.bc.registerService(ServletContextListener.class.getName(), contextListener, servletContextlistenerProps);


                Hashtable restEasyProps = new Hashtable();
                // for resteasy microprofile config use
                restEasyProps.put("resteasy.servlet.mapping.prefix", pathPrefix);
                restEasyProps.put("javax.ws.rs.Application", RootApplication.class.getName());

                /**
                 ServicePropertiesConfigSource serviceConfigSource = new ServicePropertiesConfigSource(restEasyProps);
                 ConfigProviderResolver resolver = ConfigProviderResolver.instance();
                 ConfigBuilder builder = resolver.getBuilder();
                 Config newConfig = builder.addDefaultSources().withSources(serviceConfigSource).build();

                 if (config != null) {
                 resolver.releaseConfig(config);
                 }
                 config = newConfig;
                 resolver.registerConfig(config, ResteasyConfigProvider.class.getClassLoader());
                 **/


                Hashtable props = new Hashtable();
                props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, pathPrefix + "/*");
                props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "rest");
                props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=default)");
                // for whiteboard init parameter. 'servlet.init' prefix is removed
                props.put("servlet.init.javax.ws.rs.Application", RootApplication.class.getName());
                props.put("servlet.init.resteasy.servlet.mapping.prefix", pathPrefix);


                Activator.getLogger().info("Registered  servlet " + ExtendedHttpServletDispatcher.class.getName() + " to Whiteboard service with path: " + pathPrefix);
                restEasyServletReg = Activator.bc.registerService(Servlet.class.getName(), new ExtendedHttpServletDispatcher(), props);
                lastPathPrefix = pathPrefix;

                Hashtable swaggerProps = new Hashtable();
                swaggerProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, pathPrefix + "/swagger-ui/*");
                swaggerProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/resources/swagger-ui");
                swaggerProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=default)");
                // register swagger-ui resources
                swaggerServiceReg = Activator.bc.registerService(SwaggerService.class.getName(), new SwaggerService(), swaggerProps);

                Hashtable swaggerFilterProps = new Hashtable();
                swaggerFilterProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, SwaggerFilter.class.getName());
                swaggerFilterProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, pathPrefix + "/swagger-ui/*");
                swaggerFilterProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=default)");
                // register swagger-ui filter
                swaggerFilterReg = Activator.bc.registerService(Filter.class.getName(), new SwaggerFilter(), swaggerFilterProps);
                lastPathPrefix = pathPrefix;
            }
        } else {
            stop();
        }


    }

    public void stop() {
        if (this.swaggerServiceReg != null) {
            this.swaggerServiceReg.unregister();
            this.swaggerServiceReg = null;
        }
        if (this.swaggerFilterReg != null) {
            this.swaggerFilterReg.unregister();
            this.swaggerFilterReg = null;
        }
        if (this.restEasyServletReg != null) {
            this.restEasyServletReg.unregister();
            this.restEasyServletReg = null;
        }
        if (this.restEasyContextServletListenerReg != null) {
            this.restEasyContextServletListenerReg.unregister();
            this.restEasyContextServletListenerReg = null;
        }

        if (this.servletContextHelperReg != null) {
            this.servletContextHelperReg.unregister();
            this.servletContextHelperReg = null;
        }

        lastPathPrefix = null;
    }
}
