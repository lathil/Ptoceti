package com.ptoceti.osgi.obix.impl;

import org.osgi.service.wireadmin.BasicEnvelope;

import com.ptoceti.osgi.obix.impl.back.converters.OsgiConverterFactory;
import com.ptoceti.osgi.obix.impl.back.converters.OsgiObixConverter;
import com.ptoceti.osgi.obix.object.Val;
import com.ptoceti.osgi.obix.resources.ObjResource;

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
public class CommandHandler extends BaseObixHandler {

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
		
		Activator.getObixService().getWireHandler().updateWire(env);
		
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
		href = href.substring(href.indexOf(ObjResource.uri) + ObjResource.uri.length());
		href = href.replaceAll("[//]", ".");
		
		// name was added the the href as a subpath ...
		String namePath = "." + commandIn.getName();
		// ... extract it
		String scope = href.substring(0, href.indexOf(namePath));
		
		
		return scope;
	}
}
