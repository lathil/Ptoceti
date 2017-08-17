package com.ptoceti.osgi.mqtt.impl.formater.internal;

import java.io.IOException;

import org.osgi.service.log.LogService;
import org.osgi.service.wireadmin.BasicEnvelope;
import org.osgi.service.wireadmin.Envelope;
import org.osgi.util.measurement.Measurement;
import org.osgi.util.measurement.State;
import org.osgi.util.measurement.Unit;
import org.osgi.util.position.Position;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptoceti.osgi.control.Digit;
import com.ptoceti.osgi.control.ExtendedUnit;
import com.ptoceti.osgi.control.Measure;
import com.ptoceti.osgi.control.Reference;
import com.ptoceti.osgi.control.StatusCode;
import com.ptoceti.osgi.control.Switch;
import com.ptoceti.osgi.mqtt.IMqttMessageFomatter;
import com.ptoceti.osgi.mqtt.impl.Activator;
import com.ptoceti.osgi.mqtt.impl.formater.internal.mixins.EnvelopeMixIn;
import com.ptoceti.osgi.mqtt.impl.formater.internal.mixins.ExtendedUnitMixIn;
import com.ptoceti.osgi.mqtt.impl.formater.internal.mixins.MeasurementMixIn;
import com.ptoceti.osgi.mqtt.impl.formater.internal.mixins.PositionMixIn;
import com.ptoceti.osgi.mqtt.impl.formater.internal.mixins.StateMixIn;
import com.ptoceti.osgi.mqtt.impl.formater.internal.mixins.UnitMixIn;

public class InternalMessageFormater implements IMqttMessageFomatter {

	public static final String name = "internal";

	private static ObjectMapper objectMapper;

	public InternalMessageFormater() {

		JsonFactory factory = new JsonFactory();
		factory.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);

		objectMapper = new ObjectMapper(factory);

		/**
		 * objectMapper.configure(MapperFeature.AUTO_DETECT_FIELDS, true);
		 * objectMapper.configure(MapperFeature.AUTO_DETECT_GETTERS, false);
		 * objectMapper.configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);
		 * objectMapper.configure(MapperFeature.AUTO_DETECT_SETTERS, false);
		 * 
		 * 
		 * objectMapper.setVisibility(PropertyAccessor.FIELD,
		 * JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC);
		 **/

		objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.addMixInAnnotations(BasicEnvelope.class, EnvelopeMixIn.class);
		objectMapper.addMixInAnnotations(Measurement.class, MeasurementMixIn.class);
		objectMapper.addMixInAnnotations(State.class, StateMixIn.class);
		objectMapper.addMixInAnnotations(org.osgi.util.measurement.Unit.class, UnitMixIn.class);
		objectMapper.addMixInAnnotations(Position.class, PositionMixIn.class);
		objectMapper.addMixInAnnotations(Digit.class, DigitMixIn.class);
		objectMapper.addMixInAnnotations(ExtendedUnit.class, ExtendedUnitMixIn.class);
		objectMapper.addMixInAnnotations(Measure.class, MeasureMixIn.class);
		objectMapper.addMixInAnnotations(Reference.class, ReferenceMixIn.class);
		objectMapper.addMixInAnnotations(StatusCode.class, StatusCodeMixIn.class);
		objectMapper.addMixInAnnotations(Switch.class, SwitchMixIn.class);
		objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT);
	}

	@Override
	public byte[] encode(Object in) {
		byte[] out = null;
		try {
			out = objectMapper.writeValueAsBytes(in);
		} catch (JsonProcessingException e) {
			Activator.log(LogService.LOG_ERROR, "Error serializing MQTT message payload. Error: " + e.toString());
		}

		String test = new String(out);

		return out;
	}

	@Override
	public Object decode(byte[] in) {
		Object out = null;
		try {
			// We need first to extract the full class name from type property
			JsonNode jsonNode = objectMapper.readTree(in);
			JsonNode classNode = jsonNode.get("type");
			if (classNode != null && classNode.isTextual()) {
				String typeClass = classNode.textValue();
				// Jackson by default use currentThreadLocal class loader which
				// is no good in osgi env
				Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
				// pass to jackson the type; it does not knwo how to extract it
				// from @JsonType
				out = objectMapper.readValue(in, Class.forName(typeClass));
			}
		} catch (JsonParseException e) {
			Activator.log(LogService.LOG_ERROR, "Error deserializing MQTT message payload. Error: " + e.toString());
		} catch (JsonMappingException e) {
			Activator.log(LogService.LOG_ERROR, "Error deserializing MQTT message payload. Error: " + e.toString());
		} catch (IOException e) {
			Activator.log(LogService.LOG_ERROR, "Error deserializing MQTT message payload. Error: " + e.toString());
		} catch (ClassNotFoundException e) {
			Activator.log(LogService.LOG_ERROR, "Error deserializing MQTT message payload. Error: " + e.toString());
		}

		return out;
	}

	

	@JsonAutoDetect(fieldVisibility = Visibility.PROTECTED_AND_PUBLIC, getterVisibility = Visibility.PUBLIC_ONLY, setterVisibility = Visibility.PUBLIC_ONLY, isGetterVisibility = Visibility.NONE)
	@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
	@JsonInclude(Include.NON_NULL)
	public abstract class DigitMixIn {

	}

	@JsonAutoDetect(fieldVisibility = Visibility.PROTECTED_AND_PUBLIC, getterVisibility = Visibility.PUBLIC_ONLY, setterVisibility = Visibility.PUBLIC_ONLY, isGetterVisibility = Visibility.PUBLIC_ONLY)
	@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
	@JsonInclude(Include.NON_NULL)
	public abstract class MeasureMixIn {

	}

	@JsonAutoDetect(fieldVisibility = Visibility.PROTECTED_AND_PUBLIC, getterVisibility = Visibility.PUBLIC_ONLY, setterVisibility = Visibility.PUBLIC_ONLY, isGetterVisibility = Visibility.PUBLIC_ONLY)
	@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
	@JsonInclude(Include.NON_NULL)
	public abstract class ReferenceMixIn {

	}

	@JsonAutoDetect(fieldVisibility = Visibility.PROTECTED_AND_PUBLIC, getterVisibility = Visibility.PUBLIC_ONLY, setterVisibility = Visibility.PUBLIC_ONLY, isGetterVisibility = Visibility.PUBLIC_ONLY)
	@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
	@JsonInclude(Include.NON_NULL)
	public abstract class StatusCodeMixIn {

	}

	@JsonAutoDetect(fieldVisibility = Visibility.PROTECTED_AND_PUBLIC, getterVisibility = Visibility.PUBLIC_ONLY, setterVisibility = Visibility.PUBLIC_ONLY, isGetterVisibility = Visibility.PUBLIC_ONLY)
	@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
	@JsonInclude(Include.NON_NULL)
	public abstract class SwitchMixIn {

	}

}
