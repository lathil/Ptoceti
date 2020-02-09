package com.ptoceti.osgi.rest.impl;

import com.nimbusds.jose.JOSEException;
import com.ptoceti.osgi.auth.AuthService;
import org.osgi.framework.ServiceException;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.CredentialException;
import javax.security.auth.login.CredentialExpiredException;
import javax.security.auth.login.CredentialNotFoundException;
import java.security.Principal;
import java.text.ParseException;

public class AuthServiceProxy implements AuthService {


    @Override
    public String authenticate(String username, String password, String origin) throws AccountNotFoundException, ServiceException, JOSEException {
        return Activator.getAuthServiceListener().get().authenticate(username, password, origin);
    }

    @Override
    public boolean validateJwtToken(String jwt) throws CredentialExpiredException, CredentialNotFoundException, CredentialException, ParseException, JOSEException {
        return Activator.getAuthServiceListener().get().validateJwtToken(jwt);
    }


    @Override
    public void clearCredential(String jwt) throws CredentialNotFoundException, ParseException {
        Activator.getAuthServiceListener().get().clearCredential(jwt);
    }

    @Override
    public String[] getUserRoles(String jwt) {
        return Activator.getAuthServiceListener().get().getUserRoles(jwt);
    }
}
