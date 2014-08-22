package com.ptoceti.osgi.obix.impl.domain;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : AboutDomainImpl.java
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


import java.util.Calendar;

import org.osgi.framework.Constants;

import com.ptoceti.osgi.obix.contract.About;
import com.ptoceti.osgi.obix.domain.AboutDomain;
import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Status;
import com.ptoceti.osgi.obix.impl.Activator;


public class AboutDomainImpl extends AbstractDomain implements AboutDomain{

	public AboutDomainImpl(){}
	
	public About getAbout() {
		
		About about = new About();
		about.setObixVersion("");
		about.setProductName(Activator.getManifestProperty(Constants.BUNDLE_NAME));
		about.setProductUrl(Activator.getManifestProperty(Constants.BUNDLE_DOCURL));
		about.setProductVersion(Activator.getManifestProperty(Constants.BUNDLE_VERSION));
		about.setStatus(Status.OK);
		about.setVendorName(Activator.getManifestProperty(Constants.BUNDLE_VENDOR));
		about.setVendorUrl(Activator.getManifestProperty(Constants.BUNDLE_DOCURL));
		about.setServerName(Activator.getHttpServiceName());
		about.setServerBootTime(new Abstime( "", Activator.getBootTime()));
		about.setServerTime(new Abstime( "", new Long(Calendar.getInstance().getTimeInMillis())));
		return about;
	}
}
