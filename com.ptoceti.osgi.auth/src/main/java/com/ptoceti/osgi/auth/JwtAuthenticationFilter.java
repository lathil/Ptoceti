package com.ptoceti.osgi.auth;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import com.ptoceti.osgi.auth.impl.JwtPrincipal;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.security.auth.login.CredentialException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.text.ParseException;

import static com.ptoceti.osgi.auth.AuthService.BEARER_REALM;

@Provider
@Secured
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthenticationFilter implements ContainerRequestFilter {

    private static final String BEARER_AUTHORIZATION_TOKEN_NAME = "Bearer";

    private AuthService authService;

    @Inject
    public JwtAuthenticationFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String authorisationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authorisationHeader != null) {
            if (authorisationHeader.startsWith(BEARER_AUTHORIZATION_TOKEN_NAME)) {
                try {
                    String token = authorisationHeader.substring(BEARER_AUTHORIZATION_TOKEN_NAME.length() + 1);
                    if (authService.validateJwtToken(token)) {
                        final SecurityContext securityContext = requestContext.getSecurityContext();
                        String[] roles = authService.getUserRoles(token);
                        JwtSecurityContext jwtSecurityContext = new JwtSecurityContext(securityContext, new JwtPrincipal(SignedJWT.parse(token), roles));
                        requestContext.setSecurityContext(jwtSecurityContext);
                    } else {
                        abortWithUnauthorized(requestContext);
                    }
                } catch (CredentialException e) {
                    abortWithUnauthorized(requestContext);
                } catch (JOSEException | ParseException ex) {
                    requestContext.abortWith(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
                }
            } else {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, "Bearer realm=\"" + BEARER_REALM + "\"").build());
            }
        }

    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {

        // Abort the filter chain with a 401 status code
        // The "WWW-Authenticate" is sent along with the response
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, BEARER_AUTHORIZATION_TOKEN_NAME + " realm=\"" + BEARER_REALM + "\"").build());
    }
}
