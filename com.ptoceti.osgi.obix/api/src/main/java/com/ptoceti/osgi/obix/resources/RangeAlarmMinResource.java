package com.ptoceti.osgi.obix.resources;

import org.restlet.resource.Post;

import com.ptoceti.osgi.obix.object.Val;

public interface RangeAlarmMinResource {

public static String baseuri = "min/";
	
	public static String uri = AlarmResource.uri.concat(baseuri);
	
	@Post
	void setMin(Val min) throws ResourceException;
}
