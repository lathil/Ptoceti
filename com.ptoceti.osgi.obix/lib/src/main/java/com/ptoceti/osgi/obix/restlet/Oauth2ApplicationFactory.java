package com.ptoceti.osgi.obix.restlet;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.data.ChallengeScheme;
import org.restlet.ext.oauth.AccessTokenServerResource;
import org.restlet.ext.oauth.AuthorizationServerResource;
import org.restlet.ext.oauth.ClientVerifier;
import org.restlet.ext.oauth.OAuthServerResource;
import org.restlet.ext.oauth.TokenAuthServerResource;
import org.restlet.ext.oauth.internal.ClientManager;
import org.restlet.ext.oauth.internal.ResourceOwnerManager;
import org.restlet.ext.oauth.internal.TokenManager;
import org.restlet.ext.oauth.internal.memory.MemoryClientManager;
import org.restlet.ext.oauth.internal.memory.MemoryTokenManager;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;

import com.ptoceti.osgi.obix.impl.service.ObixServiceImpl;

public class Oauth2ApplicationFactory {

	/**
	 * The oauth server application
	 */
	protected Application application;
	
	private ClientManager clientManager;
	
	private AppOwnerManager ownerManager;
	
	public static final String CLIENTTOKENENDPOINTURI = "/token";
	public static final String AUTHORIZEENDPOINTURI = "/authorize";
	public static final String TOKENAUTHENTICATORURI = "/auth_token";
	public static final String OAUTHDEFAULTSCOPE = "owner";
	
	public Oauth2ApplicationFactory(ClientManager clientManager, AppOwnerManager ownerManager){
		
		this.clientManager = clientManager;
		this.ownerManager = ownerManager;
	}
	
	private void make(){
		
		application = new Application(new Context());
		Router router = new Router(application.getContext());
		
		// create and set token manager
		TokenManager tokenManager = new MemoryTokenManager();
		application.getContext().getAttributes().put(TokenManager.class.getName(), tokenManager);
		
		application.getContext().getAttributes().put(ClientManager.class.getName(), clientManager);
		
		application.getContext().getAttributes().put(ResourceOwnerManager.class.getName(), ownerManager);
		
		application.getContext().getAttributes().put(OAuthServerResource.PARAMETER_DEFAULT_SCOPE, OAUTHDEFAULTSCOPE);
		
		// Setup Authorize Endpoint - used by the client to obtain authorization from the resource owner via user-agent redirection
        router.attach(AUTHORIZEENDPOINTURI, AuthorizationServerResource.class);
		
		 // Setup Token Endpoint - used by the client to exchange an authorization grant for an access token, typically with client authentication
        ChallengeAuthenticator clientAuthenticator = new ChallengeAuthenticator(application.getContext(), ChallengeScheme.HTTP_BASIC, ObixServiceImpl.REALM);
        BugFixClientVerifier clientVerifier = new BugFixClientVerifier(application.getContext());
        clientVerifier.setAcceptBodyMethod(true);
        clientAuthenticator.setVerifier(clientVerifier);
        clientAuthenticator.setNext(AccessTokenServerResource.class);
        router.attach(CLIENTTOKENENDPOINTURI, clientAuthenticator);

        // Setup Token Auth for Resources Server
        router.attach(TOKENAUTHENTICATORURI, TokenAuthServerResource.class);
        
        application.setInboundRoot(router);
        
	}
	
	
	public  Application getApplication() {
		if( application == null){
			make();
		}
		return application;
	}
}
