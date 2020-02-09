package com.ptoceti.osgi.auth;

import javax.annotation.Priority;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;

@Priority(Priorities.AUTHORIZATION)
public class JwtRoleBaseSecurityFilter implements ContainerRequestFilter {

    protected String[] rolesAllowed;
    protected boolean denyAll;
    protected boolean permitAll;

    JwtRoleBaseSecurityFilter(String[] rolesAllowed, boolean denyAll, boolean permitAll) {
        this.rolesAllowed = rolesAllowed;
        this.denyAll = denyAll;
        this.permitAll = permitAll;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (denyAll) {
            throw new ForbiddenException(Response.status(403).entity("Access forbidden: role not allowed").type("text/html;charset=UTF-8").build());
        }
        if (permitAll) return;
        if (rolesAllowed != null) {
            SecurityContext context = requestContext.getSecurityContext();
            if (context != null) {
                for (String role : rolesAllowed) {
                    if (context.isUserInRole(role)) return;
                }
                throw new ForbiddenException(Response.status(403).entity("Access forbidden: role not allowed").type("text/html;charset=UTF-8").build());
            }
        }
        return;
    }
}
