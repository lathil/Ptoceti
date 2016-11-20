package com.ptoceti.osgi.influxdb.ql;

/**
 * Statement builder that allows a On.. and To... clauses
 * 
 * @author LATHIL
 *
 */
public class StatementBuilderOnTo extends StatementBuilder  {

    public StatementBuilderOnTo(QueryBuilder queryBuilder, Statement statement) {
	super(queryBuilder, statement);
    }
    
    /**
     * Add a clause that allow On... To...
     * 
     * @param identifiers
     * @return
     */
    public OnToClauseBuilder On(String identifiers) {

	OnToClauseBuilder on = new OnToClauseBuilder(this, identifiers);
	statement.addClause(on.getClause());
	return on;
    }
    
    /**
     * Add a clause that allow To .. 
     * 
     * @param identifiers
     * @return
     */
    public ToClauseBuilder To(String identifiers) {

	ToClauseBuilder to = new ToClauseBuilder(this, identifiers);
	statement.addClause(to.getClause());
	return to;
    }
}
