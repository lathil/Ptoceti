package com.ptoceti.osgi.obix.impl.guice;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ptoceti.osgi.obix.domain.WatchDomain;
import com.ptoceti.osgi.obix.impl.domain.WatchDomainImpl;
import com.ptoceti.osgi.obix.impl.proxy.jdbc.JdbcConnectionProxyFactory;

public class WatchDomainProvider implements  Provider<WatchDomain>{

	JdbcConnectionProxyFactory<WatchDomain> watchProxyFactory;
	
	@Inject
	public WatchDomainProvider(JdbcConnectionProxyFactory<WatchDomain> proxyFactory ){
		watchProxyFactory = proxyFactory;
	}
	
	@Override
	public WatchDomain get() {
		WatchDomain result =  watchProxyFactory.createProxy( WatchDomainImpl.class, WatchDomain.class);
		return result;
	}

}
