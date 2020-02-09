package com.ptoceti.osgi.ihm.impl;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

import javax.servlet.Filter;
import java.util.Dictionary;
import java.util.Hashtable;

public class IhmService implements ManagedService {

    public static final String PATH_PREFIX = "com.ptoceti.osgi.ihm.path.pattern";

    ServiceRegistration ihmServiceReg = null;
    ServiceRegistration resourceServiceReg = null;
    ServiceRegistration ihmFilterReg = null;

    String lastPathPrefix = null;

    public IhmService() {
        String[] clazzes = new String[]{
                ManagedService.class.getName(),
                IhmService.class.getName()
        };
        Hashtable<String, Object> properties = new Hashtable<String, Object>();
        properties.put(Constants.SERVICE_PID, this.getClass().getName());
        ihmServiceReg = Activator.bc.registerService(clazzes, this, properties);

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

                if (pathPrefix.startsWith("/")) {
                    pathPrefix = pathPrefix.substring(1);
                }
                if (pathPrefix.lastIndexOf("/") > 0) {
                    pathPrefix = pathPrefix.substring(0, pathPrefix.indexOf("/"));
                }
                Hashtable props = new Hashtable();
                props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, "/" + pathPrefix + "/*");
                props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/resources/ihm");

                resourceServiceReg = Activator.bc.registerService(ResourceService.class.getName(), new ResourceService(), props);

                Hashtable ihmFilterProps = new Hashtable();
                ihmFilterProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, IhmFilter.class.getName());
                ihmFilterProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, "/" + pathPrefix + "/*");
                ihmFilterProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=default)");
                // register ihm filter
                ihmFilterReg = Activator.bc.registerService(Filter.class.getName(), new IhmFilter(), ihmFilterProps);
                lastPathPrefix = pathPrefix;
            }

        } else {
            stop();
        }

    }

    public void stop() {
        if (ihmFilterReg != null) {
            ihmFilterReg.unregister();
            ihmFilterReg = null;
        }
        if (resourceServiceReg != null) {
            resourceServiceReg.unregister();
            resourceServiceReg = null;
        }
        lastPathPrefix = null;
    }
}
