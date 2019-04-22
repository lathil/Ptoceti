package com.ptoceti.osgi.obix.impl.front.resources;


import com.ptoceti.osgi.obix.cache.AlarmCache;
import com.ptoceti.osgi.obix.contract.AckAlarm;
import com.ptoceti.osgi.obix.contract.Alarm;
import com.ptoceti.osgi.obix.contract.AlarmAckIn;
import com.ptoceti.osgi.obix.contract.AlarmAckOut;
import com.ptoceti.osgi.obix.contract.Nil;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.impl.front.exception.HttpNotFoundException;
import com.ptoceti.osgi.obix.object.Op;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.resources.ResourceException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;

@Singleton
@Path(AlarmServerResource.uri)
public class AlarmServerResource extends AbstractServerResource {

    public static final String ALARM_URI = "alarmuri";

    public static final String baseuri = "/alarm";

    public static final String uri = baseuri + "/{" + ALARM_URI + "}/";

	private AlarmCache cache;
	
	@Inject
	public AlarmServerResource(AlarmCache cache){
		this.cache = cache;
	}


    @GET
    @Produces({"application/xml", "application/json"})
    public Alarm retrieve(@PathParam(AlarmServerResource.ALARM_URI) String alarmuri) throws ResourceException {
        String alarmUri = AlarmServerResource.baseuri.concat("/").concat(alarmuri).concat("/");
		Alarm alarm = null;
		try {
			alarm = cache.retrieve(alarmUri);
            if (alarm != null) {
                if (alarm.getIs().containsContract(AckAlarm.contract)) {
                    // op are not persisted in backend store
                    Op ack = new Op("ack", AlarmAckIn.contract, AlarmAckOut.contract);
                    ack.setHref(new Uri("uri", AlarmAckServerResource.baseuri));
                    alarm.addChildren(ack);
                }
            } else {
                throw new HttpNotFoundException("alarm at : " + alarmUri + "not found");
            }

        } catch (DomainException ex) {
            throw new ResourceException("Exception in " + this.getClass().getName() + ".retrieve", ex);
        }
		

		return alarm;
	}


    @DELETE
    public Nil remove(@PathParam(AlarmServerResource.ALARM_URI) String alarmuri) throws ResourceException {
        String alarmUri = AlarmServerResource.baseuri.concat("/").concat(alarmuri).concat("/");
		try {
			cache.delete(alarmUri);
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".deleteWatch", ex);
		}
	
		return new Nil();
	}

    @Path(AlarmAckServerResource.baseuri)
    public Class<AlarmAckServerResource> getAlarmAckServerResource() {
        return AlarmAckServerResource.class;
    }

    @Path(RangeAlarmMaxServerResource.baseuri)
    public Class<RangeAlarmMaxServerResource> getRangeAlarmMaxServerResource() {
        return RangeAlarmMaxServerResource.class;
    }

    @Path(RangeAlarmMinServerResource.baseuri)
    public Class<RangeAlarmMinServerResource> getRangeAlarmMinServerResource() {
        return RangeAlarmMinServerResource.class;
    }
}
