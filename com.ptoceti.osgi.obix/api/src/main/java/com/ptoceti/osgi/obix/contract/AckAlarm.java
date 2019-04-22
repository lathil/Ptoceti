package com.ptoceti.osgi.obix.contract;

import java.io.Serializable;

import com.ptoceti.osgi.obix.object.Abstime;
import com.ptoceti.osgi.obix.object.Contract;
import com.ptoceti.osgi.obix.object.Op;
import com.ptoceti.osgi.obix.object.Str;
import com.ptoceti.osgi.obix.object.Uri;

public class AckAlarm extends Alarm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1427773600346306026L;
	public static final Contract contract = new Contract("obix:AckAlarm");
	
	public AckAlarm() {
		super();
		getIs().addUri(contract.getUris());
		
		Op ack = new Op("ack", AlarmAckIn.contract, AlarmAckOut.contract);
        //ack.setHref(new Uri("uri",AlarmAckResource.baseuri));
		addChildren(ack);
		
	}
	
	public Str getAckUser(){
		return (Str)(this.getChildren("ackUser"));
	}
	
	public void setAckUser(Str ackUser){
		ackUser.setName("ackUser");
		this.addChildren(ackUser);
	}
	
	public Abstime getAckTimestamp(){
		return (Abstime)(this.getChildren("ackTimestamp"));
	}
	
	public void setAckTimestamp(Abstime ackTimestamp){
		ackTimestamp.setName("ackTimestamp");
		this.addChildren(ackTimestamp);
	}
}
