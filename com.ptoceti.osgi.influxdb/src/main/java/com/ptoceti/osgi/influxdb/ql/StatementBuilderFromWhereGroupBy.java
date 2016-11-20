package com.ptoceti.osgi.influxdb.ql;

/**
 * Statement builder that allow full suite of From...Where...Into...GroupBy...
 * 
 * @author LATHIL
 *
 */
public class StatementBuilderFromWhereGroupBy extends StatementBuilder {

    public StatementBuilderFromWhereGroupBy(QueryBuilder queryBuilder, Statement statement) {
	super(queryBuilder, statement);
    }

    public StatementBuilderFromWhereGroupBy(Statement statement) {
	super(statement);
    }

    /**
     * Add From... clause that also allows furthers clauses such as Where...GroupBy..OrderBy...Limit...
     * 
     * @param identifiers
     * @return
     */
    public FromWhereGroupByClauseBuilder From(String ... identifiers ) {
	
	FromWhereGroupByClauseBuilder from = new  FromWhereGroupByClauseBuilder(this, identifiers);

	statement.addClause(from.getClause());
	return from;
    }

    /**
     * Add Into... clause that also allows furthers From...Where...GroupBy...OrderBy...Limit...
     * 
     * @param identifiers
     * @return
     */
    public IntoClauseBuilder Into(String ... identifiers ) {
	IntoClauseBuilder into = new  IntoClauseBuilder(this, identifiers);

	statement.addClause(into.getClause());
	return into;
    }
}
