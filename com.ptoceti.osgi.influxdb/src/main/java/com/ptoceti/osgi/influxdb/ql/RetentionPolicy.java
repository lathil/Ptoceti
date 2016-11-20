package com.ptoceti.osgi.influxdb.ql;

public class RetentionPolicy {

    String identifier;
    PolicyName policyName;

    public static enum PolicyName {

	DURATION("DURATION"), REPLICATION("REPLICATION"), SHARDDURATION("SHARD DURATION"), DEFAULT("DEFAULT"), NAME(
		"NAME");

	private String name;

	private PolicyName(String name) {
	    this.name = name;
	}

	public String getName() {
	    return name;
	}
    }

    public RetentionPolicy(PolicyName name) {
	this.policyName = name;
    }

    public void addIdentifier(String identifier) {
	this.identifier = identifier;
    }

    public void toQL(StringBuffer qlBuff) {

	qlBuff.append(this.policyName.getName());
	if (identifier != null && !identifier.isEmpty()) {
	    qlBuff.append(" ");
	    qlBuff.append(identifier);
	}

    }

    public String toQL() {
	StringBuffer ql = new StringBuffer();
	toQL(ql);
	return ql.toString();

    }
}
