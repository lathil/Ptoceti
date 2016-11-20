package com.ptoceti.osgi.influxdb;

import java.util.Calendar;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class Point {

    // the name of the table
    private String measurement;
    // tags are indexed, used for searching
    private SortedMap<String, String> tags = Collections.synchronizedSortedMap(new TreeMap<String, String>());
    // fields are the values
    private SortedMap<String, Object> fields = Collections.synchronizedSortedMap(new TreeMap<String, Object>());

    private long timestamp;

    public Point(String measurement) {
	this.measurement = measurement;
	this.timestamp = (Calendar.getInstance().getTimeInMillis());

    }
    
    public String getMeasurement(){
	return measurement;
    }
    
    public Map<String, String> getTags(){
	return tags;
    }
    
    public Map<String, Object> getFields(){
	return fields;
    }

    public void addTag(String tagName, String tagValue) {
	tags.put(tagName, tagValue);
    }

    public void addValue(String fieldName, Object fieldValue) {
	fields.put(fieldName, fieldValue);
    }

    public void addValue(String fieldName, Object fieldValue, long newTimeStampMillis) {
	fields.put(fieldName, fieldValue);
	timestamp = newTimeStampMillis;
    }
    
    
    public Object getFieldValue(String key){
	return fields.get(key);
    }
    
    public Object getTagValue(String key){
	return tags.get(key);
    }

    public long getTimestamp() {
	return timestamp;
    }
    
    public void setTimestamp(long timestamp){
	this.timestamp = timestamp;
    }

}
