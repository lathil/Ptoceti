package com.ptoceti.osgi.obix.service;


public interface ObixService {

	public String createOauthPublicClientID(String redirectURI);
	
	public boolean existsOauthClient(String id);
	
}
