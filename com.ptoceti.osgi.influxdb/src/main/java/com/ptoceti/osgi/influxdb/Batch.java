package com.ptoceti.osgi.influxdb;

import java.util.ArrayList;
import java.util.List;

public class Batch {
    
    List<Point> points;
    
    public Batch(){
	points = new ArrayList<Point>();
    }

    
    public void addPoint(Point point){
	points.add(point);
    }
    
    public List<Point> getPoints(){
	return points;
    }
}
