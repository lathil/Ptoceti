package com.ptoceti.osgi.timeseries.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;
import org.restlet.resource.ResourceException;

import com.ptoceti.influxdb.DatabaseSerie;
import com.ptoceti.influxdb.DurationHelper;
import com.ptoceti.influxdb.Point;
import com.ptoceti.influxdb.PointBuilder;
import com.ptoceti.influxdb.QueryResults;
import com.ptoceti.influxdb.QueryResultsHelper;
import com.ptoceti.influxdb.Serie;
import com.ptoceti.influxdb.client.exception.InfluxDbApiBadrequestException;
import com.ptoceti.influxdb.client.exception.InfluxDbApiNotFoundException;
import com.ptoceti.influxdb.client.exception.InfluxDbTransportException;
import com.ptoceti.influxdb.client.resources.PingResource;
import com.ptoceti.influxdb.client.resources.QueryResource;
import com.ptoceti.influxdb.client.resources.WriteResource;
import com.ptoceti.influxdb.factory.InfluxDbResourceFactory;
import com.ptoceti.influxdb.ql.Query;
import com.ptoceti.influxdb.ql.QueryBuilder;
import com.ptoceti.osgi.obix.contract.HistoryRecord;
import com.ptoceti.osgi.obix.contract.HistoryRollupRecord;
import com.ptoceti.osgi.obix.object.Val;
import com.ptoceti.osgi.timeseries.TimeSeriesService;

public class TimeSeriesServiceImpl implements TimeSeriesService {

    // a reference to the service registration for the Controller object.
    protected ServiceRegistration sReg = null;

    protected InfluxDbResourceFactory influxDbFactory;

    private Timer connectTimer;

    private static final String IMMEDIATDURATIONPOLICYNAME = "immediate";
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

    private long immediatePolicyDurationConverted;
    private long shortPolicyDurationConverted;
    private long mediumPolicyDurationConverted;
    private long longPolicyDurationConverted;

