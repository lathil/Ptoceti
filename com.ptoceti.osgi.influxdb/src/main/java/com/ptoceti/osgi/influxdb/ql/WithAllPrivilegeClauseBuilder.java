package com.ptoceti.osgi.influxdb.ql;

public class WithAllPrivilegeClauseBuilder extends ClauseBuilder {

    public WithAllPrivilegeClauseBuilder(StatementBuilder statementBuilder) {
	super(statementBuilder);
	Clause clause = new Clause(Clause.ClauseName.WITHALLPRIVILEGES);
	
	this.setClause(clause);
    }

}
