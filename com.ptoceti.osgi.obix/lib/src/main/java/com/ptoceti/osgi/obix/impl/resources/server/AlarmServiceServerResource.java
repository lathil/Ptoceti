package com.ptoceti.osgi.obix.impl.resources.server;

import com.google.inject.Inject;
import com.ptoceti.osgi.obix.cache.AlarmCache;
import com.ptoceti.osgi.obix.contract.Alarm;
import com.ptoceti.osgi.obix.custom.contract.AlarmService;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.object.Op;
import com.ptoceti.osgi.obix.object.Ref;
import com.ptoceti.osgi.obix.object.Uri;
import com.ptoceti.osgi.obix.resources.AlarmServiceResource;
import com.ptoceti.osgi.obix.resources.ResourceException;

public class AlarmServiceServerResource extends AbstractServerResource implements AlarmServiceResource{

	private AlarmCache cache;
	
	@Inject
	public AlarmServiceServerResource(AlarmCache cache){
		this.cache = cache;
	}
	@Override
	public AlarmService retrieve() throws ResourceException {
		AlarmService serv = new AlarmService();
		Op make = serv.getMake();
		make.setHref(new Uri("uri", AlarmServiceServerResource.uri));
		return serv;
	}

	@Override
	public Alarm make(Ref ref) throws ResourceException {
		try {
			Alarm alarm = cache.make(ref);
			return alarm;
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".make", ex);
		}
	}

}
