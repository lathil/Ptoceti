package com.ptoceti.osgi.influxdb.ql;

import java.util.ArrayList;
import java.util.List;


public class Query {

    private List<Statement> statements;
    
    public Query(){
	statements = new ArrayList<Statement>();
    }
    
    
    public void addStatement(Statement statement){
	statements.add(statement);
    }
    
    public String toQL(){
	StringBuffer qlbuff = new StringBuffer();
	
	boolean isfirst = true;
	for( Statement statement : statements){
	    if(!isfirst) qlbuff.append(";");
	    statement.toQL(qlbuff);
	    isfirst= false;
	}
	
	
	return qlbuff.toString();
    }
}
