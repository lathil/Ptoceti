package com.ptoceti.osgi.ebus.impl;

import com.ptoceti.osgi.ebus.EbusDriver;
import com.ptoceti.osgi.ebus.EbusDriverListener;
import com.ptoceti.osgi.ebus.impl.connection.EbusResponseListener;
import com.ptoceti.osgi.ebus.impl.connection.EbusSerialConnection;
import com.ptoceti.osgi.ebus.impl.message.EbusMessage;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.device.Constants;
import org.osgi.service.device.Device;
import org.osgi.service.device.Driver;
import org.osgi.service.log.LogService;
import org.osgi.service.serial.SerialDevice;
import org.osgi.service.serial.SerialEventListener;
import org.osgi.util.tracker.ServiceTracker;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.Future;

public class EbusDriverImpl implements EbusDriver, Driver {

    ServiceRegistration sReg;
    String pid;
    byte id;
    String portName;
    int lockCounter;

    protected ServiceReference sRegSerialDevice;
    protected SerialDevice serialDevice;

    protected ServiceRegistration sRegSerialListener;

    ServiceTracker serialDeviceTracker;

    EbusSerialConnection ebusConnection;

    String filterSpec = "(&(objectclass=" + SerialDevice.class.getName() + ")" + "(DEVICE_CATEGORY=" + SerialDevice.DEVICE_CATEGORY + "))";
    Filter filter;

    Set<EbusDriverListener> ebusDriverListeners = new HashSet<EbusDriverListener>();

    EbusDriverImpl(String pid, int id, String portName, int lockCounter) throws Exception {

        filter = Activator.bc.createFilter(filterSpec);
        this.pid = pid;
        this.id = ((byte) id);
        this.portName = portName;
        this.lockCounter = lockCounter;
    }

    public void start() throws Exception {

        String[] clazzes = new String[]{
                EbusDriver.class.getName(),
                Driver.class.getName()
        };

        Dictionary props = new Hashtable();
        props.put( org.osgi.framework.Constants.SERVICE_DESCRIPTION, "Ebus service implements a Device interface.");
        props.put(EbusDriver.EBUS_PORT, portName);
        props.put(Constants.DRIVER_ID, EbusDriver.class.getPackage().getName() + "." + portName);
        props.put(org.osgi.framework.Constants.SERVICE_PID, this.pid);

        sReg = Activator.bc.registerService(clazzes, this, props);
        Activator.getLogger().info("Registered " + this.getClass().getName());

    }

    public void stop() {

        if (sRegSerialListener != null) sRegSerialListener.unregister();
        if (ebusConnection != null) ebusConnection.close();
        if( sReg != null ) sReg.unregister();

        Activator.getLogger().info("Unregistered " + this.getClass().getName());
    }

    /**
     * Return this ebus driver's identificator
     *
     * @return the ebus identificator.
     */
    public byte getID() {
        return this.id;
    }


    public EbusResponseListener sendMasterMasterMessage(int destAddress, int primaryCommand, int secondaryCommand, byte[] payload){
        EbusMessage message = new EbusMessage( (byte)( destAddress & 0x00FF), (byte)( primaryCommand & 0x00FF), (byte)( secondaryCommand & 0x00FF), payload);
        EbusResponseListener listener = ebusConnection.addMessageToSend(message);
        return listener;
    }

    public EbusResponseListener sendMasterSlaveMessage(int destAddress, int primaryCommand, int secondaryCommand, byte[] payload ){
        EbusMessage message = new EbusMessage( (byte)( destAddress & 0x00FF), (byte)( primaryCommand & 0x00FF), (byte)( secondaryCommand & 0x00FF), payload);
        EbusResponseListener listener = ebusConnection.addMessageToSend(message);
        return listener;
    }

    public EbusResponseListener sendBroadcastMessage(int primaryCommand, int secondaryCommand, byte[] payload) {
        EbusMessage message = new EbusMessage((byte) (EbusMessage.BROADCAST_ADD), (byte) (primaryCommand & 0x00FF), (byte) (secondaryCommand & 0x00FF), payload);
        EbusResponseListener listener = ebusConnection.addMessageToSend(message);

        return listener;
    }

    @Override
    public int match(ServiceReference serviceReference) throws Exception {
        Activator.getLogger().debug("matching " + Activator.deviceDetails(serviceReference));
        if (filter.match(serviceReference)) {
            String serialDeviceComPort = Activator.getSerialEventListenerComPort(serviceReference);
            if ((serialDeviceComPort != null) && (serialDeviceComPort.equals(portName)))
                return 10;
            else return Device.MATCH_NONE;
        } else
            return Device.MATCH_NONE;
    }

    @Override
    public String attach(ServiceReference serviceReference) throws Exception {

        Activator.getLogger().info("Attached to device " + Activator.deviceDetails(serviceReference));
        sRegSerialDevice = serviceReference;
        serialDevice = (SerialDevice) Activator.bc.getService(sRegSerialDevice);

        ebusConnection = new EbusSerialConnection(serialDevice, portName);

        // set mdbConnection to listen to data available events.
        String[] clazzes = new String[]{
                SerialEventListener.class.getName()
        };

        Dictionary props = new Hashtable();
        props.put(SerialEventListener.SERIAL_COMPORT, portName);

        sRegSerialListener = Activator.bc.registerService(clazzes, ebusConnection, props);

        createSerialDeviceTracker(serviceReference);

        // if serial connection start ok ..
        ebusConnection.start(getID(), lockCounter);

        return null;
    }

    protected void createSerialDeviceTracker(ServiceReference serviceReference) {
        // track in case he serial device diseapear ..
        if (serialDeviceTracker != null) {
            serialDeviceTracker.close();
        }
        serialDeviceTracker = new ServiceTracker(Activator.bc, serviceReference, null) {
            @Override
            public Object addingService(ServiceReference reference) {
                Object device = super.addingService(reference);
                ebusDriverListeners.forEach(listener -> listener.ebusDriverConnected());
                return device;
            }

            @Override
            public void removedService(ServiceReference reference, Object service) {
                super.removedService(reference, service);

                ebusDriverListeners.forEach(listener -> listener.ebusDriverDisconnected());
                if (sRegSerialListener != null) {
                    sRegSerialListener.unregister();
                    sRegSerialListener = null;
                }
                if (ebusConnection != null) {
                    ebusConnection.stop();
                    ebusConnection.close();
                    ebusConnection = null;
                }
                sRegSerialDevice = null;
                serialDevice = null;
            }
        };
        serialDeviceTracker.open();
    }

    @Override
    public void addListener(EbusDriverListener listener) {
        ebusDriverListeners.add(listener);
    }

    @Override
    public void removeListener(EbusDriverListener listener) {
        ebusDriverListeners.remove(listener);
    }
}
