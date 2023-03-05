package com.ptoceti.osgi.rest.impl.application;

import com.ptoceti.osgi.auth.Secured;
import com.ptoceti.osgi.rest.impl.Activator;
import com.ptoceti.osgi.rest.impl.EventAdminEventHandler;
import com.ptoceti.osgi.rest.impl.application.model.EventWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.osgi.service.event.Event;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;
import java.util.HashMap;

@Path("events")
@Tags({@Tag(name = "events")})
@Secured
public class EventResource implements EventAdminEventHandler.EventListener {

    @Context
    private Sse sse;

    EventAdminEventHandler genericEventHandler;

    private volatile SseBroadcaster sseBroadcaster;
    private OutboundSseEvent.Builder eventBuilder;

    public EventResource(EventAdminEventHandler genericEventHandler) {
        this.genericEventHandler = genericEventHandler;
        this.genericEventHandler.addListener(this);
    }

    @GET
    @Path("broadcastEndPoint")
    @Produces(MediaType.TEXT_PLAIN)
    public String broadcastEndPoint() {
        return "events/registerBroadcast";
    }

    @GET
    @Path("nextEvent")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public EventWrapper nextEvent() {
        HashMap<String, String> properties = new HashMap();
        Event fakeEvent = new Event("com/ptoceti/osgi/rest/FAKE_EVENT", properties);
        return new EventWrapper(fakeEvent);
    }

    @GET
    @Path("registerBroadcast")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void registerBroadcast(@Context SseEventSink eventSink) {

        if (sseBroadcaster == null) {
            sseBroadcaster = sse.newBroadcaster();
        }
        if (eventBuilder == null) {
            eventBuilder = sse.newEventBuilder();
        }

        EventWrapper nullEvent = new EventWrapper();
        OutboundSseEvent sseEvent = sse.newEventBuilder()
                .mediaType(MediaType.APPLICATION_JSON_TYPE)
                .data(EventWrapper.class, nullEvent)
                .build();
        eventSink.send(sseEvent);
        sseBroadcaster.register(eventSink);
        Activator.getLogger().debug("EventResource registerBroadcast: " + eventSink.toString());
    }

    public void postEvent(Event event) {
        if (sseBroadcaster != null) {

            EventWrapper eventWrapper = new EventWrapper(event);
            OutboundSseEvent sseEvent = sse.newEventBuilder()
                    .mediaType(MediaType.APPLICATION_JSON_TYPE)
                    .data(EventWrapper.class, eventWrapper)
                    .build();

            sseBroadcaster.broadcast(sseEvent);
            Activator.getLogger().debug("EventResource postEvent: " + event.getTopic());
        }
    }
}
