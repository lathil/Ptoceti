package com.ptoceti.osgi.influxdb.ql;

/**
 * Statement builder that allows a On.. and From... clauses
 * 
 * @author LATHIL
 *
 */
public class StatementBuilderOnFrom extends StatementBuilder {

    public StatementBuilderOnFrom(QueryBuilder queryBuilder, Statement statement) {
	super(queryBuilder, statement);
    }

    /**
     * Add a clause that allow On... From...
     * 
     * @param identifiers
     * @return
     */
    public OnFromClauseBuilder On(String identifiers) {

	OnFromClauseBuilder on = new OnFromClauseBuilder(this, identifiers);
	statement.addClause(on.getClause());
	return on;
    }

    /**
     * Add a clause that allow From ..
     * 
     * @param identifiers
     * @return
     */
    public FromClauseBuilder To(String identifiers) {

	FromClauseBuilder from = new FromClauseBuilder(this, identifiers);
	statement.addClause(from.getClause());
	return from;
    }

}
