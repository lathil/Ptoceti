package com.ptoceti.osgi.influxdb.ql;

public class LimitOffsetClauseBuilder extends LimitClauseBuilder {

    public LimitOffsetClauseBuilder(StatementBuilder statementBuilder, int limit) {
	super(statementBuilder, limit);
    }

    /**
     * Add offset clause to the chain
     * 
     * @param limit
     * @return
     */
    public LimitClauseBuilder Offset(int limit) {
	LimitClauseBuilder builder = new LimitClauseBuilder(this.statementBuilder, limit);
	this.statementBuilder.addClause(builder.getClause());
	return builder;
    }

}
