package com.ptoceti.osgi.influxdb.ql;

public class NameRetentionPolicyBuilder extends RetentionPolicyBuilder {

    public NameRetentionPolicyBuilder(StatementBuilder statementBuilder, String identifier) {
	super(statementBuilder);

	RetentionPolicy policy = new RetentionPolicy(RetentionPolicy.PolicyName.NAME);
	policy.addIdentifier(identifier);
	this.setRetentionPolicy(policy);
    }
    
}
