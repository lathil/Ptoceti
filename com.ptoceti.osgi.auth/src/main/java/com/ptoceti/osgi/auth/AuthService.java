package com.ptoceti.osgi.auth;

import com.nimbusds.jose.JOSEException;
import org.osgi.framework.ServiceException;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.CredentialException;
import javax.security.auth.login.CredentialExpiredException;
import javax.security.auth.login.CredentialNotFoundException;
import java.security.Principal;
import java.text.ParseException;

public interface AuthService {

    public static final String BEARER_REALM = "ptoceti";

    public String authenticate(String username, String password, String origin) throws AccountNotFoundException, ServiceException, JOSEException;

    public boolean validateJwtToken(String jwt) throws CredentialExpiredException, CredentialNotFoundException, CredentialException, ParseException, JOSEException;

    public void clearCredential(String jwt) throws CredentialNotFoundException, ParseException;

    public String[] getUserRoles(String jwt);
}
