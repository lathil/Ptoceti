package com.ptoceti.osgi.influxdb.ql;

public class OnBeginClauseBuilder extends ClauseBuilder {

    /**
     * Build a On ... clause and add it to the chain
     * 
     * @param statementBuilder
     * @param identifier
     */
    public OnBeginClauseBuilder(StatementBuilder statementBuilder, String identifier) {
	super(statementBuilder);

	Clause clause = new Clause(Clause.ClauseName.ON);
	clause.addIdentifier(identifier);
	this.setClause(clause);
    }
    
    /**
     * Add Resample... clause to the chain.
     * 
     * @param identifiers
     * @return
     */
    public ResampleClauseBuilder Resample(String identifier) {
	ResampleClauseBuilder resample = new ResampleClauseBuilder(this.statementBuilder, identifier);
	this.statementBuilder.addClause(resample.getClause());
	return resample;
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
