package com.ptoceti.osgi.timeseries;

import org.osgi.util.measurement.Measurement;

import java.util.Date;

public class HistoryRecord {

    private Date timmestamp;
    private Measurement val;
    private String name;

    public Date getTimmestamp() {
        return timmestamp;
    }

    public void setTimmestamp(Date timmestamp) {
        this.timmestamp = timmestamp;
    }

    public Measurement getVal() {
        return val;
    }

    public void setVal(Measurement val) {
        this.val = val;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
