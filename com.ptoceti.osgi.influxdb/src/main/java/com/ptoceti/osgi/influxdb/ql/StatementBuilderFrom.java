package com.ptoceti.osgi.influxdb.ql;

/**
 * Statement builder that allows single From clause
 * 
 * @author LATHIL
 *
 */
public class StatementBuilderFrom extends StatementBuilder {

    public StatementBuilderFrom(QueryBuilder queryBuilder, Statement statement) {
	super(queryBuilder, statement);
    }

    public StatementBuilderFrom(Statement statement) {
	super(statement);
    }

    /**
     * Add simple From clause
     * 
     * @param identifiers
     * @return
     */
    public FromClauseBuilder From(String... identifiers) {

	FromClauseBuilder from = new FromClauseBuilder(this, identifiers);

	statement.addClause(from.getClause());
	return from;
    }

}
