package com.ptoceti.osgi.obix.impl.resources.server;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : OriginServerFilter.java
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


import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.engine.header.Header;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.routing.Filter;
import org.restlet.util.Series;

/**
 * 
 * @author LATHIL
 *
 */
public class OriginServerFilter extends Filter {

	public static final String ORIGIN = "Origin";
	public static final String HTTP_HEADERS_KEY = "org.restlet.http.headers";

	OriginServerFilter(Context context) {
		super(context);
	}

	@Override
	protected int beforeHandle(Request request, Response response) {
		
		Series<Header> requestHeaders = (Series<Header>) request.getAttributes().get(HeaderConstants.ATTRIBUTE_HEADERS);
		// indication of the domain from where the request is made, if present
		String origin = requestHeaders.getFirstValue(ORIGIN, true);
		// get hold of responses headers
		Series<Header> responseHeaders = (Series<Header>) response.getAttributes().get(HeaderConstants.ATTRIBUTE_HEADERS);
		
		if (Method.OPTIONS.equals(request.getMethod())) {
			// the browser is sending a pre-flight resquest, asking whether it is allowed
			// to request the resource
			if (responseHeaders == null) {
				responseHeaders = new Series<Header>(Header.class);
				response.getAttributes().put(HeaderConstants.ATTRIBUTE_HEADERS,responseHeaders);
			}
			
			// tell we allow GET,POST,DELETE and OPTIONS from everywhere
			responseHeaders.add("Access-Control-Allow-Origin", "*");
			responseHeaders.add("Access-Control-Allow-Methods","GET,POST,PUT,DELETE,OPTIONS");
			responseHeaders.add("Access-Control-Allow-Headers", "Content-Type");
			// tell to add cookies in cors request . not needed now
			//responseHeaders.add("Access-Control-Allow-Credentials", "true");
			responseHeaders.add("Access-Control-Max-Age", "60");
			response.setEntity(new EmptyRepresentation());
			return SKIP;
		} else {
			// the request is not a CORS preflight, check if Origin header is present.
			if( origin != null && !origin.isEmpty()){
				// it is a simple CORS request or a request made after a successull preflight CORS request
				// we respond in the same way.
				if (responseHeaders == null) {
					responseHeaders = new Series<Header>(Header.class);
					response.getAttributes().put(HeaderConstants.ATTRIBUTE_HEADERS,responseHeaders);
				}
				
				responseHeaders.add("Access-Control-Allow-Origin", origin);
				responseHeaders.add("Access-Control-Allow-Headers", "Content-Type");
			}
		}

		return super.beforeHandle(request, response);
	}
}
