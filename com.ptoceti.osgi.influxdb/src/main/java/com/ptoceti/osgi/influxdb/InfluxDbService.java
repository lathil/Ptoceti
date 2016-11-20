package com.ptoceti.osgi.influxdb;

public interface InfluxDbService {

    void writePoint(Point point);
}
