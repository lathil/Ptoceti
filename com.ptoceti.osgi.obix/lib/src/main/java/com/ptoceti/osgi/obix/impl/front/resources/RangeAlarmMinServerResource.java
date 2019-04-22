package com.ptoceti.osgi.obix.impl.front.resources;


import com.ptoceti.osgi.obix.cache.AlarmCache;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.object.Val;
import com.ptoceti.osgi.obix.resources.ResourceException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;

@Singleton
public class RangeAlarmMinServerResource extends AbstractServerResource {

    public static final String baseuri = "min/";
    public static final String uri = AlarmServerResource.uri.concat(baseuri);

    private AlarmCache cache;
	
	@Inject
	public RangeAlarmMinServerResource(AlarmCache cache){
		this.cache = cache;
	}

    @PUT
    @Consumes({"application/xml", "application/json"})
    public void setMin(@PathParam(AlarmServerResource.ALARM_URI) String alarmuri, Val min) throws ResourceException {
        String alarmUri = AlarmServerResource.baseuri.concat("/").concat(alarmuri).concat("/");
		
		try {
			cache.setMin(alarmUri, min);
			
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".retrieve", ex);
		}
	}

}
