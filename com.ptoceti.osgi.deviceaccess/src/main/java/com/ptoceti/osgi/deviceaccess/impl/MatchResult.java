package com.ptoceti.osgi.deviceaccess.impl;

import org.osgi.framework.ServiceReference;
import org.osgi.service.device.Match;

public class MatchResult implements Match {

    protected ServiceReference driver;
    protected int matchValue;

    public MatchResult(ServiceReference driver, int matchValue) {
        this.driver = driver;
        this.matchValue = matchValue;
    }

    @Override
    public ServiceReference getDriver() {
        return driver;
    }

    @Override
    public int getMatchValue() {
        return matchValue;
    }
}
