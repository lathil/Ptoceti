package com.ptoceti.osgi.influxdb;


/**
 * An internal DSL builder for creating batchs of points.
 * 
 * 
 * @author LATHIL
 *
 */
public class BatchBuilder {

    private Batch batch;
    
    public BatchBuilder(){
	batch = new Batch();
    }
    
    public static BatchBuilder Batch(){
	return new BatchBuilder();
    }
    
    public PointBuilder point(String measurement){
	PointBuilder builder = new PointBuilder(this, measurement);
	batch.addPoint(builder.getPoint());
	return builder;
    }
    
    public Batch getBatch(){
	return this.batch;
    }
}
