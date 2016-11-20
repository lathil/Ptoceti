package com.ptoceti.osgi.influxdb.ql;

public class StatementBuilderWithRetentionPolicy extends StatementBuilder {

    public StatementBuilderWithRetentionPolicy(QueryBuilder queryBuilder, Statement statement) {
	super(queryBuilder, statement);
    }

    public WithRetentionClauseBuilder With() {

	WithRetentionClauseBuilder clauseBuilder = new WithRetentionClauseBuilder(this);
	statement.addClause(clauseBuilder.getClause());
	return clauseBuilder;
    }
}
