package com.ptoceti.osgi.mqtt;

public interface IMqttMessageFomatter {

	public static final String MESSAGEFORMATTERNAME = "IMQTTMESSAGEFORMATTERNAME";
	
	byte[] encode(Object in);
	Object decode(byte[] in);
}
