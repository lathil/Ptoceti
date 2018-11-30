package com.ptoceti.osgi.mqtt.impl;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;
import org.osgi.service.wireadmin.Consumer;
import org.osgi.service.wireadmin.Envelope;
import org.osgi.service.wireadmin.Producer;
import org.osgi.service.wireadmin.Wire;
import org.osgi.service.wireadmin.WireConstants;

import com.ptoceti.osgi.mqtt.IMqttMessageFomatter;

/**
 * Wrapper around the Eclipse Paho Mqtt client, that exposes it as a Osgi Wire producer and consumer.
 * As a consumer, when the wrapper is connected through a wire to producer, it publish Mqtt messages to the broker
 * when it is updated with new envelope. The object in the envelope is serialized to form the payload of the message.
 * As a producer, when the wrapper is connected to consumer(s), it subscribes to topics and when messages arrived from
 * the broker, it publish them to the consumer(s)
 * 
 * @author lor
 *
 */
public class MqttClientWrapper implements Producer, Consumer, ServiceListener, MqttCallback {

	public static final String ALLSCOPE = "*";
	
	public static final int CONNECTIONRESTARTDELAY = 10000;
	// 1 minute delay for reconnection attempts at the begenning
	public static final int CONNECTIONREPERIODSHORT = 60000;
	// 30 minutes for long term
	public static final int CONNECTIONREPERIODLONG = 60000 * 30;
	
	ServiceRegistration sReg;
	
	// the collection of wires the service must update with new values as it
	// produces them.
	protected Wire consumerWires[];
	// the collection of wires the service is connected to as a consumer.
	protected Wire producerWires[];
	
	// the mqtt client from Paho
	MqttAsyncClient mqttClient;
	// the connections option for mqtt
	MqttConnectOptions connectOptions;
	// the message formatter for sending mqtt payloads
	IMqttMessageFomatter mqttMessageFormatter;
	// the topic the client is subscribing to
	String rootTopic;
	
	Boolean subscribeLock = new Boolean(true);
	Boolean connectionLock = new Boolean(true);

	List<String> subscribedTopicScopes = new ArrayList<String>();

	ConnectionAgent connectionAgent;
	
	Integer qos = 0;

