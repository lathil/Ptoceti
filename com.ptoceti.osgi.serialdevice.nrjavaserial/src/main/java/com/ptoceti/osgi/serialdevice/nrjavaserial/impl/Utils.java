package com.ptoceti.osgi.serialdevice.nrjavaserial.impl;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.serial.SerialEventListener;

public class Utils {

    public static String listenerDetails(ServiceReference sRef) {

        StringBuffer buffer = new StringBuffer();

        Object objectClass = sRef.getProperty(Constants.OBJECTCLASS);
        if (objectClass != null) {
            buffer.append(Constants.OBJECTCLASS + "=");
            if (objectClass instanceof String) {
                buffer.append(objectClass);
            } else if (objectClass instanceof String[]) {
                buffer.append("[");
                for (String category : (String[]) objectClass) {
                    buffer.append(category);
                    buffer.append(" ");
                }
                buffer.append("]");
            }
            buffer.append(" ");
        }

        Object serialComport = sRef.getProperty(SerialEventListener.SERIAL_COMPORT);
        if (serialComport != null) {
            buffer.append(SerialEventListener.SERIAL_COMPORT + "=");
            if (serialComport instanceof String) {
                buffer.append(serialComport);
            } else if (serialComport instanceof String[]) {
                buffer.append("[");
                for (String part : (String[]) serialComport) {
                    buffer.append(part);
                    buffer.append(" ");
                }
                buffer.append("]");
            }
        }

        return buffer.toString();
    }

    public static String getSerialEventListenerComPort(ServiceReference sRef) {
        String comPortProps = null;
        Object serialComport = sRef.getProperty(SerialEventListener.SERIAL_COMPORT);
        if (serialComport instanceof String) {
            comPortProps = (String) serialComport;
        }
        return comPortProps;
    }
}
