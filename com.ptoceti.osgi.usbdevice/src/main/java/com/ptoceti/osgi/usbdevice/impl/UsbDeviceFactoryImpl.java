package com.ptoceti.osgi.usbdevice.impl;

import com.ptoceti.osgi.usbdevice.UsbDeviceFactory;
import com.ptoceti.osgi.usbdevice.UsbDeviceTracker;
import org.osgi.framework.*;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.usbinfo.USBInfoDevice;
import org.osgi.util.tracker.ServiceTracker;

import javax.usb.UsbDevice;
import javax.usb.UsbInterface;
import javax.usb.UsbInterfaceDescriptor;
import javax.usb.UsbServices;
import javax.usb.event.UsbServicesEvent;
import javax.usb.event.UsbServicesListener;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

public class UsbDeviceFactoryImpl implements ManagedService, UsbDeviceFactory, UsbServicesListener, UsbDeviceTracker {

    UsbServices usbServices;
    ServiceTracker usbServicesTracker;

    ServiceRegistration reg;
    String deviceFactoryFilter;

    // Main executor that process attachment / detachment of usb devices
    ExecutorService executorService;

    public static final String DEVICE_FILTER = "com.ptoceti.osgi.usbdevice.config.device.factory.filter";


    // A map of usb devices being detected
    protected Map<UsbDevice, DeviceInfoList> usbDeviceList = new HashMap<>();
    // A map of device tracker being interested by a type of usb devices
    protected Map<UsbDeviceTracker, Filter> usbDeviceTrackerList = new HashMap();

    public UsbDeviceFactoryImpl() {

        executorService = Executors.newFixedThreadPool(1);

        String[] clazzes = new String[]{
                ManagedService.class.getName(),
                UsbDeviceFactory.class.getName()
        };
        Hashtable<String, Object> properties = new Hashtable<String, Object>();
        properties.put(Constants.SERVICE_PID, this.getClass().getName());
        reg = Activator.bc.registerService(clazzes, this, properties);

    }


    /**
     * From ManagedService, a configuration item has been created / modified / deleted for this service?
     *
     * @param dictionary
     * @throws ConfigurationException
     */
    @Override
    public void updated(Dictionary<String, ?> dictionary) throws ConfigurationException {

        if (dictionary != null) {
            String newDeviceFactoryFilter = null;

            for (Enumeration<?> e = dictionary.keys(); e.hasMoreElements(); ) {
                String key = (String) e.nextElement();
                if (key.equals(DEVICE_FILTER)) {
                    Object props = dictionary.get(key);
                    if (props instanceof String) {
                        newDeviceFactoryFilter = (String) props;
                    }
                }
            }

            if (deviceFactoryFilter == null && newDeviceFactoryFilter != null) {
                // filter spec created
                deviceFactoryFilter = newDeviceFactoryFilter;
                addUsbDeviceTracker(this, newDeviceFactoryFilter);
            } else if (deviceFactoryFilter != null && (newDeviceFactoryFilter == null || newDeviceFactoryFilter.isBlank())) {
                // filter spec removed
                deviceFactoryFilter = null;
                removeUsbDeviceTracker(this);
            } else if (deviceFactoryFilter != null && newDeviceFactoryFilter != null && !deviceFactoryFilter.equals(newDeviceFactoryFilter)) {
                // filter spec changed
                // remove old
                deviceFactoryFilter = null;
                removeUsbDeviceTracker(this);
                // replace with new
                deviceFactoryFilter = newDeviceFactoryFilter;
                addUsbDeviceTracker(this, newDeviceFactoryFilter);
            }
        }
    }

