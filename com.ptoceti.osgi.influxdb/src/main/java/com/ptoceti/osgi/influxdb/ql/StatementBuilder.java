package com.ptoceti.osgi.influxdb.ql;


public class StatementBuilder {

    protected Statement statement;
    protected QueryBuilder queryBuilder;

    public StatementBuilder(QueryBuilder queryBuilder, Statement statement) {
	this.queryBuilder = queryBuilder;
	this.statement = statement;
    }

    public StatementBuilder(Statement statement) {
	this.statement = statement;
    }
    
    public Query getQuery(){
	return queryBuilder.getQuery();
    }

    protected void addClause(Clause clause){
	statement.addClause(clause);
    }
    
    protected void addPolicy(RetentionPolicy policy){
	statement.addRetentionPolicy(policy);
    }
}
