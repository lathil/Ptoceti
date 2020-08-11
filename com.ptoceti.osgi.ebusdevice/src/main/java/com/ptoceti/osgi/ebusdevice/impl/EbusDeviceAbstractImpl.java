package com.ptoceti.osgi.ebusdevice.impl;

import com.ptoceti.osgi.ebusdevice.EbusDevice;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;
import org.osgi.service.wireadmin.*;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public abstract class EbusDeviceAbstractImpl implements EbusDevice {

    ServiceRegistration sReg;

    static protected final String ebusDriverServiceName = com.ptoceti.osgi.ebus.EbusDriver.class.getName();

    // the collection of wires the service must update with new values as it produces them.
    protected Wire consumerWires[];
    // the collection of wires the service is connected to as a consumer.
    protected Wire producerWires[];

    protected void init(String pid, String compositeIdentity){

        // Because the service is a composite producer, we need to produce a scope list that will indicate the intent of each data measurement
        // the service is producing. The intent is simply the name of each data measurement.

        List<String> producerScopes = new ArrayList<String>();
        List<String> consumerScopes = new ArrayList<String>();

        // Then we need to register our service into the framework.
        // We put here the name of the services interfaces under which to register this service.
        String[] interfaces = new String[] {
                Producer.class.getName(),
                Consumer.class.getName(),
                EbusDevice.class.getName()
        };

        // The composite identification of this Producer service.
        String[] composites = new String[] {
                compositeIdentity
        };

        // The type of objects that will be returned through the wire.
        Class[] flavors = new Class[] {
                Envelope.class
        };

        // put here the properties of the services.
        Dictionary props = new Hashtable();
        // set producer properties
        props.put( WireConstants.WIREADMIN_PRODUCER_COMPOSITE, composites);
        props.put( WireConstants.WIREADMIN_PRODUCER_SCOPE, producerScopes.toArray(new String[producerScopes.size()] ));
        props.put( WireConstants.WIREADMIN_PRODUCER_FLAVORS, flavors);
        // set consumer properties
        props.put( WireConstants.WIREADMIN_CONSUMER_COMPOSITE, composites);
        props.put( WireConstants.WIREADMIN_CONSUMER_SCOPE, consumerScopes.toArray(new String[consumerScopes.size()] ));
        props.put( WireConstants.WIREADMIN_CONSUMER_FLAVORS, flavors);

        props.put( Constants.SERVICE_PID, pid);
        props.put( Constants.SERVICE_DESCRIPTION, "ModbusDevice service");
        sReg = Activator.bc.registerService( interfaces, this, props );

        Activator.log(LogService.LOG_INFO, "Registered " + this.getClass().getName()
                + " as " +  EbusDevice.class.getName() + ", Pid = " + pid);


    }

    /**
     * Method of the Consumer Interface. Called by the framework with the collection of
     * Wires objects.
     * This method is called when the Consumer service is first registered and subsequently whenever
     * a Wire associated with this Consumer service becomes connected, is modified or becomes
     * disconnected. The Wire Admin service must call this method asynchronously. This implies
     * that implementors of Consumer can be assured that the callback will not take place during
     * registration when they execute the registration in a synchronized method.
     *
     * @param wires: an Array ow wires this Consumer is connected to.
     *
     */
    public void producersConnected(Wire[] wires ) {

        if( wires == null){
            if( this.producerWires != null){
                synchronized(this.producerWires)
                {
                    for (int i = 0; i< this.producerWires.length; i++) {
                        Activator.log( LogService.LOG_INFO, "Wire PID:"
                                + this.producerWires[i].getProperties().get(WireConstants.WIREADMIN_PID)
                                + " disconnected.");
                    }
                }
                this.producerWires = null;
            }
        } else if( this.producerWires == null ) {
            this.producerWires = wires;
            synchronized( this.producerWires ) {
                if( this.producerWires != null){
                    for (int i = 0; i< wires.length; i++) {
                        Activator.log( LogService.LOG_INFO, "Wire PID:"
                                + wires[i].getProperties().get(WireConstants.WIREADMIN_PID)
                                + " connected.");
                    }
                }
            }
        } else {
            synchronized( this.producerWires ) {
                this.producerWires = wires;
                if( this.producerWires != null){
                    for (int i = 0; i< wires.length; i++) {
                        Activator.log( LogService.LOG_INFO, "Wire PID:"
                                + wires[i].getProperties().get(WireConstants.WIREADMIN_PID)
                                + " connected.");
                    }
                }
            }
        }
    }

    /**
     * Method of the Consumer Interface. Called by the Wire object whenever its value is updated by
     * the Producer.
     *
     * @param wire
     * @param value
     */
    public void updated(Wire wire, Object value ) {

    }

    /**
     * Method of the Producer Interface. Called by the framework with the collection of
     * Wires objects the producer has to update with new values ( Measurement, Date,  .. )
     * This list id built by the WireAdmin from the configuration it has found. If the configuration
     * has been erased, this method is called with a null object.
     *
     * @param wires an Array ow wires this Producer is connected to.
     *
     */
    public void consumersConnected( Wire[] wires ) {
        // simply remember the whole collection. Discard any previous.

        if( wires == null){
            if( this.consumerWires != null) {
                synchronized(this.consumerWires)
                {
                    for (int i = 0; i< this.consumerWires.length; i++) {
                        Activator.log( LogService.LOG_INFO, "Wire PID:"
                                + this.consumerWires[i].getProperties().get(WireConstants.WIREADMIN_PID)
                                + " disconnected.");
                    }
                }
                this.consumerWires = null;
            }
        } else if( this.consumerWires == null ) {
            this.consumerWires = wires;
            synchronized( this.consumerWires ) {
                if( this.consumerWires != null){
                    for (int i = 0; i< wires.length; i++) {
                        Activator.log( LogService.LOG_INFO, "Wire PID:"
                                + wires[i].getProperties().get(WireConstants.WIREADMIN_PID)
                                + " connected.");
                    }
                }
            }
        } else {
            synchronized( this.consumerWires ) {
                this.consumerWires = wires;
                if( this.consumerWires != null){
                    for (int i = 0; i< wires.length; i++) {
                        Activator.log( LogService.LOG_INFO, "Wire PID:"
                                + wires[i].getProperties().get(WireConstants.WIREADMIN_PID)
                                + " connected.");
                    }
                }
            }
        }
    }

    /**
     * Method of the Producer Interface. There is two method for updating the wires: push and pull.
     * With the push method, the producer updates itself the wires ( the list of wires is keept internally )
     * with the new values. With the pull method, the consumer ask the framework for a new value on a wire
     * (it does that because it does not know the producer, this is of the framework domain). The framework
     * in turn poll the producer with the wire reference. The producer can then update the referenced wire.
     *
     *
     * @param wire the wire that invoque this method.
     * @return An Envelope[] Array.
     */
    public Object polled( Wire wire ) {

        if( wire != null ) {
            ArrayList envelopeList = new ArrayList();


            // return everything as an array of Envelope objects.
            return envelopeList.toArray(new Envelope[0]);
        } else {
            return null;
        }
    }

    /**
     * Refresh all the wires that this Producer service is connected to. This list of wires is provided by the
     * WireAdmnin service when invoquing the consumerConnected method of this object. This method then call the
     * update() method on each Wire. Before updating, checks are done to insure that the value type is
     * included in the Consumer's flavors. The update() method on the wire is called with an Envelope object
     * as argument.
     *
     */
    protected void refreshWires() {

        if( consumerWires != null ) {
            synchronized( this.consumerWires ) {

                // We must update every wire in the wire collection
                for( int i = 0; i < consumerWires.length; i++ ) {
                    Wire wire = consumerWires[i];
                    // Get the Consumer service flavors. Accessible from the Wire's getFlavors() method.
                    Class flavors[] = wire.getFlavors();

                    if( flavors != null ) {

                    }
                }
            }
        }
    }
}
