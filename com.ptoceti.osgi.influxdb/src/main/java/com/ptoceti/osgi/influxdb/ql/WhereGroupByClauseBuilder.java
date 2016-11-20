package com.ptoceti.osgi.influxdb.ql;

/**
 * Where ... clause builder that propose GroupBy, OrderBy, Limit and SLimit clauses.
 * 
 * @author LATHIL
 *
 */
public class WhereGroupByClauseBuilder extends ClauseBuilder {

    public WhereGroupByClauseBuilder(StatementBuilder statementBuilder, String... identifiers) {

	super(statementBuilder);

	Clause clause = new Clause(Clause.ClauseName.WHERE);
	for (String identifier : identifiers) {
	    clause.addIdentifier(identifier);
	}

	this.setClause(clause);
    }

    /**
     * Add GroupBy... clause to the chain
     * 
     * @param identifiers
     * @return
     */
    public GroupByClauseBuilder GroupBy(String... identifiers) {
	GroupByClauseBuilder groupBy = new GroupByClauseBuilder(this.statementBuilder, identifiers);
	this.statementBuilder.addClause(groupBy.getClause());
	return groupBy;
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
