package com.ptoceti.osgi.influxdb.ql;

public class QueryBuilder {

    private Query query;

    public QueryBuilder() {
	query = new Query();
    }

    public static QueryBuilder Query() {
	return new QueryBuilder();
    }

    /**
     * alter_retention_policy_stmt  = "ALTER RETENTION POLICY" policy_name on_clause retention_policy_option
     * [ retention_policy_option ][ retention_policy_option ][ retention_policy_option ] .
     * 
     * -- Set default retention policy for mydb to 1h.cpu.
     * ALTER RETENTION POLICY "1h.cpu" ON "mydb" DEFAULT
     * -- Change duration and replication factor.
     * ALTER RETENTION POLICY "policy1" ON "somedb" DURATION 1h REPLICATION 4
     * 
     * @param identifier
     * @return
     */
    public StatementBuilderOnRetention AlterRetentionPolicy(String identifier) {
	Statement statement = new Statement(Statement.StatementName.ALTERRETENTIONPOLICY);
	statement.addIdentifier(identifier);
	query.addStatement(statement);
	return new StatementBuilderOnRetention(this, statement);
    }

    public StatementBuilderContinuousQuery CreateContinuousQuery(String identifier) {
	Statement statement = new Statement(Statement.StatementName.CREATECONTINUOUSQUERY);
	statement.addIdentifier(identifier);
	query.addStatement(statement);
	return new StatementBuilderContinuousQuery(this, statement);
    }

    /**
     * create_database_stmt = "CREATE DATABASE" db_name [ WITH
     *                      [ retention_policy_duration ]
     *                      [ retention_policy_replication ]
     *                      [ retention_policy_shard_group_duration ]
     *                      [ retention_policy_name ]
     *                  ] .
     *
     * 
     * 
     * -- Create a database called foo
     * CREATE DATABASE "foo"
     * -- Create a database called bar with a new DEFAULT retention policy and specify the duration, replication, shard group duration, and name of that retention policy
     * CREATE DATABASE "bar" WITH DURATION 1d REPLICATION 1 SHARD DURATION 30m NAME "myrp"
     * -- Create a database called mydb with a new DEFAULT retention policy and specify the name of that retention policy
     * CREATE DATABASE "mydb" WITH NAME "myrp"
     * 
     * @param identifier
     * @return
     */
    public StatementBuilderWithRetentionPolicy CreateDataBase(String identifier) {
	Statement statement = new Statement(Statement.StatementName.CREATEDATABASE);
	statement.addIdentifier(identifier);
	query.addStatement(statement);
	return new StatementBuilderWithRetentionPolicy(this, statement);
    }

    /**
     * create_retention_policy_stmt = "CREATE RETENTION POLICY" policy_name on_clause retention_policy_duration retention_policy_replication
     * [ retention_policy_shard_group_duration ][ "DEFAULT" ] .
     * 
     * 
     * -- Create a retention policy.
     * CREATE RETENTION POLICY "10m.events" ON "somedb" DURATION 60m REPLICATION 2
     * -- Create a retention policy and set it as the DEFAULT.
     * CREATE RETENTION POLICY "10m.events" ON "somedb" DURATION 60m REPLICATION 2 DEFAULT
     * -- Create a retention policy and specify the shard group duration.
     * CREATE RETENTION POLICY "10m.events" ON "somedb" DURATION 60m REPLICATION 2 SHARD DURATION 30m
     * 
     * @param identifier
     * @return
     */
    public StatementBuilderOnRetention CreateRetentionPolicy(String identifier) {
	Statement statement = new Statement(Statement.StatementName.CREATERETENTIONPOLICY);
	statement.addIdentifier(identifier);
	query.addStatement(statement);
	return new StatementBuilderOnRetention(this, statement);
    }

    public StatementBuilder CreateSubscription() {
	Statement statement = new Statement(Statement.StatementName.CREATESUBSCRIPTION);
	query.addStatement(statement);
	return new StatementBuilder(this, statement);
    }

