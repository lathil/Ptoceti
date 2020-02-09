package com.ptoceti.osgi.rest.impl.application;


import com.ptoceti.osgi.auth.Secured;
import com.ptoceti.osgi.rest.impl.Activator;
import com.ptoceti.osgi.rest.impl.ConfigAdminServiceListener;
import com.ptoceti.osgi.rest.impl.DeviceAccessServiceListener;
import com.ptoceti.osgi.rest.impl.DriverServiceTracker;
import com.ptoceti.osgi.rest.impl.application.model.ConfigurationWrapper;
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

@Path("drivers")
@Tags({@Tag(name = "driver")})
@Secured
public class DriversResource {

    @Inject
    ConfigAdminServiceListener configurationAdminListener;

    @Inject
    DeviceAccessServiceListener deviceAccessServiceListener;

    @Inject
    DriverServiceTracker driverServiceTracker;

    @RolesAllowed("devices")
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<DriverWrapper> getDrivers() {
        ArrayList<DriverWrapper> result = new ArrayList<DriverWrapper>();
        ServiceReference[] driverRefs = driverServiceTracker.getServiceReferences();
        if (driverRefs != null) {
            for (ServiceReference sRef : driverRefs) {
                String driverId = null;
                if (deviceAccessServiceListener.get() != null) {
                    driverId = deviceAccessServiceListener.get().isDriverMatched(sRef.getProperty(org.osgi.service.device.Constants.DRIVER_ID).toString());
                }
                result.add(new DriverWrapper(sRef, driverId));
            }
        }
        return result;
    }

    @RolesAllowed("devices")
    @GET
    @Path("{pid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public DriverWrapper getDriver(@PathParam("pid") String pid) {
        DriverWrapper result = null;
        ServiceReference[] driverRefs = driverServiceTracker.getServiceReferences();
        if (driverRefs != null) {
            for (ServiceReference sRef : driverRefs) {
                if (sRef.getProperty(Constants.SERVICE_PID).equals(pid)) {
                    String driverId = null;
                    if (deviceAccessServiceListener.get() != null) {
                        driverId = deviceAccessServiceListener.get().isDriverMatched(sRef.getProperty(org.osgi.service.device.Constants.DRIVER_ID).toString());
                    }
                    result = new DriverWrapper(sRef, driverId);
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
