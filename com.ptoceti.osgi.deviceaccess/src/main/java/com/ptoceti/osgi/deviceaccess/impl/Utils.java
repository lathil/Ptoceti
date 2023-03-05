package com.ptoceti.osgi.deviceaccess.impl;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

public class Utils {

    public static int getServiceRanking(ServiceReference service) {
        int serviceRanking = new Integer(0);
        Object ranking = service.getProperty(Constants.SERVICE_RANKING);
        if (ranking != null) {
            serviceRanking = Integer.class.cast(ranking).intValue();
        }

        return serviceRanking;
    }

    public static int getServiceId(ServiceReference service) {
        int serviceId = Integer.MAX_VALUE;
        Object id = service.getProperty(Constants.SERVICE_ID);
        if (id != null) {
            serviceId = Integer.class.cast(id).intValue();
        }

        return serviceId;
    }

    public static boolean isDal(ServiceReference sRef) {
        String deviceCategory = sRef.getProperty(org.osgi.service.device.Constants.DEVICE_CATEGORY).toString();
        if (deviceCategory != null && deviceCategory.equals("DAL")) {
            return true;
        }
        return false;
    }

    public static String deviceDetails(ServiceReference sRef) {

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

        Object devCategory = sRef.getProperty(org.osgi.service.device.Constants.DEVICE_CATEGORY);
        if (devCategory != null) {
            buffer.append(org.osgi.service.device.Constants.DEVICE_CATEGORY + "=");
            if (devCategory instanceof String) {
                buffer.append(devCategory);
            } else if (devCategory instanceof String[]) {
                buffer.append("[");
                for (String category : (String[]) devCategory) {
                    buffer.append(category);
                    buffer.append(" ");
                }
                buffer.append("]");
            }
        }

        return buffer.toString();
    }

    public static String driverDetails(ServiceReference sRef) {
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

        Object driverId = sRef.getProperty(org.osgi.service.device.Constants.DRIVER_ID);
        if (driverId != null) {
            buffer.append(org.osgi.service.device.Constants.DRIVER_ID + "=");
            if (driverId instanceof String) {
                buffer.append(driverId);
            }
        }

        return buffer.toString();
    }

    public static String getDriverID(ServiceReference sRef) {
        Object driverId = sRef.getProperty(org.osgi.service.device.Constants.DRIVER_ID);
        if (driverId != null && driverId instanceof String) {
            return (String) driverId;
        }
        return null;
    }
}
