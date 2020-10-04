package com.ptoceti.osgi.rest.impl.application;

import com.ptoceti.osgi.mqtt.MqttService;
import com.ptoceti.osgi.rest.impl.MqttServiceListener;
import com.ptoceti.osgi.rest.impl.application.model.MqttServiceInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;

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
public class MqttResource {

    @Inject
    MqttServiceListener mqttServiceListener;

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<MqttServiceInfo> getMqttServices() {

        ArrayList<MqttServiceInfo> result = new ArrayList<MqttServiceInfo>();

        Map<String, MqttService> mqttServices = mqttServiceListener.get();
        mqttServices.forEach((id, service) -> {
            result.add(new MqttServiceInfo(id, service.isConnected()));
        });

        return result;
    }
}
