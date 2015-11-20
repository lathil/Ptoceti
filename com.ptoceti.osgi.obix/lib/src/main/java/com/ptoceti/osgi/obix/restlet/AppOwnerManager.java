package com.ptoceti.osgi.obix.restlet;


import java.util.Arrays;

import javax.naming.AuthenticationException;

import org.restlet.ext.oauth.internal.ResourceOwnerManager;
import org.restlet.security.MemoryRealm;
import org.restlet.security.User;

public class AppOwnerManager implements ResourceOwnerManager {

    public static final String ROLE_OWNER = "owner";
    
	private  MemoryRealm realm = new MemoryRealm();
	
	public void addOwner(String name, String password){
		User owner = new User(name, password);
        realm.getUsers().add(owner);
	}
	
	@Override
	public String authenticate(String username, char[] password) throws AuthenticationException {
		
		User user = realm.findUser(username);
		if( user!= null && password != null){
			if( Arrays.equals(user.getSecret(), password)) return user.getIdentifier();
		}
		
		return null;
	}

}
