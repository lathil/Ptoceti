package com.ptoceti.osgi.obix.resources;

import org.restlet.resource.Post;

import com.ptoceti.osgi.obix.object.Val;

public interface RangeAlarmMaxResource {

	public static String baseuri = "max/";
	
	public static String uri = AlarmResource.uri.concat(baseuri);
	
	@Post
	void setMax(Val max) throws ResourceException;
}
