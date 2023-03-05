package com.ptoceti.osgi.rest.impl;

import com.ptoceti.osgi.deviceadmin.DeviceAdmin;
import org.osgi.framework.*;
import org.osgi.service.wireadmin.WireAdmin;


public class DeviceAdminServiceListener implements ServiceListener {

    protected BundleContext bc;
    protected DeviceAdmin deviceAdmin;


    public DeviceAdminServiceListener(BundleContext bundleContext) throws BundleException {
        bc = bundleContext;
        String filter = "(objectclass=" + DeviceAdmin.class.getName() + ")";
        try {
            bc.addServiceListener(this, filter);
            ServiceReference sRef = bc.getServiceReference(DeviceAdmin.class.getName());
            if (sRef != null) {
                this.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, sRef));
            }
        } catch (InvalidSyntaxException e) {
            throw new BundleException("Error in filter string while registering DeviceAdminServiceListener." + e.toString());
        }
    }

    @Override
    public void serviceChanged(ServiceEvent event) {
        ServiceReference sr = event.getServiceReference();

        switch (event.getType()) {
            case ServiceEvent.REGISTERED: {
                deviceAdmin = (DeviceAdmin) bc.getService(sr);
                Activator.getLogger().info("Detecting DeviceAdmin");

            }
            break;
            case ServiceEvent.UNREGISTERING: {
                deviceAdmin = null;
            }
            break;
        }
    }

    public DeviceAdmin get() {
        return deviceAdmin;
    }
}
