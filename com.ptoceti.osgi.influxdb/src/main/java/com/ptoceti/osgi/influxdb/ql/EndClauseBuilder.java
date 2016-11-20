package com.ptoceti.osgi.influxdb.ql;

public class EndClauseBuilder extends ClauseBuilder {

    public EndClauseBuilder(StatementBuilder statementBuilder) {
	super(statementBuilder);
	Clause clause = new Clause(Clause.ClauseName.END);
	this.setClause(clause);
    }

}
