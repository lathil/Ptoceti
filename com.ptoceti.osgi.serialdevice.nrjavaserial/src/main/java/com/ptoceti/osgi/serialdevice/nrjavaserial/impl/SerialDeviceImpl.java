package com.ptoceti.osgi.serialdevice.nrjavaserial.impl;

import gnu.io.*;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.device.Constants;
import org.osgi.service.device.Device;
import org.osgi.service.serial.SerialDevice;
import org.osgi.service.serial.SerialDeviceException;
import org.osgi.service.serial.SerialPortConfiguration;
import org.osgi.util.tracker.ServiceTracker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.TooManyListenersException;

public class SerialDeviceImpl implements SerialDevice, Device {

    ServiceRegistration sReg = null;

    // The port the teleinfo devce is connected to
    String serialPortName = "";
    // The servive pid the device is registered under
    String pid = "";

    protected SerialPort serialPort;

    ServiceTracker serialPortEventListenerTracker;
    SerialEventListenerProxy serialEventListenerProxy;

    public SerialDeviceImpl(String pid, String portName) {
        this.serialPortName = portName;
        this.pid = pid;
    }

    public void start() throws SerialDeviceException {

        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(serialPortName);
            if (portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                serialPort = portIdentifier.open(pid, (int) 1000);
            }

            serialEventListenerProxy = new SerialEventListenerProxy(serialPort);
            serialEventListenerProxy.start();

            serialPortEventListenerTracker = createSerialPortEventListenerTracker();


        } catch (NoSuchPortException ex) {
            Activator.getLogger().error("Error while opening serial port: " + serialPortName + ", " + ex.getMessage());
            throw new SerialDeviceException(SerialDeviceException.PORT_IN_USE, ex.getMessage());
        } catch (PortInUseException ex) {
            Activator.getLogger().error("Error while opening serial port: " + serialPortName + ", " + ex.getMessage());
            throw new SerialDeviceException(SerialDeviceException.UNKNOWN, ex.getMessage());
        } catch (InvalidSyntaxException ex) {
            Activator.getLogger().error("Error creating serialPort eventlistener on serial port: " + serialPortName + ", " + ex.getMessage());
        } catch (TooManyListenersException ex) {
            Activator.getLogger().error("Error creating serialPort eventlistener on serial port: " + serialPortName + ", " + ex.getMessage());
        }

        register();
    }

    protected void register() {

        String[] clazzes = new String[]{
                SerialDevice.class.getName(),
        };

        Dictionary props = new Hashtable();
        props.put(Constants.DEVICE_CATEGORY, new String[]{SerialDevice.DEVICE_CATEGORY});
        props.put(SerialDevice.SERIAL_COMPORT, serialPortName);
        props.put(Constants.DEVICE_DESCRIPTION, "Serial device");
        props.put(Constants.DEVICE_SERIAL, serialPortName);
        props.put(org.osgi.framework.Constants.SERVICE_PID, pid);


        sReg = Activator.bc.registerService(clazzes, this, props);

        Activator.getLogger().info("Registered " + this.getClass().getName() + " as " + SerialDevice.class.getName() + " on port : " + props.get(SerialDevice.SERIAL_COMPORT));

    }

    public void stop() {

        if (serialPortEventListenerTracker != null) {
            serialPortEventListenerTracker.close();
            serialPortEventListenerTracker = null;
        }
        if (serialEventListenerProxy != null) {
            serialEventListenerProxy.stop();
            serialEventListenerProxy = null;
        }
        if (serialPort != null) {
            serialPort.close();
        }
        if (sReg != null) {
            this.sReg.unregister();
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (serialPort != null) {
            return serialPort.getInputStream();
        }
        return null;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (serialPort != null) {
            return serialPort.getOutputStream();
        }
        return null;
    }

    @Override
    public SerialPortConfiguration getConfiguration() {
        if (serialPort != null) {
            return new SerialPortConfiguration(serialPort.getBaudRate(), serialPort.getDataBits(), serialPort.getFlowControlMode(), serialPort.getParity(), serialPort.getStopBits());
        }
        return null;
    }

    @Override
    public void setConfiguration(SerialPortConfiguration serialPortConfiguration) throws SerialDeviceException {
        if (serialPort != null) {
            try {
                serialPort.setSerialPortParams(serialPortConfiguration.getBaudRate(), serialPortConfiguration.getDataBits(), serialPortConfiguration.getStopBits(), serialPortConfiguration.getParity());
            } catch (UnsupportedCommOperationException e) {
                throw new SerialDeviceException(SerialDeviceException.UNSUPPORTED_OPERATION, "Error configuring port: " + e);
            }
        }
    }

    @Override
    public boolean isDTR() {
        if (serialPort != null) {
            return serialPort.isDTR();
        }
        return false;
    }

    @Override
    public boolean isRTS() {
        if (serialPort != null) {
            return serialPort.isRTS();
        }
        return false;
    }

    @Override
    public boolean isDSR() {
        if (serialPort != null) {
            return serialPort.isDSR();
        }
        return false;
    }

    @Override
    public boolean isCTS() {
        if (serialPort != null) {
            return serialPort.isCTS();
        }
        return false;
    }

    @Override
    public void setDTR(boolean b) throws SerialDeviceException {
        if (serialPort != null) {
            serialPort.setDTR(b);
        }
    }

    @Override
    public void setRTS(boolean b) throws SerialDeviceException {
        if (serialPort != null) {
            serialPort.setRTS(b);
        }
    }

    @Override
    public void noDriverFound() {

    }

    protected ServiceTracker createSerialPortEventListenerTracker() throws InvalidSyntaxException {

        String serialEventListenerFilterSpec = "(objectClass=" + SerialPortEventListener.class.getName() + ")";
        ServiceTracker tracker = null;

        Filter deviceFilter = Activator.bc.createFilter(serialEventListenerFilterSpec);
        tracker = new ServiceTracker(Activator.bc, deviceFilter, null) {
            @Override
            public Object addingService(ServiceReference reference) {
                Object eventListener = super.addingService(reference);
                Activator.getLogger().info("Detect new eventListener:  " + Utils.listenerDetails(reference));
                serialEventListenerProxy.addListener(reference);

                return eventListener;
            }

            @Override
            public void removedService(ServiceReference reference, Object service) {
                super.removedService(reference, service);
                Activator.getLogger().info("EventListener removed:  " + Utils.listenerDetails(reference));
                serialEventListenerProxy.removeListener(reference);

            }
        };


        return tracker;
    }
}
