package com.ptoceti.osgi.auth.impl.application;

import com.nimbusds.jose.JOSEException;
import com.ptoceti.osgi.auth.AuthService;
import com.ptoceti.osgi.auth.JwtSecurityContext;
import com.ptoceti.osgi.auth.Secured;
import com.ptoceti.osgi.auth.impl.JwtPrincipal;
import com.ptoceti.osgi.auth.impl.application.model.Credential;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.osgi.framework.ServiceException;

import javax.inject.Inject;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.CredentialNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.text.ParseException;
import java.util.Base64;


@Path("login")
@Tags({@Tag(name = "login")})
public class LoginResource {

    @Inject
    AuthService authService;

    /**
     * Authentification according to RFC-2617
     *
     * @param authorisationHeader Authentification Basic Authorize header
     * @param username            user name and credential MAY come in request body
     * @param credentials         user name and credential MAY come in request body
     * @return
     */
    @POST
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Credential login(@Context HttpHeaders headers, @HeaderParam(HttpHeaders.AUTHORIZATION) String authorisationHeader, @FormParam("username") String username, @FormParam("credential") String credentials) {

        String name;
        try {
            boolean authenticated = false;
            String token = null;
            String host = headers.getRequestHeaders().getFirst("X-Forwarded-Host");
            if (host == null) {
                host = headers.getRequestHeaders().getFirst(HttpHeaders.HOST);
            }
            if (host != null && host.contains(":")) {
                host = host.split(":")[0];
            }

            ;
            if (authorisationHeader != null && authorisationHeader.startsWith("Basic")) {
                String[] tokens = (new String(Base64.getDecoder().decode(authorisationHeader.split(" ")[1]), "UTF-8")).split(":");
                token = authService.authenticate(tokens[0], tokens[1], host);
            } else if (username != null && credentials != null) {
                token = authService.authenticate(username, credentials, host);
            } else {
                Response response = Response.status(401).header(HttpHeaders.WWW_AUTHENTICATE, "Basic").build();
                throw new WebApplicationException(response);
            }

            if (token != null) {
                // Return the token on the response
                Credential credential = new Credential();
                credential.setToken(token);
                return credential;
            }
            // not authenticated ...
            Response response = Response.status(Response.Status.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, "Basic").build();
            throw new WebApplicationException(response);
        } catch (AccountNotFoundException ex) {
            Response response = Response.status(Response.Status.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, "Basic").build();
            throw new WebApplicationException(response);
        } catch (JOSEException | UnsupportedEncodingException | ServiceException ex) {
            Response response = Response.serverError().build();
            throw new WebApplicationException(response);
        }
    }

    @POST
    @Secured
    @Path("logout")
    public Response logout(@Context SecurityContext securityContext) {

        try {
            if (securityContext.getUserPrincipal() != null) {
                authService.clearCredential(((JwtPrincipal) securityContext.getUserPrincipal()).getJwt().serialize());
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, "Basic").build();
            }
        } catch (CredentialNotFoundException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, "Basic").build();
        } catch (ParseException ex) {
            return Response.serverError().build();
        }
        return Response.status(Response.Status.OK).build();

    }

    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Principal principal(@Context SecurityContext securityContext) {
        return securityContext.getUserPrincipal();
    }
}
