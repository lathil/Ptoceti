package com.ptoceti.osgi.modbusdevice.impl.functions;

import com.ptoceti.osgi.modbusdevice.ModbusDevice;
import com.ptoceti.osgi.modbusdevice.impl.Activator;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dal.Device;
import org.osgi.util.tracker.ServiceTracker;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractModbusFunction {

    protected String functionId;
    protected String deviceId;
    protected String serviceType;

    protected ModbusDevice modbusDevice;
    protected ServiceTracker modbusDeviceTracker;

    ScheduledExecutorService scheduledExecutor;

    protected AbstractModbusFunction(String functionId, String deviceId, String serviceType) {
        this.deviceId = deviceId;
        this.functionId = functionId;
        this.serviceType = serviceType;
    }

    protected void start() {

        String devicefilter = "(&(objectclass=" + ModbusDevice.class.getName() + ")(" + org.osgi.service.dal.Device.SERVICE_UID + "=" + this.deviceId + "))";
        try {
            Filter deviceFilter = Activator.getBc().createFilter(devicefilter);
            modbusDeviceTracker = new ServiceTracker(Activator.getBc(), deviceFilter, null) {
                @Override
                public Object addingService(ServiceReference reference) {
                    Object device = super.addingService(reference);
                    modbusDevice = (ModbusDevice) device;
                    Activator.getLogger().info("Modbus Function detect modbusdevice added: " + reference.getProperty(org.osgi.service.dal.Device.SERVICE_UID));
                    return device;
                }

                @Override
                public void removedService(ServiceReference reference, Object service) {
                    super.removedService(reference, service);
                    modbusDevice = null;
                    Activator.getLogger().info("Modbus Function detect modbusdevice removed: " + reference.getProperty(org.osgi.service.dal.Device.SERVICE_UID));
                }
            };
            modbusDeviceTracker.open();

        } catch (InvalidSyntaxException ex) {
            Activator.getLogger().error("Error creating ModbusDevice tracker: " + ex.toString());
        }

        scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (modbusDevice != null) {
                    if (modbusDevice.getServiceProperty(Device.SERVICE_STATUS).equals(Device.STATUS_ONLINE)) {
                        submitCommand(modbusDevice);
                    }
                }
            }
        }, 1000, 1000, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        if (scheduledExecutor != null) {
            scheduledExecutor.shutdown();
            try {
                if (!scheduledExecutor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    scheduledExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduledExecutor.shutdownNow();
            }
            scheduledExecutor = null;
        }

        if (modbusDeviceTracker != null) {
            modbusDeviceTracker.close();
            modbusDeviceTracker = null;
        }
    }

    abstract void submitCommand(ModbusDevice modbusDevice);
}
