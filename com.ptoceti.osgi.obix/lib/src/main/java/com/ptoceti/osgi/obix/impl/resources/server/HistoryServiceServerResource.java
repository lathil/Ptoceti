package com.ptoceti.osgi.obix.impl.resources.server;

import org.restlet.resource.Get;
import org.restlet.resource.Post;

import com.google.inject.Inject;
import com.ptoceti.osgi.obix.cache.HistoryCache;
import com.ptoceti.osgi.obix.contract.History;
import com.ptoceti.osgi.obix.custom.contract.HistoryService;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.object.Op;
import com.ptoceti.osgi.obix.object.Ref;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.resources.HistoryServiceResource;
import com.ptoceti.osgi.obix.resources.ResourceException;

public class HistoryServiceServerResource extends AbstractServerResource implements HistoryServiceResource{

private HistoryCache cache;
	
	@Inject
	public HistoryServiceServerResource(HistoryCache cache){
		this.cache = cache;
	}
	
	@Get
	public HistoryService retrieve() throws ResourceException {
		HistoryService serv = new HistoryService();
		Op make = serv.getMake();
		make.setHref(new Uri("uri", HistoryServiceServerResource.uri));
		return serv;
	}

	@Post("xml|json")
	public History make(Ref ref) throws ResourceException {
		try {
			History history = cache.make(ref);
			return history;
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".make", ex);
		}
	}

}