    public void start() throws InvalidSyntaxException {

        String usbServicesFilterSpec = "(objectClass=" + UsbServices.class.getName() + ")";
        usbServicesTracker = null;
        UsbServicesListener usbServiceListener = this;

        Filter deviceFilter = Activator.bc.createFilter(usbServicesFilterSpec);
        usbServicesTracker = new ServiceTracker(Activator.bc, deviceFilter, null) {
            @Override
            public Object addingService(ServiceReference reference) {
                Object device = super.addingService(reference);
                usbServices = (UsbServices) device;
                usbServices.addUsbServicesListener(usbServiceListener);
                Activator.getLogger().info("Detect new UsbServices:  " + usbServices.getClass().getName());
                return device;
            }

            @Override
            public void removedService(ServiceReference reference, Object service) {
                super.removedService(reference, service);
                usbServices = null;
                Activator.getLogger().info("UsbServices removed:  " + service.getClass().getName());

            }
        };
        usbServicesTracker.open();
    }

    public void stop() {

        executorService.shutdown();

        if (usbServices != null) {
            usbServices.removeUsbServicesListener(this);
        }
        if (usbServicesTracker != null) {
            usbServicesTracker.close();
        }
    }

    /**
     * From UsbServicesListener. A new Usb Device has been attached
     */
    @Override
    public void usbDeviceAttached(UsbServicesEvent event) {

        UsbDevice device = event.getUsbDevice();
        if (!device.isUsbHub()) {
            Activator.getLogger().info("UsbDevice attached: " + Utils.getUsbDeviceStringInfo(device));
            executorService.submit(new DeviceAddedCommand(device))
            ;
        }

    }

    /**
     * From UsbServicesListener. A Usb Device has been removed
     */
    @Override
    public void usbDeviceDetached(UsbServicesEvent event) {
        UsbDevice device = event.getUsbDevice();
        if (!device.isUsbHub()) {
            //Activator.getLogger().info("UsbDevice detached: " + Utils.getUsbDeviceStringInfo(device));
            Activator.getLogger().info("UsbDevice detached: ");
            executorService.submit(new DeviceRemovedCommand(device));
        }
    }


    /**
     * From UsbDeviceFactory
     * Add a device tracker with its filter to internal list
     *
     * @param udbDeviceInfoFilter
     * @param tracker
     */
    @Override
    public void addUsbDeviceTracker(UsbDeviceTracker tracker, String udbDeviceInfoFilter) {
        executorService.submit(new AddDeviceTrackerCommand(udbDeviceInfoFilter, tracker));
    }

    /**
     * From UsbDeviceFactory
     * Remove a device tracker from the internal list
     *
     * @param tracker
     */
    @Override
    public void removeUsbDeviceTracker(UsbDeviceTracker tracker) {
        executorService.submit(new RemoveDeviceTrackerCommand(tracker));
    }

    /***
     * From UsbDeviceTracker
     * Notified that a device has benn added.
     * Create a UsbInfoDevice for this usb device
     * @param usbDevice
     * @param usbDeviceInfo
     */
    @Override
    public void deviceAdded(UsbDevice usbDevice, Map<String, Object> usbDeviceInfo) {
        usbDeviceList.get(usbDevice).usbDeviceinfoList.add(new USBInfoDeviceImpl(usbDevice, usbDeviceInfo));
    }

    /**
     * From UsbDeviceTracker
     * Notified that a device has been removed
     * Remove associated UsbInfoDevice
     *
     * @param usbDevice
     */
    @Override
    public void deviceRemoved(UsbDevice usbDevice) {
        if (usbDeviceList.containsKey(usbDevice)) {
            usbDeviceList.get(usbDevice).usbDeviceinfoList.forEach(usbDeviceInfo -> {
                usbDeviceInfo.stop();
            });
        }
    }

    /**
     * Add a device tracker to internal list and notify it of any existing usb device that matches its filter
     */
    protected class AddDeviceTrackerCommand implements Callable<Object> {

        private String udbDeviceInfoFilter;
        private UsbDeviceTracker tracker;

        protected AddDeviceTrackerCommand(String udbDeviceInfoFilter, UsbDeviceTracker tracker) {
            this.udbDeviceInfoFilter = udbDeviceInfoFilter;
            this.tracker = tracker;
        }

