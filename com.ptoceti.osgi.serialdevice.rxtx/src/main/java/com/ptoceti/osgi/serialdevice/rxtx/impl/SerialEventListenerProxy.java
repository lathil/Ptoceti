package com.ptoceti.osgi.serialdevice.rxtx.impl;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.serial.SerialEventListener;

import java.util.HashSet;
import java.util.Set;
import java.util.TooManyListenersException;

public class SerialEventListenerProxy implements SerialPortEventListener {

    Set<ServiceReference> eventListeners = new HashSet();

    private SerialPort serialPort;

    public SerialEventListenerProxy(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    public void start() throws TooManyListenersException {
        serialPort.addEventListener(this);
        serialPort.notifyOnDataAvailable(true);
    }

    public void stop() {
        serialPort.removeEventListener();
    }

    public void addListener(ServiceReference listenerSRef) {
        if (Activator.bc.getService(listenerSRef) instanceof SerialEventListener) {
            eventListeners.add(listenerSRef);
        }
    }

    public void removeListener(ServiceReference listenerSRef) {
        eventListeners.remove(listenerSRef);
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        eventListeners.forEach(listenerSRef -> {
            SerialEventListener listener = (SerialEventListener) Activator.bc.getService(listenerSRef);
            String serialComPortProps = Utils.getSerialEventListenerComPort(listenerSRef);
            if (serialComPortProps != null) {
                if (serialComPortProps.equals(serialPort.getName())) {
                    SerialEventImpl event = new SerialEventImpl(serialPortEvent.getEventType(), serialPort.getName());
                    listener.notifyEvent(event);
                }
            } else {
                SerialEventImpl event = new SerialEventImpl(serialPortEvent.getEventType(), serialPort.getName());
                listener.notifyEvent(event);
            }
        });
    }
}

