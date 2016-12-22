package com.ptoceti.osgi.timeseries.impl;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;
import org.restlet.resource.ResourceException;

import com.ptoceti.osgi.influxdb.DatabaseSerie;
import com.ptoceti.osgi.influxdb.QueryResults;
import com.ptoceti.osgi.influxdb.QueryResultsHelper;
import com.ptoceti.osgi.influxdb.client.exception.InfluxDbApiBadrequestException;
import com.ptoceti.osgi.influxdb.client.resources.PingResource;
import com.ptoceti.osgi.influxdb.client.resources.QueryResource;
import com.ptoceti.osgi.influxdb.impl.factory.restlet.InfluxDbResourceFactory;
import com.ptoceti.osgi.influxdb.ql.Query;
import com.ptoceti.osgi.influxdb.ql.QueryBuilder;
import com.ptoceti.osgi.timeseries.TimeSeriesService;

public class TimeSeriesServiceImpl implements TimeSeriesService {

    // a reference to the service registration for the Controller object.
    protected ServiceRegistration sReg = null;

    protected InfluxDbResourceFactory influxDbFactory;

    private Timer connectTimer;
    private TimerTask connectionTask;

    private static final String IMMEDIATDURATIONPOLICYNAME = "immediat";
    private static final String SHORTDURATIONPOLICYNAME = "short";
    private static final String MEDIUMDURATIONPOLICYNAME = "medium";
    private static final String LONGDURATIONPOLICYNAME = "long";

    private String immediatePolicyDuration;
    private String shortPolicyDuration;
    private String mediumPolicyDuration;
    private String longPolicyDuration;
    
    private String immediateAggregateDuration;
    private String shortAggregateDuration;
    private String mediumAggregateDuration;

    public TimeSeriesServiceImpl(InfluxDbResourceFactory influxDbFactory, Dictionary properties) {

	this.influxDbFactory = influxDbFactory;

	this.immediatePolicyDuration = (String) properties.get(InfluxDbFactoryService.TIMESERIESIMMEDIATEPOLICYDURATION);
	this.shortPolicyDuration = (String) properties.get(InfluxDbFactoryService.TIMESERIESSHORTPOLICYDURATION);
	this.mediumPolicyDuration = (String) properties.get(InfluxDbFactoryService.TIMESERIESMEDIUMPOLICYDURATION);
	this.longPolicyDuration = (String) properties.get(InfluxDbFactoryService.TIMESERIESLONGPOLICYDURATION);
	
	this.immediateAggregateDuration = (String) properties.get(InfluxDbFactoryService.TIMESERIESIMMEDIATEAGGREGATEDURATION);
	this.shortAggregateDuration = (String) properties.get(InfluxDbFactoryService.TIMESERIESSHORTAGGREGATEDURATION);
	this.mediumAggregateDuration = (String) properties.get(InfluxDbFactoryService.TIMESERIESMEDIUMAGGREGATEDURATION);
	    
	
	// check if database server is up
	connectTimer = new Timer();

	class ConnectionTask extends TimerTask {

	    @Override
	    public void run() {
		try {
		    ping();
		} catch (Exception e) {
		    Activator.log(LogService.LOG_INFO, "InfluxDb database not responding trying later ....");
		    connectTimer.schedule(new ConnectionTask(), 10000);
		    return;
		}
		Activator.log(LogService.LOG_INFO, "InfluxDb database ping ok.");
		checkDatabase();
		register();
	    }

	}

	connectTimer.schedule(new ConnectionTask(), 1000);
    }

    private void ping() {
	Activator.log(LogService.LOG_INFO, "Pinging InfluxDb database ....");
	PingResource pingResource = this.influxDbFactory.getPingResource();
	pingResource.ping();
    }

