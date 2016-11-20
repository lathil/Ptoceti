package com.ptoceti.osgi.influxdb.ql;



public class ClauseBuilder {

    private Clause clause;
    protected StatementBuilder statementBuilder;

    public ClauseBuilder(StatementBuilder statementBuilder) {
	this.statementBuilder = statementBuilder;
    }
    
    public void setClause(Clause clause){
	this.clause = clause;
    }
    
    public Clause getClause(){
	return this.clause;
    }
    
    public Query getQuery(){
   	return statementBuilder.getQuery();
       }
}
