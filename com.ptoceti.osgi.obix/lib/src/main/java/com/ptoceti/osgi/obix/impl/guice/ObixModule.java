package com.ptoceti.osgi.obix.impl.guice;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : ObixModule.java
 * 
 * This file is part of the Ptoceti project. More information about
 * this project can be found here: http://www.ptoceti.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2013 - 2014 ptoceti
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.ptoceti.osgi.obix.cache.HistoryCache;
import com.ptoceti.osgi.obix.cache.ObjCache;
import com.ptoceti.osgi.obix.cache.WatchCache;
import com.ptoceti.osgi.obix.domain.AboutDomain;
import com.ptoceti.osgi.obix.domain.HistoryDomain;
import com.ptoceti.osgi.obix.domain.ObjDomain;
import com.ptoceti.osgi.obix.domain.WatchDomain;
import com.ptoceti.osgi.obix.impl.cache.HistoryCacheImpl;
import com.ptoceti.osgi.obix.impl.cache.ObjCacheImpl;
import com.ptoceti.osgi.obix.impl.cache.WatchCacheImpl;
import com.ptoceti.osgi.obix.impl.domain.AboutDomainImpl;
import com.ptoceti.osgi.obix.impl.proxy.jdbc.JdbcConnectionProxyFactory;
import com.ptoceti.osgi.obix.object.Obj;

/**
 * Configuration module for Guice.
 * 
 * @author LATHIL
 *
 */
public class ObixModule extends AbstractModule{

	
	/**
	 * Constructor
	 * 
	 */
	public ObixModule() {
	}
	
	/**
	 * Configure bindings.
	 */
	@Override
	protected void configure() {
		bind(AboutDomain.class).to((Class<? extends AboutDomain>) AboutDomainImpl.class);
		bind(ObjDomain.class).toProvider(ObjDomainProvider.class);
		bind(WatchDomain.class).toProvider(WatchDomainProvider.class);
		bind(HistoryDomain.class).toProvider(HistoryDomainProvider.class);
		bind(ObjCache.class).to(ObjCacheImpl.class);
		bind(HistoryCache.class).to(HistoryCacheImpl.class);
		bind(WatchCache.class).to(WatchCacheImpl.class);
		
	}
	
	@Provides @Singleton
	JdbcConnectionProxyFactory<WatchDomain> watchDomainProxyFactoryProvider(){
		return new JdbcConnectionProxyFactory<WatchDomain>();
	}
	
	@Provides @Singleton
	JdbcConnectionProxyFactory<ObjDomain> objectDomainProxyFactoryProvider(){
		return new JdbcConnectionProxyFactory<ObjDomain>();
	}
	
	@Provides @Singleton
	JdbcConnectionProxyFactory<HistoryDomain> historyDomainProxyFactoryProvider(){
		return new JdbcConnectionProxyFactory<HistoryDomain>();
	}
	
	@Provides @Singleton
	Cache<String, Obj> objCacheProvider(){
		return CacheBuilder.newBuilder().maximumSize(1000).build();
	}
}
