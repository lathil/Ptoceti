package com.ptoceti.osgi.ebus;

import java.util.concurrent.Future

public interface EbusDriver {

    public static final String EBUS_PORT = "com.ptoceti.osgi.ebusdriver.port";
    public static final String EBUS_ID = "com.ptoceti.osgi.ebusdriver.id";

    Future sendMasterMasterMessage(int destAddress, int primaryCommand, int secondaryCommand, byte[] payload);

    Future<byte[]> sendMasterSlaveMessage(int destAddress, int primaryCommand, int secondaryCommand, byte[] payload );

    Future sendBraodcastMessage(int destAddress, int primaryCommand, int secondaryCommand, byte[] payload);

}
