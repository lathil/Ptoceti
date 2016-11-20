package com.ptoceti.osgi.influxdb.ql;

/**
 * GroupBy ... clause builder that propose OrderBy, Limit and SLimit clauses.
 * 
 * @author LATHIL
 *
 */
public class GroupByClauseBuilder extends ClauseBuilder {

    public GroupByClauseBuilder(StatementBuilder statementBuilder, String... identifiers) {

	super(statementBuilder);

	Clause clause = new Clause(Clause.ClauseName.GROUPBY);
	for (String identifier : identifiers) {
	    clause.addIdentifier(identifier);
	}

	this.setClause(clause);
    }

    /**
     * Add OrderBy... clause to the chain
     * 
     * @param identifiers
     * @return
     */
    public OrderByClauseBuilder OrderBy(String... identifiers) {
	OrderByClauseBuilder orderBy = new OrderByClauseBuilder(this.statementBuilder, identifiers);
	this.statementBuilder.addClause(orderBy.getClause());
	return orderBy;
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
