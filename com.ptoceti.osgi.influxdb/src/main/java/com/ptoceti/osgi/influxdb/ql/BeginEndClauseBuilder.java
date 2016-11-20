package com.ptoceti.osgi.influxdb.ql;

public class BeginEndClauseBuilder extends ClauseBuilder {

    /**
     * Build a Begin ... clause and add it to the chain
     * 
     * @param statementBuilder
     * @param identifier
     */
    public BeginEndClauseBuilder(StatementBuilder statementBuilder, String identifier) {
	super(statementBuilder);

	Clause clause = new Clause(Clause.ClauseName.BEGIN);
	clause.addIdentifier(identifier);
	this.setClause(clause);
    }
    
    public BeginEndClauseBuilder(StatementBuilder statementBuilder, Query query) {
	super(statementBuilder);

	Clause clause = new Clause(Clause.ClauseName.BEGIN);
	clause.addIdentifier(query.toQL());
	this.setClause(clause);
    }

    /**
     * Add End... clause to the chain.
     * 
     * @param identifiers
     * @return
     */
    public EndClauseBuilder End() {
	EndClauseBuilder end = new EndClauseBuilder(this.statementBuilder);
	this.statementBuilder.addClause(end.getClause());
	return end;
    }
}
