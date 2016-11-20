package com.ptoceti.osgi.influxdb.ql;

/**
 * SOffset ... clause builder that propose SOffset clause.
 * 
 * @author LATHIL
 *
 */
public class SOffsetClauseBuilder extends ClauseBuilder {

    public SOffsetClauseBuilder(StatementBuilder statementBuilder, int limit) {
	super(statementBuilder);

	Clause clause = new Clause(Clause.ClauseName.SOFFSET);
	clause.addIdentifier(Integer.toString(limit));

	this.setClause(clause);
    }
}
