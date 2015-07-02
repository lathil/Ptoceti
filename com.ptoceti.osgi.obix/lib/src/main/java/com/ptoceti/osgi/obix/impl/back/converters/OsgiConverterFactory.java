package com.ptoceti.osgi.obix.impl.back.converters;

import java.util.HashMap;

import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Val;


/**
 * Factory that provide converters to converter to and from osgi and obix entities.
 * 
 * @author lor
 *
 */
public class OsgiConverterFactory {
	
	protected static OsgiConverterFactory instance = new OsgiConverterFactory();
	
	// map of converters searchable from obix names
	HashMap<String, OsgiObixConverter<?>> obixNamedConverters = new HashMap<String, OsgiObixConverter<?>>();
	// map of converters searchable from obix names
	HashMap<String, OsgiObixConverter<?>> obixContractConverters = new HashMap<String, OsgiObixConverter<?>>();
	// map of converters searchable from osgi names
	HashMap<String, OsgiObixConverter<?>> osgiNamedConverters = new HashMap<String, OsgiObixConverter<?>>();
	
	
	/**
	 * The factory implement singleton pattern. Access from instance.
	 * 
	 */
	private OsgiConverterFactory() {
		
		addConverter(new MeasureConverter());
		addConverter(new ReferenceConverter());
		addConverter(new SwitchConverter());
		addConverter(new DigitConverter());
		addConverter(new DoubleConverter());
		addConverter(new IntegerConverter());
		addConverter(new StringConverter());
	}
	
	/**
	 * The factory instance accessor.
	 * @return OsgiConverterFactory instance
	 */
	public static OsgiConverterFactory getInstance(){
		return instance;
	}
	

	private void addConverter(OsgiObixConverter converter){
		if( !obixNamedConverters.containsKey(converter.getObixClassName())){
			obixNamedConverters.put(converter.getObixClassName(), converter);
		}
		if( converter.getObixContract() != null && !obixContractConverters.containsKey(converter.getObixContract().toUniformString())){
			obixContractConverters.put(converter.getObixContract().toUniformString(), converter);
		}
		if( !osgiNamedConverters.containsKey(converter.getOsgiClassName())){
			osgiNamedConverters.put(converter.getOsgiClassName(), converter);
		}
	}
	
	/**
	 * Return a osgi / obix converter from a type of Obix value
	 * 
	 * @param in the type of obix value object
	 * @return the converter
	 */
	public OsgiObixConverter getConverterFromObix(Val in){
		OsgiObixConverter result = null;
		if( obixNamedConverters.containsKey(in.getClass().getName())){
			result =  obixNamedConverters.get(in.getClass().getName());
		} 
		
		return result;
		
	}
	
	public OsgiObixConverter getConverterFromObixContract(Val in){
		OsgiObixConverter result = null;
		if( in.getIs() != null && obixContractConverters.containsKey(in.getIs().toUniformString())){
			result =  obixContractConverters.get(in.getIs().toUniformString());
		} 
		
		return result;
		
	}
	
	/**
	 * return a osgi / obix converter from a type of Osgi value object
	 * 
	 * @param in the type of osgi value object
	 * @return the converter
	 */
	public OsgiObixConverter getConverterFromOsgi(Object in){
		OsgiObixConverter result = null;
		if( osgiNamedConverters.containsKey(in.getClass().getName())){
			result = osgiNamedConverters.get(in.getClass().getName());
		} 
		
		return result;
	}

}
