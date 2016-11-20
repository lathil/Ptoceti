package com.ptoceti.osgi.influxdb.ql;



/**
 * Clause builder that extends From clause builder by proposing a Where clause.
 * 
 * @author LATHIL
 *
 */
public class FromWhereClauseBuilder extends FromClauseBuilder {

    /**
     * Build a From ... clause and add it to the chain
     * 
     * @param statementBuilder
     * @param identifiers
     */
    public FromWhereClauseBuilder(StatementBuilder statementBuilder, String... identifiers) {
	super(statementBuilder, identifiers);

    }

    /**
     * Add a Where ... clause to the chain
     * 
     * @param identifiers
     * @return
     */
    public WhereClauseBuilder Where(String... identifiers) {
	WhereClauseBuilder where = new WhereClauseBuilder(this.statementBuilder, identifiers);
	this.statementBuilder.addClause(where.getClause());
	return where;
    }

}
