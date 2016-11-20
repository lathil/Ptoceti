package com.ptoceti.osgi.influxdb;

import java.util.Iterator;
import java.util.List;

public class GrantSerie extends SerieWrapper implements Iterator<GrantSerie.Grant>, Iterable<GrantSerie.Grant>{

    protected static final String DATABASEFIELD = "database";
    protected static final String PRIVILEGEFIELD = "privilege";
    
    public GrantSerie(Serie serie) {
	super(serie);
    }
    
    @Override
    public Iterator<Grant> iterator() {
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
    public Grant next(){
	 return new Grant(delegate.next());
    }
    
    public class Grant{
	
	private String database;
	private String privilege;
	
	public Grant(List<Object> values){
	    setDatabase( (String)values.get(fields.get(DATABASEFIELD)));
	    setPrivilege( (String)values.get(fields.get(PRIVILEGEFIELD)));
	}

	public String getDatabase() {
	    return database;
	}

	public void setDatabase(String database) {
	    this.database = database;
	}

	public String getPrivilege() {
	    return privilege;
	}

	public void setPrivilege(String privilege) {
	    this.privilege = privilege;
	}
    }


}
