package com.ptoceti.osgi.rest.impl;

import com.ptoceti.osgi.mqtt.MqttService;
import org.osgi.framework.*;
import org.osgi.service.wireadmin.WireAdmin;

import java.util.HashMap;
import java.util.Map;

public class MqttServiceListener implements ServiceListener {
    protected BundleContext bc;
    protected Map<String, MqttService> mqttServices = new HashMap<String, MqttService>();

    public MqttServiceListener(BundleContext bundleContext) throws BundleException {
        bc = bundleContext;
        String filter = "(objectclass=" + MqttService.class.getName() + ")";
        try {
            bc.addServiceListener(this, filter);
            ServiceReference srLog = bc.getServiceReference(MqttService.class.getName());
            if (srLog != null) {
                this.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, srLog));
            }
        } catch (InvalidSyntaxException e) {
            throw new BundleException("Error in filter string while registering MqttServiceListener." + e.toString());
        }
    }

    /**
     * Unique method of the ServiceListener interface.
     */
    public void serviceChanged(ServiceEvent event) {

        ServiceReference sr = event.getServiceReference();

        switch (event.getType()) {
            case ServiceEvent.REGISTERED: {

                String pid = sr.getProperty(Constants.SERVICE_PID).toString();
                if (!mqttServices.containsKey(pid)) {
                    mqttServices.put(pid, (MqttService) bc.getService(sr));
                    sr.getProperty(Constants.SERVICE_PID);
                    Activator.getLogger().info("Detecting MqttService");
                }

            }
            break;
            case ServiceEvent.UNREGISTERING: {
                String pid = sr.getProperty(Constants.SERVICE_PID).toString();
                if (mqttServices.containsKey(pid)) {
                    mqttServices.remove(pid);
                }
            }
            break;
        }
    }


    public Map<String, MqttService> get() {
        return mqttServices;
    }
}
