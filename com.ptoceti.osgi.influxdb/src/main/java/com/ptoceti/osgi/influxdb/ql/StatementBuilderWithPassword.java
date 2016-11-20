package com.ptoceti.osgi.influxdb.ql;

public class StatementBuilderWithPassword extends StatementBuilder {

    public StatementBuilderWithPassword(QueryBuilder queryBuilder, Statement statement) {
	super(queryBuilder, statement);
    }

    public WithPasswordClauseBuilder WithPassword(String identifiers) {

	WithPasswordClauseBuilder clauseBuilder = new WithPasswordClauseBuilder(this, identifiers);
	statement.addClause(clauseBuilder.getClause());
	return clauseBuilder;
    }

}
