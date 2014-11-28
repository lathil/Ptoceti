package com.ptoceti.osgi.obix.impl.guice;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ptoceti.osgi.obix.domain.ObjDomain;
import com.ptoceti.osgi.obix.impl.domain.ObjDomainImpl;
import com.ptoceti.osgi.obix.impl.proxy.jdbc.JdbcConnectionProxyFactory;

public class ObjDomainProvider implements Provider<ObjDomain>{

	JdbcConnectionProxyFactory<ObjDomain> objProxyFactory;

	@Inject
	ObjDomainProvider(JdbcConnectionProxyFactory<ObjDomain> proxyFactory ){
		objProxyFactory = proxyFactory;
	}
	
	
	@Override
	public ObjDomain get() {
		ObjDomain result = objProxyFactory.createProxy( ObjDomainImpl.class, ObjDomain.class);
		return result;
	}
}
