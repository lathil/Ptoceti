package com.ptoceti.osgi.influxdb.ql;

public class WhereGroupByLimitOffsetClauseBuilder extends ClauseBuilder {

    public WhereGroupByLimitOffsetClauseBuilder(StatementBuilder statementBuilder, String... identifiers) {

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
    public GroupByLimitOffsetClauseBuilder GroupBy(String... identifiers) {
	GroupByLimitOffsetClauseBuilder groupBy = new GroupByLimitOffsetClauseBuilder(this.statementBuilder, identifiers);
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
	LimitOffsetClauseBuilder builder = new LimitOffsetClauseBuilder(this.statementBuilder, limit);
	this.statementBuilder.addClause(builder.getClause());
	return builder;
    }

}
