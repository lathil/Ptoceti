package com.ptoceti.osgi.influxdb.ql;

public class StatementBuilderFromWithKey extends StatementBuilder {

    public StatementBuilderFromWithKey(QueryBuilder queryBuilder, Statement statement) {
	super(queryBuilder, statement);
    }

    public StatementBuilderFromWithKey(Statement statement) {
	super(statement);
    }

    /**
     * Add simple From clause
     * 
     * @param identifiers
     * @return
     */
    public FromWithKeyClauseBuilder From(String... identifiers) {

	FromWithKeyClauseBuilder from = new FromWithKeyClauseBuilder(this, identifiers);

	statement.addClause(from.getClause());
	return from;
    }

    public WithKeyClauseBuilder WithKey(String... identifiers) {

	WithKeyClauseBuilder from = new WithKeyClauseBuilder(this, identifiers);

	statement.addClause(from.getClause());
	return from;
    }
}
