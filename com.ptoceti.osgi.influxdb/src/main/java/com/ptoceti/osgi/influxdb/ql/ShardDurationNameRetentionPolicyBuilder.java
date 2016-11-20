package com.ptoceti.osgi.influxdb.ql;

public class ShardDurationNameRetentionPolicyBuilder extends RetentionPolicyBuilder {

    public ShardDurationNameRetentionPolicyBuilder(StatementBuilder statementBuilder, String identifier) {
	super(statementBuilder);

	RetentionPolicy policy = new RetentionPolicy(RetentionPolicy.PolicyName.SHARDDURATION);
	policy.addIdentifier(identifier);

    }

    public NameRetentionPolicyBuilder Name(String identifier) {
	NameRetentionPolicyBuilder builder = new NameRetentionPolicyBuilder(this.statementBuilder, identifier);
	this.statementBuilder.addPolicy(builder.getRetentionPolicy());
	return builder;
    }
}
