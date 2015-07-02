package com.ptoceti.osgi.pi.impl;

public class PinConfig {
	
	private String identification;
	
	private String scope;
	
	private boolean digital;
	
	private boolean directionIn;
	
	private Integer pinNumber;
	
	private Integer value;
	
	private Integer lastValue;
	
	private Double scale;
	
	private Double offset;
	
	private String unit;

	public String getIdentification() {
		return identification;
	}

	public void setIdentification(String identification) {
		this.identification = identification;
	}

	public Integer getValue() {
		return value;
	}
	
	public void setValue(Integer value) {
		this.value = value;
	}

	public Double getScale() {
		return scale;
	}

	public void setScale(Double scale) {
		this.scale = scale;
	}

	public Double getOffset() {
		return offset;
	}

	public void setOffset(Double offset) {
		this.offset = offset;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Integer getLastValue() {
		return lastValue;
	}

	public void setLastValue(Integer lastValue) {
		this.lastValue = lastValue;
	}

	public boolean isDigital() {
		return digital;
	}

	public void setDigital(boolean digital) {
		this.digital = digital;
	}

	public boolean isDirectionIn() {
		return directionIn;
	}

	public void setDirectionIn(boolean directionIn) {
		this.directionIn = directionIn;
	}

	public Integer getPinNumber() {
		return pinNumber;
	}

	public void setPinNumber(Integer pinNumber) {
		this.pinNumber = pinNumber;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}


}
