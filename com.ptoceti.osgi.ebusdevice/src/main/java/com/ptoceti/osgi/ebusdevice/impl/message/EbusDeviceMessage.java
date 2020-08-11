package com.ptoceti.osgi.ebusdevice.impl.message;

import java.util.ArrayList;

public class EbusDeviceMessage  {

    public String name;
    public String pb;
    public String eb;

    public ArrayList<EbusDeviceMessageDataItem> paylod;
    public ArrayList<EbusDeviceMessageDataItem> slaveresponse;
}
