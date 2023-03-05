package com.ptoceti.osgi.rest.impl.application;


import com.ptoceti.osgi.rest.impl.application.model.Lobby;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.awt.*;


@Path("lobby")
@Tags({@Tag(name = "lobby")})
public class RootResource {
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Lobby getLobby() {
        Lobby lobby = new Lobby();
        lobby.setDeviceServiceUrl("/");
        return lobby;
    }
}
