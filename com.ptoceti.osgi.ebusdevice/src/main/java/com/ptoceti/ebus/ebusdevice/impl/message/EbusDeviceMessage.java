package com.ptoceti.ebus.ebusdevice.impl.message;

import com.ptoceti.osgi.ebus.impl.message.EbusMessage;

import java.util.ArrayList;

public class EbusDeviceMessage  {

    public String name;
    public String pb;
    public String eb;

    public ArrayList<EbusDeviceMessageDataItem> paylod;
    public ArrayList<EbusDeviceMessageDataItem> slaveresponse;
}
