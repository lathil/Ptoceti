package com.ptoceti.osgi.mqtt.impl.formater.internal.mixins;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonAutoDetect(fieldVisibility = Visibility.PROTECTED_AND_PUBLIC, getterVisibility = Visibility.PUBLIC_ONLY, setterVisibility = Visibility.PUBLIC_ONLY, isGetterVisibility = Visibility.NONE)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonInclude(Include.NON_NULL)
public abstract class EnvelopeMixIn {
	@JsonCreator
	private EnvelopeMixIn(@JsonProperty("value") Object value, @JsonProperty("identification") Object identification,
			@JsonProperty("scope") String scope) {
	}
}
