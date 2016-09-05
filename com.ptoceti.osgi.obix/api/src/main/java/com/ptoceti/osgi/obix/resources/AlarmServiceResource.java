package com.ptoceti.osgi.obix.resources;

import org.restlet.resource.Get;
import org.restlet.resource.Post;

import com.ptoceti.osgi.obix.contract.Alarm;
import com.ptoceti.osgi.obix.custom.contract.AlarmService;
import com.ptoceti.osgi.obix.object.Ref;

public interface AlarmServiceResource {

public static String uri = "/alarmservice/";
	
	@Get
	public AlarmService retrieve() throws ResourceException;
	
	@Post
	Alarm make(Ref ref) throws ResourceException;
}
