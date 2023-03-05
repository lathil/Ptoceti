package com.ptoceti.osgi.auth.impl;

import com.google.inject.Module;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.ptoceti.osgi.auth.AuthService;
import com.ptoceti.osgi.auth.impl.application.RootApplication;
import com.ptoceti.osgi.auth.impl.guice.ApplicationModule;
import com.ptoceti.osgi.auth.impl.microprofile.ServiceConfig;
import com.ptoceti.osgi.auth.impl.microprofile.ServicePropertiesConfigSource;
import com.ptoceti.osgi.auth.impl.resteasy.servlet.ExtendedGuiceRestEasyServletContextListener;
import com.ptoceti.osgi.auth.impl.resteasy.servlet.ExtendedHttpServletDispatcher;
import io.smallrye.config.SmallRyeConfigProviderResolver;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.jboss.resteasy.microprofile.config.ResteasyConfigProvider;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;

import javax.security.auth.login.*;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletContextListener;
import java.security.Principal;
import java.security.SecureRandom;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;

public class AuthServiceImpl implements AuthService, ManagedService {

    public static final String PATH_PREFIX = "com.ptoceti.osgi.auth.api.path.pattern";

    ServiceRegistration servletContextHelperReg = null;
    ServiceRegistration authServiceReg = null;
    ServiceRegistration restEasyServletReg = null;
    ServiceRegistration restEasyContextServletListenerReg = null;

    ServiceRegistration swaggerServiceReg = null;
    ServiceRegistration swaggerFilterReg = null;

    ServiceConfig serviceConfig = null;
    Config config = null;
    String lastPathPrefix = null;

    // shared secreet fo JWT signing and verifying
    byte[] sharedSecret;
    // Create HMAC signer
    JWSSigner signer;
    // Create HMAC verifier
    JWSVerifier verifier;
    // store login / jwt token here
    Map<String, User> authorisationTokenStorage = new HashMap();

    public AuthServiceImpl() {
        // create a ConfigProviderResolver
        SmallRyeConfigProviderResolver configServiceProvider = new SmallRyeConfigProviderResolver();
        ConfigProviderResolver instance = null;
        try {
            instance = ConfigProviderResolver.instance();
        } catch (Exception ex) {

        }
        if (instance == null) {
            ConfigProviderResolver.setInstance(configServiceProvider);
        }

        try {
            SecureRandom random = new SecureRandom();
            sharedSecret = new byte[32];
            random.nextBytes(sharedSecret);
            signer = new MACSigner(sharedSecret);
            verifier = new MACVerifier(sharedSecret);
        } catch (KeyLengthException e) {
            e.printStackTrace();
        } catch (JOSEException e) {
            e.printStackTrace();
        }

        String[] clazzes = new String[]{
                ManagedService.class.getName(),
                AuthService.class.getName()
        };
        Hashtable<String, Object> properties = new Hashtable<String, Object>();
        properties.put(Constants.SERVICE_PID, AuthService.class.getName());
        authServiceReg = Activator.bc.registerService(clazzes, this, properties);

        Activator.getLogger().info("Registered " + this.getClass().getName() + ", Pid = " + (String) properties.get(Constants.SERVICE_PID));
    }

    @Override
    public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
        if (properties != null && !properties.isEmpty()) {
            String pathPrefix = (String) properties.get(PATH_PREFIX);
            if (pathPrefix != null && !pathPrefix.isBlank()) {

                if (lastPathPrefix != null && lastPathPrefix.equals(pathPrefix)) {
                    // no changes, just return.
                    return;
                }

                // clear previous
                stop();

                // Register specific servlet context helper and servlet context
                /**
                 AuthServletContext servletContext = new AuthServletContext(Activator.bc.getBundle());
                 Hashtable servletContextProps = new Hashtable();
                 servletContextProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "auth");
                 servletContextProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, pathPrefix);
                 servletContextHelperReg = Activator.bc.registerService(ServletContextHelper.class, servletContext, servletContextProps);
                 **/

                List<? extends Module> applicationModules = Arrays.asList(new ApplicationModule());
                ExtendedGuiceRestEasyServletContextListener contextListener = new ExtendedGuiceRestEasyServletContextListener(applicationModules);

                Hashtable servletContextlistenerProps = new Hashtable();
                servletContextlistenerProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER, "true");
                servletContextlistenerProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=default)");
                Activator.getLogger().info("Registered  servlet context listener " + ExtendedGuiceRestEasyServletContextListener.class.getName() + " to Whiteboard service");
                restEasyContextServletListenerReg = Activator.bc.registerService(ServletContextListener.class.getName(), contextListener, servletContextlistenerProps);

