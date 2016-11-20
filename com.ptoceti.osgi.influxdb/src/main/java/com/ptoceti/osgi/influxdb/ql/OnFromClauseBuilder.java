package com.ptoceti.osgi.influxdb.ql;

public class OnFromClauseBuilder extends OnClauseBuilder {

    /**
     * Build a On ... clause and add it to the chain
     * 
     * @param statementBuilder
     * @param identifiers
     */
    public OnFromClauseBuilder(StatementBuilder statementBuilder, String identifier) {
	super(statementBuilder, identifier);
    }
    
    /**
     * Add From... clause
     * 
     * @param identifiers
     * @return
     */
    public FromClauseBuilder From(String identifier) {

	FromClauseBuilder from = new FromClauseBuilder(this.statementBuilder, identifier);
	this.statementBuilder.addClause(from.getClause());
	return from;
    }
}
