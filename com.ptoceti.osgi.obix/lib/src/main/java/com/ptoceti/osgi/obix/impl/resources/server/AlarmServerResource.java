package com.ptoceti.osgi.obix.impl.resources.server;

import com.google.inject.Inject;
import com.ptoceti.osgi.obix.cache.AlarmCache;
import com.ptoceti.osgi.obix.contract.AckAlarm;
import com.ptoceti.osgi.obix.contract.Alarm;
import com.ptoceti.osgi.obix.contract.AlarmAckIn;
import com.ptoceti.osgi.obix.contract.AlarmAckOut;
import com.ptoceti.osgi.obix.contract.Nil;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.object.Op;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.resources.AlarmAckResource;
import com.ptoceti.osgi.obix.resources.AlarmResource;
import com.ptoceti.osgi.obix.resources.ResourceException;

public class AlarmServerResource extends AbstractServerResource implements AlarmResource {

	private AlarmCache cache;
	
	@Inject
	public AlarmServerResource(AlarmCache cache){
		this.cache = cache;
	}
	
	@Override
	public Alarm retrieve() throws ResourceException {
		String alarmUri = AlarmResource.baseuri.concat("/").concat((String)getRequest().getAttributes().get(AlarmResource.ALARM_URI)).concat("/");
		Alarm alarm = null;
		try {
			alarm = cache.retrieve(alarmUri);
			
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".retrieve", ex);
		}
		
		if( alarm.getIs().containsContract(AckAlarm.contract)){
			// op are not persisted in backend store
			Op ack = new Op("ack", AlarmAckIn.contract, AlarmAckOut.contract);
			ack.setHref(new Uri("uri",AlarmAckResource.baseuri));
			alarm.addChildren(ack);
		}
		return alarm;
	}

	@Override
	public Nil remove() throws ResourceException {
		String alarmUri = AlarmResource.baseuri.concat("/").concat((String)getRequest().getAttributes().get(AlarmResource.ALARM_URI)).concat("/");
		try {
			cache.delete(alarmUri);
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".deleteWatch", ex);
		}
	
		return new Nil();
	}

}