    public TimeSeriesServiceImpl(InfluxDbResourceFactory influxDbFactory2, Dictionary properties) {

	this.influxDbFactory = influxDbFactory2;

	this.immediatePolicyDuration = (String) properties
		.get(InfluxDbFactoryService.TIMESERIESIMMEDIATEPOLICYDURATION);
	this.shortPolicyDuration = (String) properties.get(InfluxDbFactoryService.TIMESERIESSHORTPOLICYDURATION);
	this.mediumPolicyDuration = (String) properties.get(InfluxDbFactoryService.TIMESERIESMEDIUMPOLICYDURATION);
	this.longPolicyDuration = (String) properties.get(InfluxDbFactoryService.TIMESERIESLONGPOLICYDURATION);

	this.immediatePolicyDurationConverted = DurationHelper.convertToMillis(immediatePolicyDuration);
	this.shortPolicyDurationConverted = DurationHelper.convertToMillis(shortPolicyDuration);
	this.mediumPolicyDurationConverted = DurationHelper.convertToMillis(mediumPolicyDuration);
	this.longPolicyDurationConverted = DurationHelper.convertToMillis(longPolicyDuration);

	this.immediateAggregateDuration = (String) properties
		.get(InfluxDbFactoryService.TIMESERIESIMMEDIATEAGGREGATEDURATION);
	this.shortAggregateDuration = (String) properties.get(InfluxDbFactoryService.TIMESERIESSHORTAGGREGATEDURATION);
	this.mediumAggregateDuration = (String) properties
		.get(InfluxDbFactoryService.TIMESERIESMEDIUMAGGREGATEDURATION);

	// check if database server is up
	connectTimer = new Timer();

	class ConnectionTask extends TimerTask {

	    @Override
	    public void run() {
		
		if( !ping()){
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

    private boolean ping() {
	Activator.log(LogService.LOG_INFO, "Pinging InfluxDb database ....");
	PingResource pingResource = this.influxDbFactory.getPingResource();

	boolean result = false;
	try {
	    result = pingResource.ping();
	} catch (InfluxDbApiNotFoundException  ex) {
	    Activator.log(LogService.LOG_ERROR, "Error sending ping to database: " + ex.getError());
	} catch (InfluxDbApiBadrequestException ex){
	    Activator.log(LogService.LOG_ERROR, "Error sending ping to database: " + ex.getError());
	}

	return result;
    }

    /**
     * 
     * Check if the database exists already in the indicated database. If if
     * not, create it with configured retentions.
     * 
     */
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
	} catch (InfluxDbApiNotFoundException ex) {
	    Activator.log(LogService.LOG_ERROR, "Error reading existing database: " + ex.getError() + " for query: "
		    + query.toQL());
	} catch (InfluxDbTransportException ex) {
	    Activator.log(LogService.LOG_ERROR, "Error reading existing database: " + ex.getMessage() + " for query: "
		    + query.toQL());
	} finally {
	    // resource.close();
	}

	if (!foundDatabase) {
	    initializeDataBase();
	}
    }

    /**
     * Initialise databse: - create db with provided name - create retention
     * policy
     * 
     * Database is created with the immediate retention policy being the default
     * one.
     */
    private void initializeDataBase() {
	// Create immediate duration policy

	String errorMessage = "Error creating immediatePolicyDuration duration policy: ";
	QueryResource resource = influxDbFactory.getQueryResource();
	Query query = QueryBuilder.Query().CreateDataBase(influxDbFactory.getDbName()).With()
		.Duration(immediatePolicyDuration).Replication("1").Name(IMMEDIATDURATIONPOLICYNAME).getQuery();
	try {

	    resource.post(query);

	    errorMessage = "Error creating shortPolicyDuration duration policy: ";
	    query = QueryBuilder.Query().CreateRetentionPolicy(SHORTDURATIONPOLICYNAME).On(influxDbFactory.getDbName())
		    .Duration(shortPolicyDuration).Replication("1").getQuery();
	    resource.post(query);

	    errorMessage = "Error creating mediumPolicyDuration duration policy: ";

	    query = QueryBuilder.Query().CreateRetentionPolicy(MEDIUMDURATIONPOLICYNAME)
		    .On(influxDbFactory.getDbName()).Duration(mediumPolicyDuration).Replication("1").getQuery();
	    resource.post(query);

	    errorMessage = "Error creating longPolicyDuration duration policy: ";
	    query = QueryBuilder.Query().CreateRetentionPolicy(LONGDURATIONPOLICYNAME).On(influxDbFactory.getDbName())
		    .Duration(longPolicyDuration).Replication("1").getQuery();
	    resource.post(query);

	} catch (ResourceException ex) {
	    Activator.log(LogService.LOG_ERROR, errorMessage + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Activator.log(LogService.LOG_ERROR, errorMessage + ex.getError() + " for query: " + query.toQL());
	} catch (InfluxDbApiNotFoundException ex) {
	    Activator.log(LogService.LOG_ERROR, errorMessage + ex.getError() + " for query: " + query.toQL());
	} catch (InfluxDbTransportException ex) {
	    Activator.log(LogService.LOG_ERROR, errorMessage + ex.getMessage() + " for query: " + query.toQL());
	}

	if (resource != null) {
	    // resource.close();
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

	// Build first continuous query, transfer data from immediate policy to
	// short policy
	StringBuffer selectShortIdentifiers = new StringBuffer();
	boolean isFirst = true;
	for (String fieldName : fieldNames) {
	    if (!isFirst) {
		selectShortIdentifiers.append(", ");
	    }
	    // selectShortIdentifiers.append(" \"value\"::tag, \"contract\"::tag");
	    selectShortIdentifiers.append("mean( " + fieldName + "::field) AS mean_short_" + fieldName);
	    selectShortIdentifiers.append(", max( " + fieldName + "::field) AS max_short_" + fieldName);
	    selectShortIdentifiers.append(", min( " + fieldName + "::field) AS min_short_" + fieldName);
	    isFirst = false;
	}

	String errorMessage = "Error creating continuous query: ";
	QueryResource resource = influxDbFactory.getQueryResource();

	Query query = QueryBuilder
		.Query()
		.CreateContinuousQuery(escapeIdentifier(measurementName + "_immediate"))
		.On(influxDbFactory.getDbName())
		.Begin(QueryBuilder.Query().Select(selectShortIdentifiers.toString())
			.Into(SHORTDURATIONPOLICYNAME + "." + escapeIdentifier(measurementName))
			.From(IMMEDIATDURATIONPOLICYNAME + "." + escapeIdentifier(measurementName))
			.GroupBy(" time(" + immediateAggregateDuration + "), \"name\"::tag, \"contract\"::tag")
			.getQuery()).End().getQuery();
	QueryResults results = null;

	try {
	    results = resource.post(query);

	    // Build second query, transfer data from short policy to medium
	    // policy.
	    StringBuffer selectMediumIdentifiers = new StringBuffer();
	    isFirst = true;
	    for (String fieldName : fieldNames) {
		if (!isFirst) {
		    selectMediumIdentifiers.append(", ");
		}
		// selectMediumIdentifiers.append(" \"value\"::tag, \"contract\"::tag");
		selectMediumIdentifiers.append("mean(mean_short_" + fieldName + "::field) AS mean_medium_" + fieldName);
		selectMediumIdentifiers.append(", max(max_short_" + fieldName + "::field) AS max_medium_" + fieldName);
		selectMediumIdentifiers.append(", min(min_short_" + fieldName + "::field) AS min_medium_" + fieldName);
		isFirst = false;
	    }

	    errorMessage = "Error creating continuous query: ";
	    query = QueryBuilder
		    .Query()
		    .CreateContinuousQuery(escapeIdentifier(measurementName + "_medium"))
		    .On(influxDbFactory.getDbName())
		    .Begin(QueryBuilder.Query().Select(selectMediumIdentifiers.toString())
			    .Into(MEDIUMDURATIONPOLICYNAME + "." + escapeIdentifier(measurementName))
			    .From(SHORTDURATIONPOLICYNAME + "." + escapeIdentifier(measurementName))
			    .GroupBy("  time(" + mediumAggregateDuration + "), \"name\"::tag, \"contract\"::tag ")
			    .getQuery()).End().getQuery();
	    results = null;

	    results = resource.post(query);
	} catch (ResourceException ex) {
	    Activator.log(LogService.LOG_ERROR, errorMessage + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Activator.log(LogService.LOG_ERROR, errorMessage + ex.getError() + " for query: " + query.toQL());
	} catch (InfluxDbApiNotFoundException ex) {
	    Activator.log(LogService.LOG_ERROR, errorMessage + ex.getError() + " for query: " + query.toQL());
	} catch (InfluxDbTransportException ex) {
	    Activator.log(LogService.LOG_ERROR, errorMessage + ex.getMessage() + " for query: " + query.toQL());
	}

	if (resource != null) {
	    // resource.close();
	}
    }

    public void dropMeasurement(String measurementName) {

	String errorMessage = "Error droppping continuous query: ";
	QueryResource resource = influxDbFactory.getQueryResource();
	Query query = QueryBuilder.Query().DropContinuousQuery(escapeIdentifier(measurementName + "_immediate"))
		.getQuery();
	QueryResults results = null;

	try {
	    results = resource.post(query);

	    errorMessage = "Error droppping continuous query: ";
	    query = QueryBuilder.Query().DropContinuousQuery(escapeIdentifier(measurementName + "_medium")).getQuery();
	    results = null;

	    errorMessage = "Error droppping measurement: ";
	    query = QueryBuilder.Query().DropMeasurement(escapeIdentifier(measurementName)).getQuery();
	    results = null;
	    results = resource.post(query);

	} catch (ResourceException ex) {
	    Activator.log(LogService.LOG_ERROR, errorMessage + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Activator.log(LogService.LOG_ERROR, errorMessage + ex.getError() + " for query: " + query.toQL());
	} catch (InfluxDbApiNotFoundException ex) {
	    Activator.log(LogService.LOG_ERROR, errorMessage + ex.getError() + " for query: " + query.toQL());
	} catch (InfluxDbTransportException ex) {
	    Activator.log(LogService.LOG_ERROR, errorMessage + ex.getMessage() + " for query: " + query.toQL());
	} finally {
	    // resource.close();
	}

    }

    public void saveMeasurementRecord(String measurementName, Val record) {

	// Do not double quote measurement name in line protocol
	PointBuilder pointBuilder = PointBuilder.Point(measurementName)
		.addTag("contract", record.getContract().toUniformString()).addTag("name", record.getName())
		.addField("value", record.getVal());

	WriteResource resource = influxDbFactory.getWriteResource(null, null, null, IMMEDIATDURATIONPOLICYNAME);

	Point point = pointBuilder.getPoint();

	try {
	    resource.write(point);
	} catch (ResourceException ex) {
	    Activator.log(LogService.LOG_ERROR, "Error inserting point: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Activator.log(LogService.LOG_ERROR, "Error inserting point: " + ex);
	} catch (InfluxDbApiNotFoundException ex) {
	    Activator.log(LogService.LOG_ERROR, "Error inserting point: " + ex);
	} catch (InfluxDbTransportException ex) {
	    Activator.log(LogService.LOG_ERROR, "Error inserting point: " + ex);
	} finally {
	    // resource.close();
	}
    }

    /*
     * @param start lower bound
     * 
     * @param end upper bound > start
     * 
     * (non-Javadoc)
     * 
     * @see
     * com.ptoceti.osgi.timeseries.TimeSeriesService#loadMeasurementRollUpRecords
     * (java.lang.String, java.util.Date, java.util.Date, java.lang.Integer)
     */
    public ArrayList<HistoryRollupRecord> loadMeasurementRollUpRecords(String measurementName, Date start, Date end,
	    Integer limit) {

	ArrayList<HistoryRollupRecord> results = new ArrayList<HistoryRollupRecord>();

	Date now = Calendar.getInstance().getTime();
	// default start period
	Date filteredEnd = (end == null ? now : end);
	// default period is short
	String selectedDurationPeriod = SHORTDURATIONPOLICYNAME;
	String selectIdentifiers = "mean_short_value::field AS avg, max_short_value::field AS max, min_short_value::field as min";
	if (start != null) {
	    // choose duration policy to use according to end desired periods
	    long startToMillis = start.getTime();
	    if (startToMillis < (now.getTime() - mediumPolicyDurationConverted)) {
		// not in medium retention policy, must be in long
		selectedDurationPeriod = LONGDURATIONPOLICYNAME;
		selectIdentifiers = "mean_long_value::field AS avg, max_long_value::field AS max, min_long_value::field as min";
	    } else if (startToMillis < (now.getTime() - shortPolicyDurationConverted)) {
		// not in short retention Policy, must be in medium
		selectedDurationPeriod = MEDIUMDURATIONPOLICYNAME;
		selectIdentifiers = "mean_medium_value::field AS avg, max_medium_value::field AS max, min_medium_value::field as min";
	    }
	}

	Query query = QueryBuilder
		.Query()
		.Select(selectIdentifiers)
		.From(selectedDurationPeriod + "." + escapeIdentifier(measurementName))
		.Where("time < " + filteredEnd.getTime() + "ms "
			+ (start != null ? " AND time >= " + start.getTime() + "ms" : "")).OrderBy("time ASC")
		.Limit(limit).getQuery();

	QueryResource resource = influxDbFactory.getQueryResource(query);
	QueryResults queryResults = null;

	try {

	    queryResults = resource.get();
	    if (queryResults != null && queryResults.getResults() != null && queryResults.getResults().size() > 0) {
		Serie serie = QueryResultsHelper.getSerie(0, queryResults.getResults().get(0));
		if (serie != null) {
		    HistoryRollupRecordSerie rollupSerie = new HistoryRollupRecordSerie(serie);

		    HistoryRollupRecord previous = null;
		    while (rollupSerie.hasNext()) {
			HistoryRollupRecord next = rollupSerie.next();
			if (previous != null) {
			    previous.getEnd().setVal(new Date(((Date) next.getStart().getVal()).getTime() - 1));
			}
			results.add(next);
			previous = next;
		    }

		    previous.getEnd().setVal(new Date(end.getTime() - 1));
		}
	    }

	} catch (ResourceException ex) {
	    Activator.log(LogService.LOG_ERROR, "Error Accessing resource: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Activator.log(LogService.LOG_ERROR,
		    "Error Accessing resource: " + ex.getError() + " for query: " + query.toQL());
	} catch (InfluxDbApiNotFoundException ex) {
	    Activator.log(LogService.LOG_ERROR,
		    "Error Accessing resource: " + ex.getError() + " for query: " + query.toQL());
	} catch (InfluxDbTransportException ex) {
	    Activator.log(LogService.LOG_ERROR,
		    "Error Accessing resource: " + ex.getMessage() + " for query: " + query.toQL());
	} finally {
	    // resource.close();
	}

	return results;
    }

    public ArrayList<HistoryRecord> loadMeasurementRecords(String measurementName, Date start, Date end, Integer limit) {

	ArrayList<HistoryRecord> results = new ArrayList<HistoryRecord>();

	Date now = Calendar.getInstance().getTime();
	// default start period
	Date filteredEnd = (end == null ? now : end);
	// default period is short
	String selectedDurationPeriod = SHORTDURATIONPOLICYNAME;
	String selectIdentifiers = "mean_short_value::field AS avg, max_short_value::field AS max, min_short_value::field as min";
	if (start != null) {
	    // choose duration policy to use according to end desired periods
	    long startToMillis = start.getTime();
	    if (startToMillis < (now.getTime() - mediumPolicyDurationConverted)) {
		// not in medium retention policy, must be in long
		selectedDurationPeriod = LONGDURATIONPOLICYNAME;
		selectIdentifiers = "mean_long_value::field AS avg, max_long_value::field AS max, min_long_value::field as min";
	    } else if (startToMillis < (now.getTime() - shortPolicyDurationConverted)) {
		// not in short retention Policy, must be in medium
		selectedDurationPeriod = MEDIUMDURATIONPOLICYNAME;
		selectIdentifiers = "mean_medium_value::field AS avg, max_medium_value::field AS max, min_medium_value::field as min";
	    }
	}

	Query query = QueryBuilder
		.Query()
		.Select("value")
		.From(selectedDurationPeriod + "." + escapeIdentifier(measurementName))
		.Where("time <= " + filteredEnd.getTime() + "ms "
			+ (end != null ? " AND time > " + start.getTime() + "ms" : "")).OrderBy("time ASC")
		.Limit(limit).getQuery();

	QueryResource resource = influxDbFactory.getQueryResource(query);
	QueryResults queryResults = null;

	try {

	    queryResults = resource.get();
	    if (queryResults != null && queryResults.getResults() != null && queryResults.getResults().size() > 0) {
		Serie serie = QueryResultsHelper.getSerie(0, queryResults.getResults().get(0));
		if (serie != null) {
		    HistoryRecordSerie rollupSerie = new HistoryRecordSerie(QueryResultsHelper.getSerie(0, queryResults
			    .getResults().get(0)));

		    while (rollupSerie.hasNext()) {
			results.add(rollupSerie.next());
		    }
		}
	    }

	} catch (ResourceException ex) {
	    Activator.log(LogService.LOG_ERROR, "Error Accessing resource: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Activator.log(LogService.LOG_ERROR,
		    "Error Accessing resource: " + ex.getError() + " for query: " + query.toQL());
	} catch (InfluxDbApiNotFoundException ex) {
	    Activator.log(LogService.LOG_ERROR,
		    "Error Accessing resource: " + ex.getError() + " for query: " + query.toQL());
	} catch (InfluxDbTransportException ex) {
	    Activator.log(LogService.LOG_ERROR,
		    "Error Accessing resource: " + ex.getMessage() + " for query: " + query.toQL());
	} finally {
	    // resource.close();
	}

	return results;
    }

    protected String escapeIdentifier(String identifier) {

	String escapedString = identifier.replace("\\", "\\\\");
	if (!escapedString.startsWith("\"")) {
	    escapedString = "\"" + escapedString;
	}

	if (!escapedString.endsWith("\"")) {
	    escapedString = escapedString + "\"";
	}

	return escapedString;

    }

}
