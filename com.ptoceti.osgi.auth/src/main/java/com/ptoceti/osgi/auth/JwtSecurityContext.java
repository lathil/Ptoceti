package com.ptoceti.osgi.auth;

import com.ptoceti.osgi.auth.impl.JwtPrincipal;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.Arrays;

public class JwtSecurityContext implements SecurityContext {

    SecurityContext delegate;
    JwtPrincipal principal;

    public JwtSecurityContext(SecurityContext delegate, JwtPrincipal principal) {
        this.delegate = delegate;
        this.principal = principal;
    }

    @Override
    public Principal getUserPrincipal() {
        return principal;
    }

    @Override
    public boolean isUserInRole(String role) {
        return Arrays.stream(principal.getRoles()).anyMatch(next -> next.equals(role));
    }

    @Override
    public boolean isSecure() {
        return delegate.isSecure();
    }

    @Override
    public String getAuthenticationScheme() {
        return null;
    }
}
