package com.ptoceti.osgi.influxdb;

/**
 * An internal DSL builder for creating points.
 * 
 * @author LATHIL
 *
 */
public class PointBuilder {

    private Point point;
    private BatchBuilder batchBuilder;
    
    
    public PointBuilder(BatchBuilder builder, String measurement){
	this.batchBuilder = builder;
	point = new Point(measurement);
    }
    
    public PointBuilder(String measurement){
	point = new Point(measurement);
    }
    
    public static PointBuilder Point(String measurement){
	return new PointBuilder(measurement);
    }
    
    public PointBuilder addTag(String tagName, String tagValue){
	point.addTag(tagName, tagValue);
	return this;
    }
    
    public PointBuilder addField(String fieldName, Object fieldValue){
	point.addValue(fieldName, fieldValue);
	return this;
    }
    
    public PointBuilder setTimestamp(long timestamp){
	point.setTimestamp(timestamp);
	return this;
    }
    
    public Point getPoint(){
	return point;
    }
    
    public BatchBuilder add(){
	return batchBuilder;
    }
}
