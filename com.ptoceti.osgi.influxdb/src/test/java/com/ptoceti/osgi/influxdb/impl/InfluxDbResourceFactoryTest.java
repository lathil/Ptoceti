package com.ptoceti.osgi.influxdb.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runners.MethodSorters;
import org.restlet.resource.ResourceException;

import com.ptoceti.osgi.influxdb.Batch;
import com.ptoceti.osgi.influxdb.BatchBuilder;
import com.ptoceti.osgi.influxdb.DatabaseSerie;
import com.ptoceti.osgi.influxdb.MeasurementSerie;
import com.ptoceti.osgi.influxdb.Point;
import com.ptoceti.osgi.influxdb.PointBuilder;
import com.ptoceti.osgi.influxdb.QueryResults;
import com.ptoceti.osgi.influxdb.QueryResultsHelper;
import com.ptoceti.osgi.influxdb.Result;
import com.ptoceti.osgi.influxdb.RetentionPolicySerie;
import com.ptoceti.osgi.influxdb.Serie;
import com.ptoceti.osgi.influxdb.SerieWrapper;
import com.ptoceti.osgi.influxdb.impl.InfluxDbResourceFactory;
import com.ptoceti.osgi.influxdb.impl.InfluxDbServiceImpl;
import com.ptoceti.osgi.influxdb.impl.client.resources.InfluxDbApiBadrequestException;
import com.ptoceti.osgi.influxdb.impl.client.resources.InfluxDbApiNotFoundException;
import com.ptoceti.osgi.influxdb.impl.client.resources.QueryResource;
import com.ptoceti.osgi.influxdb.impl.client.resources.WriteResource;
import com.ptoceti.osgi.influxdb.impl.converter.LineProtocol;
import com.ptoceti.osgi.influxdb.ql.Privilege;
import com.ptoceti.osgi.influxdb.ql.Query;
import com.ptoceti.osgi.influxdb.ql.QueryBuilder;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InfluxDbResourceFactoryTest {

    public static final String TESTDATABASENAME = "test";
    public static final String TESTUSER1 = "testuser1";
    public static final String TESTUSER1PASSWORD = "testuser1password";
    public static final String TESTADMINUSER = "testadminuser";
    public static final String TESTADMINUSERPASSWORD = "testadminuserpassword";
    public static final String TESTRETENTIONPOLICY100WNAME = "policy100w";
    public static final String TESTRETENTIONPOLICY1WNAME = "policy1w";

    // The factory use during the tests for creating endpoints
    protected static InfluxDbResourceFactory factory;

    @BeforeClass
    public static void setup() {

	Dictionary<String, Object> properties = new Hashtable<String, Object>();
	properties.put(InfluxDbServiceImpl.INFLUXDBURL, "http://127.0.0.1:8086");
	properties.put(InfluxDbServiceImpl.INFLUXDBNAME, TESTDATABASENAME);
	factory = new InfluxDbResourceFactory(properties);

    }

    @AfterClass
    public static void tearDown() {

    }

    /**
     * Settup test Database
     * 
     */
    @Test
    public void test1000createTestDataBase() {

	// first get list of already exixting databases in the server
	Query query = QueryBuilder.Query().ShowDataBases().getQuery();
	QueryResource resource = factory.getQueryResource(query);
	QueryResults queryresults = null;

	try {
	    queryresults = resource.get();
	    if (queryresults.getResults().size() > 0) {
		DatabaseSerie dbNamesSerie = new DatabaseSerie(QueryResultsHelper.getSerie(
			DatabaseSerie.getSerieName(), queryresults.getResults().get(0)));

		for (DatabaseSerie.Database database : dbNamesSerie) {
		    if (database.getName().contains(TESTDATABASENAME)) {
			return;
		    }
		}

	    }

	} catch (ResourceException ex) {
	    Assert.fail("Error reading database: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Assert.fail("Error reading database: " + ex.getError() + " for query: " + query.toQL());
	}

	resource = factory.getQueryResource();
	query = QueryBuilder.Query().CreateDataBase(TESTDATABASENAME).With().Duration("1w").Replication("1")
		.Name(TESTRETENTIONPOLICY1WNAME).getQuery();

	try {
	    resource.post(query);
	} catch (ResourceException ex) {
	    Assert.fail("Error creating database: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Assert.fail("Error creating database: " + ex.getError() + " for query: " + query.toQL());
	}

    }

    @Test
    public void test1002CreateRetentionPolicy() {

	QueryResource resource = factory.getQueryResource();
	Query query = QueryBuilder.Query().CreateRetentionPolicy(TESTRETENTIONPOLICY100WNAME).On(TESTDATABASENAME)
		.Duration("100w").Replication("1").Default().getQuery();
	QueryResults results = null;

	try {
	    results = resource.post(query);
	} catch (ResourceException ex) {
	    Assert.fail("Error creating retention policy 1: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Assert.fail("Error creating retention policy 1: " + ex.getError() + " for query: " + query.toQL());
	}
    }

    @Test
    public void test1003ModifyRetentionPolicy() {
	QueryResource resource = factory.getQueryResource();
	Query query = QueryBuilder.Query().AlterRetentionPolicy(TESTRETENTIONPOLICY1WNAME).On(TESTDATABASENAME)
		.Duration("10w").Replication("1").getQuery();
	QueryResults results = null;

	try {
	    results = resource.post(query);
	} catch (ResourceException ex) {
	    Assert.fail("Error creating retention policy 1: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Assert.fail("Error creating retention policy 1: " + ex.getError() + " for query: " + query.toQL());
	}

	query = QueryBuilder.Query().ShowRetentionPolicies().On(TESTDATABASENAME).getQuery();
	try {
	    results = resource.post(query);
	} catch (ResourceException ex) {
	    Assert.fail("Error getting list of retention policies: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Assert.fail("Error getting list of retention policies: " + ex.getError() + " for query: " + query.toQL());
	}

	if (results.getResults().size() > 0) {
	    RetentionPolicySerie retentionSerie = new RetentionPolicySerie(QueryResultsHelper.getSerie(0, results
		    .getResults().get(0)));

	    Assert.assertEquals("Number of retention policies does not matches", 2, retentionSerie.size());

	}

    }

    /**
     * Create 2 users for the test and assign privileges.
     * 
     * 
     */
    @Test
    public void test1004CreateUser() {

	QueryResource resource = factory.getQueryResource();
	Query query = QueryBuilder.Query().CreateUser(TESTUSER1).WithPassword(TESTUSER1PASSWORD).getQuery();
	QueryResults results = null;

	try {
	    results = resource.post(query);
	} catch (ResourceException ex) {
	    Assert.fail("Error creating test user1: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Assert.fail("Error creating test user1: " + ex.getError() + " for query: " + query.toQL());
	}

	query = QueryBuilder.Query().Grant(Privilege.ALL.getName()).On(TESTDATABASENAME).To(TESTUSER1).getQuery();
	try {
	    results = resource.post(query);
	} catch (ResourceException ex) {
	    Assert.fail("Error adding privileges to test user1: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Assert.fail("Error adding privileges to test user1: " + ex.getError() + " for query: " + query.toQL());
	}

	query = QueryBuilder.Query().CreateUser(TESTADMINUSER).WithPassword(TESTADMINUSERPASSWORD).WhitAllPrivileges()
		.getQuery();
	try {
	    results = resource.post(query);
	} catch (ResourceException ex) {
	    Assert.fail("Error creating admin user: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Assert.fail("Error creating admin user:: " + ex.getError() + " for query: " + query.toQL());
	}

	query = QueryBuilder.Query().ShowUsers().getQuery();
	try {
	    results = resource.post(query);
	} catch (ResourceException ex) {
	    Assert.fail("Error getting list of users: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Assert.fail("Error getting list of users " + ex.getError() + " for query: " + query.toQL());
	}
    }

    @Test
    public void test1004CreateContinuousQuery(){
	QueryResource resource = factory.getQueryResource();
	Query query = QueryBuilder.Query().CreateContinuousQuery("1h_WaterLevel").On(TESTDATABASENAME).Begin(
		QueryBuilder.Query().Select("").Into("").From("").GroupBy("").getQuery()).End().getQuery();
	QueryResults results = null;

	try {
	    results = resource.post(query);
	} catch (ResourceException ex) {
	    Assert.fail("Error creating continuous query: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Assert.fail("Error creating continuous query: " + ex.getError() + " for query: " + query.toQL());
	}
    }
    
    
    /**
     * Drop the two users and theirs privileges created for the test
     * 
     */
    @Test
    public void test3000DeleteUser() {

	QueryResource resource = factory.getQueryResource();

	Query query = QueryBuilder.Query().Revoke(Privilege.ALL.getName()).On(TESTDATABASENAME).From(TESTUSER1)
		.getQuery();
	try {
	    resource.post(query);
	} catch (ResourceException ex) {
	    Assert.fail("Error droping privileges from test user1: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Assert.fail("Error droping privileges from test user1: " + ex.getError() + " for query: " + query.toQL());
	}

	query = QueryBuilder.Query().DropUser(TESTUSER1).getQuery();
	try {
	    resource.post(query);
	} catch (ResourceException ex) {
	    Assert.fail("Error deleting test user1: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Assert.fail("deleting test user1: " + ex.getError() + " for query: " + query.toQL());
	}

	query = QueryBuilder.Query().DropUser(TESTADMINUSER).getQuery();

	try {
	    resource.post(query);
	} catch (ResourceException ex) {
	    Assert.fail("Error deleting admin user: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Assert.fail("Error deleting admin user: " + ex.getError() + " for query: " + query.toQL());
	}

    }

    @Test
    public void test3001DropRetentionPolicy() {
	QueryResource resource = factory.getQueryResource();
	Query query = QueryBuilder.Query().DropRetentionPolicy(TESTRETENTIONPOLICY100WNAME).On(TESTDATABASENAME)
		.getQuery();
	QueryResults results = null;

	try {
	    results = resource.post(query);
	} catch (ResourceException ex) {
	    Assert.fail("Error droping retention policy 1: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Assert.fail("Error droping retention policy 1: " + ex.getError() + " for query: " + query.toQL());
	}

	resource = factory.getQueryResource();
	query = QueryBuilder.Query().DropRetentionPolicy(TESTRETENTIONPOLICY1WNAME).On(TESTDATABASENAME).getQuery();

	try {
	    results = resource.post(query);
	} catch (ResourceException ex) {
	    Assert.fail("Error droping retention policy 2: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Assert.fail("Error droping retention policy 2: " + ex.getError() + " for query: " + query.toQL());
	}
    }

    /**
     * Delete the test database
     * 
     */
    @Test
    public void test3002deleteTestDataBase() {

	QueryResource resource = factory.getQueryResource();
	Query query = QueryBuilder.Query().DropDataBase(TESTDATABASENAME).getQuery();

	try {
	    resource.post(query);
	} catch (ResourceException ex) {
	    Assert.fail("Error Accessing resource: " + ex);
	}
    }

    @Test
    public void test2000injectNOAATestDatabase() {

	WriteResource resource = factory.getWriteResource(null, null, null, null, TESTRETENTIONPOLICY100WNAME, null);

	URL url = getClass().getClassLoader().getResource("NOAA_data.txt");
	Path resPath;
	try {
	    resPath = Paths.get(url.toURI());
	    String batchLp = new String(Files.readAllBytes(resPath), "UTF8");
	    String[] pointsLp = batchLp.split("\r\n");
	    LineProtocol lineProtocol = new LineProtocol();

	    int count = 0;
	    Batch batch = new Batch();
	    boolean isfirst = true;
	    for (String pointLp : pointsLp) {

		batch.addPoint(lineProtocol.toPoint(pointLp + "000"));
		count++;

		if (isfirst) {
		    long firstPointTimestamp = batch.getPoints().get(0).getTimestamp();
		    Date timestamp = new Date(firstPointTimestamp);
		    System.out.println(timestamp.toString());
		}

		if (count > 99) {
		    resource.write(batch);
		    count = 0;
		    batch = new Batch();
		}

		isfirst = false;

	    }
	    if (count > 0) {
		resource.write(batch);
	    }

	} catch (URISyntaxException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (UnsupportedEncodingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (ResourceException ex) {
	    Assert.fail("Error injecting batch points: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Assert.fail("Error injecting batch points: " + ex.getError());
	}

    }

    @Test
    public void test2001ShowMeasurements() {
	Query query = QueryBuilder.Query().ShowMeasurement().getQuery();
	QueryResource resource = factory.getQueryResource(query);
	QueryResults queryResults = null;

	try {
	    queryResults = resource.get();

	    MeasurementSerie measurementSerie = new MeasurementSerie(QueryResultsHelper.getSerie(
		    MeasurementSerie.getSerieName(), queryResults.getResults().get(0)));

	    Assert.assertEquals("Number of mesurements not as expected: ", 5, measurementSerie.size());

	} catch (ResourceException ex) {
	    Assert.fail("Error getting measurements: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Assert.fail("Error getting measurements: " + ex.getError() + " for query: " + query.toQL());
	}
    }
    
    @Test
    public void test2002ShowSeries(){
	
	Query query = QueryBuilder.Query().ShowSeries().From("h2o_feet").getQuery();
	QueryResource resource = factory.getQueryResource(query);
	
	try {
	    QueryResults queryResults = resource.get();

	    SerieWrapper serie = new SerieWrapper(QueryResultsHelper.getSerie(0, queryResults.getResults().get(0)));
	    
	    int nbSerie = serie.size();
	    Assert.assertEquals("nb series not as expected: ", true, nbSerie > 0);
	} catch (ResourceException ex) {
	    Assert.fail("Error getting series: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Assert.fail("Error getting series: " + ex.getError() + " for query: " + query.toQL());
	}
    }
    
    @Test
    public void test2003ShowTagKeys(){
	
	Query query = QueryBuilder.Query().ShowTagKeys().From("h2o_feet").getQuery();
	QueryResource resource = factory.getQueryResource(query);
	
	try {
	    QueryResults queryResults = resource.get();

	    SerieWrapper serie = new SerieWrapper(QueryResultsHelper.getSerie(0, queryResults.getResults().get(0)));
	    
	    int nbSerie = serie.size();
	    Assert.assertEquals("nb tag keys not as expected: ", true, nbSerie > 0);
	} catch (ResourceException ex) {
	    Assert.fail("Error getting tag keys: " + ex);
	} catch (InfluxDbApiNotFoundException ex) {
	    Assert.fail("Error getting tag keys: " + ex.getError() + " for query: " + query.toQL());
	} catch (InfluxDbApiBadrequestException ex) {
	    Assert.fail("Error getting tag keys: " + ex.getError() + " for query: " + query.toQL());
	}
    }
    
    @Test
    public void test2004ShowFieldsKeys(){
	Query query = QueryBuilder.Query().ShowFieldKeys().From("h2o_feet").getQuery();
	QueryResource resource = factory.getQueryResource(query);
	
	try {
	    QueryResults queryResults = resource.get();

	    SerieWrapper serie = new SerieWrapper(QueryResultsHelper.getSerie(0, queryResults.getResults().get(0)));
	    
	    int nbSerie = serie.size();
	    Assert.assertEquals("nb field keys not as expected: ", true, nbSerie > 0);
	} catch (ResourceException ex) {
	    Assert.fail("Error getting field keys: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Assert.fail("Error getting field keys: " + ex.getError() + " for query: " + query.toQL());
	}
    }

    @Test
    public void test2005ShowTagValues(){
	Query query = QueryBuilder.Query().ShowTagsValue().From("h2o_feet").WithKey(" = location").getQuery();
	QueryResource resource = factory.getQueryResource(query);
	
	try {
	    QueryResults queryResults = resource.get();

	    SerieWrapper serie = new SerieWrapper(QueryResultsHelper.getSerie(0, queryResults.getResults().get(0)));
	    
	    int nbSerie = serie.size();
	    Assert.assertEquals("nb values not as expected: ", true, nbSerie > 0);
	    
	} catch (ResourceException ex) {
	    Assert.fail("Error getting tag values: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Assert.fail("Error getting tag values: " + ex.getError() + " for query: " + query.toQL());
	}
    }
    
    @Test
    public void test2005WritePointDataBase() {

	WriteResource resource = factory.getWriteResource();

	Point point = PointBuilder.Point("testmeasurement").addTag("testTag", "tag1").addField("testfield", 1)
		    .addField("testfield2", 1).getPoint();
	try {
	    resource.write(point);
	} catch (ResourceException ex) {
	    Assert.fail("Error injecting  points: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Assert.fail("Error injecting  points: " + ex.getError());
	}

    }

    @Test
    public void test2006WriteBatchDataBase() {

	WriteResource resource = factory.getWriteResource();

	Batch batch = BatchBuilder.Batch().point("testmeasurement").addTag("testTag", "tag1")
		    .addField("testfield", 2).addField("testfield2", 2).add().point("testmeasurement")
		    .addTag("testTag", "tag1").addField("testfield", 3).addField("testfield2", 3).add().getBatch();
	try {
	    resource.write(batch);
	} catch (ResourceException ex) {
	    Assert.fail("Error injecting  batch:: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Assert.fail("Error injecting  batch: " + ex.getError());
	}

    }

    @Test
    public void test2007QuerySelectDataBase() {

	Query query = QueryBuilder.Query().Select("*").From("testmeasurement").Where("testTag = 'tag1'").getQuery();
	QueryResource resource = factory.getQueryResource(query);
	QueryResults queryResults = null;

	try {
	    queryResults = resource.get();
	    checkResults(queryResults, "testmeasurement", 4, 2, 4);
	} catch (ResourceException ex) {
	    Assert.fail("Error Accessing resource: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Assert.fail("Error Accessing resource: " + ex.getError() + " for query: " + query.toQL());
	}

	query = QueryBuilder.Query().Select("*").From("testmeasurement").Where("testTag = 'tag1'").GroupBy("testTag")
		.getQuery();
	resource = factory.getQueryResource(query);
	try {
	    queryResults = resource.get();
	    // groupby testTag, this one not resulting columns
	    checkResults(queryResults, "testmeasurement", 3, 2, 3);
	} catch (ResourceException ex) {
	    Assert.fail("Error Accessing resource: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Assert.fail("Error Accessing resource: " + ex.getError() + " for query: " + query.toQL());
	}

    }

    @Test
    public void test2008QuerySelectDataBase() {

	Query query = QueryBuilder.Query().Select("testfield", "testfield2").From("testmeasurement")
		.Where("testTag = 'tag1'").getQuery();
	QueryResource resource = factory.getQueryResource(query);

	QueryResults queryResults = null;

	try {
	    queryResults = resource.get();
	    checkResults(queryResults, "testmeasurement", 3, 2, 3);
	} catch (ResourceException ex) {
	    Assert.fail("Error Accessing resource: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Assert.fail("Error Accessing resource: " + ex.getError() + " for query: " + query.toQL());
	}

    }
    
    @Test
    public void test2009QuerySelectNOAADatabase(){
	Query query = QueryBuilder.Query().Select("*").From("h2o_feet").Where("location = 'coyote_creek'").getQuery();
	QueryResource resource = factory.getQueryResource(query);

	QueryResults queryResults = null;

	try {
	    queryResults = resource.get();
	    SerieWrapper serie = new SerieWrapper(QueryResultsHelper.getSerie(0, queryResults.getResults().get(0)));
	    
	    Assert.assertTrue("NOAA select <= 0", serie.size() > 0);
	    
	} catch (ResourceException ex) {
	    Assert.fail("Error Accessing resource: " + ex);
	} catch (InfluxDbApiBadrequestException ex) {
	    Assert.fail("Error Accessing resource: " + ex.getError() + " for query: " + query.toQL());
	}
    }

    private void checkResults(QueryResults queryResults, String serieName, int serieColumnsSize, int valuesSize,
	    int valuesWidth) {

	// one query sent, there should only be one result
	List<Result> results = queryResults.getResults();
	Assert.assertTrue("result size different to one", results.size() == 1);

	Result result = results.get(0);
	List<Serie> series = result.getSeries();
	// series is the collection of data that share a retention policy,
	// measurement, and tag set
	// since we only have a single tag, there is only one serie
	Assert.assertTrue("serie size different to one", series.size() == 1);

	Serie serie = series.get(0);
	Assert.assertEquals("Serie's name not as expected:", serieName, serie.getName());

	Assert.assertEquals("Serie's colums number not as expected", serieColumnsSize, serie.getColumns().size());

	// values collections are sorted by time stamp, here we only get one
	Assert.assertEquals("Serie's values number not as expected", valuesSize, serie.getValues().size());

	// number of values fields and tage in each value set
	Assert.assertEquals("Serie's values width not as expected", valuesWidth, serie.getValues().get(0).size());

    }
}
