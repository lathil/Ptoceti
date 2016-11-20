package com.ptoceti.osgi.influxdb.ql;

public class OnRetentionPolicyClauseBuilder extends ClauseBuilder {

    public OnRetentionPolicyClauseBuilder(StatementBuilder statementBuilder, String identifier) {
	super(statementBuilder);

	Clause clause = new Clause(Clause.ClauseName.ON);
	clause.addIdentifier(identifier);

	this.setClause(clause);
    }

    public DurationRetentionPolicyBuilder Duration(String identifier) {
	DurationRetentionPolicyBuilder builder = new DurationRetentionPolicyBuilder(this.statementBuilder, identifier);
	this.statementBuilder.addPolicy(builder.getRetentionPolicy());
	return builder;
    }

    public ReplicationRetentionPolicyBuilder Replication(String identifier) {
	ReplicationRetentionPolicyBuilder builder = new ReplicationRetentionPolicyBuilder(this.statementBuilder, identifier);
	this.statementBuilder.addPolicy(builder.getRetentionPolicy());
	return builder;
    }
    
    public ShardDurationRetentionPolicyBuilder ShardDuration(String identifier) {
	ShardDurationRetentionPolicyBuilder builder = new ShardDurationRetentionPolicyBuilder(this.statementBuilder, identifier);
	this.statementBuilder.addPolicy(builder.getRetentionPolicy());
	return builder;
    }
    
    public DefaultRetentionPolicyBuilder Default() {
	DefaultRetentionPolicyBuilder builder = new DefaultRetentionPolicyBuilder(this.statementBuilder);
	this.statementBuilder.addPolicy(builder.getRetentionPolicy());
	return builder;
    }

}