	/**
	 * Create the async mqtt client, registers as a producer and consumer, and get hold of a IMqttMessageFomatter service.
	 * Connect to the mqtt broker once the message formatter service is obtained.
	 * 
	 * @param pid the unique pid used to register the mqtt client with.
	 * @param qos the mqtt quality of service to use.
	 * @param persistanceDir the persistance file path to be used  with qos > 0
	 * @param compositeIdentity the wire admin composite identity
	 * @param rootTopic the topic to place in front of wire scope to form the full topic.
	 * @param messageFormaterServiceName the name of the IMqttMessageFomatter service to obtain.
	 * @param mqttOptions other generic mqtt option received from the service configuration
	 */
	public MqttClientWrapper(String pid, Integer qos, String persistanceDir, String compositeIdentity, String rootTopic, String messageFormaterServiceName, MqttConnectOptions mqttOptions) {

		List<String> producerScopes = new ArrayList<String>();
		List<String> consumerScopes = new ArrayList<String>();
		consumerScopes.add(ALLSCOPE);
		
		// Then we need to register our service into the framework.
		// We put here the name of the services interfaces under which to
		// register this service.
		String[] interfaces = new String[] { Producer.class.getName(), Consumer.class.getName(), MqttClientWrapper.class.getName() };

		// The composite identification of this Producer service.
		String[] composites = new String[] { compositeIdentity };

		// The type of objects that will be returned through the wire.
		Class[] flavors = new Class[] { Envelope.class };

		// put here the properties of the services.
		Dictionary props = new Hashtable();
		// set producer properties
		props.put(WireConstants.WIREADMIN_PRODUCER_COMPOSITE, composites);
		props.put(WireConstants.WIREADMIN_PRODUCER_SCOPE, producerScopes.toArray(new String[producerScopes.size()]));
		props.put(WireConstants.WIREADMIN_PRODUCER_FLAVORS, flavors);
		// set consumer properties
		props.put(WireConstants.WIREADMIN_CONSUMER_COMPOSITE, composites);
		props.put(WireConstants.WIREADMIN_CONSUMER_SCOPE, consumerScopes.toArray(new String[consumerScopes.size()]));
		props.put(WireConstants.WIREADMIN_CONSUMER_FLAVORS, flavors);

		props.put(Constants.SERVICE_PID, pid);
		props.put(Constants.SERVICE_DESCRIPTION, this.getClass().getName());
		sReg = Activator.bc.registerService(interfaces, this, props);

		Activator.log(LogService.LOG_INFO, "Registered " + this.getClass().getName() + " as " + MqttClientWrapper.class.getName() + ", Pid = " + pid);

		// create the connection agent in charge of re-connection
		connectionAgent = new ConnectionAgent();

		// remember the mqtt connection options
		connectOptions = mqttOptions;
		
		// remember the subscription topic
		this.rootTopic = rootTopic;
		// remember quality of service
		this.qos = qos;
		
		try {
			if(this.qos > 0 ){
				mqttClient = new MqttAsyncClient(connectOptions.getServerURIs()[0], (String)sReg.getReference().getProperty(Constants.SERVICE_PID), new MqttDefaultFilePersistence(persistanceDir));
			} else {
				mqttClient = new MqttAsyncClient(connectOptions.getServerURIs()[0], (String)sReg.getReference().getProperty(Constants.SERVICE_PID));
			}
			Activator.log(LogService.LOG_DEBUG, "Creating mqtt client id:" + (String)sReg.getReference().getProperty(Constants.SERVICE_PID) );
			mqttClient.setCallback(this);
		} catch (MqttException e) {
			Activator.log(LogService.LOG_ERROR, "Error creating mqtt client id:" + (String)sReg.getReference().getProperty(Constants.SERVICE_PID) + ", server uri: " + connectOptions.getServerURIs()[0] + ", error: " + e.toString());
		}
		
		
		// We need a reference to the mqtt message formater service for this client
		String filter = "(&(objectclass=" + IMqttMessageFomatter.class.getName() + ")" + "(" + IMqttMessageFomatter.MESSAGEFORMATTERNAME + "=" + messageFormaterServiceName + "))";
	
		try {
			Activator.bc.addServiceListener( this, filter);
			// in case the service is already registered, we send a REGISTERED event to its listener.
			ServiceReference mqttFomatterSrv[] = Activator.bc.getServiceReferences( IMqttMessageFomatter.class.getName(), filter );
			if( mqttFomatterSrv != null ) {
				this.serviceChanged(new ServiceEvent( ServiceEvent.REGISTERED, mqttFomatterSrv[0] ));
			}
		} catch ( InvalidSyntaxException e ) {
			// We known there shouldn't be an exception thrown here since we made the filter string.
		}
		
	}


	@Override
	public void updated(Wire wire, Object value) {
		if( mqttClient != null && mqttMessageFormatter != null){
			
			if (value instanceof Envelope[]) {
				Envelope[] envelopes = (Envelope[]) value;
				for (int i = 0; i < envelopes.length; i++) {
					publishMessage(envelopes[i]);
				}
			} else if (value instanceof Envelope) {
				publishMessage((Envelope)value);
			}
		}

	}
	/**
	 * Publish a MqttMessage to the broker through the mqtt client.
	 * The Mqtt message payload is made of the envelope .
	 * The Mqtt message topic is made of the envelope scope
	 * 
	 * @param enveloppe The envelope that will compose the message.
	 */
	private void publishMessage( Envelope enveloppe){
		MqttMessage message = new MqttMessage(mqttMessageFormatter.encode(enveloppe));
		message.setQos(this.qos);
		message.setRetained(false);
		String topic = enveloppe.getScope();
		// Remove white space
		topic = topic.replaceAll("\t\n\f\r", "");
		// Replace any '.' by '/'.
		topic = topic.replaceAll("[.]", "/");

		try {
			mqttClient.publish( topic, message);
		} catch (MqttPersistenceException e) {
			Activator.log(LogService.LOG_ERROR, "Mqtt client id: " +  mqttClient.getClientId() + ", error publish message" + e.toString());
		} catch (MqttException e) {
			Activator.log(LogService.LOG_ERROR, "Mqtt client id: " +  mqttClient.getClientId() + ", error publish message" + e.toString());
		}
	}

