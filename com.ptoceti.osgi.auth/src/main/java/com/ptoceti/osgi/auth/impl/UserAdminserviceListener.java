package com.ptoceti.osgi.auth.impl;

import org.osgi.framework.*;
import org.osgi.service.useradmin.UserAdmin;

public class UserAdminserviceListener implements ServiceListener {

    protected BundleContext bc;
    protected UserAdmin userAdmin;


    public UserAdminserviceListener(BundleContext bundleContext) throws BundleException {
        bc = bundleContext;
        String filter = "(objectclass=" + UserAdmin.class.getName() + ")";
        try {
            bc.addServiceListener(this, filter);
            ServiceReference sRef = bc.getServiceReference(UserAdmin.class.getName());
            if (sRef != null) {
                this.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, sRef));
            }
        } catch (InvalidSyntaxException e) {
            throw new BundleException("Error in filter string while registering UserAdminserviceListener." + e.toString());
        }
    }

    @Override
    public void serviceChanged(ServiceEvent event) {
        ServiceReference sr = event.getServiceReference();

        switch (event.getType()) {
            case ServiceEvent.REGISTERED: {
                userAdmin = (UserAdmin) bc.getService(sr);
                Activator.getLogger().info("Detecting UserAdmin");

            }
            break;
            case ServiceEvent.UNREGISTERING: {
                userAdmin = null;
            }
            break;
        }
    }

    public UserAdmin getUserAdmin() {
        return userAdmin;
    }
}
