package com.ptoceti.osgi.influxdb.ql;

public class WithKeyClauseBuilder extends ClauseBuilder {

    public WithKeyClauseBuilder(StatementBuilder statementBuilder, String... identifiers) {
	super(statementBuilder);

	Clause clause = new Clause(Clause.ClauseName.WITHKEY);
	for (String identifier : identifiers) {
	    clause.addIdentifier(identifier);
	}
	this.setClause(clause);
    }

    /**
     * Add Where... clause to the chain.
     * 
     * @param identifiers
     * @return
     */
    public WhereGroupByLimitOffsetClauseBuilder Where(String... identifiers) {
	WhereGroupByLimitOffsetClauseBuilder where = new WhereGroupByLimitOffsetClauseBuilder(this.statementBuilder,
		identifiers);
	this.statementBuilder.addClause(where.getClause());
	return where;
    }

    /**
     * Add GroupBy... clause to the chain
     * 
     * @param identifiers
     * @return
     */
    public GroupByLimitOffsetClauseBuilder GroupBy(String... identifiers) {
	GroupByLimitOffsetClauseBuilder groupBy = new GroupByLimitOffsetClauseBuilder(this.statementBuilder,
		identifiers);
	this.statementBuilder.addClause(groupBy.getClause());
	return groupBy;
    }

    /**
     * Add limit clause to the chain
     * 
     * @param limit
     * @return
     */
    public LimitOffsetClauseBuilder Limit(int limit) {
	LimitOffsetClauseBuilder orderBy = new LimitOffsetClauseBuilder(this.statementBuilder, limit);
	this.statementBuilder.addClause(orderBy.getClause());
	return orderBy;
    }
}
