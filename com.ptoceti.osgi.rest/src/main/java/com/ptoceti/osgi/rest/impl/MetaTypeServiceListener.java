package com.ptoceti.osgi.rest.impl;

import com.google.inject.Provides;
import org.osgi.framework.*;
import org.osgi.service.metatype.MetaTypeService;

public class MetaTypeServiceListener implements ServiceListener {


    protected BundleContext bc;
    protected MetaTypeService metatypeService;

    public MetaTypeServiceListener(BundleContext bundleContext) throws BundleException {
        bc = bundleContext;
        String filter = "(objectclass=" + MetaTypeService.class.getName() + ")";
        try {
            bc.addServiceListener(this, filter);
            ServiceReference sRef = bc.getServiceReference(MetaTypeService.class.getName());
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
                metatypeService = (MetaTypeService) bc.getService(sr);
                Activator.getLogger().info("Detecting Metatype service");

            }
            break;
            case ServiceEvent.UNREGISTERING: {
                metatypeService = null;
            }
            break;
        }
    }

    public MetaTypeService get() {
        return metatypeService;
    }
}
