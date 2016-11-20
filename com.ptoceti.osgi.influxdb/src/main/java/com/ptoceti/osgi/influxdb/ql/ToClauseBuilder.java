package com.ptoceti.osgi.influxdb.ql;

/**
 * To ... clause builder that propose To clause.
 * 
 * @author LATHIL
 *
 */
public class ToClauseBuilder extends ClauseBuilder {

    public ToClauseBuilder(StatementBuilder statementBuilder, String identifier) {
   	super(statementBuilder);
   	
   	Clause clause = new Clause(Clause.ClauseName.TO);
   	clause.addIdentifier(identifier);

   	this.setClause(clause);
       }
}
