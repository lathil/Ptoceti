package com.ptoceti.osgi.obix.impl.service;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : CommandHandler.java
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

import java.util.concurrent.Callable;

import com.ptoceti.osgi.obix.impl.front.resources.ObjServerResource;
import org.osgi.service.wireadmin.BasicEnvelope;

import com.ptoceti.osgi.obix.impl.back.converters.OsgiConverterFactory;
import com.ptoceti.osgi.obix.impl.back.converters.OsgiObixConverter;
import com.ptoceti.osgi.obix.object.Val;

/**
 * Handle a command that is to be sent to one of the wire consumer attached to the Obix service. The command take the form of an
 * Obix object.
 * 
 * The handler convert the obix object to one understood by the wire consumer and place inside an enveloppe. The enveloppe is then
 * forwarded to the wire handler that will dispatch it to the proper wire consumer, based to the wire scope.
 * 
 * @author lor
 *
 */
public class CommandHandler  {

	/**
	 * Send a command to a wire consumer. Consumer scope is extracted from the obix href value. The name of the obix val is
	 * used as the identification.
	 * 
	 * @param commandIn Obix command object.
	 */
	public void sendCommand( Val commandIn){
		
		Object commandOut;
		OsgiObixConverter converter = OsgiConverterFactory.getInstance().getConverterFromObixContract(commandIn);
		if( converter != null) {
			commandOut = converter.fromBaseObix(commandIn);
		} else {
			converter = OsgiConverterFactory.getInstance().getConverterFromObix(commandIn);
			commandOut = converter.fromObix(commandIn);
		}
		
		String name = commandIn.getName();
		String scope = extractScope(commandIn);
		
		BasicEnvelope env = new BasicEnvelope(commandOut, name, scope);
		
		Activator.getObixService().getExecutorService().submit(new AsyncCommand(env));
		//Activator.getObixService().getWireHandler().updateWire(env);
		
	}
	
	/**
	 * Extract original scope from the object href path.
	 * 
	 * @param commandIn Obix value as the command
	 * @return String the scope.
	 */
	protected String extractScope(Val commandIn) {
		String href = commandIn.getHref().getPath();
		// extract as well the root uri for an object resource.
        href = href.substring(href.indexOf(ObjServerResource.uri) + ObjServerResource.uri.length());
		href = href.replaceAll("[//]", ".");
		
		// name was added the the href as a subpath ...
		String namePath = "." + commandIn.getName();
		// ... extract it
		String scope = href.substring(0, href.indexOf(namePath));
		
		
		return scope;
	}
	
	protected class AsyncCommand implements Callable {

		BasicEnvelope enveloppe;
		
		public AsyncCommand(BasicEnvelope env){
			enveloppe = env;
		}
		
		@Override
		public Object call() throws Exception {
			boolean result = false;
			WireHandler wHandler = Activator.getObixService().getWireHandler();
			if( wHandler != null){
				result = wHandler.updateWire(enveloppe);
			}
			
			return Boolean.valueOf(result);
		}
		
	}
}
