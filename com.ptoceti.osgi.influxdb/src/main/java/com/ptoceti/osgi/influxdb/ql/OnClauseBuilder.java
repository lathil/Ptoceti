package com.ptoceti.osgi.influxdb.ql;

/**
 * On ... clause builder that propose On clause.
 * 
 * @author LATHIL
 *
 */
public class OnClauseBuilder extends ClauseBuilder  {

    public OnClauseBuilder(StatementBuilder statementBuilder, String identifier) {
	super(statementBuilder);
	
	Clause clause = new Clause(Clause.ClauseName.ON);
	clause.addIdentifier(identifier);

	this.setClause(clause);
    }

}