        @Override
        public Object call() throws Exception {

            try {
                Filter filter = Activator.bc.createFilter(udbDeviceInfoFilter);
                ;
                usbDeviceTrackerList.put(tracker, filter);
                usbDeviceList.forEach((device, deviceinfo) -> {

                    Map<String, Object> deviceProps = Utils.getUsbDeviceDescriptorInfo(device);
                    List<UsbInterface> usbInterfaces = device.getActiveUsbConfiguration().getUsbInterfaces();
                    for (UsbInterface usbInterface : usbInterfaces) {
                        deviceProps.putAll(Utils.getUsbInterfaceDescriptor(usbInterface.getUsbInterfaceDescriptor()));
                        if (filter.matches(deviceProps)) {
                            tracker.deviceAdded(device, deviceProps);
                        }
                    }
                });
            } catch (InvalidSyntaxException ex) {
                Activator.getLogger().error("Failed to add usb device tracker with filter: " + udbDeviceInfoFilter + " error: " + ex.toString());
                throw ex;
            }

            return null;
        }
    }

    /**
     * Remove the device tracker from the internal list.
     */
    protected class RemoveDeviceTrackerCommand implements Callable<Object> {

        private UsbDeviceTracker tracker;

        protected RemoveDeviceTrackerCommand(UsbDeviceTracker tracker) {
            this.tracker = tracker;
        }

        @Override
        public Object call() throws Exception {
            if (usbDeviceTrackerList.containsKey(tracker)) {
                usbDeviceTrackerList.remove(tracker);
            }
            return null;
        }
    }

    /**
     * Add usb device to the internal list and notify any device tracker that match the device props.
     */
    protected class DeviceAddedCommand implements Callable<Object> {

        private UsbDevice device;

        protected DeviceAddedCommand(UsbDevice usbDevice) {
            this.device = usbDevice;
        }

        @Override
        public Object call() throws Exception {
            if (!usbDeviceList.containsKey(device)) {
                Map<String, Object> deviceProps = Utils.getUsbDeviceDescriptorInfo(device);
                usbDeviceList.put(device, new DeviceInfoList(deviceProps));
            }
            usbDeviceTrackerList.forEach((tracker, filter) -> {
                List<UsbInterface> usbInterfaces = device.getActiveUsbConfiguration().getUsbInterfaces();
                for (UsbInterface usbInterface : usbInterfaces) {
                    Map<String, Object> interfaceProps = Utils.getUsbInterfaceDescriptor(usbInterface.getUsbInterfaceDescriptor());
                    interfaceProps.putAll(usbDeviceList.get(device).deviceProps);
                    if (filter.matches(interfaceProps)) {
                        tracker.deviceAdded(device, interfaceProps);
                    }
                }
            });
            return null;
        }
    }

    /**
     * A devive is removed. Notify any device tracker that match the device props and remove device from internal list as wel as attaches UsbInfodevice associated with it.
     */
    protected class DeviceRemovedCommand implements Callable<Object> {

        private UsbDevice device;

        protected DeviceRemovedCommand(UsbDevice usbDevice) {
            this.device = usbDevice;
        }

        @Override
        public Object call() throws Exception {
            usbDeviceTrackerList.forEach((tracker, filter) -> {
                List<UsbInterface> usbInterfaces = device.getActiveUsbConfiguration().getUsbInterfaces();
                for (UsbInterface usbInterface : usbInterfaces) {
                    Map<String, Object> interfaceProps = Utils.getUsbInterfaceDescriptor(usbInterface.getUsbInterfaceDescriptor());
                    interfaceProps.putAll(usbDeviceList.get(device).deviceProps);
                    if (filter.matches(interfaceProps)) {
                        tracker.deviceRemoved(device);
                    }
                }
            });

            if (usbDeviceList.containsKey(device)) {
                usbDeviceList.remove(device);
            }

            return null;
        }
    }

    protected class DeviceInfoList {
        Map<String, Object> deviceProps;
        List<USBInfoDeviceImpl> usbDeviceinfoList;

        protected DeviceInfoList(Map<String, Object> deviceProps) {
            this.deviceProps = deviceProps;
            usbDeviceinfoList = new ArrayList<>();
        }
    }
}
