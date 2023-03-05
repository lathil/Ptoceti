package com.ptoceti.osgi.rest.impl.resteasy.servlet;

import com.ptoceti.osgi.rest.impl.Activator;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.plugins.server.servlet.ServletBootstrap;
import org.jboss.resteasy.plugins.server.servlet.ServletContainerDispatcher;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Map;

public class ExtendedHttpServletDispatcher extends HttpServletDispatcher {

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {

        // set the current thead context class loader as resteasy-spi use it to load the FactoryProviderImpl class
        Thread currentThread = Thread.currentThread();
        ClassLoader previousCL = currentThread.getContextClassLoader();
        try {
            currentThread.setContextClassLoader(this.getClass().getClassLoader());
            super.init(servletConfig);
        } catch (Exception ex) {
            // You should set the original CL back as other technology might use the TCC tricks, too
            currentThread.setContextClassLoader(previousCL);
            Activator.getLogger().error("Error initializing HttpServletDispatcher", ex);
        } finally {
            // You should set the original CL back as other technology might use the TCC tricks, too
            currentThread.setContextClassLoader(previousCL);
        }

    }
}