                Hashtable restEasyProps = new Hashtable();
                // for resteasy microprofile config use
                restEasyProps.put("resteasy.servlet.mapping.prefix", pathPrefix);
                restEasyProps.put("javax.ws.rs.Application", RootApplication.class.getName());
                /**
                 ServicePropertiesConfigSource serviceConfigSource = new ServicePropertiesConfigSource(restEasyProps);
                 ConfigProviderResolver resolver = ConfigProviderResolver.instance();
                 ConfigBuilder builder = resolver.getBuilder();
                 Config newConfig = builder.addDefaultSources().withSources(serviceConfigSource).build();

                 if (config != null) {
                 resolver.releaseConfig(config);
                 }
                 config = newConfig;
                 resolver.registerConfig(config, getClass().getClassLoader());
                 **/


                Hashtable props = new Hashtable();
                props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, pathPrefix + "/*");
                props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, "auth");
                props.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=default)");
                props.put("servlet.init.javax.ws.rs.Application", RootApplication.class.getName());
                props.put("servlet.init.resteasy.servlet.mapping.prefix", pathPrefix);

                Activator.getLogger().info("Registered  servlet " + ExtendedHttpServletDispatcher.class.getName() + " to Whiteboard service with path: " + pathPrefix);
                restEasyServletReg = Activator.bc.registerService(Servlet.class.getName(), new ExtendedHttpServletDispatcher(), props);
                lastPathPrefix = pathPrefix;

                Hashtable swaggerProps = new Hashtable();
                swaggerProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, pathPrefix + "/swagger-ui/*");
                swaggerProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, "/resources/swagger-ui");
                swaggerProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=default)");
                // register swagger-ui resources
                swaggerServiceReg = Activator.bc.registerService(SwaggerService.class.getName(), new SwaggerService(), swaggerProps);

                Hashtable swaggerFilterProps = new Hashtable();
                swaggerFilterProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME, SwaggerFilter.class.getName());
                swaggerFilterProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN, pathPrefix + "/swagger-ui/*");
                swaggerFilterProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT, "(osgi.http.whiteboard.context.name=default)");
                // register swagger-ui filter
                swaggerFilterReg = Activator.bc.registerService(Filter.class.getName(), new SwaggerFilter(), swaggerFilterProps);
                lastPathPrefix = pathPrefix;

            }
        } else {
            stop();
        }


    }

    /**
     * Authenticate a user with his name and credential/
     *
     * @param username user name
     * @param password user credential
     * @return true if authenticated
     * @throws AccountNotFoundException
     * @throws ServiceException
     */
    public String authenticate(String username, String password, String origin) throws AccountNotFoundException, ServiceException, JOSEException {
        UserAdmin userAdmin = Activator.getUserAdminServiceListener().getUserAdmin();
        if (userAdmin != null) {
            User user = userAdmin.getUser("name", username);
            if (user != null) {
                String credential = user.getCredentials().get("password").toString();
                if (credential.equals(password)) {
                    String jwt = issueJwtToken(user, origin);
                    return jwt;
                } else {
                    throw new AccountNotFoundException();
                }
            } else {
                throw new AccountNotFoundException();
            }
        } else {
            throw new ServiceException("Service UserAdmin not found", ServiceException.UNREGISTERED);
        }
    }

    /**
     * Create a new Jwt token for the user if an existing one is not yet associated to the user id
     *
     * @param user the user
     * @return String the Jwt token as string
     * @throws JOSEException
     */
    protected String issueJwtToken(User user, String origin) throws JOSEException {
        // Prepare JWT with claims set

        Date now = new Date();
        String[] roles = new String[0];
        UserAdmin userAdmin = Activator.getUserAdminServiceListener().getUserAdmin();
        if (userAdmin != null) {
            roles = userAdmin.getAuthorization(user).getRoles();
        }
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getName())
                .issuer(origin)
                .issueTime(now)
                .notBeforeTime(now)
                .expirationTime(new Date(new Date().getTime() + (60 * 60 * 1000)))
                .claim("roles", roles)
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

        // Apply the HMAC protection
        signedJWT.sign(signer);

        String jwt = signedJWT.serialize();
        authorisationTokenStorage.put(jwt, user);

        // Serialize to compact form, produces something like
        return jwt;
    }

    /**
     * Validate Jwt token, check that token subject claim match a user for which a jwt ha been generated.
     * Check token expiration date, and token validity.
     *
     * @param jwt the token to validate.
     * @return true if token is valide, false otherwise
     * @throws CredentialNotFoundException
     * @throws CredentialExpiredException
     * @throws ParseException
     * @throws JOSEException
     */
    public boolean validateJwtToken(String jwt) throws CredentialExpiredException, CredentialNotFoundException, CredentialException, ParseException, JOSEException {
        if (authorisationTokenStorage.containsKey(jwt)) {
            SignedJWT signedJWT = SignedJWT.parse(jwt);
            if (signedJWT.getHeader().getAlgorithm().getName().equals(Algorithm.NONE.getName())) {
                // Alg cannot be none
                throw new CredentialNotFoundException();
            }
            String subject = signedJWT.getJWTClaimsSet().getSubject();
            Date expDate = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expDate != null && expDate.before(new Date())) {
                authorisationTokenStorage.remove(jwt);
                throw new CredentialExpiredException();
            }
            return signedJWT.verify(verifier);
        }
        throw new CredentialNotFoundException();
    }

    /**
     * Clear Jwt token for user id, making it as it need to re-authenticate
     *
     * @param jwt the user token
     * @throws CredentialNotFoundException
     */
    public void clearCredential(String jwt) throws CredentialNotFoundException, ParseException {
        if (authorisationTokenStorage.containsKey(jwt)) {
            authorisationTokenStorage.remove(jwt);
        } else {
            throw new CredentialNotFoundException();
        }

    }

    /**
     * retriev the user roles
     *
     * @param jwt token identifying the user.
     * @return
     */
    public String[] getUserRoles(String jwt) {
        String[] roles = null;
        if (authorisationTokenStorage.containsKey(jwt)) {
            User user = authorisationTokenStorage.get(jwt);
            UserAdmin userAdmin = Activator.getUserAdminServiceListener().getUserAdmin();
            if (userAdmin != null) {
                roles = userAdmin.getAuthorization(user).getRoles();
            }
        }
        return roles;
    }

    public void stop() {

        if (this.swaggerServiceReg != null) {
            this.swaggerServiceReg.unregister();
            this.swaggerServiceReg = null;
        }
        if (this.swaggerFilterReg != null) {
            this.swaggerFilterReg.unregister();
            this.swaggerFilterReg = null;
        }

        if (this.restEasyServletReg != null) {
            this.restEasyServletReg.unregister();
            this.restEasyServletReg = null;
        }
        if (this.restEasyContextServletListenerReg != null) {
            this.restEasyContextServletListenerReg.unregister();
            this.restEasyContextServletListenerReg = null;
        }

        if (this.servletContextHelperReg != null) {
            this.servletContextHelperReg.unregister();
            this.servletContextHelperReg = null;
        }

        lastPathPrefix = null;
    }
}
