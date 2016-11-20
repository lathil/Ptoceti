package com.ptoceti.osgi.influxdb.ql;

public class WithRetentionClauseBuilder extends ClauseBuilder {

    public WithRetentionClauseBuilder(StatementBuilder statementBuilder) {
	super(statementBuilder);

	Clause clause = new Clause(Clause.ClauseName.WITH);
	this.setClause(clause);
    }

    public DurationNameRetentionPolicyBuilder Duration(String identifier) {
	DurationNameRetentionPolicyBuilder builder = new DurationNameRetentionPolicyBuilder(this.statementBuilder, identifier);
	this.statementBuilder.addPolicy(builder.getRetentionPolicy());
	return builder;
    }

    public ReplicationNameRetentionPolicyBuilder Replication(String identifier) {
	ReplicationNameRetentionPolicyBuilder builder = new ReplicationNameRetentionPolicyBuilder(this.statementBuilder,
		identifier);
	this.statementBuilder.addPolicy(builder.getRetentionPolicy());
	return builder;
    }

    public ShardDurationNameRetentionPolicyBuilder ShardDuration(String identifier) {
	ShardDurationNameRetentionPolicyBuilder builder = new ShardDurationNameRetentionPolicyBuilder(this.statementBuilder,
		identifier);
	this.statementBuilder.addPolicy(builder.getRetentionPolicy());
	return builder;
    }

    public DefaulNameRetentionPolicyBuilder Default() {
	DefaulNameRetentionPolicyBuilder builder = new DefaulNameRetentionPolicyBuilder(this.statementBuilder);
	this.statementBuilder.addPolicy(builder.getRetentionPolicy());
	return builder;
    }
    
    public NameRetentionPolicyBuilder Name(String identifier) {
	NameRetentionPolicyBuilder builder = new NameRetentionPolicyBuilder(this.statementBuilder, identifier);
	this.statementBuilder.addPolicy(builder.getRetentionPolicy());
	return builder;
    }

}
