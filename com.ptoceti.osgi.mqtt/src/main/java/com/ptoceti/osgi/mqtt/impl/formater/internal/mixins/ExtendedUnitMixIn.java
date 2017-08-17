package com.ptoceti.osgi.mqtt.impl.formater.internal.mixins;

import org.osgi.util.measurement.Unit;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC, getterVisibility=Visibility.PUBLIC_ONLY, setterVisibility=Visibility.PUBLIC_ONLY, isGetterVisibility=Visibility.PUBLIC_ONLY)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property="type")
@JsonInclude(Include.NON_NULL)
public abstract class ExtendedUnitMixIn {

	@JsonCreator
	 private ExtendedUnitMixIn(@JsonProperty("siUnit")Unit unit, @JsonProperty("isSI")boolean isSI, @JsonProperty("nonSIName")String nonSIName, @JsonProperty("scale")double scale, @JsonProperty("offset")double offset){
	 }
}
