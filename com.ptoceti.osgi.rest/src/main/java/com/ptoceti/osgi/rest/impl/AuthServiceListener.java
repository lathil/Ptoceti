package com.ptoceti.osgi.rest.impl;

import com.ptoceti.osgi.auth.AuthService;
import com.ptoceti.osgi.deviceaccess.DeviceManager;
import com.ptoceti.osgi.deviceadmin.DeviceAdmin;
import org.osgi.framework.*;

public class AuthServiceListener implements ServiceListener {

    protected BundleContext bc;
    protected AuthService authService;


    public AuthServiceListener(BundleContext bundleContext) throws BundleException {
        bc = bundleContext;
        String filter = "(objectclass=" + AuthService.class.getName() + ")";
        try {
            bc.addServiceListener(this, filter);
            ServiceReference sRef = bc.getServiceReference(AuthService.class.getName());
            if (sRef != null) {
                this.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, sRef));
            }
        } catch (InvalidSyntaxException e) {
            throw new BundleException("Error in filter string while registering AuthServiceListener." + e.toString());
        }
    }

    @Override
    public void serviceChanged(ServiceEvent event) {
        ServiceReference sr = event.getServiceReference();

        switch (event.getType()) {
            case ServiceEvent.REGISTERED: {
                authService = (AuthService) bc.getService(sr);
                Activator.getLogger().info("Detecting AuthService");

            }
            break;
            case ServiceEvent.UNREGISTERING: {
                authService = null;
            }
            break;
        }
    }

    public AuthService get() {
        return authService;
    }
}
