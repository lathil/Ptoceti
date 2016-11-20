package com.ptoceti.osgi.influxdb.ql;

public class RetentionPolicyBuilder {

    private RetentionPolicy policy;
    protected StatementBuilder statementBuilder;

    public RetentionPolicyBuilder(StatementBuilder statementBuilder) {
	this.statementBuilder = statementBuilder;
    }

    public void setRetentionPolicy(RetentionPolicy policy) {
	this.policy = policy;
    }

    public RetentionPolicy getRetentionPolicy() {
	return this.policy;
    }

    public Query getQuery() {
	return statementBuilder.getQuery();
    }

}
