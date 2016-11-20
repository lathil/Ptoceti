package com.ptoceti.osgi.influxdb.ql;

public class ReplicationNameRetentionPolicyBuilder extends RetentionPolicyBuilder {

    public ReplicationNameRetentionPolicyBuilder(StatementBuilder statementBuilder, String identifier) {
	super(statementBuilder);

	RetentionPolicy policy = new RetentionPolicy(RetentionPolicy.PolicyName.REPLICATION);
	policy.addIdentifier(identifier);
	
	this.setRetentionPolicy(policy);

    }

    public ShardDurationNameRetentionPolicyBuilder ShardDuration(String identifier) {
	ShardDurationNameRetentionPolicyBuilder builder = new ShardDurationNameRetentionPolicyBuilder(this.statementBuilder,
		identifier);
	this.statementBuilder.addPolicy(builder.getRetentionPolicy());
	return builder;
    }

    public NameRetentionPolicyBuilder Name(String identifier) {
	NameRetentionPolicyBuilder builder = new NameRetentionPolicyBuilder(this.statementBuilder, identifier);
	this.statementBuilder.addPolicy(builder.getRetentionPolicy());
	return builder;
    }
   
}
