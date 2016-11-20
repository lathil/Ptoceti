package com.ptoceti.osgi.influxdb.ql;

public class ShardDurationRetentionPolicyBuilder extends RetentionPolicyBuilder {

    public ShardDurationRetentionPolicyBuilder(StatementBuilder statementBuilder, String identifier) {
	super(statementBuilder);

	RetentionPolicy policy = new RetentionPolicy(RetentionPolicy.PolicyName.SHARDDURATION);
	policy.addIdentifier(identifier);
	this.setRetentionPolicy(policy);

    }

    public DefaultRetentionPolicyBuilder Default() {
	DefaultRetentionPolicyBuilder builder = new DefaultRetentionPolicyBuilder(this.statementBuilder);
	this.statementBuilder.addPolicy(builder.getRetentionPolicy());
	return builder;
    }
}
