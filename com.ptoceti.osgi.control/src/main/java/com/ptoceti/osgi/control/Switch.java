package com.ptoceti.osgi.control;

import java.util.Calendar;

/**
 * A command for setting on or off a boolean receiver
 * 
 * @author lor
 *
 */
public class Switch {

	private boolean state = false;
	private long	time;
	private String name;
	
	public Switch(boolean state) {
		this(state, null);
	}
	
	public Switch( boolean state, String name){
		this.setState(state);
		this.setName(name);
		this.setTime(Calendar.getInstance().getTimeInMillis());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
		this.setTime(Calendar.getInstance().getTimeInMillis());
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	
}
