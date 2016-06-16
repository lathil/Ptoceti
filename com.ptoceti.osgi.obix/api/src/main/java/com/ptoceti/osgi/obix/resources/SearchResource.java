package com.ptoceti.osgi.obix.resources;

import org.restlet.resource.Post;

import com.ptoceti.osgi.obix.custom.contract.SearchOut;
import com.ptoceti.osgi.obix.object.Ref;

public interface SearchResource {

	public static String uri = "/search/";
	
	@Post
	SearchOut search(Ref query) throws ResourceException;
}
