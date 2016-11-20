package com.ptoceti.osgi.influxdb.ql;

/**
 * SLimit ... clause builder.
 * 
 * @author LATHIL
 *
 */
public class SLimitClauseBuilder extends ClauseBuilder {

    public SLimitClauseBuilder(StatementBuilder statementBuilder,  int limit) {
	super(statementBuilder);

	Clause clause = new Clause(Clause.ClauseName.SLIMIT);
	clause.addIdentifier(Integer.toString(limit));

	this.setClause(clause);
    }
    
    /**
     * Add soffset clause to the chain
     * 
     * @param limit
     * @return
     */
    public SOffsetClauseBuilder Offset(int limit) {
	SOffsetClauseBuilder builder = new SOffsetClauseBuilder(this.statementBuilder, limit);
	this.statementBuilder.addClause(builder.getClause());
	return builder;
    }

}