    /**
     * create_user_stmt = "CREATE USER" user_name "WITH PASSWORD" password [ "WITH ALL PRIVILEGES" ]
     * 
     * -- Create a normal database user.
     * CREATE USER "jdoe" WITH PASSWORD '1337password'
     * 
     * -- Create an admin user.
     * -- Note: Unlike the GRANT statement, the "PRIVILEGES" keyword is required here.
     * CREATE USER "jdoe" WITH PASSWORD '1337password' WITH ALL PRIVILEGES
     * 
     * @param identifier
     * @return
     */
    public StatementBuilderWithPassword CreateUser(String identifier) {
	Statement statement = new Statement(Statement.StatementName.CREATEUSER);
	statement.addIdentifier(identifier);
	query.addStatement(statement);
	return new StatementBuilderWithPassword(this, statement);
    }

    /**
     * delete_stmt = "DELETE" ( from_clause | where_clause | from_clause where_clause ) .
     * 
     * DELETE FROM "cpu"
     * DELETE FROM "cpu" WHERE time < '2000-01-01T00:00:00Z'
     * DELETE WHERE time < '2000-01-01T00:00:00Z'
     * 
     * @return
     */
    public StatementBuilderFromWhere Delete() {
	Statement statement = new Statement(Statement.StatementName.DELETE);
	query.addStatement(statement);
	return new StatementBuilderFromWhere(this, statement);
    }

    /**
     * drop_continuous_query_stmt = "DROP CONTINUOUS QUERY" query_name on_clause .
     * DROP CONTINUOUS QUERY "myquery" ON "mydb"
     * 
     * @param identifier
     * @return
     */
    public StatementBuilderOn DropContinuousQuery(String identifier) {
	Statement statement = new Statement(Statement.StatementName.DROPCONTINUOUSQUERY);
	statement.addIdentifier(identifier);
	query.addStatement(statement);
	return new StatementBuilderOn(this, statement);
    }

    /**
     * drop_database_stmt = "DROP DATABASE" db_name .
     * DROP DATABASE "mydb"
     * 
     * @param identifier
     * @return
     */
    public StatementBuilder DropDataBase(String identifier) {
	Statement statement = new Statement(Statement.StatementName.DROPDATABASE);
	statement.addIdentifier(identifier);
	query.addStatement(statement);
	return new StatementBuilder(this, statement);
    }

    /**
     * -- drop the cpu measurement
     * DROP MEASUREMENT "cpu"
     * 
     * 
     * @param identifier
     * @return
     */
    public StatementBuilder DropMeasurement(String identifier) {
	Statement statement = new Statement(Statement.StatementName.DROPMEASUREMENT);
	statement.addIdentifier(identifier);
	query.addStatement(statement);
	return new StatementBuilder(this, statement);
    }

    /**
     * -- drop the retention policy named 1h.cpu from mydb
     * DROP RETENTION POLICY "1h.cpu" ON "mydb"
     * 
     * @param identifier
     * @return
     */
    public StatementBuilderOn DropRetentionPolicy(String identifier) {
	Statement statement = new Statement(Statement.StatementName.DROPRETENTIONPOLICY);
	statement.addIdentifier(identifier);
	query.addStatement(statement);
	return new StatementBuilderOn(this, statement);
    }

    /**
     * drop_series_stmt = "DROP SERIES" ( from_clause | where_clause | from_clause where_clause ) .
     * DROP SERIES FROM "telegraf"."autogen"."cpu" WHERE cpu = 'cpu8'
     * 
     * 
     * @param identifier
     * @return
     */
    public StatementBuilderFromWhere DropSeries(String identifier) {
	Statement statement = new Statement(Statement.StatementName.DROPSERIES);
	statement.addIdentifier(identifier);
	query.addStatement(statement);
	return new StatementBuilderFromWhere(this, statement);
    }

