package com.ptoceti.osgi.mqtt.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.log.LogService;




public class MqttClientFactory implements org.osgi.service.cm.ManagedServiceFactory {

	public static final String mqttClientCompositeIdentityKey = "com.ptoceti.osgi.mqtt.compositidentity";
	public static final String mqttClientUsernameKey = "com.ptoceti.osgi.mqtt.username";
	public static final String mqttClientPasswordKey = "com.ptoceti.osgi.mqtt.password";
	public static final String mqttClientServerUriKey = "com.ptoceti.osgi.mqtt.serveruri";
	public static final String mqttClientSSLPropsKey = "com.ptoceti.osgi.mqtt.sslpropts";
	public static final String mqttClientWillDestinationKey = "com.ptoceti.osgi.mqtt.willdestination";
	public static final String mqttClientWillMessageKey = "com.ptoceti.osgi.mqtt.willmessage";
	public static final String mqttClientMessageFormatter = "com.ptoceti.osgi.mqtt.messageformatter";
	public static final String mqttClientRootTopic = "com.ptoceti.osgi.mqtt.roottopic";
	public static final String mqttClientCleanSession = "com.ptoceti.osgi.mqtt.cleansession";
	public static final String mqttClientKeepAliveInterval = "com.ptoceti.osgi.mqtt.keepaliveinterval";
	public static final String mqttClientQos = "com.ptoceti.osgi.mqtt.qos";
	public static final String mqttClientPersitanceDir = "com.ptoceti.osgi.mqtt.persistance.dir";
	
	/**
	 * the hashtable contain the references to all SensorNode instances created.
	 */
	Hashtable<String, MqttClientWrapper> mqttClients;
	
	/**
	 *  a reference to the service registration for the SensorNodeFactory.
	 */
	ServiceRegistration mqttClientFactoryReg = null;
	
	
	public MqttClientFactory() {
		
		mqttClients = new Hashtable<String, MqttClientWrapper>();
		// register the class as a service factory.
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put( Constants.SERVICE_PID, this.getClass().getName());
		mqttClientFactoryReg = Activator.bc.registerService(ManagedServiceFactory.class.getName(),this, properties );
		
		Activator.log(LogService.LOG_INFO, "Registered " + MqttClientFactory.class.getName()
			+ " as " + ManagedServiceFactory.class.getName());
		
	}
	
	/**
	 * ManagedServiceFactory Interface method
	 *
	 * @return the name of this factory.
	 */
	public String getName() {
	
		return( this.getName());
	}

	@Override
	public void updated(String pid, Dictionary properties) throws ConfigurationException {
		
		String compositIdentity = (String) properties.get(mqttClientCompositeIdentityKey);
		String username = (String) properties.get(mqttClientUsernameKey);
		String password = (String) properties.get(mqttClientPasswordKey);
		String serveruri = (String) properties.get(mqttClientServerUriKey);
		String sslprops = (String) properties.get(mqttClientSSLPropsKey);
		String willdestination = (String) properties.get(mqttClientWillDestinationKey);
		String willmessage = (String) properties.get(mqttClientWillMessageKey);
		String rootTopic = (String) properties.get(mqttClientRootTopic);
		
		String messageFormaterServiceName = (String) properties.get(mqttClientMessageFormatter);
		
		// remove client with same id if it exists.
		deleted(pid);
		
		MqttConnectOptions mqttOptions = new MqttConnectOptions();
		mqttOptions.setUserName(username);
		mqttOptions.setPassword(password.toCharArray());
		mqttOptions.setServerURIs(serveruri.split(";"));

		
		if( sslprops != null){
			Properties props = new Properties();
			StringReader reader = new StringReader(sslprops);
			try {
				props.load(reader);
			} catch (IOException e) {
				Activator.log(LogService.LOG_ERROR, "Error loading ssl properties");
			}
			mqttOptions.setSSLProperties(props);
		}
		
		Object doCleanSession = properties.get(mqttClientCleanSession);
		Boolean cleanSession = doCleanSession instanceof Boolean ? (Boolean) doCleanSession: Boolean.parseBoolean(doCleanSession != null ? doCleanSession.toString(): "false");
		mqttOptions.setCleanSession(cleanSession);
		
		Object dokeepAliveInterval = (String) properties.get(mqttClientKeepAliveInterval);
		Integer keepAliveInterval = dokeepAliveInterval instanceof Integer ? (Integer) dokeepAliveInterval: Integer.parseInt(dokeepAliveInterval != null ? dokeepAliveInterval.toString(): "60");
		mqttOptions.setKeepAliveInterval(keepAliveInterval);
		
		Object doQos = (String) properties.get(mqttClientQos);
		Integer qos = doQos instanceof Integer ? (Integer) doQos: Integer.parseInt(doQos != null ? doQos.toString(): "0");
		
		String persistanceDir = (String) properties.get(mqttClientPersitanceDir);
		
		MqttClientWrapper mqttClient = new MqttClientWrapper(pid, qos > 2 ? 2: qos, persistanceDir, compositIdentity, rootTopic, messageFormaterServiceName, mqttOptions);
		
		mqttClients.put(pid, mqttClient);
	}

	@Override
	public void deleted(String pid) {
		MqttClientWrapper mqttClient = mqttClients.get( pid );
		// simple precaution, we first check that we effectively got an instance with this pid
		if ( mqttClient != null ) {
			// then we got rid of it.
			mqttClient.stop();
			mqttClients.remove(pid);
			Activator.log(LogService.LOG_INFO,"Removed Mqtt client type: " + mqttClient.getClass().getName() + ", service pid: " + pid);
		}
	}

	/**
	 * Called when the main bundles is stopped
	 */
	public void stop(){
		for ( String pid: mqttClients.keySet()) {
			mqttClients.get(pid).stop();
		}
		
	}
}
