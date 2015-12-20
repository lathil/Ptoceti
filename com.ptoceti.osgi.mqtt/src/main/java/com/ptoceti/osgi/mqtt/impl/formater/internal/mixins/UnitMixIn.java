package com.ptoceti.osgi.mqtt.impl.formater.internal.mixins;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.PUBLIC_ONLY, setterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property="type")
@JsonInclude(Include.NON_NULL)
public abstract class UnitMixIn {
	 @JsonProperty("name") 
	 private  String		name;
	 @JsonProperty("type")
	 private  long			type;
		
	 @JsonCreator
	 private UnitMixIn( @JsonProperty("name") String name, @JsonProperty("type") long type){
	 }
}