    /**
     * drop_shard_stmt = "DROP SHARD" ( shard_id ) .
     * DROP SHARD 1
     * 
     * @param identifier
     * @return
     */
    public StatementBuilder DropShard(String identifier) {
	Statement statement = new Statement(Statement.StatementName.DROPSHARD);
	statement.addIdentifier(identifier);
	query.addStatement(statement);
	return new StatementBuilder(this, statement);
    }

    /**
     * -- drop_subscription_stmt = "DROP SUBSCRIPTION" subscription_name "ON" db_name "." retention_policy .
     * DROP SUBSCRIPTION "sub0" ON "mydb"."autogen"
     * 
     * @param identifier
     * @return
     */
    public StatementBuilderOn DropSubscription(String identifier) {
	Statement statement = new Statement(Statement.StatementName.DROPSUBSCRIPTION);
	statement.addIdentifier(identifier);
	query.addStatement(statement);
	return new StatementBuilderOn(this, statement);
    }

    /**
     * drop_user_stmt = "DROP USER" user_name .
     * DROP USER "jdoe"
     * 
     * @param identifier
     * @return
     */
    public StatementBuilder DropUser(String identifier) {
	Statement statement = new Statement(Statement.StatementName.DROPUSER);
	statement.addIdentifier(identifier);
	query.addStatement(statement);
	return new StatementBuilder(this, statement);
    }

    /**
     * grant_stmt = "GRANT" privilege [ on_clause ] to_clause .
     * 
     * -- grant admin privileges
     * GRANT ALL TO "jdoe"
     * -- grant read access to a database
     * GRANT READ ON "mydb" TO "jdoe"
     * 
     * @param privilege
     * @return
     */
    public StatementBuilderOnTo Grant(String privilege) {
	Statement statement = new Statement(Statement.StatementName.GRANT);
	statement.addIdentifier(privilege);
	query.addStatement(statement);
	return new StatementBuilderOnTo(this, statement);
    }

    /**
     * kill_query_statement = "KILL QUERY" query_id .
     * --- kill a query with the query_id 36
     * KILL QUERY 36

     * @param identifier
     * @return
     */
    public StatementBuilder KillQuery(String identifier) {
	Statement statement = new Statement(Statement.StatementName.KILLQUERY);
	statement.addIdentifier(identifier);
	query.addStatement(statement);
	return new StatementBuilder(this, statement);
    }

    /**
     * revoke_stmt = "REVOKE" privilege [ on_clause ] "FROM" user_name .
     * 
     * -- revoke admin privileges from jdoe
     * REVOKE ALL PRIVILEGES FROM "jdoe"
     * -- revoke read privileges from jdoe on mydb
     * REVOKE READ ON "mydb" FROM "jdoe"
     * 
     * @param privilege
     * @return
     */
    public StatementBuilderOnFrom Revoke(String privilege) {
	Statement statement = new Statement(Statement.StatementName.REVOKE);
	statement.addIdentifier(privilege);
	query.addStatement(statement);
	return new StatementBuilderOnFrom(this, statement);
    }

    /**
     * show_continuous_queries_stmt = "SHOW CONTINUOUS QUERIES" .
     * -- show all continuous queries
     * SHOW CONTINUOUS QUERIES
     * 
     * @return
     */
    public StatementBuilder ShowContinuousQuery() {
	Statement statement = new Statement(Statement.StatementName.SHOWCONTINUOUSQUERIES);
	query.addStatement(statement);
	return new StatementBuilder(this, statement);
    }

    /**
     * show_databases_stmt = "SHOW DATABASES" .
     * -- show all databases
     * SHOW DATABASES
     * 
     * @return
     */
    public StatementBuilder ShowDataBases() {
	Statement statement = new Statement(Statement.StatementName.SHOWDATABASE);
	query.addStatement(statement);
	return new StatementBuilder(this, statement);
    }

