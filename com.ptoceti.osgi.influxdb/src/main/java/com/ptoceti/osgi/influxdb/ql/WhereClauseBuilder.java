package com.ptoceti.osgi.influxdb.ql;


/**
 * Basic Where clause. No other clauses proposed after.
 * 
 * @author LATHIL
 *
 */
public class WhereClauseBuilder extends ClauseBuilder {

    /**
     * Build a Where ... clause and add it to the chain
     * 
     * @param statementBuilder
     * @param identifiers
     */
    public WhereClauseBuilder(StatementBuilder statementBuilder, String... identifiers) {
	super(statementBuilder);

	Clause clause = new Clause(Clause.ClauseName.WHERE);
	for (String identifier : identifiers) {
	    clause.addIdentifier(identifier);
	}

	this.setClause(clause);
    }
}
