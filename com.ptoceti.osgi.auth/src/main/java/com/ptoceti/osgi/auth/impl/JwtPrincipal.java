package com.ptoceti.osgi.auth.impl;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.osgi.service.useradmin.User;

import java.security.Principal;
import java.text.ParseException;

public class JwtPrincipal implements Principal {

    SignedJWT jwt;
    JWTClaimsSet claims;
    String[] roles;

    public JwtPrincipal(SignedJWT jwt, String[] roles) throws ParseException {
        this.jwt = jwt;
        this.claims = jwt.getJWTClaimsSet();
        this.roles = roles;
    }

    @Override
    public String getName() {
        return claims.getSubject();
    }

    public SignedJWT getJwt() {
        return jwt;
    }

    public String[] getRoles() {
        return roles;
    }


}
