package com.ptoceti.osgi.influxdb;

import java.util.List;
import java.util.Map;

public class Serie {

    private String name;
    private List<String> columns;
    private Map<String, String> tags;
    private List<List<Object>> values;
    
    public String getName() {
	return name;
    }
    
    public void setName(String name) {
	this.name = name;
    }
    
    public List<String> getColumns() {
	return columns;
    }
    
    public void setColomns(List<String> columns) {
	this.columns = columns;
    }
    
    public List<List<Object>> getValues() {
	return values;
    }
    
    public void setValues(List<List<Object>> values) {
	this.values = values;
    }

    public Map<String, String> getTags() {
	return tags;
    }

    public void setTags(Map<String, String> tags) {
	this.tags = tags;
    }
    
    public int size(){
	return this.values.size();
    }
    
}
