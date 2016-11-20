package com.ptoceti.osgi.influxdb.ql;

public class ReplicationRetentionPolicyBuilder extends RetentionPolicyBuilder {

    public ReplicationRetentionPolicyBuilder(StatementBuilder statementBuilder, String identifier) {
	super(statementBuilder);

	RetentionPolicy policy = new RetentionPolicy(RetentionPolicy.PolicyName.REPLICATION);
	policy.addIdentifier(identifier);
	this.setRetentionPolicy(policy);

    }

    public ShardDurationRetentionPolicyBuilder ShardDuration(String identifier) {
	ShardDurationRetentionPolicyBuilder builder = new ShardDurationRetentionPolicyBuilder(this.statementBuilder,
		identifier);
	this.statementBuilder.addPolicy(builder.getRetentionPolicy());
	return builder;
    }

    public DefaultRetentionPolicyBuilder Default() {
	DefaultRetentionPolicyBuilder builder = new DefaultRetentionPolicyBuilder(this.statementBuilder);
	this.statementBuilder.addPolicy(builder.getRetentionPolicy());
	return builder;
    }
}
