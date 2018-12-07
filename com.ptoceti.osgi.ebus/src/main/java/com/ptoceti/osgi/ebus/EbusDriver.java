package com.ptoceti.osgi.ebus;

import java.util.concurrent.Future;

public interface EbusDriver {

    public static final String EBUS_PORT = "com.ptoceti.osgi.ebusdriver.port";
    public static final String EBUS_ID = "com.ptoceti.osgi.ebusdriver.id";
    public static final String EBUS_LOCKCOUNTER_MAX = "com.ptoceti.osgi.ebusdriver.lockcounter";
    public static final String EBUS_SENDQUEUELENGTH = "com.ptoceti.osgi.ebusdriver.sendqueuelength";

    Future sendMasterMasterMessage(int destAddress, int primaryCommand, int secondaryCommand, byte[] payload);

    Future<byte[]> sendMasterSlaveMessage(int destAddress, int primaryCommand, int secondaryCommand, byte[] payload );

    Future sendBroadcastMessage( int primaryCommand, int secondaryCommand, byte[] payload);

}