	@Override
	/**
	 * Method of the Consumer Interface. Called by the framework with the collection of
	 * Wires objects.
	 * This method is called when the Consumer service is first registered and subsequently whenever
	 * a Wire associated with this Consumer service becomes connected, is modified or becomes
	 * disconnected. The Wire Admin service must call this method asynchronously. This implies
	 * that implementors of Consumer can be assured that the callback will not take place during
	 * registration when they execute the registration in a synchronized method. 
	 *
	 * @param Wire[] an Array ow wires this Consumer is connected to.
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
						+ " producer disconnected.");
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
						+ " producer connected.");
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
						+ " producer connected.");
					}
				}
			}
		}
	}

	@Override
	public Object polled(Wire wire) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Method of the Producer Interface. Called by the framework with the collection of
	 * Wires objects the producer has to update with new values ( Measurement, Date,  .. )
	 * This list id built by the WireAdmin from the configuration it has found. If the configuration
	 * has been erased, this method is called with a null object.
	 *
	 * @param Wire[] an Array ow wires this Producer is connected to.
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
						+ " consumer disconnected.");
					}
				}
				unsubscribe();
				this.consumerWires = null;
			}
		} else if( this.consumerWires == null ) {
			this.consumerWires = wires;
			synchronized( this.consumerWires ) {
				if( this.consumerWires != null){
					for (int i = 0; i< wires.length; i++) {
						Activator.log( LogService.LOG_INFO, "Wire PID:"
						+ wires[i].getProperties().get(WireConstants.WIREADMIN_PID)
						+ " consumer connected.");
					}
				}
				subscribe();
			}
		} else {
			synchronized( this.consumerWires ) {
				this.consumerWires = wires;
				if( this.consumerWires != null){
					for (int i = 0; i< wires.length; i++) {
						Activator.log( LogService.LOG_INFO, "Wire PID:"
						+ wires[i].getProperties().get(WireConstants.WIREADMIN_PID)
						+ " consumer connected.");
					}
					subscribe();
				}
			}
		}
	}
	
	/**
	 * Subscribe the mqtt client to a set of topics at the broker.
	 * The set of topic is extracted from the scopes of the consumers wires connected. Each scoped is append '#' to make it
	 * a multi level topic.
	 * 
	 */
	private void subscribe(){
		synchronized(subscribeLock){
			if( mqttClient != null && mqttClient.isConnected() && this.consumerWires != null && this.consumerWires.length > 0){
				try {
					List<String> topicScopes = new ArrayList<String>();
					synchronized( this.consumerWires ) {
					
						for( int i = 0; i < consumerWires.length; i++ ) {
							Wire wire = consumerWires[i];
							String scopes[] = wire.getScope();
							for( String scope : scopes){
								if( !topicScopes.contains(scope)){
									String multiLevelTopic = scope.trim();
									if( !multiLevelTopic.endsWith("/")) multiLevelTopic.concat("/");
									multiLevelTopic.concat("#");
									topicScopes.add(scope);
								}
							}
						}
					}
					
					if(topicScopes.size() == 0){
						topicScopes.add("#");
					}
					
					// calculate the additional scopes we have to add since last time we subscribe
					List<String> retainedScopes = new ArrayList<String>(topicScopes);
					retainedScopes.removeAll(subscribedTopicScopes);
					
					// calculate the list of scopes we do not subscribe anymore
					List<String> supressedScopes = new ArrayList<String>();
					for( String nextScope : subscribedTopicScopes){
						if( !topicScopes.contains(nextScope)){
							supressedScopes.add(nextScope);
						}
					}
					
					if(supressedScopes.size() > 0){
						mqttClient.unsubscribe(supressedScopes.toArray(new String[supressedScopes.size()]));
						Activator.log(LogService.LOG_DEBUG, "Unsubscribing to topic:" + supressedScopes.toString() +  " with client id: " + (String)sReg.getReference().getProperty(Constants.SERVICE_PID));
						this.subscribedTopicScopes.removeAll(supressedScopes);
					}
					if(retainedScopes.size() > 0){
						int[] qoss = new int[retainedScopes.size()];
						for( int i = 0 ; i < retainedScopes.size(); i ++) { qoss[i] = this.qos; }
						mqttClient.subscribe(retainedScopes.toArray(new String[retainedScopes.size()]), qoss);
						Activator.log(LogService.LOG_DEBUG, "Subscribing to topic:" + retainedScopes.toString() +  " with client id: " + (String)sReg.getReference().getProperty(Constants.SERVICE_PID));
						this.subscribedTopicScopes.addAll(retainedScopes);
					}
				
					

					
				} catch (MqttException e) {
					Activator.log(LogService.LOG_ERROR, "Error subscribing to topic:" + rootTopic +  " with client id: " + (String)sReg.getReference().getProperty(Constants.SERVICE_PID) + ", error: " + e.toString());
				}
			}
		}
	}
	
