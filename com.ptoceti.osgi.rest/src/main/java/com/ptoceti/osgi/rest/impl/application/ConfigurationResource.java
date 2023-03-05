package com.ptoceti.osgi.rest.impl.application;

import com.ptoceti.osgi.auth.Secured;
import com.ptoceti.osgi.rest.impl.Activator;
import com.ptoceti.osgi.rest.impl.ConfigAdminServiceListener;
import com.ptoceti.osgi.rest.impl.DeviceServiceTracker;
import com.ptoceti.osgi.rest.impl.application.model.ConfigurationWrapper;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

@Path("configuration")
@Tags({@Tag(name = "configuration")})
@Secured
public class ConfigurationResource {

    @Inject
    ConfigAdminServiceListener configurationAdminListener;

    @Inject
    DeviceServiceTracker deviceServiceTracker;

    @RolesAllowed("admin")
    @GET
    @Path("/conf/{pid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public ConfigurationWrapper getConfiguration(@PathParam("pid") String pid) {

        return getPidConfiguration(pid);
    }

    @RolesAllowed("admin")
    @PUT
    @Path("/conf/{pid}")
    public void updateConfiguration(@PathParam("pid") String pid, ConfigurationWrapper configurationWrapper) {

        try {
            ConfigurationAdmin confAdmin = configurationAdminListener.get();
            if (confAdmin != null) {

                Configuration conf = confAdmin.getConfiguration(pid);
                Hashtable<String, String> props = new Hashtable();
                configurationWrapper.getProperties().forEach((entry) -> props.put(entry.getKey(), entry.getValue()));

                conf.update(props);
            }
        } catch (IOException ex) {

        }
    }

    @RolesAllowed("admin")
    @DELETE
    @Path("/conf/{pid}")
    public void deleteConfiguration(@PathParam("pid") String pid) {

        try {
            ConfigurationAdmin confAdmin = configurationAdminListener.get();
            if (confAdmin != null) {

                Configuration conf = confAdmin.getConfiguration(pid);
                conf.delete();
            }
        } catch (IOException ex) {

        }
    }

    @RolesAllowed("admin")
    @POST
    @Path("/conf")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void createConfiguration(ConfigurationWrapper configurationWrapper) {

        try {
            ConfigurationAdmin confAdmin = configurationAdminListener.get();
            if (confAdmin != null) {

                Configuration conf = null;
                if (configurationWrapper.getFactoryPid() != null && configurationWrapper.getFactoryPid().length() > 0) {
                    conf = confAdmin.createFactoryConfiguration(configurationWrapper.getFactoryPid(), null);
                } else if (configurationWrapper.getPid() != null & configurationWrapper.getPid().length() > 0) {
                    conf = confAdmin.getConfiguration(configurationWrapper.getPid(), null);
                }

                Hashtable<String, String> props = new Hashtable();
                configurationWrapper.getProperties().forEach((entry) -> props.put(entry.getKey(), entry.getValue()));

                conf.update(props);
            }
        } catch (IOException ex) {

        }
    }

    @RolesAllowed("admin")
    @GET
    @Path("/devicedrivers/confs/")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public ConfigurationWrapper[] getDevicesDriversConfigurations() {

        List<ConfigurationWrapper> configurations = new ArrayList<>();

        try {
            ServiceReference[] deviceRefs = deviceServiceTracker.getServiceReferences();
            if (deviceRefs != null) {
                for (ServiceReference sRef : deviceRefs) {
                    String pid = (String) sRef.getProperty(Constants.SERVICE_PID);
                    if (pid != null) {
                        ConfigurationWrapper conf = getPidConfiguration(pid);
                        if (conf != null) {
                            configurations.add(conf);
                        }
                    }
                }
            }

            ServiceReference[] driversRef = Activator.getBundleContext().getServiceReferences(Driver.class.getName(), null);
            if (driversRef != null) {
                for (ServiceReference sRef : driversRef) {
                    String pid = (String) sRef.getProperty(Constants.SERVICE_PID);
                    if (pid != null) {
                        ConfigurationWrapper conf = getPidConfiguration(pid);
                        if (conf != null) {
                            configurations.add(conf);
                        }
                    }
                }
            }

        } catch (InvalidSyntaxException ex) {

        }

        return configurations.toArray(new ConfigurationWrapper[configurations.size()]);
    }

    private ConfigurationWrapper getPidConfiguration(String pid) {
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
        return result;
    }

}


