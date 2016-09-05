package com.ptoceti.osgi.obix.resources;

import org.restlet.resource.Post;

import com.ptoceti.osgi.obix.contract.AlarmAckIn;
import com.ptoceti.osgi.obix.contract.AlarmAckOut;


public interface AlarmAckResource {

public static String baseuri = "ack/";
	
	public static String uri = AlarmResource.uri.concat(baseuri);
	
	@Post
	AlarmAckOut ackAlarm(AlarmAckIn in) throws ResourceException;
}
