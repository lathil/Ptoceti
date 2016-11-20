package com.ptoceti.osgi.influxdb;

import java.util.Iterator;
import java.util.List;


public class DatabaseSerie extends SerieWrapper implements Iterator<DatabaseSerie.Database>, Iterable<DatabaseSerie.Database>{

    protected static final String SERIENAME = "databases";
    protected static final String NAMEFIELD = "name";
    
    public DatabaseSerie(Serie serie) {
	super(serie);
    }
    
    @Override
    public Iterator<Database> iterator() {
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
    public DatabaseSerie.Database next(){
	 return new Database(delegate.next());
    }
    
    public static String getSerieName(){
	return SERIENAME;
    }
    
    public class Database {
	
	private String name;
	
	Database(List<Object> values){
	    setName( (String)values.get(fields.get(NAMEFIELD)));
	}

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}
    }
 
}
