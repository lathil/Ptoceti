package com.ptoceti.osgi.rest.impl.resteasy.servlet;

import com.google.inject.Module;
import com.ptoceti.osgi.rest.impl.Activator;
import org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.util.List;

public class ExtendedGuiceRestEasyServletContextListener extends GuiceResteasyBootstrapServletContextListener {

    protected List<? extends Module> modules;

    public ExtendedGuiceRestEasyServletContextListener(List<? extends Module> modules) {
        this.modules = modules;
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        // set the current thead context class loader as resteasy-spi use it to load the ResteasyProviderFactoryImpl class
        Thread currentThread = Thread.currentThread();
        ClassLoader previousCL = currentThread.getContextClassLoader();
        try {
            currentThread.setContextClassLoader(this.getClass().getClassLoader());
            super.contextInitialized(event);
        } catch (Exception ex) {
            // You should set the original CL back as other technology might use the TCC tricks, too
            currentThread.setContextClassLoader(previousCL);
            Activator.getLogger().error("Error initializing HttpServletDispatcher", ex);
        } finally {
            // You should set the original CL back as other technology might use the TCC tricks, too
            currentThread.setContextClassLoader(previousCL);
        }
    }

    @Override
    protected List<? extends Module> getModules(final ServletContext context) {
        return this.modules;
    }
}
