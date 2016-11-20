package com.ptoceti.osgi.influxdb.ql;

public class DefaultRetentionPolicyBuilder extends RetentionPolicyBuilder {

    public DefaultRetentionPolicyBuilder(StatementBuilder statementBuilder) {
	super(statementBuilder);

	RetentionPolicy policy = new RetentionPolicy(RetentionPolicy.PolicyName.DEFAULT);
	this.setRetentionPolicy(policy);
    }

}
