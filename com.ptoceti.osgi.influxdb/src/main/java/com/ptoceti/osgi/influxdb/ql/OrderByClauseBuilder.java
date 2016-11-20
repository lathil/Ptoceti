package com.ptoceti.osgi.influxdb.ql;

/**
 * OrderBy ... clause builder that propose Limit and SLimit clauses.
 * 
 * @author LATHIL
 *
 */
public class OrderByClauseBuilder extends ClauseBuilder {

    public OrderByClauseBuilder(StatementBuilder statementBuilder, String... identifiers) {

	super(statementBuilder);

	Clause clause = new Clause(Clause.ClauseName.ORDERBY);
	for (String identifier : identifiers) {
	    clause.addIdentifier(identifier);
	}

	this.setClause(clause);
    }

    /**
     * Add limit clause to the chain
     * 
     * @param limit
     * @return
     */
    public LimitOffsetSLimitClauseBuilder Limit(int limit) {
	LimitOffsetSLimitClauseBuilder builder = new LimitOffsetSLimitClauseBuilder(this.statementBuilder, limit);
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
