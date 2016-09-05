package com.ptoceti.osgi.obix.impl.resources.server;

import org.restlet.resource.Put;

import com.google.inject.Inject;
import com.ptoceti.osgi.obix.cache.AlarmCache;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.object.Val;
import com.ptoceti.osgi.obix.resources.AlarmResource;
import com.ptoceti.osgi.obix.resources.RangeAlarmMinResource;
import com.ptoceti.osgi.obix.resources.ResourceException;

public class RangeAlarmMinServerResource extends AbstractServerResource implements RangeAlarmMinResource{

private AlarmCache cache;
	
	@Inject
	public RangeAlarmMinServerResource(AlarmCache cache){
		this.cache = cache;
	}
	
	@Put("xml|json")
	public void setMin(Val min) throws ResourceException {
		String alarmUri = AlarmResource.baseuri.concat("/").concat((String)getRequest().getAttributes().get(AlarmResource.ALARM_URI)).concat("/");
		
		try {
			cache.setMin(alarmUri, min);
			
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".retrieve", ex);
		}
	}

}
