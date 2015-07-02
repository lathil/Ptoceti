package com.ptoceti.osgi.obix.impl.front.converters;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptoceti.osgi.obix.contract.About;
import com.ptoceti.osgi.obix.contract.Batch;
import com.ptoceti.osgi.obix.contract.BatchIn;
import com.ptoceti.osgi.obix.contract.BatchOut;
import com.ptoceti.osgi.obix.contract.Dimension;
import com.ptoceti.osgi.obix.contract.History;
import com.ptoceti.osgi.obix.contract.HistoryFilter;
import com.ptoceti.osgi.obix.contract.HistoryQueryOut;
import com.ptoceti.osgi.obix.contract.HistoryRecord;
import com.ptoceti.osgi.obix.contract.HistoryRollupIn;
import com.ptoceti.osgi.obix.contract.HistoryRollupOut;
import com.ptoceti.osgi.obix.contract.HistoryRollupRecord;
import com.ptoceti.osgi.obix.contract.Lobby;
import com.ptoceti.osgi.obix.contract.Nil;
import com.ptoceti.osgi.obix.contract.Point;
import com.ptoceti.osgi.obix.contract.Read;
import com.ptoceti.osgi.obix.contract.Unit;
import com.ptoceti.osgi.obix.contract.Watch;
import com.ptoceti.osgi.obix.contract.WatchIn;
import com.ptoceti.osgi.obix.contract.WatchInItem;
import com.ptoceti.osgi.obix.contract.WatchOut;
import com.ptoceti.osgi.obix.contract.WatchService;
import com.ptoceti.osgi.obix.contract.WritablePoint;
import com.ptoceti.osgi.obix.contract.Write;
import com.ptoceti.osgi.obix.contract.WritePoint;
import com.ptoceti.osgi.obix.contract.WritePointIn;
import com.ptoceti.osgi.obix.custom.contract.MeasurePoint;
import com.ptoceti.osgi.obix.custom.contract.MonitoredPoint;
import com.ptoceti.osgi.obix.custom.contract.DigitPoint;
import com.ptoceti.osgi.obix.custom.contract.ReferencePoint;
import com.ptoceti.osgi.obix.custom.contract.SwitchPoint;
import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Bool;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Err;
import com.ptoceti.osgi.obix.object.Feed;
import com.ptoceti.osgi.obix.object.Int;
import com.ptoceti.osgi.obix.object.List;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Op;
import com.ptoceti.osgi.obix.object.Real;
import com.ptoceti.osgi.obix.object.Ref;
import com.ptoceti.osgi.obix.object.Reltime;
import com.ptoceti.osgi.obix.object.Str;
import com.ptoceti.osgi.obix.object.Uri;

public class ObjectMapperFactory {

	 private static ObjectMapper objectMapper;
	 
	 public static ObjectMapper configure() {
		 
		 if( objectMapper == null ) {
			 
			JsonFactory factory = new JsonFactory();
			factory.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);

			objectMapper = new ObjectMapper(factory);

			/**
			 * objectMapper.configure(MapperFeature.AUTO_DETECT_FIELDS, true);
			 * objectMapper.configure(MapperFeature.AUTO_DETECT_GETTERS, false);
			 * objectMapper.configure(MapperFeature.AUTO_DETECT_IS_GETTERS,
			 * false); objectMapper.configure(MapperFeature.AUTO_DETECT_SETTERS,
			 * false);
			 * 
			 * 
			 * objectMapper.setVisibility(PropertyAccessor.FIELD,
			 * JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC);
			 **/

			objectMapper.configure(
					DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
			objectMapper.configure(
					DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			objectMapper.addMixInAnnotations(Contract.class,
					ContractMixIn.class);
			objectMapper.addMixInAnnotations(Obj.class, ObjMixIn.class);
		 }
		 
		 return objectMapper;
	 }
	 
	 @JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
	 @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property="type")
	 @JsonInclude(Include.NON_NULL)
	 @JsonSubTypes({
	 	@Type(value=Abstime.class, name="abstime"),
	 	@Type(value=Bool.class, name="bool"),
	 	@Type(value=Enum.class, name="enum"),
	 	@Type(value=Err.class, name="err"),
	 	@Type(value=Feed.class, name="feed"),
	 	@Type(value=Int.class, name="int"),
	 	@Type(value=List.class, name="list"),
	 	@Type(value=Op.class, name="op"),
	 	@Type(value=Real.class, name="real"),
	 	@Type(value=Ref.class, name="ref"),
	 	@Type(value=Reltime.class, name="reltime"),
	 	@Type(value=Str.class, name="str"),
	 	@Type(value=Uri.class, name="uri"),
	 	@Type(value=Obj.class, name="obj"),
	 	
	 	@Type(value=About.class, name="about"),
	 	@Type(value=Batch.class, name="batch"),
	 	@Type(value=BatchIn.class, name="batchin"),
	 	@Type(value=BatchOut.class, name="batchout"),
	 	@Type(value=Dimension.class, name="dimension"),
	 	@Type(value=Lobby.class, name="lobby"),
	 	@Type(value=Nil.class, name="nil"),
	 	@Type(value=Point.class, name="point"),
	 	@Type(value=Read.class, name="read"),
	 	@Type(value=Unit.class, name="unit"),
	 	@Type(value=Watch.class, name="watch"),
	 	@Type(value=WatchIn.class, name="watchin"),
	 	@Type(value=WatchInItem.class, name="watchinitem"),
	 	@Type(value=WatchOut.class, name="watchout"),
	 	@Type(value=WatchService.class, name="watchservice"),
	 	@Type(value=WritablePoint.class, name="writablepoint"),
	 	@Type(value=Write.class, name="write"),
	 	@Type(value=WritePoint.class, name="writepoint"),
	 	@Type(value=WritePointIn.class, name="writepointin"),
	 	@Type(value=History.class,name="history"),
	 	@Type(value=HistoryRecord.class,name="historyrecord"),
	 	@Type(value=HistoryFilter.class,name="historyfilter"),
	 	@Type(value=HistoryQueryOut.class,name="historyqueryout"),
	 	@Type(value=HistoryRollupIn.class,name="historyrollupin"),
	 	@Type(value=HistoryRollupOut.class,name="historyrollupout"),
	 	@Type(value=HistoryRollupRecord.class,name="historyrolluprecord"),
	 	
	 	@Type(value=MonitoredPoint.class, name="monitoredpoint")
	 
	 	
	 })
	 
	
	 public abstract class ObjMixIn {

		 @JsonProperty("val") public abstract String encodeVal();

		 @JsonProperty("val") public abstract void decodeVal(String value);
		 
	 }
	 
	 @JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
	 @JsonInclude(Include.NON_NULL)
	 public abstract class ContractMixIn {
		 
	 }
}
