package com.ptoceti.osgi.obix.impl.hk2;


import com.ptoceti.osgi.obix.domain.WatchDomain;
import com.ptoceti.osgi.obix.impl.domain.WatchDomainImpl;
import com.ptoceti.osgi.obix.impl.proxy.jdbc.JdbcConnectionProxyFactory;
import org.glassfish.hk2.api.Factory;

import javax.inject.Inject;

public class WatchDomainFactory implements Factory<WatchDomain> {

    JdbcConnectionProxyFactory<WatchDomain> watchProxyFactory = new JdbcConnectionProxyFactory<WatchDomain>();


    @Override
    public WatchDomain provide() {
        WatchDomain result = watchProxyFactory.createProxy(WatchDomainImpl.class, WatchDomain.class);
        return result;
    }

    @Override
    public void dispose(WatchDomain watchDomain) {

    }
}
