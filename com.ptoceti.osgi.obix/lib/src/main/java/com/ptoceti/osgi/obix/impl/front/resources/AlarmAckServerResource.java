package com.ptoceti.osgi.obix.impl.front.resources;


import com.ptoceti.osgi.obix.cache.AlarmCache;
import com.ptoceti.osgi.obix.contract.Alarm;
import com.ptoceti.osgi.obix.contract.AlarmAckIn;
import com.ptoceti.osgi.obix.contract.AlarmAckOut;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.resources.ResourceException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;

@Singleton
public class AlarmAckServerResource extends AbstractServerResource {

    public static final String baseuri = "ack/";

    public static final String uri = AlarmServerResource.uri.concat(baseuri);

    private AlarmCache cache;
	
	@Inject
	public AlarmAckServerResource(AlarmCache cache) {
		this.cache = cache;
	}


    @POST
    @Consumes({"application/xml", "application/json"})
    public AlarmAckOut ackAlarm(@PathParam(AlarmServerResource.ALARM_URI) String alarmuri, AlarmAckIn in) throws ResourceException {
        String alarmUri = AlarmServerResource.baseuri.concat("/").concat(alarmuri).concat("/");
		AlarmAckOut result = new AlarmAckOut();
		try {
			Alarm alarm = cache.ack(alarmUri, (in.getAckuser() != null ? (String)in.getAckuser().getVal() : null));
			result.setAlarm(alarm);
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".deleteWatch", ex);
		}
		
		return result;
	}

}
