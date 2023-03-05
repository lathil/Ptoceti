package com.ptoceti.osgi.rest.impl;

import com.google.inject.Provider;
import com.ptoceti.osgi.rest.impl.Activator;
import org.osgi.framework.*;
import org.osgi.service.log.LogService;
import org.osgi.service.wireadmin.WireAdmin;

public class WireAdminServiceListener implements ServiceListener {

    protected BundleContext bc;
    protected WireAdmin wireAdmin;

    public WireAdminServiceListener(BundleContext bundleContext) throws BundleException {
        bc = bundleContext;
        String filter = "(objectclass=" + WireAdmin.class.getName() + ")";
        try {
            bc.addServiceListener(this, filter);
            ServiceReference sRef = bc.getServiceReference(WireAdmin.class.getName());
            if (sRef != null) {
                this.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, sRef));
            }
        } catch (InvalidSyntaxException e) {
            throw new BundleException("Error in filter string while registering WireAdminServiceListener." + e.toString());
        }
    }

    /**
     * Unique method of the ServiceListener interface.
     */
    public void serviceChanged(ServiceEvent event) {

        ServiceReference sr = event.getServiceReference();

        switch (event.getType()) {
            case ServiceEvent.REGISTERED: {
                wireAdmin = (WireAdmin) bc.getService(sr);
                Activator.getLogger().info("Detecting WireAdmin");

            }
            break;
            case ServiceEvent.UNREGISTERING: {
                wireAdmin = null;
            }
            break;
        }
    }


    public WireAdmin get() {
        return wireAdmin;
    }
}
