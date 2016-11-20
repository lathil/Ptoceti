package com.ptoceti.osgi.influxdb.ql;

public class ResampleClauseBuilder extends ClauseBuilder {

    public ResampleClauseBuilder(StatementBuilder statementBuilder, String identifier) {
	super(statementBuilder);

	Clause clause = new Clause(Clause.ClauseName.RESAMPLE);
	clause.addIdentifier(identifier);
	this.setClause(clause);
    }

    /**
     * Add Begin... clause to the chain.
     * 
     * @param identifiers
     * @return
     */
    public BeginEndClauseBuilder Begin(Query query) {
	BeginEndClauseBuilder begin = new BeginEndClauseBuilder(this.statementBuilder, query);
	this.statementBuilder.addClause(begin.getClause());
	return begin;
    }
}
