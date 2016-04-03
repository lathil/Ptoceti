package com.ptoceti.osgi.obix.resources;

import org.restlet.resource.Get;
import org.restlet.resource.Post;

import com.ptoceti.osgi.obix.contract.History;
import com.ptoceti.osgi.obix.custom.contract.HistoryService;
import com.ptoceti.osgi.obix.object.Ref;
import com.ptoceti.osgi.obix.object.Uri;

public interface HistoryServiceResource {

public static String uri = "/historyservice/";
	
	@Get
	public HistoryService retrieve() throws ResourceException;
	
	@Post
	History make(Ref ref) throws ResourceException;
}
