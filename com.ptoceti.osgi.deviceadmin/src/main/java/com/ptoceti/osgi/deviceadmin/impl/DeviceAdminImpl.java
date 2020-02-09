package com.ptoceti.osgi.deviceadmin.impl;

import com.ptoceti.osgi.deviceadmin.DeviceAdmin;
import com.ptoceti.osgi.deviceadmin.DeviceFactoryInfo;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.BundleTracker;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DeviceAdminImpl implements DeviceAdmin {

    protected ServiceRegistration sReg;
    protected BundleTracker bundleTracker;

    protected Map<Long, Set<DeviceFactoryInfo>> deviceFactoryInfos;
    protected DevicefactoryInfoReader metatypeReader;

    protected static final String FACTORIES_DOCUMENT_LOCATION = "FACTORY-INF/factories";

    protected DeviceAdminImpl() {

        deviceFactoryInfos = new ConcurrentHashMap<Long, Set<DeviceFactoryInfo>>();
        metatypeReader = new DevicefactoryInfoReader();

        String[] clazzes = new String[]{DeviceAdmin.class.getName()};
        // register the class as a managed service.
        Hashtable<String, String> properties = new Hashtable<String, String>();
        properties.put(Constants.SERVICE_PID, this.getClass().getName());
        sReg = Activator.bc.registerService(clazzes, this, properties);

        Activator.getLogger().info("Registered " + this.getClass().getName() + ", Pid = "
                + (String) properties.get(Constants.SERVICE_PID));

        bundleTracker = new BundleTracker(Activator.bc, Bundle.INSTALLED | Bundle.RESOLVED | Bundle.ACTIVE | Bundle.STARTING, null) {

            @Override
            public Object addingBundle(Bundle bundle, BundleEvent event) {

                Enumeration<URL> factoriesLocations = bundle.findEntries(FACTORIES_DOCUMENT_LOCATION, null, false);
                if (factoriesLocations != null && factoriesLocations.hasMoreElements()) {
                    Activator.getLogger().debug("Bundle: " + bundle.getSymbolicName() + " is tracked");
                    addfactoryMetatype(bundle, factoriesLocations);
                    return super.addingBundle(bundle, event);
                }

                return null;
            }

            @Override
            public void removedBundle(Bundle bundle, BundleEvent event, Object object) {
                if (event.getType() == BundleEvent.UNINSTALLED) {
                    Activator.getLogger().debug("Bundle " + bundle.getSymbolicName() + " is un-tracked.");
                    removeFactoryMetatype(bundle);
                    super.removedBundle(bundle, event, object);
                }
            }
        };

        bundleTracker.open();

    }

    protected void addfactoryMetatype(Bundle bundle, Enumeration<URL> factoriesLocations) {
        Set<DeviceFactoryInfo> factoryMetatypes = new HashSet<>();
        while (factoriesLocations.hasMoreElements()) {
            URL nextLocation = factoriesLocations.nextElement();
            try {
                List<DeviceFactoryInfo> mtf = metatypeReader.parse(nextLocation);
                if (mtf != null) {
                    factoryMetatypes.addAll(mtf);
                    Activator.getLogger().debug("Registered device factory info: " + nextLocation.getPath() + " from bundle " + bundle.getSymbolicName());
                }
            } catch (IOException e) {

            }
        }
        if (factoryMetatypes.size() > 0) {
            deviceFactoryInfos.put(Long.valueOf(bundle.getBundleId()), factoryMetatypes);
        }
    }

    protected void removeFactoryMetatype(Bundle bundle) {
        this.deviceFactoryInfos.remove(Long.valueOf(bundle.getBundleId()));
        Activator.getLogger().debug("Released device factory infos from bundle " + bundle.getSymbolicName());
    }

    protected void stop() {
        if (bundleTracker != null) {
            bundleTracker.close();
            bundleTracker = null;
        }
    }

    @Override
    public List<DeviceFactoryInfo> getFactories() {

        List<DeviceFactoryInfo> result = new ArrayList<DeviceFactoryInfo>();
        deviceFactoryInfos.forEach((bundleId, factorySet) -> result.addAll(factorySet));
        return result;
    }

}
