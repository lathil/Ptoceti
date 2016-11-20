package com.ptoceti.osgi.influxdb.ql;

public class StatementBuilderWithMeasurement extends StatementBuilder {

    public StatementBuilderWithMeasurement(QueryBuilder queryBuilder, Statement statement) {
	super(queryBuilder, statement);
    }

    public WithMeasurementClauseBuilder WithMeasurement() {

	WithMeasurementClauseBuilder clauseBuilder = new WithMeasurementClauseBuilder(this);
	statement.addClause(clauseBuilder.getClause());
	return clauseBuilder;
    }

    /**
     * Add a Where ... clause to the chain
     * 
     * @param identifiers
     * @return
     */
    public WhereOffsetClauseBuilder Where(String... identifiers) {
	WhereOffsetClauseBuilder where = new WhereOffsetClauseBuilder(this, identifiers);
	this.addClause(where.getClause());
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
	this.addClause(builder.getClause());
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
	this.addClause(builder.getClause());
	return builder;
    }
}
