package com.ptoceti.osgi.influxdb.ql;

/**
 * Basic From clause builder. No other clauses proposed after.
 * 
 * @author LATHIL
 *
 */
public class FromClauseBuilder extends ClauseBuilder {

    /**
     * Build a From ... clause and add it to the chain
     * 
     * @param statementBuilder
     * @param identifiers
     */
    public FromClauseBuilder(StatementBuilder statementBuilder, String... identifiers) {
	super(statementBuilder);

	Clause clause = new Clause(Clause.ClauseName.FROM);
	for (String identifier : identifiers) {
	    clause.addIdentifier(identifier);
	}

	this.setClause(clause);
    }

}
