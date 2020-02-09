package com.ptoceti.osgi.rest.impl.application;


import com.ptoceti.osgi.auth.Secured;
import com.ptoceti.osgi.control.Measure;
import com.ptoceti.osgi.rest.impl.Activator;
import com.ptoceti.osgi.rest.impl.WireAdminServiceListener;
import com.ptoceti.osgi.rest.impl.application.model.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.wireadmin.Wire;
import org.osgi.service.wireadmin.WireAdmin;
import org.osgi.service.wireadmin.WireConstants;
import org.osgi.util.measurement.Measurement;
import org.osgi.util.position.Position;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("wires")
@Tags({@Tag(name = "wires")})
@Secured
public class WiresResource {

    @Inject
    WireAdminServiceListener wireAdminServiceListener;


    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<WireWrapper> getWires() {

        ArrayList<WireWrapper> result = new ArrayList<>();

        try {
            WireAdmin wireAdmin = wireAdminServiceListener.get();
            if (wireAdmin != null) {
                Wire[] wires = wireAdmin.getWires(null);
                if (wires != null) {
                    for (Wire wire : wires) {
                        result.add(new WireWrapper(wire));
                    }
                }
            }
        } catch (InvalidSyntaxException ex) {
            Activator.getLogger().error("Error getting wires. ex:", ex.toString());
        }
        return result;
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public WireWrapper getWire(@PathParam("id") String id) {
        return getWires().get(0);

    }
}
