package com.ptoceti.osgi.influxdb.ql;

/**
 * Limit ... clause builder that propose Limit clause.
 * 
 * @author LATHIL
 *
 */
public class LimitOffsetSLimitClauseBuilder extends LimitClauseBuilder {

    public LimitOffsetSLimitClauseBuilder(StatementBuilder statementBuilder,  int limit) {
	super(statementBuilder, limit);
    }
    
    /**
     * Add offset clause to the chain
     * 
     * @param limit
     * @return
     */
    public OffsetSLimitClauseBuilder Offset(int limit) {
	OffsetSLimitClauseBuilder builder = new OffsetSLimitClauseBuilder(this.statementBuilder, limit);
	this.statementBuilder.addClause(builder.getClause());
	return builder;
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
