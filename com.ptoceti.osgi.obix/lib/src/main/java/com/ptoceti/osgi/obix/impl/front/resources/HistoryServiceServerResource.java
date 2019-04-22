package com.ptoceti.osgi.obix.impl.front.resources;


import com.ptoceti.osgi.obix.cache.HistoryCache;
import com.ptoceti.osgi.obix.contract.History;
import com.ptoceti.osgi.obix.custom.contract.HistoryService;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.object.Op;
import com.ptoceti.osgi.obix.object.Ref;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.resources.ResourceException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;

@Singleton
@Path(HistoryServiceServerResource.uri)
public class HistoryServiceServerResource extends AbstractServerResource {

    public static final String uri = "/historyservice/";

    private HistoryCache cache;
	
	@Inject
	public HistoryServiceServerResource(HistoryCache cache){
		this.cache = cache;
	}

    @GET
    @Produces({"application/xml", "application/json"})
	public HistoryService retrieve() throws ResourceException {
		HistoryService serv = new HistoryService();
		Op make = serv.getMake();
		make.setHref(new Uri("uri", HistoryServiceServerResource.uri));
		return serv;
	}

    @POST
    @Consumes({"application/xml", "application/json"})
	public History make(Ref ref) throws ResourceException {
		try {
			History history = cache.make(ref);
			return history;
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".make", ex);
		}
	}

}
