package com.ptoceti.osgi.influxdb.ql;

public enum Privilege {

    READ("READ"),
    WRITE("WRITE"),
    ALL("ALL"),
    ALL_PRIVILEGES("ALL PRIVILEGES");

    private String name;

    private Privilege(String name) {
	this.name = name;
    }

    public String getName() {
	return name;
    }
}
