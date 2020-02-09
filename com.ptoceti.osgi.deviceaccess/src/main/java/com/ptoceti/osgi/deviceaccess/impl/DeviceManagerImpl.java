package com.ptoceti.osgi.deviceaccess.impl;

import com.ptoceti.osgi.deviceaccess.DeviceEvent;
import com.ptoceti.osgi.deviceaccess.DeviceManager;
import org.osgi.framework.*;
import org.osgi.service.device.Device;
import org.osgi.service.device.Driver;
import org.osgi.service.device.DriverSelector;
import org.osgi.service.device.Match;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventProperties;
import org.osgi.util.tracker.ServiceTracker;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeviceManagerImpl implements DeviceManager {

    ServiceRegistration sReg;

    ServiceTracker deviceTracker;
    ServiceTracker driverTracker;
    ServiceTracker selectorTracker;

    ServiceTracker eventAdminTracker;

    // Main executor that process attachment / detachment of devices / drivers
    ExecutorService executorService;
    // a map of currents device - driver matches
    Map<ServiceReference, ServiceReference> matches;

    LinkedList<ServiceReference> idleDevices;

    public DeviceManagerImpl() {

        String[] clazzes = new String[]{DeviceManager.class.getName()};
        // register the class as a managed service.
        Hashtable<String, String> properties = new Hashtable<String, String>();
        properties.put(Constants.SERVICE_PID, DeviceManagerImpl.class.getName());
        sReg = Activator.bc.registerService(clazzes, this, properties);

        Activator.getLogger().info("Registered " + this.getClass().getName() + ", Pid = "
                + (String) properties.get(Constants.SERVICE_PID));

        matches = new HashMap<ServiceReference, ServiceReference>();
        idleDevices = new LinkedList<ServiceReference>();

        executorService = Executors.newFixedThreadPool(1);

        try {
            deviceTracker = createDeviceTrackers();
            driverTracker = createDriverTrackers();
            selectorTracker = createDriverSelectorTracker();

            eventAdminTracker = new ServiceTracker(Activator.bc, EventAdmin.class.getName(), null);
            eventAdminTracker.open();

            deviceTracker.open();
            driverTracker.open();
            selectorTracker.open();

        } catch (InvalidSyntaxException ex) {
            Activator.getLogger().error(ex.toString());
        }
    }

    protected void stop() {

        executorService.shutdown();
        eventAdminTracker.close();
        deviceTracker.close();
        driverTracker.close();
        selectorTracker.close();
        sReg.unregister();
    }

    public ServiceTracker createDeviceTrackers() throws InvalidSyntaxException {

        String deviceFilterSpec = "(|(objectClass=" + Device.class.getName() + ")(DEVICE_CATEGORY=*) )";
        //String deviceFilterSpec = "(objectClass=" + Device.class.getName() + " )";
        ServiceTracker tracker = null;

        Filter deviceFilter = Activator.bc.createFilter(deviceFilterSpec);
        tracker = new ServiceTracker(Activator.bc, deviceFilter, null) {
            @Override
            public Object addingService(ServiceReference reference) {
                Object device = super.addingService(reference);
                Activator.getLogger().info("Detect new device:  " + Utils.deviceDetails(reference));
                EventAdmin eventAdmin = (EventAdmin) eventAdminTracker.getService();
                if (eventAdmin != null) {
                    HashMap<String, String> properties = new HashMap();
                    properties.put(Constants.SERVICE_PID, reference.getProperty(Constants.SERVICE_PID).toString());
                    EventProperties eventProps = new EventProperties(properties);
                    eventAdmin.postEvent(new DeviceEvent(Utils.isDal(reference) ? DeviceEvent.DAL_ADDED_TOPIC : DeviceEvent.DEVICE_ADDED_TOPIC, eventProps));
                }

                MatchingStrategy matchStrategy = new DefaultMatchingStrategy(driverTracker.getServiceReferences(), getDriverSelector());
                executorService.submit(new AttachDriverCommand(reference, matchStrategy));
                return device;
            }

            @Override
            public void removedService(ServiceReference reference, Object service) {
                super.removedService(reference, service);
                Activator.getLogger().info("Device removed:  " + Utils.deviceDetails(reference));

                EventAdmin eventAdmin = (EventAdmin) eventAdminTracker.getService();
                if (eventAdmin != null) {
                    HashMap<String, String> properties = new HashMap();
                    properties.put(Constants.SERVICE_PID, reference.getProperty(Constants.SERVICE_PID).toString());
                    EventProperties eventProps = new EventProperties(properties);
                    eventAdmin.postEvent(new DeviceEvent(Utils.isDal(reference) ? DeviceEvent.DAL_REMOVED_TOPIC : DeviceEvent.DEVICE_REMOVED_TOPIC, eventProps));
                }

                executorService.submit(new RemoveDeviceCommand(reference));
            }
        };


        return tracker;
    }

    public ServiceTracker createDriverTrackers() throws InvalidSyntaxException {
        String driverFilterSpec = "(objectClass=" + Driver.class.getName() + ")";
        ServiceTracker tracker = null;

        Filter driverFilter = Activator.bc.createFilter(driverFilterSpec);
        tracker = new ServiceTracker(Activator.bc, driverFilter, null) {
            @Override
            public Object addingService(ServiceReference reference) {
                Object driver = super.addingService(reference);
                Activator.getLogger().info("Detect new driver:  " + Utils.driverDetails(reference));
                EventAdmin eventAdmin = (EventAdmin) eventAdminTracker.getService();
                if (eventAdmin != null) {
                    HashMap<String, String> properties = new HashMap();
                    properties.put(Constants.SERVICE_PID, reference.getProperty(Constants.SERVICE_PID).toString());
                    EventProperties eventProps = new EventProperties(properties);
                    eventAdmin.postEvent(new DeviceEvent(DeviceEvent.DRIVER_ADDED_TOPIC, eventProps));
                }
                executorService.submit(new AttachIdleDevicesCommand(reference));
                return driver;
            }

            @Override
            public void removedService(ServiceReference reference, Object service) {
                super.removedService(reference, service);
                Activator.getLogger().info("Driver removed:  " + Utils.driverDetails(reference));
                EventAdmin eventAdmin = (EventAdmin) eventAdminTracker.getService();
                if (eventAdmin != null) {
                    HashMap<String, String> properties = new HashMap();
                    properties.put(Constants.SERVICE_PID, reference.getProperty(Constants.SERVICE_PID).toString());
                    EventProperties eventProps = new EventProperties(properties);
                    eventAdmin.postEvent(new DeviceEvent(DeviceEvent.DRIVER_REMOVED_TOPIC, eventProps));
                }
                executorService.submit(new RemoveDriverCommand(reference));
            }
        };


        return tracker;
    }

    public ServiceTracker createDriverSelectorTracker() throws InvalidSyntaxException {
        String driverSelectorFilterSpec = "(objectClass=" + DriverSelector.class.getName() + ")";
        ServiceTracker tracker = null;

        Filter driverSelectorFilter = Activator.bc.createFilter(driverSelectorFilterSpec);
        tracker = new ServiceTracker(Activator.bc, driverSelectorFilter, null) {
            @Override
            public Object addingService(ServiceReference reference) {
                Object selector = super.addingService(reference);
                Activator.getLogger().info("Detect new driverSelector: " + Activator.bc.getService(reference).getClass() + ", ranking: " + Utils.getServiceRanking(reference));
                return selector;
            }

            @Override
            public void removedService(ServiceReference reference, Object service) {
                super.removedService(reference, service);
                Activator.getLogger().info("DriverSelector removed:  " + Activator.bc.getService(reference).getClass() + ", ranking: " + Utils.getServiceRanking(reference));
            }
        };

        return tracker;
    }

    /**
     * Match the device with drivers using the provided matching strategy. Record selected driver if match succesfull, add device to idle device list for re-matching when a new driver will appear.
     */
    protected class AttachDriverCommand implements Callable<Object> {

        ServiceReference deviceSRef;
        MatchingStrategy strategy;

        protected AttachDriverCommand(ServiceReference deviceSRef, MatchingStrategy matchingStrategy) {
            this.deviceSRef = deviceSRef;
            strategy = matchingStrategy;
        }

        @Override
        public Object call() throws Exception {

            Activator.getLogger().debug("Launch AttachDriverCommand");
            Match result = strategy.doMatch(deviceSRef);
            if ((result != null) && (result.getMatchValue() > Device.MATCH_NONE)) {
                matches.put(deviceSRef, result.getDriver());
                EventAdmin eventAdmin = (EventAdmin) eventAdminTracker.getService();
                if (eventAdmin != null) {
                    HashMap<String, String> properties = new HashMap();
                    properties.put(org.osgi.service.device.Constants.DRIVER_ID, result.getDriver().getProperty(org.osgi.service.device.Constants.DRIVER_ID).toString());
                    properties.put(org.osgi.service.device.Constants.DEVICE_SERIAL, deviceSRef.getProperty(org.osgi.service.device.Constants.DEVICE_SERIAL).toString());
                    properties.put(Constants.SERVICE_PID, deviceSRef.getProperty(Constants.SERVICE_PID).toString());
                    EventProperties eventProps = new EventProperties(properties);
                    eventAdmin.postEvent(new DeviceEvent(DeviceEvent.DRIVER_ATTACHED_TOPIC, eventProps));
                }

            } else {
                if (!idleDevices.contains(deviceSRef)) {
                    idleDevices.add(deviceSRef);
                }
            }
            return null;
        }
    }

    /**
     * Remove device service reference from matched or idle devices lists
     */
    protected class RemoveDeviceCommand implements Callable<Object> {

        ServiceReference deviceSRef;

        protected RemoveDeviceCommand(ServiceReference deviceSRef) {
            this.deviceSRef = deviceSRef;
        }

        public Object call() throws Exception {
            Activator.getLogger().debug("Launch RemoveDeviceCommand");
            idleDevices.remove(deviceSRef);
            matches.remove(deviceSRef);
            return null;
        }
    }


    /**
     * Iterate on idle devices for matching when a new driver has appeared
     */
    protected class AttachIdleDevicesCommand implements Callable<Object> {

        ServiceReference driverSRef;

        protected AttachIdleDevicesCommand(ServiceReference driverSRef) {
            this.driverSRef = driverSRef;
        }

        @Override
        public Object call() throws Exception {

            Activator.getLogger().debug("Launch AttachIdleDevicesCommand");
            while (idleDevices.size() > 0) {
                ServiceReference deviceSRef = idleDevices.removeFirst();
                if (deviceSRef != null) {
                    MatchingStrategy matchStrategy = new DefaultMatchingStrategy(new ServiceReference[]{driverSRef}, getDriverSelector());
                    executorService.submit(new AttachDriverCommand(deviceSRef, matchStrategy));
                }
            }
            return null;
        }
    }

    /**
     * Remove driver from match devices map and make device idle
     */
    protected class RemoveDriverCommand implements Callable<Object> {

        ServiceReference driverSRef;

        protected RemoveDriverCommand(ServiceReference driverSRef) {
            this.driverSRef = driverSRef;
        }

        @Override
        public Object call() throws Exception {

            ServiceReference deviceSref = null;
            do {
                deviceSref = null;
                for (Map.Entry<ServiceReference, ServiceReference> entry : matches.entrySet()) {
                    if (entry.getValue().equals(driverSRef)) {
                        deviceSref = entry.getKey();
                    }
                }
                if (deviceSref != null) {
                    matches.remove(deviceSref);
                    idleDevices.add(deviceSref);
                }
            } while (deviceSref != null);

            return null;
        }
    }

    /**
     * find device matched with a driver
     *
     * @param driverId the DRIVER_ID props of the driver
     * @return device serial
     */
    public String isDriverMatched(String driverId) {

        for (Map.Entry<ServiceReference, ServiceReference> entry : matches.entrySet()) {
            ServiceReference driverSref = entry.getValue();
            String driverIdProp = driverSref.getProperty(org.osgi.service.device.Constants.DRIVER_ID).toString();
            if (driverIdProp != null && driverIdProp.equals(driverId)) {
                ServiceReference deviceSref = entry.getKey();
                return deviceSref.getProperty(org.osgi.service.device.Constants.DEVICE_SERIAL).toString();
            }
        }

        return null;
    }

    /**
     * Get appropriate DriverSelector using service ranking if multipples ones exist.
     *
     * @return
     */
    protected DriverSelector getDriverSelector() {
        DriverSelector selector = null;
        ServiceReference[] selectorSRefs = selectorTracker.getServiceReferences();
        if (selectorSRefs != null) {
            if (selectorSRefs.length == 1) {
                Object service = Activator.bc.getService(selectorSRefs[0]);
                if (service instanceof DriverSelector) {
                    selector = (DriverSelector) service;
                }
            } else if (selectorSRefs.length > 1) {

                List<ServiceReference> sRefList = Arrays.asList(selectorSRefs);
                sRefList.sort(new ServiceRankingComparator());
                ServiceReference selectorSRef = sRefList.get(sRefList.size() - 1);
                Object service = (DriverSelector) Activator.bc.getService(selectorSRef);
                if (service instanceof DriverSelector) {
                    selector = (DriverSelector) service;
                }
            }
        }

        return selector;
    }

    private class ServiceRankingComparator implements Comparator<ServiceReference> {

        @Override
        public int compare(ServiceReference o1, ServiceReference o2) {

            int o1ServiceRanking = Utils.getServiceRanking(o1);
            int o2ServiceRanking = Utils.getServiceRanking(o2);

            return o1ServiceRanking - o2ServiceRanking;
        }
    }

}
