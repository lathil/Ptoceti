package com.ptoceti.ebus.ebusdevice.impl;

import com.ptoceti.ebus.ebusdevice.EbusDevice;
import com.ptoceti.osgi.ebus.EbusDriver;
import org.osgi.framework.*;
import org.osgi.service.log.LogService;
import org.osgi.service.wireadmin.*;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class EbusDeviceImpl extends EbusDeviceAbstractImpl {


    // A thread object that get hold of an Ebus driver service.
    private EbusDriverCommunicator ebuCommunicator = null;

    public EbusDeviceImpl( String pid, String compositeIdentity, String ebusPort, int modbusId, int  modbusPoolingRateS) {
        // Force the list of wires to null. It will get initialiwed by the wire admin.
        consumerWires = null;

        init(pid, compositeIdentity );

        // Create a new ModbusDriverCommunicator. Will be responsible to communicate with the device.
        ebuCommunicator = new EbusDriverCommunicator(ebusPort, modbusId, modbusPoolingRateS);
    }

    public int getId() {
        return 0;
    }

    public String getPortName() {
        return "";
    }
    /**
     * Stop this ModbusDevice; Stopping consist in stopping any threads instanciated by this service. Here we
     * stop the ModbusCommunicator thread that pool on the ModbusDriver service. Everything else is taken care
     * of by the framework.
     *
     *
     */
    public void stop() {

        ebuCommunicator.disconnect();
        sReg.unregister();
        Activator.log(LogService.LOG_INFO, "Unregistered " + this.getClass().getName());
    }

    /**
     * ModbusDriverCommunicator managed the communication with the modbus device; Its send modbus requests
     * to get state of the holding and input registers, and stores the response in the outer class HoldingRegisters
     * and InputRegisters objects. The class own a thread that pool the device at regular time.
     * The communication to the modbus link is done through the ModbusDriver service. The class is registered as a Listener
     * to the service, so it will received even when the ModbusDriver service is registered and de-registered from
     * the framework. The class will only send messages to the Modbus link if it got a valid instance of a
     * ModbusDriver service.
     *
     */

    private class EbusDriverCommunicator implements Runnable, ServiceListener {

        // the id of the ModbusDevice
        private int id;
        // the port name to use.
        private String portName;
        // a reference to the modbus driver service
        private EbusDriver ebusDr;
        // a flag asking to suspend communicating with the device.
        private boolean disconnect = false;
        // pooling time between successive communications with the modbus device.
        private long poolingTimeMill = 1000;
        // the thread that manage the communucation work with the device.
        Thread myThread = null;

        public EbusDriverCommunicator( String ebusPort, int ebusId, int  ebusPoolingRateS ) {
            this.id = ebusId;
            this.portName = ebusPort;
            this.poolingTimeMill = ((long)ebusPoolingRateS);

            // We first need to get a reference to the modbus driver service. We try to get this reference dynamically
            // by constructing a listener that will detect when the modbus driver service appear or disapear.
            String filter = "(&(objectclass=" + ebusDriverServiceName + ")"
                    + "(" + EbusDriver.EBUS_PORT + "=" + ebusPort + "))";

            try {
                Activator.bc.addServiceListener( this, filter);
                // in case the service is already registered, we send a REGISTERED event to its listener.
                ServiceReference srEbus[] = Activator.bc.getServiceReferences( ebusDriverServiceName, filter );
                if( srEbus != null ) {
                    this.serviceChanged(new ServiceEvent( ServiceEvent.REGISTERED, srEbus[0] ));
                }
            } catch ( InvalidSyntaxException e ) {
                // We known there shouldn't be an exception thrown here since we made the filter string.
            }
        }

        public int getId(){
            return this.id;
        }

        public String getPortName() {
            return this.portName;
        }

        synchronized public void disconnect(){
            disconnect = true;
        }

        /**
         * Unique method of the Runnable interface.
         * Excecute the class ModbusDriverCommunicator's thread that will send messages
         * to the actual device in order to actualise it's buffered state in this class.
         * After the thread has sent all necessary messages, it will pause for a
         * predefined time.
         *
         */
        public void run() {
            while(!getDisconnect()){
                if(ebusDr != null ) {

                    refreshWires();
                }
                try {
                    Thread.sleep( poolingTimeMill );
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        synchronized public boolean getDisconnect(){
            return disconnect;
        }

        /**
         * Unique method of the ServiceListener interface. The framework invoke this method when
         * a event has been posted. Since we registered this listener for registered and unregistered
         * event from the modbus driver service, we will receive notification of theses here.
         *
         */
        public void serviceChanged( ServiceEvent event ) {

            ServiceReference sr = event.getServiceReference();
            switch(event.getType()) {
                case ServiceEvent.REGISTERED: {
                    ebusDr = (EbusDriver) Activator.bc.getService(sr);
                    myThread = new Thread(this);
                    myThread.start();
                    Activator.log( LogService.LOG_INFO, "Getting instance of service: " + (String) sr.getProperty( Constants.SERVICE_PID)
                            + ", " + Constants.SERVICE_ID + "=" + ((Long)sr.getProperty(Constants.SERVICE_ID)).toString() );
                }
                break;
                case ServiceEvent.UNREGISTERING: {
                    Activator.log( LogService.LOG_INFO, "Releasing instance of service: " + (String) sr.getProperty( Constants.SERVICE_PID)
                            + ", " + Constants.SERVICE_ID + "=" + ((Long)sr.getProperty(Constants.SERVICE_ID)).toString());
                    ebusDr = null;
                    myThread.interrupt();
                }
                break;
            }
        }
    }
}
