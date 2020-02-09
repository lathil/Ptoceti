package com.ptoceti.osgi.rest.impl;

import com.ptoceti.osgi.deviceaccess.DeviceManager;
import org.osgi.framework.*;

public class DeviceAccessServiceListener implements ServiceListener {
    protected BundleContext bc;
    protected DeviceManager deviceManager;


    public DeviceAccessServiceListener(BundleContext bundleContext) throws BundleException {
        bc = bundleContext;
        String filter = "(objectclass=" + DeviceManager.class.getName() + ")";
        try {
            bc.addServiceListener(this, filter);
            ServiceReference sRef = bc.getServiceReference(DeviceManager.class.getName());
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
                deviceManager = (DeviceManager) bc.getService(sr);
                Activator.getLogger().info("Detecting DeviceManager");

            }
            break;
            case ServiceEvent.UNREGISTERING: {
                deviceManager = null;
            }
            break;
        }
    }

    public DeviceManager get() {
        return deviceManager;
    }
}
