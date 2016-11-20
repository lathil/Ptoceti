package com.ptoceti.osgi.influxdb.ql;

public class FromWithKeyClauseBuilder extends FromClauseBuilder {

    /**
     * Build a From ... clause and add it to the chain
     * 
     * @param statementBuilder
     * @param identifiers
     */
    public FromWithKeyClauseBuilder(StatementBuilder statementBuilder, String... identifiers) {
	super(statementBuilder, identifiers);
    }
    
    /**
     * Add a With Key ... clause to the chain
     * 
     * @param identifiers
     * @return
     */
    public WithKeyClauseBuilder WithKey(String... identifiers) {
	WithKeyClauseBuilder where = new WithKeyClauseBuilder(this.statementBuilder, identifiers);
	this.statementBuilder.addClause(where.getClause());
	return where;
    }

}
