package com.ptoceti.osgi.influxdb.ql;

public class StatementBuilderOnRetention extends StatementBuilder {

    public StatementBuilderOnRetention(QueryBuilder queryBuilder, Statement statement) {
	super(queryBuilder, statement);
    }

    /**
     * Add a clause that allow On ..
     * 
     * @param identifiers
     * @return
     */
    public OnRetentionPolicyClauseBuilder On(String identifiers) {

	OnRetentionPolicyClauseBuilder on = new OnRetentionPolicyClauseBuilder(this, identifiers);
	statement.addClause(on.getClause());
	return on;
    }
    
}
