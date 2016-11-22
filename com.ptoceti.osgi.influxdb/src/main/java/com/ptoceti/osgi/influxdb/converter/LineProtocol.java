package com.ptoceti.osgi.influxdb.converter;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.ptoceti.osgi.influxdb.Batch;
import com.ptoceti.osgi.influxdb.Point;

public class LineProtocol {

    
    protected static NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);

    public LineProtocol() {
	
    }

    private void addTags(StringBuilder line, Point point) {

	for (Entry<String, String> entry : point.getTags().entrySet()) {
	    if (entry.getValue() != null && !entry.getValue().isEmpty()) {
		// each tag start with ","
		line.append(",");
		line.append(entry.getKey());
		line.append("=");
		line.append(entry.getValue().toString());
	    }
	}
    }

    private void addFields(StringBuilder line, Point point) {
	// fields values entries start with white space
	line.append(" ");
	boolean isFirst = true;
	for (Entry<String, Object> entry : point.getFields().entrySet()) {
	    Object value = entry.getValue();
	    
	    if (value != null) {
		
		if( !isFirst){
		    line.append(",");
		}
		line.append(entry.getKey());
		line.append("=");

		if( value instanceof String){
		    line.append(value);
		} else if (value instanceof Boolean){
		    line.append(((Boolean)value).toString());
		} else if (value instanceof Double || value instanceof Float || value instanceof BigDecimal){
		    line.append(LineProtocol.numberFormat.format(value));
		} else if (value instanceof Long || value instanceof Integer){
		    line.append(value.toString());
		    line.append("i");
		}
		
		isFirst = false;
	    }

	}
    }
    
    public void addTimeStamp(StringBuilder line, Point point){
	line.append(" ");
	
	line.append(TimeUnit.NANOSECONDS.convert(point.getTimestamp(), TimeUnit.MILLISECONDS));

    }

    public String toLine(Point point) {
	StringBuilder line = new StringBuilder();
	
	line = new StringBuilder();
	line.append(point.getMeasurement());
	addTags(line, point);
	addFields(line, point);
	addTimeStamp(line, point);

	return line.toString();
    }
    
    public Point toPoint(String line){
	
	int measurementEndAt = line.indexOf(",");
	String measurement = line.substring(0, measurementEndAt);
	
	Point point = new Point(measurement);
	
	int tagEndAt = line.substring(measurementEndAt + 1).indexOf(" ");
	
	String[] tags = line.substring(measurementEndAt + 1, measurementEndAt + 1 + tagEndAt).split(",");
	for(String tag: tags){
	    String[] kv = tag.split("=");
	    point.addTag(kv[0], kv[1]);
	}
	
	int fieldEndAt = line.substring(measurementEndAt + 1 + tagEndAt + 1).indexOf(" ");
	
	String[] fields = line.substring(measurementEndAt + 1 + tagEndAt + 1, measurementEndAt + 1 + tagEndAt + 1 + fieldEndAt).split(",");
	for(String field: fields){
	    String[] kv = field.split("=");
	    point.addValue(kv[0], kv[1]);
	}
	
	String timestamp = line.substring(measurementEndAt + 1 + tagEndAt + 1 + fieldEndAt + 1);
	
	point.setTimestamp(Long.parseLong(timestamp));
	
	return point;
	
    }
    
    public String toLine(Batch batch){
	StringBuilder batchLine = new StringBuilder();
	
	boolean isfirst = true;
	for( Point point : batch.getPoints()){
	    if( !isfirst ) batchLine.append("\n");
	    batchLine.append(this.toLine(point));
	    isfirst = false;
	}
	
	return batchLine.toString();
    }
}
