package com.ptoceti.osgi.rest.impl.application;


import com.ptoceti.osgi.control.Measure;
import com.ptoceti.osgi.rest.impl.Activator;
import com.ptoceti.osgi.rest.impl.WireAdminServiceListener;
import com.ptoceti.osgi.rest.impl.application.model.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.wireadmin.Wire;
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
public class WiresResource {

    @Inject
    WireAdminServiceListener wireAdminServiceListener;


    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<WireInfo> getWires() {

        ArrayList<WireInfo> result = new ArrayList<>();

        try {
            Wire[] wires = wireAdminServiceListener.get().getWires(null);
            for (Wire wire : wires) {


                ArrayList<String> scopesList = new ArrayList<String>();
                String[] scopes = wire.getScope();
                if (scopes != null) {
                    for (String scope : wire.getScope()) {
                        scopesList.add(scope);
                    }
                }

                Object lastValue = wire.getLastValue();
                Sample sampleValue = null;
                if (lastValue instanceof Measurement) {
                    sampleValue = new Sample(new MeasurementData((Measurement) lastValue));
                } else if (lastValue instanceof Position) {
                    sampleValue = new Sample(new PositionData((Position) lastValue));
                } else if (lastValue instanceof Measure) {
                    sampleValue = new Sample(new MeasurementData((Measure) lastValue));
                }

                result.add(new WireInfo(wire.isConnected(), sampleValue, wire.getProperties().get(WireConstants.WIREADMIN_CONSUMER_PID).toString(), wire.getProperties().get(WireConstants.WIREADMIN_PRODUCER_PID).toString(), scopesList));

            }
        } catch (InvalidSyntaxException ex) {
            Activator.getLogger().error("Error getting wires. ex:", ex.toString());
        }
        return result;
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public WireInfo getWire(@PathParam("id") String id) {
        return getWires().get(0);

    }
}
