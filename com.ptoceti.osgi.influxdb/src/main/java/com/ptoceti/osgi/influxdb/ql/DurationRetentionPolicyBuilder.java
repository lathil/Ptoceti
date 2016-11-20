package com.ptoceti.osgi.influxdb.ql;

public class DurationRetentionPolicyBuilder extends RetentionPolicyBuilder {

    public DurationRetentionPolicyBuilder(StatementBuilder statementBuilder, String identifier) {
	super(statementBuilder);

	RetentionPolicy policy = new RetentionPolicy(RetentionPolicy.PolicyName.DURATION);
	policy.addIdentifier(identifier);
	this.setRetentionPolicy(policy);

    }

    public ReplicationRetentionPolicyBuilder Replication(String identifier) {
	ReplicationRetentionPolicyBuilder builder = new ReplicationRetentionPolicyBuilder(this.statementBuilder,
		identifier);
	this.statementBuilder.addPolicy(builder.getRetentionPolicy());
	return builder;
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
