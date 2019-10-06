package com.ptoceti.osgi.obix.impl.front.resources;


import com.ptoceti.osgi.obix.cache.AlarmCache;
import com.ptoceti.osgi.obix.contract.AckAlarm;
import com.ptoceti.osgi.obix.contract.Alarm;
import com.ptoceti.osgi.obix.contract.AlarmAckIn;
import com.ptoceti.osgi.obix.contract.AlarmAckOut;
import com.ptoceti.osgi.obix.custom.contract.AlarmService;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.object.Op;
import com.ptoceti.osgi.obix.object.Ref;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.resources.ResourceException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;

@Singleton
@Path(AlarmServiceServerResource.uri)
public class AlarmServiceServerResource extends AbstractServerResource {

    public static final String uri = "/alarmservice/";


	private AlarmCache cache;
	
	@Inject
	public AlarmServiceServerResource(AlarmCache cache){
		this.cache = cache;
	}

    @GET
    @Produces({"application/xml", "application/json"})
	public AlarmService retrieve() throws ResourceException {
		AlarmService serv = new AlarmService();
		Op make = serv.getMake();
		make.setHref(new Uri("uri", AlarmServiceServerResource.uri));
		return serv;
	}

    @POST
    @Consumes({"application/xml", "application/json"})
	public Alarm make(Ref ref) throws ResourceException {
		try {
			Alarm alarm = cache.make(ref);
            if (alarm.getIs().containsContract(AckAlarm.contract)) {
                // op are not persisted in backend store
                Op ack = new Op("ack", AlarmAckIn.contract, AlarmAckOut.contract);
                ack.setHref(new Uri("uri", AlarmAckServerResource.baseuri));
                alarm.addChildren(ack);
            }
			return alarm;
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".make", ex);
		}
	}

}
