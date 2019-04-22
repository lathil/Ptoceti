package com.ptoceti.osgi.obix.impl.hk2;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ptoceti.osgi.obix.cache.AlarmCache;
import com.ptoceti.osgi.obix.cache.HistoryCache;
import com.ptoceti.osgi.obix.cache.ObjCache;
import com.ptoceti.osgi.obix.cache.WatchCache;
import com.ptoceti.osgi.obix.domain.AlarmDomain;
import com.ptoceti.osgi.obix.domain.HistoryDomain;
import com.ptoceti.osgi.obix.domain.ObjDomain;
import com.ptoceti.osgi.obix.domain.WatchDomain;
import com.ptoceti.osgi.obix.impl.cache.AlarmCacheImpl;
import com.ptoceti.osgi.obix.impl.cache.HistoryCacheImpl;
import com.ptoceti.osgi.obix.impl.cache.ObjCacheImpl;
import com.ptoceti.osgi.obix.impl.cache.WatchCacheImpl;
import com.ptoceti.osgi.obix.object.Obj;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;


public class ApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {


        bindFactory(new ObjCachefactory()).to(Cache.class).in(Singleton.class);

        bind(ObjCacheImpl.class).to(ObjCache.class);
        bind(HistoryCacheImpl.class).to(HistoryCache.class);
        bind(WatchCacheImpl.class).to(WatchCache.class);
        bind(AlarmCacheImpl.class).to(AlarmCache.class);

        bindFactory(new AlarmDomainFactory()).to(AlarmDomain.class).proxy(false);
        bindFactory(new HistoryDomainFactory()).to(HistoryDomain.class).proxy(false);
        bindFactory(new WatchDomainFactory()).to(WatchDomain.class).proxy(false);
        bindFactory(new ObjDomainFactory()).to(ObjDomain.class).proxy(false);

    }


    public class ObjCachefactory implements Factory<Cache<String, Obj>> {

        @Override
        public Cache<String, Obj> provide() {
            return CacheBuilder.newBuilder().maximumSize(1000).build();
        }

        @Override
        public void dispose(Cache<String, Obj> stringObjCache) {

        }
    }
}
