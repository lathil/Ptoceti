package com.ptoceti.osgi.rest.impl;

import org.osgi.framework.*;
import org.osgi.service.cm.ConfigurationAdmin;


public class ConfigAdminServiceListener implements ServiceListener {

    protected BundleContext bc;
    protected ConfigurationAdmin configurationAdmin;

    public ConfigAdminServiceListener(BundleContext bundleContext) throws BundleException {
        bc = bundleContext;
        String filter = "(objectclass=" + ConfigurationAdmin.class.getName() + ")";
        try {
            bc.addServiceListener(this, filter);
            ServiceReference sRef = bc.getServiceReference(ConfigurationAdmin.class.getName());
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
                configurationAdmin = (ConfigurationAdmin) bc.getService(sr);
                Activator.getLogger().info("Detecting ConfigAdmin");

            }
            break;
            case ServiceEvent.UNREGISTERING: {
                configurationAdmin = null;
            }
            break;
        }
    }

    public ConfigurationAdmin get() {
        return configurationAdmin;
    }

}