    /**
     * show_field_keys_stmt = "SHOW FIELD KEYS" [ from_clause ] .
     * 
     * -- show field keys and field value data types from all measurements
     * SHOW FIELD KEYS
     * -- show field keys and field value data types from specified measurement
     * SHOW FIELD KEYS FROM "cpu"
     * 
     * @return
     */
    public StatementBuilderFrom ShowFieldKeys() {
	Statement statement = new Statement(Statement.StatementName.SHOWFIELDKEYS);
	query.addStatement(statement);
	return new StatementBuilderFrom(this, statement);
    }

    /**
     * show_grants_stmt = "SHOW GRANTS FOR" user_name .
     * -- show grants for jdoe
     * SHOW GRANTS FOR "jdoe"
     * 
     * @return
     */
    public StatementBuilder ShowGrantsFor(String identifier) {
	Statement statement = new Statement(Statement.StatementName.SHOWGRANTSFOR);
	statement.addIdentifier(identifier);
	query.addStatement(statement);
	return new StatementBuilder(this, statement);
    }

    /**
     * show_measurements_stmt = "SHOW MEASUREMENTS" [ with_measurement_clause ] [ where_clause ] [ limit_clause ] [ offset_clause ] .
     * 
     * -- show all measurements
     * SHOW MEASUREMENTS
     * -- show measurements where region tag = 'uswest' AND host tag = 'serverA'
     * SHOW MEASUREMENTS WHERE "region" = 'uswest' AND "host" = 'serverA'
     * -- show measurements that start with 'h2o'
     * SHOW MEASUREMENTS WITH MEASUREMENT =~ /h2o.
     * 
     * @return
     */
    public StatementBuilderWithMeasurement ShowMeasurement() {
	Statement statement = new Statement(Statement.StatementName.SHOWMEASUREMENTS);
	query.addStatement(statement);
	return new StatementBuilderWithMeasurement(this, statement);
    }

    /**
     * show_queries_stmt = "SHOW QUERIES" .
     * -- show all currently-running queries
     * SHOW QUERIES
     * 
     * @return
     */
    public StatementBuilder ShowQueries() {
	Statement statement = new Statement(Statement.StatementName.SHOWQUERIES);
	query.addStatement(statement);
	return new StatementBuilder(this, statement);
    }

    /**
     * -- show all retention policies on a database
     * SHOW RETENTION POLICIES ON "mydb"
     * 
     * @return
     */
    public StatementBuilderOn ShowRetentionPolicies() {
	Statement statement = new Statement(Statement.StatementName.SHOWRETENTIONPOLICIES);
	query.addStatement(statement);
	return new StatementBuilderOn(this, statement);
    }

    /**
     * show_series_stmt = "SHOW SERIES" [ from_clause ] [ where_clause ] [ limit_clause ] [ offset_clause ] .
     * SHOW SERIES FROM "telegraf"."autogen"."cpu" WHERE cpu = 'cpu8'
     * 
     * @return
     */
    public StatementBuilderFromWhereLimitOffset ShowSeries() {
	Statement statement = new Statement(Statement.StatementName.SHOWSERIES);
	query.addStatement(statement);
	return new StatementBuilderFromWhereLimitOffset(this, statement);
    }

    /**
     * show_shard_groups_stmt = "SHOW SHARD GROUPS" .
     * SHOW SHARD GROUPS
     * 
     * @return
     */
    public StatementBuilder ShowShardGroups() {
	Statement statement = new Statement(Statement.StatementName.SHOWSHARDGROUPS);
	query.addStatement(statement);
	return new StatementBuilder(this, statement);
    }

    /**
     * show_shards_stmt = "SHOW SHARDS" .
     * SHOW SHARDS
     * 
     * @return
     */
    public StatementBuilder ShowShards() {
	Statement statement = new Statement(Statement.StatementName.SHOWSHARDS);
	query.addStatement(statement);
	return new StatementBuilder(this, statement);
    }

