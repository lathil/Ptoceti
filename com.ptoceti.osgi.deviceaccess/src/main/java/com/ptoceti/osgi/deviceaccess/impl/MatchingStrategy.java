package com.ptoceti.osgi.deviceaccess.impl;

import org.osgi.framework.ServiceReference;
import org.osgi.service.device.Match;

public interface MatchingStrategy {

    public Match doMatch(ServiceReference device);
}
