package com.ptoceti.osgi.influxdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

public class SerieWrapper {

    protected Serie serie;
    protected Map<String, Integer> fields;
    
    protected Iterator<List<Object>> delegate;

    public SerieWrapper(Serie serie) {
	this.serie = serie;
	this.fields = new HashMap<String, Integer>();

	for (int columnsindex = 0; columnsindex < serie.getColumns().size(); columnsindex++) {
	    fields.put(serie.getColumns().get(columnsindex), columnsindex);
	}
	
	delegate = serie.getValues().iterator();

    }

    protected <T extends Object> List<T> getValuesForField(String fieldName, Class<T> returnType) {

	List<T> values = new ArrayList<T>();
	Integer fieldIndex = fields.get(fieldName);

	if (fieldIndex != null) {
	    for (List<Object> dbinfos : serie.getValues()) {
		values.add( returnType.cast(dbinfos.get(fieldIndex)));
	    }
	}

	return values;
    }
    
    public int size(){
	return serie.getValues().size();
    }
}
