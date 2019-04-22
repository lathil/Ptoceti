package com.ptoceti.osgi.obix.impl.front.filters;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@PreMatching
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    public static final String ORIGIN = "Origin";

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {

        if (isPreflight(containerRequestContext)) {
            containerRequestContext.abortWith(Response.ok().build());
            return;
        }
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {

        if (isPreflight(containerRequestContext)) {

            // tell we allow GET,POST,DELETE and OPTIONS from everywhere
            containerResponseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
            containerResponseContext.getHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            containerResponseContext.getHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            // tell to add cookies in cors request . not needed now
            //responseHeaders.add("Access-Control-Allow-Credentials", "true");
            // note: chrome ignore of max age is > 10 minutes (600 seconds)
            containerResponseContext.getHeaders().add("Access-Control-Max-Age", "600");
        } else if (containerRequestContext.getHeaderString(ORIGIN) != null) {
            String origin = (String) containerResponseContext.getHeaders().getFirst(ORIGIN);
            if (origin != null && !origin.isEmpty()) {
                containerResponseContext.getHeaders().add("Access-Control-Allow-Origin", origin);
                containerResponseContext.getHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            }
        }
    }

    private boolean isPreflight(ContainerRequestContext containerRequestContext) {

        boolean result = false;

        if (containerRequestContext.getMethod().equalsIgnoreCase(HttpMethod.OPTIONS) && (containerRequestContext.getHeaderString(ORIGIN) != null)) {
            result = true;
        }

        return result;
    }
}
