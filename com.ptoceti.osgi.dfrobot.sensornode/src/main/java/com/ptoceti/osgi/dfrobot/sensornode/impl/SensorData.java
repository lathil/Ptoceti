 package com.ptoceti.osgi.dfrobot.sensornode.impl;

import org.osgi.util.measurement.Measurement;

public class SensorData {

	private Integer id;
	
	private String identification;
	
	private String scope;
	
	private Measurement value;
	
	private Integer scale;
	
	private Integer offset;
	
	private String unit;


	public String getScope() {
		return scope;
	}
	
	public void setScope(String scope) {
		this.scope = scope;
	}


	public String getIdentification() {
		return identification;
	}

	public void setIdentification(String identification) {
		this.identification = identification;
	}

	public Measurement getValue() {
		return value;
	}
	
	public void setValue(Measurement value) {
		this.value = value;
	}

	public Integer getScale() {
		return scale;
	}

	public void setScale(Integer scale) {
		this.scale = scale;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}


}
