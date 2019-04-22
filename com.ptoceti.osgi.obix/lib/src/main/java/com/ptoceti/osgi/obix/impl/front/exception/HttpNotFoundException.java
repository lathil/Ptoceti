package com.ptoceti.osgi.obix.impl.front.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class HttpNotFoundException extends WebApplicationException {

    public HttpNotFoundException() {
        super(Response.status(Response.Status.NOT_FOUND).build());
    }

    public HttpNotFoundException(String message) {
        super(Response.status(Response.Status.NOT_FOUND).
                entity(message).type("text/plain").build());
    }
}
