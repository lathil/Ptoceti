package com.ptoceti.osgi.influxdb.ql;

public class DurationNameRetentionPolicyBuilder extends RetentionPolicyBuilder {

    public DurationNameRetentionPolicyBuilder(StatementBuilder statementBuilder, String identifier) {
	super(statementBuilder);

	RetentionPolicy policy = new RetentionPolicy(RetentionPolicy.PolicyName.DURATION);
	policy.addIdentifier(identifier);

	this.setRetentionPolicy(policy);
    }

    public ReplicationNameRetentionPolicyBuilder Replication(String identifier) {
	ReplicationNameRetentionPolicyBuilder builder = new ReplicationNameRetentionPolicyBuilder(
		this.statementBuilder, identifier);
	this.statementBuilder.addPolicy(builder.getRetentionPolicy());
	return builder;
    }

    public ShardDurationNameRetentionPolicyBuilder ShardDuration(String identifier) {
	ShardDurationNameRetentionPolicyBuilder builder = new ShardDurationNameRetentionPolicyBuilder(
		this.statementBuilder, identifier);
	this.statementBuilder.addPolicy(builder.getRetentionPolicy());
	return builder;
    }

    public NameRetentionPolicyBuilder Name(String identifier) {
	NameRetentionPolicyBuilder builder = new NameRetentionPolicyBuilder(this.statementBuilder, identifier);
	this.statementBuilder.addPolicy(builder.getRetentionPolicy());
	return builder;
    }

}
