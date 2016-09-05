package com.ptoceti.osgi.obix.impl.guice;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ptoceti.osgi.obix.domain.AlarmDomain;
import com.ptoceti.osgi.obix.impl.domain.AlarmDomainImpl;
import com.ptoceti.osgi.obix.impl.proxy.jdbc.JdbcConnectionProxyFactory;

public class AlarmDomainProvider implements Provider<AlarmDomain>{

	JdbcConnectionProxyFactory<AlarmDomain> alarmProxyFactory;
	
	@Inject
	public AlarmDomainProvider(JdbcConnectionProxyFactory<AlarmDomain> factoryProxy){
		alarmProxyFactory = factoryProxy;
	}
	
	
	@Override
	public AlarmDomain get() {
		AlarmDomain result = alarmProxyFactory.createProxy( AlarmDomainImpl.class, AlarmDomain.class);
		return result;
	}

}
