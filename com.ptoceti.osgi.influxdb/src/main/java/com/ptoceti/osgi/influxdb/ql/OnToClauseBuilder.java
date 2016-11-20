package com.ptoceti.osgi.influxdb.ql;

public class OnToClauseBuilder extends OnClauseBuilder {

    /**
     * Build a On ... clause and add it to the chain
     * 
     * @param statementBuilder
     * @param identifiers
     */
    public OnToClauseBuilder(StatementBuilder statementBuilder, String identifier) {
	super(statementBuilder, identifier);
    }
    
    /**
     * Add To... clause
     * 
     * @param identifiers
     * @return
     */
    public ToClauseBuilder To(String identifier) {

	ToClauseBuilder to = new ToClauseBuilder(this.statementBuilder, identifier);
	this.statementBuilder.addClause(to.getClause());
	return to;
    }
}
