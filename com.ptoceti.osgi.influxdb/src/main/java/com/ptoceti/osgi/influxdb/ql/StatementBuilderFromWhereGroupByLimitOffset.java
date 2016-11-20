package com.ptoceti.osgi.influxdb.ql;

public class StatementBuilderFromWhereGroupByLimitOffset extends StatementBuilder {

    public StatementBuilderFromWhereGroupByLimitOffset(QueryBuilder queryBuilder, Statement statement) {
	super(queryBuilder, statement);
    }

    public StatementBuilderFromWhereGroupByLimitOffset(Statement statement) {
	super(statement);
    }

    /**
     * Add From... clause that also allows furthers clauses such as
     * Where...GroupBy..OrderBy...Limit...
     * 
     * @param identifiers
     * @return
     */
    public FromWhereGroupByLimitOffsetClauseBuilder From(String... identifiers) {
	FromWhereGroupByLimitOffsetClauseBuilder from = new FromWhereGroupByLimitOffsetClauseBuilder(this, identifiers);
	statement.addClause(from.getClause());
	return from;
    }

    /**
     * Add a Where ... clause to the chain
     * 
     * @param identifiers
     * @return
     */
    public WhereGroupByLimitOffsetClauseBuilder Where(String... identifiers) {
	WhereGroupByLimitOffsetClauseBuilder where = new WhereGroupByLimitOffsetClauseBuilder(this, identifiers);
	statement.addClause(where.getClause());
	return where;
    }

    /**
     * Add GroupBy... clause to the chain
     * 
     * @param identifiers
     * @return
     */
    public GroupByLimitOffsetClauseBuilder GroupBy(String... identifiers) {
	GroupByLimitOffsetClauseBuilder groupBy = new GroupByLimitOffsetClauseBuilder(this, identifiers);
	statement.addClause(groupBy.getClause());
	return groupBy;
    }
    
    /**
     * Add limit clause to the chain
     * 
     * @param limit
     * @return
     */
    public LimitOffsetClauseBuilder Limit(int limit) {
	LimitOffsetClauseBuilder builder = new LimitOffsetClauseBuilder(this, limit);
	statement.addClause(builder.getClause());
	return builder;
    }
}
