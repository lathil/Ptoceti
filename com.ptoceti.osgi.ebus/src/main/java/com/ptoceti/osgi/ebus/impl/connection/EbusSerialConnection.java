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
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEventListener;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;
import gnu.io.SerialPortEvent;
import org.osgi.service.log.LogService;

public class EbusSerialConnection extends EbusConnection implements SerialPortEventListener {

    protected OutputStream outStream;
    protected InputStream inStream;

    protected SerialPort serialPort;
    protected String portName;

    public static final int EBUS_BAUDRATE = 2400;


    public EbusSerialConnection( String portName ) throws Exception {

        Enumeration portList;
        CommPortIdentifier portID;
        boolean hasFoundPort = false;

        this.portName = portName;

        try {

            portID = CommPortIdentifier.getPortIdentifier(portName);
            if( portID.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                // if this is the port we want,
                if( portID.getName().equals( portName )) {
                    // try to get hold of this port. If not successfull within 1 second, throws an exception.
                    serialPort = (SerialPort) portID.open( this.getClass().getName(), (int)1000 );
                    // if the parity bit is not used, the  port is configured to send two stop bits.

                    serialPort.setSerialPortParams(EBUS_BAUDRATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                    // set itself to listen to data available events.
                    serialPort.addEventListener(this);
                    serialPort.notifyOnDataAvailable(true);
                    // get hold of the inputs and outputs streams.
                    inStream = serialPort.getInputStream();
                    outStream = serialPort.getOutputStream();
                    // success ! we got our port !
                    hasFoundPort = true;
                }
            }
            // We did not found the port in the list of ports availables. throw an exception.
            if( hasFoundPort == false ) throw new Exception("Could not find port with name: " + portName + ".");


        } catch ( PortInUseException e) { throw new Exception("Port " + portName + " is already in use."); }
        catch (UnsupportedCommOperationException e) { throw new Exception("Port does not support this operation."); }
        catch (TooManyListenersException e) { throw new Exception("Could not create listener on port: " + portName + "."); }
        catch (IOException e) { throw new Exception("Could not open input or output streams on port: " + portName + "."); }

    }

    public void close() {

        //busListener.stop();
        serialPort.close();
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
    public void serialEvent( SerialPortEvent event ) {

        int nbAvailableBytes = 0;
        byte[] newBytes = new byte[255];

        if( event.getEventType() == SerialPortEvent.DATA_AVAILABLE ) {
            synchronized (bytesInRaw){
                try {
                    nbAvailableBytes = inStream.available();
                    inStream.read( newBytes, 0, nbAvailableBytes);
                    receivedBytes( newBytes, nbAvailableBytes);
                } catch (IOException e ) {}
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
