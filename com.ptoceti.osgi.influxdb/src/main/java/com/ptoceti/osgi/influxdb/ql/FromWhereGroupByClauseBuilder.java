package com.ptoceti.osgi.influxdb.ql;

/**
 * Clause builder that extends on From clause builder and propose clauses Where, GroupBy, OrderBy and Limit
 * 
 * 
 * @author LATHIL
 *
 */
public class FromWhereGroupByClauseBuilder extends FromClauseBuilder {

    public FromWhereGroupByClauseBuilder(StatementBuilder statementBuilder, String... identifiers) {
	super(statementBuilder, identifiers);
    }

    /**
     * Add Where... clause to the chain.
     * 
     * @param identifiers
     * @return
     */
    public WhereGroupByClauseBuilder Where(String... identifiers) {
	WhereGroupByClauseBuilder where = new WhereGroupByClauseBuilder(this.statementBuilder, identifiers);
	this.statementBuilder.addClause(where.getClause());
	return where;
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
	LimitOffsetSLimitClauseBuilder orderBy = new LimitOffsetSLimitClauseBuilder(this.statementBuilder, limit);
	this.statementBuilder.addClause(orderBy.getClause());
	return orderBy;
    }
}
