package com.ptoceti.osgi.rest.impl.application;


import com.ptoceti.osgi.auth.Secured;
import com.ptoceti.osgi.rest.impl.Activator;
import com.ptoceti.osgi.rest.impl.ConfigAdminServiceListener;
import com.ptoceti.osgi.rest.impl.DeviceServiceTracker;
import com.ptoceti.osgi.rest.impl.application.model.ConfigurationWrapper;
import com.ptoceti.osgi.rest.impl.application.model.DeviceWrapper;
import com.ptoceti.osgi.rest.impl.application.model.DriverWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.device.Driver;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Path("devices")
@Tags({@Tag(name = "devices")})
@Secured
public class DevicesResource {

    @Inject
    DeviceServiceTracker deviceServiceTracker;

    @Inject
    ConfigAdminServiceListener configurationAdminListener;

    @RolesAllowed("devices")
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<DeviceWrapper> getDevices() {

        ArrayList<DeviceWrapper> result = new ArrayList<DeviceWrapper>();

        ServiceReference[] deviceRefs = deviceServiceTracker.getServiceReferences();
        if (deviceRefs != null) {
            if (deviceRefs != null) {
                for (ServiceReference sRef : deviceRefs) {
                    result.add(new DeviceWrapper(sRef));
                }
            }
        }

        return result;
    }

    @RolesAllowed("devices")
    @GET
    @Path("{pid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public DeviceWrapper getDevice(@PathParam("pid") String pid) {
        DeviceWrapper result = null;

        ServiceReference[] deviceRefs = deviceServiceTracker.getServiceReferences();
        if (deviceRefs != null) {
            for (ServiceReference sRef : deviceRefs) {
                if (sRef.getProperty(Constants.SERVICE_PID).equals(pid)) {
                    result = new DeviceWrapper(sRef);
                }
            }
        }

        if (result == null) {
            throw new NotFoundException();
        }

        return result;
    }

    @RolesAllowed("devices")
    @GET
    @Path("{pid}/configuration")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public ConfigurationWrapper getConfiguration(@PathParam("pid") String pid) {
        ConfigurationWrapper result = null;
        try {
            ConfigurationAdmin confAdmin = configurationAdminListener.get();
            if (confAdmin != null) {
                Configuration[] confs = confAdmin.listConfigurations("(" + Constants.SERVICE_PID + "=" + pid + ")");
                if (confs != null) {
                    for (Configuration conf : confs) {
                        if (conf.getPid().equals(pid)) {
                            result = new ConfigurationWrapper(conf);
                        }
                    }
                }
            }
        } catch (IOException | InvalidSyntaxException ex) {

        }

        if (result == null) {
            throw new NotFoundException();
        }

        return result;
    }
}
