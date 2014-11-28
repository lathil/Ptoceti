package com.ptoceti.osgi.obix.impl.guice;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ptoceti.osgi.obix.domain.HistoryDomain;
import com.ptoceti.osgi.obix.impl.domain.HistoryDomainImpl;
import com.ptoceti.osgi.obix.impl.proxy.jdbc.JdbcConnectionProxyFactory;

public class HistoryDomainProvider implements Provider<HistoryDomain>{

	JdbcConnectionProxyFactory<HistoryDomain> historyProxyFactory;
	
	@Inject
	public HistoryDomainProvider(JdbcConnectionProxyFactory<HistoryDomain> factoryProxy){
		historyProxyFactory = factoryProxy;
	}
	
	
	@Override
	public HistoryDomain get() {
		HistoryDomain result = historyProxyFactory.createProxy( HistoryDomainImpl.class, HistoryDomain.class);
		return result;
	}

}
