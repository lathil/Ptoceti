package com.ptoceti.osgi.influxdb.ql;

public class IntoClauseBuilder extends ClauseBuilder {

    public IntoClauseBuilder(StatementBuilder statementBuilder, String... identifiers) {

	super(statementBuilder);

	Clause clause = new Clause(Clause.ClauseName.INTO);
	for (String identifier : identifiers) {
	    clause.addIdentifier(identifier);
	}

	this.setClause(clause);
    }

    /**
     * Add From... clause that also allows furthers clauses such as Where...GroupBy..OrderBy...Limit...
     * 
     * @param identifiers
     * @return
     */
    public FromWhereGroupByClauseBuilder From(String... identifiers) {

	FromWhereGroupByClauseBuilder from = new FromWhereGroupByClauseBuilder(this.statementBuilder, identifiers);
	this.statementBuilder.addClause(from.getClause());
	return from;
    }

}
