package com.ptoceti.osgi.influxdb;

import java.util.Iterator;
import java.util.List;

public class UserSerie extends SerieWrapper implements Iterator<UserSerie.User>, Iterable<UserSerie.User>{

    protected static final String USERFIELD = "user";
    protected static final String ADMINFIELD = "admin";
    
    
    public UserSerie(Serie serie) {
	super(serie);
    }
    
    @Override
    public Iterator<User> iterator() {
	return this;
    }
    
    @Override
    public boolean hasNext() {
	return delegate.hasNext();
    }

    @Override
    public void remove() {
	delegate.remove();
	
    }
    
    @Override
    public UserSerie.User next(){
	 return new User(delegate.next());
    }
    
    public class User {
	
	User(List<Object> values){
	    setUser( (String)values.get(fields.get(USERFIELD)));
	    setAdmin( Boolean.valueOf((String)values.get(fields.get(ADMINFIELD))));
	}
	
	private String user;
	private Boolean admin;
	public String getUser() {
	    return user;
	}
	public void setUser(String user) {
	    this.user = user;
	}
	public Boolean getAdmin() {
	    return admin;
	}
	public void setAdmin(Boolean admin) {
	    this.admin = admin;
	}
    }

   
}
