package com.ptoceti.osgi.obix.resources;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;

import com.ptoceti.osgi.obix.contract.Alarm;
import com.ptoceti.osgi.obix.contract.Nil;

public interface AlarmResource {

public static final String ALARM_URI = "alarmuri";
	
	public static String baseuri = "/alarm";
	
	public static String uri = baseuri + "/{" + ALARM_URI + "}/";
	
	@Get
	public Alarm retrieve() throws ResourceException;
	
	@Delete
	public Nil remove() throws ResourceException;
}
