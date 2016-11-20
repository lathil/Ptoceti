package com.ptoceti.osgi.influxdb.ql;

public class WithPasswordClauseBuilder extends ClauseBuilder  {

    public WithPasswordClauseBuilder(StatementBuilder statementBuilder, String password) {
	super(statementBuilder);
	
	// Password must be enclosed with "'"
	Clause clause = new Clause(Clause.ClauseName.WITHPASSWORD);
	StringBuffer quotedPassword = new StringBuffer();
	if( !password.startsWith("\'")){
	    quotedPassword.append("\'");
	}
	quotedPassword.append(password);
	if( !password.endsWith("\'")){
	    quotedPassword.append("\'");
	}
	clause.addIdentifier(quotedPassword.toString());

	this.setClause(clause);
    }

    public WithAllPrivilegeClauseBuilder WhitAllPrivileges() {
	WithAllPrivilegeClauseBuilder clauseBuilder = new WithAllPrivilegeClauseBuilder(this.statementBuilder);
	this.statementBuilder.addClause(clauseBuilder.getClause());
	return clauseBuilder;
    }
    
}
