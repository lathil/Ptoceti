package com.ptoceti.osgi.rest.impl;

import com.ptoceti.osgi.timeseries.TimeSeriesService;
import org.osgi.framework.*;

import java.util.HashMap;
import java.util.Map;

public class TimeSeriesServiceListener implements ServiceListener {

    protected BundleContext bc;

    protected ServiceReference sRef;

    public TimeSeriesServiceListener(BundleContext bundleContext) throws BundleException {
        bc = bundleContext;
        String filter = "(objectclass=" + TimeSeriesService.class.getName() + ")";
        try {
            bc.addServiceListener(this, filter);
            ServiceReference srLog = bc.getServiceReference(TimeSeriesService.class.getName());
            if (srLog != null) {
                this.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, srLog));
            }
        } catch (InvalidSyntaxException e) {
            throw new BundleException("Error in filter string while registering TimeSeriesServiceListener." + e.toString());
        }
    }

    /**
     * Unique method of the ServiceListener interface.
     */
    public void serviceChanged(ServiceEvent event) {

        ServiceReference sr = event.getServiceReference();

        switch (event.getType()) {
            case ServiceEvent.REGISTERED: {
                sRef = sr;
                Activator.getLogger().info("Detecting TimeSeries");

            }
            break;
            case ServiceEvent.UNREGISTERING: {
                sRef = null;
            }
            break;
        }
    }


    public ServiceReference get() {
        return sRef;
    }
}
