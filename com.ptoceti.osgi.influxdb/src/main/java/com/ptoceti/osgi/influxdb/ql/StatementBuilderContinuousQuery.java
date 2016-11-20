package com.ptoceti.osgi.influxdb.ql;

public class StatementBuilderContinuousQuery extends StatementBuilder {

    public StatementBuilderContinuousQuery(QueryBuilder queryBuilder, Statement statement) {
	super(queryBuilder, statement);
    }

    public StatementBuilderContinuousQuery(Statement statement) {
	super(statement);
    }
    
    /*
    * Add simple OnBegin clause
    * 
    * @param identifiers
    * @return
    */
   public OnBeginClauseBuilder On(String identifier) {

	OnBeginClauseBuilder on = new OnBeginClauseBuilder(this, identifier);

	statement.addClause(on.getClause());
	return on;
   }
}
