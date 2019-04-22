package com.ptoceti.osgi.obix.impl.hk2;

import com.ptoceti.osgi.obix.impl.domain.AlarmDomainImpl;
import com.ptoceti.osgi.obix.impl.proxy.jdbc.JdbcConnectionProxyFactory;
import org.glassfish.hk2.api.Factory;

import com.ptoceti.osgi.obix.domain.AlarmDomain;

import javax.inject.Inject;

public class AlarmDomainFactory implements Factory<AlarmDomain> {

    JdbcConnectionProxyFactory<AlarmDomain> alarmProxyFactory = new JdbcConnectionProxyFactory<AlarmDomain>();

    @Override
    public AlarmDomain provide() {
        AlarmDomain result = alarmProxyFactory.createProxy(AlarmDomainImpl.class, AlarmDomain.class);
        return result;
    }

    @Override
    public void dispose(AlarmDomain alarmDomain) {

    }
}