	/**
	 *  Unsubscribe the client to topics at the broker. The list of topics to unsubscribe is made of all the topic actively subscribed to.
	 * 
	 */
	private void unsubscribe(){
		synchronized(subscribeLock){
			if( mqttClient != null && mqttClient.isConnected() ){
				try {
					mqttClient.unsubscribe(this.subscribedTopicScopes.toArray(new String[this.subscribedTopicScopes.size()]));
					this.subscribedTopicScopes.clear();
				} catch (MqttException e) {
					Activator.log(LogService.LOG_ERROR, "Error unsubscribing to topic:" + rootTopic +  " with client id: " + (String)sReg.getReference().getProperty(Constants.SERVICE_PID) + ", error: " + e.toString());
				}
			}
		}
	}

	/**
	 * Called when the wrapper received allocation of the converter service. Connect to the broker and once the connection is made subscribe to topics
	 * if the list of consumers has been received.
	 * 
	 * 
	 */
	private void start(){
		synchronized(connectionLock){

			Activator.log(LogService.LOG_WARNING, "Mqtt connecting client id:" + (String)sReg.getReference().getProperty(Constants.SERVICE_PID) + ", server uri: " + connectOptions.getServerURIs()[0] );
			try {
				mqttClient.connect(connectOptions, new IMqttActionListener(){

					@Override
					public void onFailure(IMqttToken token, Throwable e) {
						Activator.log(LogService.LOG_ERROR, "Error connecting mqtt client id:" + (String)sReg.getReference().getProperty(Constants.SERVICE_PID) + ", server uri: " + connectOptions.getServerURIs()[0] + ", error: " + e.toString());
						connectionAgent.notifyDisconnect();
					}

					@Override
					public void onSuccess(IMqttToken token) {
						Activator.log(LogService.LOG_INFO, "Connecting mqtt client id:" + (String)sReg.getReference().getProperty(Constants.SERVICE_PID) + ", server uri: " + connectOptions.getServerURIs()[0]);
						connectionAgent.notifyReconnect();
						// On connection, subscribe
						subscribe();
					}

				});

			} catch (MqttException e) {
				Activator.log(LogService.LOG_ERROR, "Error creating mqtt client id:" + (String)sReg.getReference().getProperty(Constants.SERVICE_PID) + ", server uri: " + connectOptions.getServerURIs()[0] + ", error: " + e.toString());
			}

		}
	}
	
	/**
	 * Unsubscribe the listed topics and disconnect from the mqtt broker.
	 * 
	 */
	public void stop(){
		synchronized(connectionLock){
			try {
				unsubscribe();
				connectionAgent.stop();
				mqttClient.disconnect();
			} catch (MqttException e) {
				Activator.log(LogService.LOG_ERROR, "Error closing mqtt client id:" + mqttClient.getClientId() + ", server uri: " + mqttClient.getServerURI() + ", error: " + e.toString());
			}
		}
	}

	@Override
	public void serviceChanged(ServiceEvent event) {
		ServiceReference sr = event.getServiceReference();
		switch(event.getType()) {
			case ServiceEvent.REGISTERED: {
				mqttMessageFormatter = (IMqttMessageFomatter) Activator.bc.getService(sr);
				Activator.log( LogService.LOG_INFO, "Getting instance of service: " + (String) sr.getProperty( Constants.SERVICE_PID)
					+ ", " + Constants.SERVICE_ID + "=" + ((Long)sr.getProperty(Constants.SERVICE_ID)).toString() );
				start();
			}
			break;
			case ServiceEvent.UNREGISTERING: {
				Activator.log( LogService.LOG_INFO, "Releasing instance of service: " + (String) sr.getProperty( Constants.SERVICE_PID)
					+ ", " + Constants.SERVICE_ID + "=" + ((Long)sr.getProperty(Constants.SERVICE_ID)).toString());
				mqttMessageFormatter = null;
				stop();
			}
			break;
		}
		
	}

