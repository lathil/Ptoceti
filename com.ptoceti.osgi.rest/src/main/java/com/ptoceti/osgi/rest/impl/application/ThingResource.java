package com.ptoceti.osgi.rest.impl.application;

import com.ptoceti.osgi.auth.Secured;
import com.ptoceti.osgi.rest.impl.Activator;
import com.ptoceti.osgi.rest.impl.ConfigAdminServiceListener;
import com.ptoceti.osgi.rest.impl.DalServiceTracker;
import com.ptoceti.osgi.rest.impl.application.model.ConfigurationWrapper;
import com.ptoceti.osgi.rest.impl.application.model.ThingWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.dal.Device;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Path("things")
@Tags({@Tag(name = "things")})
@Secured
public class ThingResource {

    @Inject
    ConfigAdminServiceListener configurationAdminListener;

    @Inject
    DalServiceTracker dalServiceTracker;

    @RolesAllowed("things")
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<ThingWrapper> getThings() {

        ArrayList<ThingWrapper> result = new ArrayList<ThingWrapper>();

        ServiceReference[] dalRefs = dalServiceTracker.getServiceReferences();
        if (dalRefs != null) {
            for (ServiceReference sRef : dalRefs) {
                result.add(new ThingWrapper(sRef));
            }
        }

        return result;
    }

    @RolesAllowed("things")
    @GET
    @Path("{pid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public ThingWrapper getThing(@PathParam("pid") String pid) {
        ThingWrapper result = null;

        ServiceReference[] dalRefs = dalServiceTracker.getServiceReferences();
        if (dalRefs != null) {
            for (ServiceReference sRef : dalRefs) {
                if (sRef.getProperty(Constants.SERVICE_PID).equals(pid)) {
                    result = new ThingWrapper(sRef);
                }
            }
        }

        if (result == null) {
            throw new NotFoundException();
        }

        return result;
    }

    @RolesAllowed("things")
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
