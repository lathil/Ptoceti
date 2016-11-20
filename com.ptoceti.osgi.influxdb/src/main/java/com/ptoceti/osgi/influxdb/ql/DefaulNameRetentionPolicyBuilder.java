package com.ptoceti.osgi.influxdb.ql;

public class DefaulNameRetentionPolicyBuilder extends DefaultRetentionPolicyBuilder {

    public DefaulNameRetentionPolicyBuilder(StatementBuilder statementBuilder) {
	super(statementBuilder);

    }

    public NameRetentionPolicyBuilder Name(String identifier) {
	NameRetentionPolicyBuilder builder = new NameRetentionPolicyBuilder(this.statementBuilder, identifier);
	this.statementBuilder.addPolicy(builder.getRetentionPolicy());
	return builder;
    }

}
