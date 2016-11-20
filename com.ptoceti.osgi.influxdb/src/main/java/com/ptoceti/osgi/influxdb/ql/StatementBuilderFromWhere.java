package com.ptoceti.osgi.influxdb.ql;

/**
 * Statement builder that allows From or Where clauses but no other clauses
 * 
 * @author LATHIL
 * 
 */
public class StatementBuilderFromWhere extends StatementBuilderFrom {

    public StatementBuilderFromWhere(QueryBuilder queryBuilder, Statement statement) {
	super(queryBuilder, statement);
    }

    public StatementBuilderFromWhere(Statement statement) {
	super(statement);
    }

    /**
     * Add a clause that allow From .. Where...
     * 
     * @param identifiers
     * @return
     */
    public FromWhereClauseBuilder From(String... identifiers) {

	FromWhereClauseBuilder from = new FromWhereClauseBuilder(this, identifiers);

	statement.addClause(from.getClause());
	return from;
    }

    /**
     * Add a clause that allow Where...
     * 
     * @param identifiers
     * @return
     */
    public WhereClauseBuilder Where(String... identifiers) {
	WhereClauseBuilder where = new WhereClauseBuilder(this, identifiers);

	statement.addClause(where.getClause());
	return where;
    }
}
