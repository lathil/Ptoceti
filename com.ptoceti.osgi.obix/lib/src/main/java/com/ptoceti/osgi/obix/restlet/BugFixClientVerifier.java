package com.ptoceti.osgi.obix.restlet;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Form;
import org.restlet.ext.oauth.ClientVerifier;
import org.restlet.ext.oauth.OAuthError;
import org.restlet.ext.oauth.OAuthException;
import org.restlet.ext.oauth.OAuthServerResource;
import org.restlet.ext.oauth.internal.Client;
import org.restlet.ext.oauth.internal.ClientManager;
import org.restlet.security.SecretVerifier;
import org.restlet.security.User;
import org.restlet.security.Verifier;

public class BugFixClientVerifier implements Verifier {
	private boolean acceptBodyMethod = false;

	private Context context;

	public BugFixClientVerifier(Context context) {
		this.context = context;
	}

	/**
	 * @return the acceptBodyMethod
	 */
	public boolean isAcceptBodyMethod() {
		return acceptBodyMethod;
	}

	/**
	 * @param acceptBodyMethod
	 *            the acceptBodyMethod to set
	 */
	public void setAcceptBodyMethod(boolean acceptBodyMethod) {
		this.acceptBodyMethod = acceptBodyMethod;
	}

	public int verify(Request request, Response response) {
		final String clientId;
		final char[] clientSecret;
		ChallengeResponse cr = request.getChallengeResponse();
		if (cr == null) {
			if (!isAcceptBodyMethod()) {
				return RESULT_MISSING;
			}
			// Alternative method...
			Form params = new Form(request.getEntity());
			clientId = params.getFirstValue(OAuthServerResource.CLIENT_ID);
			if (clientId == null || clientId.isEmpty()) {
				return RESULT_MISSING;
			}
			String s = params.getFirstValue(OAuthServerResource.CLIENT_SECRET);
			if (s == null || s.isEmpty()) {
				clientSecret = new char[0];
			} else {
				clientSecret = s.toCharArray();
			}
			// Restore the body
			request.setEntity(params.getWebRepresentation());
		} else {
			if (!cr.getScheme().equals(ChallengeScheme.HTTP_BASIC)) {
				// XXX: May be unsupported
				return RESULT_UNSUPPORTED;
			}
			clientId = cr.getIdentifier();
			clientSecret = cr.getSecret();
		}

		int result = verify(clientId, clientSecret);
		if (result == RESULT_VALID) {
			request.getClientInfo().setUser(new User(clientId));
		} else {
			response.setEntity(OAuthServerResource.responseErrorRepresentation(new OAuthException(OAuthError.invalid_client, "Invalid client", null)));
		}
		return result;
	}

	private int verify(String clientId, char[] clientSecret) {
		ClientManager clients = (ClientManager) context.getAttributes().get(ClientManager.class.getName());
		Client client = clients.findById(clientId);
		if (client == null) {
			return RESULT_UNKNOWN;
		}
		char[] s = client.getClientSecret();
		
		boolean anull = false;
		if( (s == null) || (s.length == 0)) anull = true;
		boolean bnull = false;
		if( (clientSecret == null) || (clientSecret.length == 0)) bnull = true;
		if( anull == true && bnull == true) return RESULT_VALID;
		
		
		if (!SecretVerifier.compare(s, clientSecret)) {
			return RESULT_INVALID;
		}
		return RESULT_VALID;
	}
}
