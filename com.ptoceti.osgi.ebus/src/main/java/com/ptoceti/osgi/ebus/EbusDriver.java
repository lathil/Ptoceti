package com.ptoceti.osgi.ebus;

import com.ptoceti.osgi.ebus.impl.connection.EbusResponseListener;

import java.util.concurrent.Future;

public interface EbusDriver {

    public static final String EBUS_PORT = "com.ptoceti.osgi.ebusdriver.port";
    public static final String EBUS_ID = "com.ptoceti.osgi.ebusdriver.id";
    public static final String EBUS_LOCKCOUNTER_MAX = "com.ptoceti.osgi.ebusdriver.lockcounter";
    public static final String EBUS_SENDQUEUELENGTH = "com.ptoceti.osgi.ebusdriver.sendqueuelength";

    EbusResponseListener sendMasterMasterMessage(int destAddress, int primaryCommand, int secondaryCommand, byte[] payload);

    EbusResponseListener sendMasterSlaveMessage(int destAddress, int primaryCommand, int secondaryCommand, byte[] payload );

    EbusResponseListener sendBroadcastMessage( int primaryCommand, int secondaryCommand, byte[] payload);

}
