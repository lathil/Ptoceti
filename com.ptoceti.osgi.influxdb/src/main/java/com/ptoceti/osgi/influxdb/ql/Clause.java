package com.ptoceti.osgi.influxdb.ql;

import java.util.ArrayList;
import java.util.List;

public class Clause {

    
    List<String> identifiers;
    ClauseName clauseName;

    public static enum ClauseName {

	FROM("FROM "),
	GROUPBY("GROUP BY"),
	INTO("INTO"),
	LIMIT("LIMIT"),
	OFFSET("OFFSET"),
	SLIMIT("SLIMIT"),
	SOFFSET("SOFFSET"),
	ON("ON"),
	ORDERBY("ORDER BY"),
	TO("TO"),
	WHERE("WHERE"),
	WITH("WITH"),
	WITHMEASUREMENT("WITH MEASUREMENT"),
	WITHKEY("WITH KEY"),
	WITHPASSWORD("WITH PASSWORD"),
	WITHALLPRIVILEGES("WITH ALL PRIVILEGES"),
	BEGIN("BEGIN"),
	END("END"),
	RESAMPLE("RESAMPLE");
	
	private String name;

	private ClauseName(String name) {
	    this.name = name;
	}
	
	public String getName(){
	    return name;
	}
    }

    public Clause(ClauseName name) {
	this.clauseName = name;
	identifiers = new ArrayList<String>();
    }

    public void addIdentifier(String identifier) {
	identifiers.add(identifier);
    }
    
    public void toQL(StringBuffer qlBuff){
	
	qlBuff.append(this.clauseName.getName());
	boolean firstIdentifier = true;
	for( String identifier: identifiers){
	 // separate first identifier with space
	    if( firstIdentifier) qlBuff.append(" ");
	 // separate identifiers with comma
	    if( !firstIdentifier) qlBuff.append(",");
	    qlBuff.append(identifier);
	    firstIdentifier = false;
	}
    }
    
    
    public String toQL(){
	StringBuffer ql = new StringBuffer();
	toQL(ql);
	return ql.toString();
	
    }

}
