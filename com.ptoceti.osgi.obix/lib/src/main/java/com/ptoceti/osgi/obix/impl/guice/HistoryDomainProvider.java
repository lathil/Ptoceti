package com.ptoceti.osgi.obix.impl.guice;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : HistoryDomainProvider.java
 * 
 * This file is part of the Ptoceti project. More information about
 * this project can be found here: http://www.ptoceti.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2013 - 2015 ptoceti
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