    /**
     * show_subscriptions_stmt = "SHOW SUBSCRIPTIONS" .
     * SHOW SUBSCRIPTIONS
     * 
     * @return
     */
    public StatementBuilder ShowSubscription() {
	Statement statement = new Statement(Statement.StatementName.SHOWSUBSCRIPTIONS);
	query.addStatement(statement);
	return new StatementBuilder(this, statement);
    }

    /**
     * show_tag_keys_stmt = "SHOW TAG KEYS" [ from_clause ] [ where_clause ] [ group_by_clause ][ limit_clause ] [ offset_clause ] .
     * 
     * -- show all tag keys
     * SHOW TAG KEYS
     * -- show all tag keys from the cpu measurement
     * SHOW TAG KEYS FROM "cpu"
     * -- show all tag keys from the cpu measurement where the region key = 'uswest'
     * SHOW TAG KEYS FROM "cpu" WHERE "region" = 'uswest'
     * -- show all tag keys where the host key = 'serverA'
     * SHOW TAG KEYS WHERE "host" = 'serverA'
     * 
     * @return
     */
    public StatementBuilderFromWhereGroupByLimitOffset ShowTagKeys() {
	Statement statement = new Statement(Statement.StatementName.SHOWTAGKEYS);
	query.addStatement(statement);
	return new StatementBuilderFromWhereGroupByLimitOffset(this, statement);
    }

    /**
     * show_tag_values_stmt = "SHOW TAG VALUES" [ from_clause ] with_tag_clause [ where_clause ][ group_by_clause ] [ limit_clause ] [ offset_clause ] .
     * 
     * -- show all tag values across all measurements for the region tag
     * SHOW TAG VALUES WITH KEY = "region"
     * -- show tag values from the cpu measurement for the region tag
     * SHOW TAG VALUES FROM "cpu" WITH KEY = "region"
     * -- show tag values across all measurements for all tag keys that do not include the letter c
     * SHOW TAG VALUES WITH KEY !~ /.*c.
     * -- show tag values from the cpu measurement for region & host tag keys where service = 'redis'
     * SHOW TAG VALUES FROM "cpu" WITH KEY IN ("region", "host") WHERE "service" = 'redis'
     * 
     * @return
     */
    public StatementBuilderFromWithKey ShowTagsValue() {
	Statement statement = new Statement(Statement.StatementName.SHOWTAGVALUES);
	query.addStatement(statement);
	return new StatementBuilderFromWithKey(this, statement);
    }

    /**
     * show_users_stmt = "SHOW USERS" .
     * -- show all users
     * SHOW USERS
     * 
     * @return
     */
    public StatementBuilder ShowUsers() {
	Statement statement = new Statement(Statement.StatementName.SHOWUSERS);
	query.addStatement(statement);
	return new StatementBuilder(this, statement);
    }

    /**
     * select_stmt = "SELECT" fields from_clause [ into_clause ] [ where_clause ]
     * [ group_by_clause ] [ order_by_clause ] [ limit_clause ]
     * [ offset_clause ] [ slimit_clause ] [ soffset_clause ] .
     * 
     * 
     * -- select mean value from the cpu measurement where region = 'uswest' grouped by 10 minute intervals
     * SELECT mean("value") FROM "cpu" WHERE "region" = 'uswest' GROUP BY time(10m) fill(0
     * -- select from all measurements beginning with cpu into the same measurement name in the cpu_1h retention policy
     * SELECT mean("value") INTO "cpu_1h".:MEASUREMENT FROM cpu.
     * 
     * @param identifiers
     * @return
     */
    public StatementBuilderFromWhereGroupBy Select(String... identifiers) {
	Statement statement = new Statement(Statement.StatementName.SELECT);
	for (String identifier : identifiers) {
	    statement.addIdentifier(identifier);
	}

	query.addStatement(statement);
	return new StatementBuilderFromWhereGroupBy(this, statement);
    }

    public Query getQuery() {
	return query;
    }
}
