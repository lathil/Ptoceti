package com.ptoceti.osgi.obix.impl.resources.server;

import org.restlet.resource.Post;
import org.restlet.resource.Put;

import com.google.inject.Inject;
import com.ptoceti.osgi.obix.cache.AlarmCache;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.object.Val;
import com.ptoceti.osgi.obix.resources.AlarmResource;
import com.ptoceti.osgi.obix.resources.HistoryResource;
import com.ptoceti.osgi.obix.resources.RangeAlarmMaxResource;
import com.ptoceti.osgi.obix.resources.ResourceException;

public class RangeAlarmMaxServerResource extends AbstractServerResource implements RangeAlarmMaxResource{

	private AlarmCache cache;
	
	@Inject
	public RangeAlarmMaxServerResource(AlarmCache cache){
		this.cache = cache;
	}
	
	@Put("xml|json")
	public void setMax(Val max) throws ResourceException {
		String alarmUri = AlarmResource.baseuri.concat("/").concat((String)getRequest().getAttributes().get(AlarmResource.ALARM_URI)).concat("/");
		
		try {
			cache.setMax(alarmUri, max);
			
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".retrieve", ex);
		}
	}

}
