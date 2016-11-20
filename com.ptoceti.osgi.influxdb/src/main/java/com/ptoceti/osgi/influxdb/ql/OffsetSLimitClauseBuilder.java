package com.ptoceti.osgi.influxdb.ql;

/**
 * Offset ... clause builder that propose Offset clause.
 * 
 * @author LATHIL
 *
 */
public class OffsetSLimitClauseBuilder extends ClauseBuilder {

    public OffsetSLimitClauseBuilder(StatementBuilder statementBuilder, int limit) {
	super(statementBuilder);

	Clause clause = new Clause(Clause.ClauseName.OFFSET);
	clause.addIdentifier(Integer.toString(limit));

	this.setClause(clause);
    }
    
    /**
     * Add slimit clause to the chain
     * 
     * @param limit
     * @return
     */
    public SLimitClauseBuilder SLimit(int limit) {
	SLimitClauseBuilder builder = new SLimitClauseBuilder(this.statementBuilder, limit);
	this.statementBuilder.addClause(builder.getClause());
	return builder;
    }

}
