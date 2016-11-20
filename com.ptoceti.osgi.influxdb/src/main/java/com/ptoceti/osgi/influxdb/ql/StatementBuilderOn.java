package com.ptoceti.osgi.influxdb.ql;

/**
 * Statement builder that allows a simple On... clause
 * 
 * @author LATHIL
 *
 */
public class StatementBuilderOn extends StatementBuilder {

    public StatementBuilderOn(QueryBuilder queryBuilder, Statement statement) {
	super(queryBuilder, statement);
    }

    /**
     * Add a clause that allow On .. 
     * 
     * @param identifiers
     * @return
     */
    public OnClauseBuilder On(String identifiers) {

	OnClauseBuilder on = new OnClauseBuilder(this, identifiers);
	statement.addClause(on.getClause());
	return on;
    }
}
