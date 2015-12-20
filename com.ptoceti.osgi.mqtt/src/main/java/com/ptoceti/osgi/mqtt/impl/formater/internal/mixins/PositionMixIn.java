package com.ptoceti.osgi.mqtt.impl.formater.internal.mixins;

import org.osgi.util.measurement.Measurement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property="type")
@JsonInclude(Include.NON_NULL)
public abstract class PositionMixIn {
	 @JsonCreator
	 public PositionMixIn(@JsonProperty("latitude")Measurement lat, @JsonProperty("longitude")Measurement lon, @JsonProperty("altitude")Measurement alt,
			 @JsonProperty("speed")Measurement speed, @JsonProperty("track")Measurement track) {
		 
	 }
}
