package com.ptoceti.osgi.obix.impl.hk2;


import com.ptoceti.osgi.obix.domain.HistoryDomain;
import com.ptoceti.osgi.obix.impl.domain.HistoryDomainImpl;
import com.ptoceti.osgi.obix.impl.proxy.jdbc.JdbcConnectionProxyFactory;
import org.glassfish.hk2.api.Factory;

import javax.inject.Inject;

public class HistoryDomainFactory implements Factory<HistoryDomain> {

    JdbcConnectionProxyFactory<HistoryDomain> historyProxyFactory = new JdbcConnectionProxyFactory<HistoryDomain>();

    @Override
    public HistoryDomain provide() {
        HistoryDomain result = historyProxyFactory.createProxy(HistoryDomainImpl.class, HistoryDomain.class);
        return result;
    }

    @Override
    public void dispose(HistoryDomain historyDomain) {

    }
}