	@Override
	public void connectionLost(Throwable arg0) {
		Activator.log(LogService.LOG_WARNING, "Mqtt connection lost. Client id:" + (String)sReg.getReference().getProperty(Constants.SERVICE_PID) + ", server uri: " + connectOptions.getServerURIs()[0] + ", error: " + arg0.toString());
		try {
			// force unsubscribing to all subjects
			mqttClient.unsubscribe(this.subscribedTopicScopes.toArray(new String[this.subscribedTopicScopes.size()]));
		} catch ( MqttException ex){

		}

		// clear the subscribed topics list, will have to re-subscribe
		this.subscribedTopicScopes.clear();
		// notify reconnection thread to start attemptng reconnect
		connectionAgent.notifyDisconnect();

	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		Activator.log(LogService.LOG_DEBUG, "Mqtt message delivered. Client id:" + (String)sReg.getReference().getProperty(Constants.SERVICE_PID) + ", message id: " + token.getMessageId() );
	}

	/**
	 * When message arrives, dispatch the value to all connected consumers.
	 * 
	 */
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		Activator.log(LogService.LOG_DEBUG, "Mqtt message arrived. Client id:" + (String)sReg.getReference().getProperty(Constants.SERVICE_PID) );
		Object value = mqttMessageFormatter.decode(message.getPayload());
		
		if( consumerWires != null ) {
			synchronized( this.consumerWires ) {
			
				// We must update every wire in the wire collection
				for( int i = 0; i < consumerWires.length; i++ ) {
					Wire wire = consumerWires[i];
					wire.update(value);
					
				}
			}
		}
	}




	/**
	 * Runnable class in charge of reconnecting mqtt client in case of disconnection
	 * Disconnection can occur when:
	 * - mqtt server shutdown
	 * - mqtt clien has been forced to disconnect
	 * - network problem
	 *
	 */
	public class ConnectionAgent implements  Runnable {

		Thread connectionThread;

		Boolean disconnectLock = new Boolean(true);
		boolean disconnectFlag = false;
		Boolean reconnectLock = new Boolean(true);
		boolean reconnectFlag = false;

		int reconnectAttemptsCount = 0;

		public ConnectionAgent() {

			connectionThread = new Thread(this);
			connectionThread.start();
		}

		/**
		 * Main thread.
		 * Wait for disconnect event, then attempt to reconnect. If failed wait for 1 minute and try again, as long as not reconnected.
		 * After 30 tries, waiting period between reconnection attempts increase to 30 minutes.
		 */
		public void run() {

			Activator.log(LogService.LOG_DEBUG, "Connection agent started.");
			while(!connectionThread.isInterrupted()){
				try {
					// wait for a disconnection message
					waitDisconect();
					Activator.log(LogService.LOG_WARNING, "Connection agent detected connection lost. Client id:" + (String)sReg.getReference().getProperty(Constants.SERVICE_PID) + ", server uri: " + connectOptions.getServerURIs()[0]);

					synchronized (this) {
						// first 30 tries each at 1 minute interval
						if( reconnectAttemptsCount < 30 ) {
							wait(CONNECTIONREPERIODSHORT);
						} else {
							// afte that try every 30 minutes
							wait(CONNECTIONREPERIODLONG);
						}
					}
					reconnectAttemptsCount++;
					// try to reconnect
					start();

				} catch ( InterruptedException ex) {
					connectionThread.interrupt();
				}

			}
			Activator.log(LogService.LOG_DEBUG, "Connection agent stopped");

		}

		public void stop(){
			Activator.log(LogService.LOG_DEBUG, "Connection agent stopping");
			connectionThread.interrupt();
		}

		protected void notifyDisconnect() {
			synchronized(disconnectLock){
				Activator.log(LogService.LOG_DEBUG, "notify disconnect");
				disconnectFlag = true;
				disconnectLock.notifyAll();
			}

		}

		protected void waitDisconect() throws InterruptedException {
			synchronized (disconnectLock){
				Activator.log(LogService.LOG_DEBUG, "wait disconnect");
				while(!disconnectFlag) {
					disconnectLock.wait();
				}
				disconnectFlag = false;
			}
		}

		protected void notifyReconnect() {
			synchronized(reconnectLock){
				reconnectAttemptsCount = 0;
				reconnectFlag = true;
				reconnectLock.notifyAll();
			}
		}

		protected void waitReconnect() throws InterruptedException{
			synchronized (reconnectLock){
				reconnectFlag = false;
				while(!reconnectFlag) {
					reconnectLock.wait();
				}
			}
		}
	}
}
