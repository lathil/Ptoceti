package com.ptoceti.osgi.obix.impl.hk2;

import com.ptoceti.osgi.obix.domain.ObjDomain;
import com.ptoceti.osgi.obix.impl.domain.ObjDomainImpl;
import com.ptoceti.osgi.obix.impl.proxy.jdbc.JdbcConnectionProxyFactory;
import org.glassfish.hk2.api.Factory;

import javax.inject.Inject;

public class ObjDomainFactory implements Factory<ObjDomain> {

    JdbcConnectionProxyFactory<ObjDomain> objDomainFactory = new JdbcConnectionProxyFactory<ObjDomain>();


    @Override
    public ObjDomain provide() {
        ObjDomain result = objDomainFactory.createProxy(ObjDomainImpl.class, ObjDomain.class);
        return result;
    }

    @Override
    public void dispose(ObjDomain objDomain) {

    }
}