    private void checkDatabase() {
	Query query = QueryBuilder.Query().ShowDataBases().getQuery();
	QueryResource resource = influxDbFactory.getQueryResource(query);
	QueryResults queryresults = null;

	boolean foundDatabase = false;
	try {
	    queryresults = resource.get();
	    if (queryresults.getResults().size() > 0) {
		DatabaseSerie dbNamesSerie = new DatabaseSerie(QueryResultsHelper.getSerie(
			DatabaseSerie.getSerieName(), queryresults.getResults().get(0)));

		for (DatabaseSerie.Database database : dbNamesSerie) {
		    if (database.getName().contains(influxDbFactory.getDbName())) {
			foundDatabase = true;
			break;
		    }
		}
	    }

	} catch (ResourceException ex) {
	    Activator.log(LogService.LOG_ERROR, "Error reading existing database: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Activator.log(LogService.LOG_ERROR, "Error reading existing database: " + ex.getError() + " for query: "
		    + query.toQL());
	}

	if (!foundDatabase) {
	    // Create short duration policy
	    resource = influxDbFactory.getQueryResource();
	    query = QueryBuilder.Query().CreateDataBase(influxDbFactory.getDbName()).With()
		    .Duration(immediatePolicyDuration).Replication("1").Name(IMMEDIATDURATIONPOLICYNAME).getQuery();
	    try {
		resource.post(query);
	    } catch (ResourceException ex) {
		Activator.log(LogService.LOG_ERROR, "Error creating immediatePolicyDuration duration policy: " + ex);
	    } catch (InfluxDbApiBadrequestException ex) {
		Activator.log(LogService.LOG_ERROR,
			"Error creating immediatePolicyDuration duration policy: " + ex.getError() + " for query: "
				+ query.toQL());
	    }

	    try {
		query = QueryBuilder.Query().CreateRetentionPolicy(SHORTDURATIONPOLICYNAME)
			.On(influxDbFactory.getDbName()).Duration(shortPolicyDuration).Replication("1").Default()
			.getQuery();
		resource.post(query);
	    } catch (ResourceException ex) {
		Activator.log(LogService.LOG_ERROR, "Error creating shortPolicyDuration duration policy: " + ex);
	    } catch (InfluxDbApiBadrequestException ex) {
		Activator.log(
			LogService.LOG_ERROR,
			"Error creating shortPolicyDuration duration policy: " + ex.getError() + " for query: "
				+ query.toQL());
	    }

	    try {
		query = QueryBuilder.Query().CreateRetentionPolicy(MEDIUMDURATIONPOLICYNAME)
			.On(influxDbFactory.getDbName()).Duration(mediumPolicyDuration).Replication("1").Default()
			.getQuery();
		resource.post(query);
	    } catch (ResourceException ex) {
		Activator.log(LogService.LOG_ERROR, "Error creating mediumPolicyDuration duration policy: " + ex);
	    } catch (InfluxDbApiBadrequestException ex) {
		Activator.log(LogService.LOG_ERROR,
			"Error creating mediumPolicyDuration duration policy: " + ex.getError() + " for query: "
				+ query.toQL());
	    }

	    try {
		query = QueryBuilder.Query().CreateRetentionPolicy(LONGDURATIONPOLICYNAME)
			.On(influxDbFactory.getDbName()).Duration(longPolicyDuration).Replication("1").Default()
			.getQuery();
		resource.post(query);
	    } catch (ResourceException ex) {
		Activator.log(LogService.LOG_ERROR, "Error creating longPolicyDuration duration policy: " + ex);
	    } catch (InfluxDbApiBadrequestException ex) {
		Activator.log(
			LogService.LOG_ERROR,
			"Error creating longPolicyDuration duration policy: " + ex.getError() + " for query: "
				+ query.toQL());
	    }
	}
    }

    private void register() {
	String[] clazzes = new String[] { TimeSeriesService.class.getName() };
	// register the class as a managed service.
	Hashtable<String, String> properties = new Hashtable<String, String>();
	properties.put(Constants.SERVICE_PID, TimeSeriesService.class.getName());
	sReg = Activator.bc.registerService(clazzes, this, properties);

	Activator
		.log(LogService.LOG_INFO,
			"Registered " + this.getClass().getName() + ", Pid = "
				+ (String) properties.get(Constants.SERVICE_PID));
    }

