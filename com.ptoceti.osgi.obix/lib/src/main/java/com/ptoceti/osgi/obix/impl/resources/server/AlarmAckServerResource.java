package com.ptoceti.osgi.obix.impl.resources.server;

import com.google.inject.Inject;
import com.ptoceti.osgi.obix.cache.AlarmCache;
import com.ptoceti.osgi.obix.contract.Alarm;
import com.ptoceti.osgi.obix.contract.AlarmAckIn;
import com.ptoceti.osgi.obix.contract.AlarmAckOut;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.resources.AlarmAckResource;
import com.ptoceti.osgi.obix.resources.AlarmResource;
import com.ptoceti.osgi.obix.resources.ResourceException;

public class AlarmAckServerResource extends AbstractServerResource implements AlarmAckResource
{

private AlarmCache cache;
	
	@Inject
	public AlarmAckServerResource(AlarmCache cache) {
		this.cache = cache;
	}
	@Override
	public AlarmAckOut ackAlarm(AlarmAckIn in) throws ResourceException {
		String alarmUri = AlarmResource.baseuri.concat("/").concat((String)getRequest().getAttributes().get(AlarmResource.ALARM_URI)).concat("/");
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
