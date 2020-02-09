package com.ptoceti.osgi.ebus.impl.connection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import com.ptoceti.osgi.ebus.impl.Activator;
import com.ptoceti.osgi.ebus.impl.message.EbusMessage;
import com.ptoceti.osgi.ebus.impl.utils.EbusUtils;

import org.osgi.service.log.LogService;
import org.osgi.service.serial.*;

public class EbusSerialConnection extends EbusConnection implements SerialEventListener {

    protected OutputStream outStream;
    protected InputStream inStream;

    protected SerialDevice serialDevice;
    protected String portName;

    public static final int EBUS_BAUDRATE = 2400;


    public EbusSerialConnection(SerialDevice serialDevice, String portName) throws Exception {

        Enumeration portList;
        boolean hasFoundPort = false;

        this.portName = portName;

        try {
            SerialPortConfiguration serialPortConfiguration = new SerialPortConfiguration(EBUS_BAUDRATE,
                    SerialConstants.DATABITS_8, SerialConstants.FLOWCONTROL_NONE, SerialConstants.PARITY_NONE, SerialConstants.STOPBITS_1);
            serialDevice.setConfiguration(serialPortConfiguration);

            // get hold of the inputs and outputs streams.
            inStream = serialDevice.getInputStream();
            outStream = serialDevice.getOutputStream();

        } catch (IOException e) {
            throw new Exception("Could not open input or output streams on port: " + portName + ".");
        }

    }

    public void close() {

        inStream = null;
        outStream = null;
    }

    public String getPortName() {
        return portName;
    }


    /**
     * Serial port's SerialPortEventistener. When a event is produced by the SerialPort class,
     * is is sent to this method. This mechanism is used to listen to incoming bytes on the serial bus.
     * Once the start and end special chars are received, the incoming bytes are packed as a proper frame
     * and passed on to be converted back to binary ( from the hex bytes ).
     *
     * @param event : the SerialPortEvent event.
     */
    public void notifyEvent(SerialEvent event) {

        int nbAvailableBytes = 0;
        byte[] newBytes = new byte[255];

        if (event.getType() == SerialEvent.DATA_AVAILABLE) {
            synchronized (bytesInRaw) {
                try {
                    nbAvailableBytes = inStream.available();
                    inStream.read(newBytes, 0, nbAvailableBytes);
                    receivedBytes(newBytes, nbAvailableBytes);
                } catch (IOException e) {
                }
            }
        }
    }

    @Override
    void sendByte(byte out) throws IOException{
        outStream.write(out);
    }

    @Override
    void sendBytes(byte[] bytesOut) throws IOException {
        outStream.write( bytesOut);
    }



}
