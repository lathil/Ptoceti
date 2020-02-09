package com.ptoceti.osgi.rest.impl.application;

import com.ptoceti.osgi.auth.Secured;
import com.ptoceti.osgi.deviceadmin.DeviceAdmin;
import com.ptoceti.osgi.deviceadmin.DeviceFactoryInfo;
import com.ptoceti.osgi.rest.impl.DeviceAdminServiceListener;
import com.ptoceti.osgi.rest.impl.application.model.DeviceFactoryInfoWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("factories")
@Tags({@Tag(name = "factories")})
@Secured
public class FactoriesResource {

    @Inject
    DeviceAdminServiceListener deviceAdminServiceListener;

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<DeviceFactoryInfoWrapper> getDeviceFactoryInfos() {

        ArrayList<DeviceFactoryInfoWrapper> result = new ArrayList<DeviceFactoryInfoWrapper>();

        List<DeviceFactoryInfo> factoryInfos = deviceAdminServiceListener.get().getFactories();
        if (factoryInfos != null) {
            for (DeviceFactoryInfo factoryInfo : factoryInfos) {
                result.add(new DeviceFactoryInfoWrapper(factoryInfo));
            }
        }

        return result;
    }
}