    @Override
    public void setupMeasurement(String measurementName, String[] fieldNames) {
	// TODO Auto-generated method stub
	
	StringBuffer selectShortIdentifiers = new StringBuffer();
	boolean isFirst = true;
	for( String fieldName : fieldNames){
	    if( !isFirst){
		selectShortIdentifiers.append(", ");
	    }
	    selectShortIdentifiers.append("mean(\"" + fieldName + "::field\") AS \"mean_short_" + fieldName + "\")");
	    selectShortIdentifiers.append(", max(\"" + fieldName + "::field\") AS \"max_short_" + fieldName + "\")");
	    selectShortIdentifiers.append(", min(\"" + fieldName + "::field\") AS \"min_short_" + fieldName + "\")");
	    isFirst = false;
	}
	
	QueryResource resource = influxDbFactory.getQueryResource();
	
	Query query = QueryBuilder.Query().CreateContinuousQuery( measurementName + "_immediate").On(influxDbFactory.getDbName()).Begin(
		QueryBuilder.Query().Select(selectShortIdentifiers.toString()).Into(SHORTDURATIONPOLICYNAME + "." + measurementName).From(IMMEDIATDURATIONPOLICYNAME + "." + measurementName ).GroupBy("time("+ immediateAggregateDuration + ")").getQuery()).End().getQuery();
	QueryResults results = null;
	
	try {
	    results = resource.post(query);
	} catch (ResourceException ex) {
	    Activator
		.log(LogService.LOG_INFO,"Error creating continuous query: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Activator
		.log(LogService.LOG_INFO,"Error creating continuous query: " + ex.getError() + " for query: " + query.toQL());
	}
	
	StringBuffer selectMediumIdentifiers = new StringBuffer();
	isFirst = true;
	for( String fieldName : fieldNames){
	    if( !isFirst){
		selectMediumIdentifiers.append(", ");
	    }
	    selectMediumIdentifiers.append("mean(\"mean_short_" + fieldName + "::field\") AS \"mean_medium_" + fieldName + "\")");
	    selectMediumIdentifiers.append(", max(\"max_short_" + fieldName + "::field\") AS \"max_medium_" + fieldName + "\")");
	    selectMediumIdentifiers.append(", min(\"min_short_" + fieldName + "::field\") AS \"min_medium_" + fieldName + "\")");
	    isFirst = false;
	}
	
	query = QueryBuilder.Query().CreateContinuousQuery( measurementName + "_medium").On(influxDbFactory.getDbName()).Begin(
		QueryBuilder.Query().Select(selectMediumIdentifiers.toString()).Into(MEDIUMDURATIONPOLICYNAME + "." + measurementName).From(SHORTDURATIONPOLICYNAME + "." + measurementName ).GroupBy("time("+ mediumAggregateDuration + ")").getQuery()).End().getQuery();
	results = null;
	
	try {
	    results = resource.post(query);
	} catch (ResourceException ex) {
	    Activator
		.log(LogService.LOG_INFO,"Error creating continuous query: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Activator
		.log(LogService.LOG_INFO,"Error creating continuous query: " + ex.getError() + " for query: " + query.toQL());
	}
    }
    
    public void dropMeasurement(String measurementName){
	
	QueryResource resource = influxDbFactory.getQueryResource();
	Query query = QueryBuilder.Query().DropContinuousQuery( measurementName + "_immediate").getQuery();
	QueryResults results = null;

	try {
	    results = resource.post(query);
	} catch (ResourceException ex) {
	    Activator
		.log(LogService.LOG_INFO,"Error droppping continuous query: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Activator
		.log(LogService.LOG_INFO,"Error dropping continuous query: " + ex.getError() + " for query: " + query.toQL());
	}
	
	query = QueryBuilder.Query().DropContinuousQuery( measurementName + "_medium").getQuery();
	results = null;

	try {
	    results = resource.post(query);
	} catch (ResourceException ex) {
	    Activator
		.log(LogService.LOG_INFO,"Error droppping continuous query: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Activator
		.log(LogService.LOG_INFO,"Error dropping continuous query: " + ex.getError() + " for query: " + query.toQL());
	}
	
	query = QueryBuilder.Query().DropMeasurement(measurementName).getQuery();
	results = null;

	try {
	    results = resource.post(query);
	} catch (ResourceException ex) {
	    Activator
		.log(LogService.LOG_INFO,"Error droppping measurement: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Activator
		.log(LogService.LOG_INFO,"Error dropping measurement: " + ex.getError() + " for query: " + query.toQL());
	}
	
    }
}
