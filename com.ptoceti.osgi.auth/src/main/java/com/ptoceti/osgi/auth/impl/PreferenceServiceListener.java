package com.ptoceti.osgi.auth.impl;

import org.osgi.framework.*;
import org.osgi.service.prefs.PreferencesService;
import org.osgi.service.useradmin.UserAdmin;

public class PreferenceServiceListener implements ServiceListener {

    protected BundleContext bc;
    protected PreferencesService preferenceservice;

    public PreferenceServiceListener(BundleContext bundleContext) throws BundleException {
        bc = bundleContext;
        String filter = "(objectclass=" + UserAdmin.class.getName() + ")";
        try {
            bc.addServiceListener(this, filter);
            ServiceReference sRef = bc.getServiceReference(PreferencesService.class.getName());
            if (sRef != null) {
                this.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, sRef));
            }
        } catch (InvalidSyntaxException e) {
            throw new BundleException("Error in filter string while registering PreferencesService." + e.toString());
        }

    }

    @Override
    public void serviceChanged(ServiceEvent event) {
        ServiceReference sr = event.getServiceReference();

        switch (event.getType()) {
            case ServiceEvent.REGISTERED: {
                preferenceservice = (PreferencesService) bc.getService(sr);
                Activator.getLogger().info("Detecting PreferencesService");

            }
            break;
            case ServiceEvent.UNREGISTERING: {
                preferenceservice = null;
            }
            break;
        }
    }

    public PreferencesService getPreferenceservice() {
        return preferenceservice;
    }
}
