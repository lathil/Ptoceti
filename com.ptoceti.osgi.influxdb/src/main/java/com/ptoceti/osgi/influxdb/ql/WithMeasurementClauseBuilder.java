package com.ptoceti.osgi.influxdb.ql;

public class WithMeasurementClauseBuilder extends ClauseBuilder {

    public WithMeasurementClauseBuilder(StatementBuilder statementBuilder) {
	super(statementBuilder);

	Clause clause = new Clause(Clause.ClauseName.WITHMEASUREMENT);
	this.setClause(clause);
    }

    /**
     * Add a Where ... clause to the chain
     * 
     * @param identifiers
     * @return
     */
    public WhereOffsetClauseBuilder Where(String... identifiers) {
	WhereOffsetClauseBuilder where = new WhereOffsetClauseBuilder(this.statementBuilder, identifiers);
	this.statementBuilder.addClause(where.getClause());
	return where;
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

}
