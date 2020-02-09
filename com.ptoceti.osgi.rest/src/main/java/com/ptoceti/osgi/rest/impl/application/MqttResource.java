package com.ptoceti.osgi.rest.impl.application;

import com.ptoceti.osgi.auth.Secured;
import com.ptoceti.osgi.mqtt.MqttService;
import com.ptoceti.osgi.rest.impl.MqttServiceListener;
import com.ptoceti.osgi.rest.impl.application.model.MqttServiceWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.osgi.framework.ServiceReference;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("mqtt")
@Tags({@Tag(name = "mqtt")})
@Secured
public class MqttResource {

    @Inject
    MqttServiceListener mqttServiceListener;

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<MqttServiceWrapper> getMqttServices() {

        ArrayList<MqttServiceWrapper> result = new ArrayList<MqttServiceWrapper>();

        Map<String, ServiceReference> mqttServices = mqttServiceListener.get();
        mqttServices.forEach((id, serviceSref) -> {
            result.add(new MqttServiceWrapper(serviceSref));
        });

        return result;
    }
}
