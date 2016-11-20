package com.ptoceti.osgi.influxdb.ql;

public class LimitClauseBuilder extends ClauseBuilder {

    public LimitClauseBuilder(StatementBuilder statementBuilder, int limit) {
	super(statementBuilder);

	Clause clause = new Clause(Clause.ClauseName.LIMIT);
	clause.addIdentifier(Integer.toString(limit));

	this.setClause(clause);
    }
}
