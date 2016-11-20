package com.ptoceti.osgi.influxdb.impl;

import java.util.Dictionary;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.engine.Engine;
import org.restlet.resource.ClientResource;

import com.ptoceti.osgi.influxdb.impl.client.resources.PingResource;
import com.ptoceti.osgi.influxdb.impl.client.resources.QueryResource;
import com.ptoceti.osgi.influxdb.impl.client.resources.WriteResource;
import com.ptoceti.osgi.influxdb.impl.converter.InfluxDbConverter;
import com.ptoceti.osgi.influxdb.ql.Query;

public class InfluxDbResourceFactory {

    private static final String QUERYPARAMNAME = "q";
    private static final String WRITEDBNAMEPARAM = "db";
    private static final String PRECISONPARAMNAME = "precision";
    private static final String RETENTIONPOLICYPARAMNAME = "rp";
    private static final String CONSISTENCYPARAMNAME = "consistency";
    private static final String PASSWORDPARAMNAME = "p";
    private static final String USERPARAMNAME = "u";
    
    
    // the configuration properties
    private Dictionary<String, ?> configProps = null;

    private Application application = null;

    public InfluxDbResourceFactory(Dictionary<String, ?> properties) {

	this.application = new Application();
	Context context = new Context();
	application.setContext(context);

	Engine.getInstance().getRegisteredConverters().add(new InfluxDbConverter());

	this.configProps = properties;
    }

    protected void resetConfig(Dictionary<String, ?> properties) {
	this.configProps = properties;
    }

    /**
     * Configure a resource to write a point in the configured database
     * 
     * curl -i -XPOST 'http://localhost:8086/write?db=mydb' --data-binary
     * 'cpu_load_short,host=server01,region=us-west value=0.64
     * 1434055562000000000'
     * 
     * @param consistency Sets the write consistency for the point
     * @param database Sets the target database for the write.
     * @param password Sets the password for authentication if you’ve enabled authentication
     * @param precision Sets the precision for the supplied Unix time values.
     * @param retentionPolicy Sets the target retention policy for the write
     * @param username Sets the username for authentication if you’ve enabled authentication.
     * @return the configured resource
     */
    protected WriteResource getWriteResource(String consistency, String database, String password, String precision, String retentionPolicy, String username) {

	ClientResource clientResource = new ClientResource((String) configProps.get(InfluxDbServiceImpl.INFLUXDBURL));
	
	if( consistency != null && !consistency.isEmpty()) {
	    clientResource.addQueryParameter(CONSISTENCYPARAMNAME, consistency);
	}
	// insert database name as param
	if( database != null && !database.isEmpty()) {
	    clientResource.addQueryParameter(WRITEDBNAMEPARAM, database);
	} else {
	    clientResource.addQueryParameter(WRITEDBNAMEPARAM, (String) configProps.get(InfluxDbServiceImpl.INFLUXDBNAME));
	}
	
	if( password != null && !password.isEmpty()) {
	    clientResource.addQueryParameter(PASSWORDPARAMNAME, password);
	}
	
	if( precision != null && !precision.isEmpty()) {
	    clientResource.addQueryParameter(PRECISONPARAMNAME, precision);
	}
	
	if( retentionPolicy != null && !retentionPolicy.isEmpty()) {
	    clientResource.addQueryParameter(RETENTIONPOLICYPARAMNAME, retentionPolicy);
	}
	
	if( username != null && !username.isEmpty()) {
	    clientResource.addQueryParameter(USERPARAMNAME, username);
	}
	
	clientResource.addSegment(WriteResource.path);

	WriteResource wResource = clientResource.wrap(WriteResource.class);
	return wResource;

    }
    
    /**
     * 
     * @return
     */
    protected WriteResource getWriteResource() {
	return getWriteResource(null, null, null, null,null, null);
    }

    /**
     * Prepare a resource for sending queries through POST method to the influxdb server.
     * 
     * ex: curl -XPOST 'http://localhost:8086/query' --data-urlencode 'q=CREATE DATABASE "mydb"'
     * 
     * Should be used for any query with statement other that SELECT or SHOW. Statement such as SELECT * INTO  should be send throught POST as well.
     * 
     * @return  QueryResource the result of the query
     */
    protected QueryResource getQueryResource() {

	ClientResource clientResource = new ClientResource((String) configProps.get(InfluxDbServiceImpl.INFLUXDBURL));
	clientResource.addSegment(QueryResource.path);
	QueryResource qResource = clientResource.wrap(QueryResource.class);

	return qResource;

    }

    /**
     * Prepare a query to be sent by GET method to the influxdb server The query must be sent as url-encoded in the query string:
     * 
     * ex: curl -GET 'http://localhost:8086/query?pretty=true' --data-urlencode "db=mydb" --data-urlencode "q=SELECT \"value\" FROM \"cpu_load_short\" WHERE \"region\"='us-west'"
     * 
     * Should normaly only be used for query with SELECT or SHOW statements.
     * 
     * @param query the SELECT or SHOW query
     * @return QueryResource the result of the query
     */
    protected QueryResource getQueryResource(Query query) {

	ClientResource clientResource = new ClientResource((String) configProps.get(InfluxDbServiceImpl.INFLUXDBURL));
	clientResource.addSegment(QueryResource.path);
	clientResource.addQueryParameter(WRITEDBNAMEPARAM, (String) configProps.get(InfluxDbServiceImpl.INFLUXDBNAME));

	clientResource.addQueryParameter(QUERYPARAMNAME, query.toQL());
	QueryResource qResource = clientResource.wrap(QueryResource.class);

	return qResource;

    }

    protected PingResource getPingResource() {

	ClientResource clientResource = new ClientResource((String) configProps.get(InfluxDbServiceImpl.INFLUXDBURL));

	PingResource pResource = clientResource.wrap(PingResource.class);

	return pResource;

    }

}
