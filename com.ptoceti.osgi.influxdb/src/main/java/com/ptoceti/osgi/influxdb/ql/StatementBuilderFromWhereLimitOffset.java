package com.ptoceti.osgi.influxdb.ql;

public class StatementBuilderFromWhereLimitOffset extends StatementBuilder {

    public StatementBuilderFromWhereLimitOffset(QueryBuilder queryBuilder, Statement statement) {
	super(queryBuilder, statement);
    }

    public StatementBuilderFromWhereLimitOffset(Statement statement) {
	super(statement);
    }

    /**
     * Add From... clause that also allows furthers clauses such as
     * Where...GroupBy..OrderBy...Limit...
     * 
     * @param identifiers
     * @return
     */
    public FromWhereOffsetLimitClauseBuilder From(String... identifiers) {
	FromWhereOffsetLimitClauseBuilder from = new FromWhereOffsetLimitClauseBuilder(this, identifiers);
	statement.addClause(from.getClause());
	return from;
    }
    
    /**
     * Add a Where ... clause to the chain
     * 
     * @param identifiers
     * @return
     */
    public WhereOffsetClauseBuilder Where(String... identifiers) {
	WhereOffsetClauseBuilder where = new WhereOffsetClauseBuilder(this, identifiers);
	statement.addClause(where.getClause());
	return where;
    }
    
    /**
     * Add limit clause to the chain
     * 
     * @param limit
     * @return
     */
    public LimitOffsetSLimitClauseBuilder Limit(int limit) {
	LimitOffsetSLimitClauseBuilder builder = new LimitOffsetSLimitClauseBuilder(this, limit);
	statement.addClause(builder.getClause());
	return builder;
    }
    
    /**
     * Add offset clause to the chain
     * 
     * @param limit
     * @return
     */
    public OffsetSLimitClauseBuilder Offset(int limit) {
	OffsetSLimitClauseBuilder builder = new OffsetSLimitClauseBuilder(this, limit);
	statement.addClause(builder.getClause());
	return builder;
    }

}
