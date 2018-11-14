package com.ptoceti.osgi.ebus.impl.connection;

import com.ptoceti.osgi.ebus.impl.Activator;
import com.ptoceti.osgi.ebus.impl.exception.BadCrcException;
import com.ptoceti.osgi.ebus.impl.message.EbusMessage;
import com.ptoceti.osgi.ebus.impl.message.EbusResponse;
import com.ptoceti.osgi.ebus.impl.utils.EbusUtils;
import org.osgi.service.log.LogService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class EbusConnection {

    // default max value for the lock counter
    public static final int LOCKCOUNTERMAXVALUE = 25;
    // size of the queue for message to be sent on bus
    public static final int ENTRYMESSAGEQUEUESIZE = 10;

    // the list of message to send through the bus
    LinkedBlockingQueue<MessageEntry> messagesQueue = new LinkedBlockingQueue<>(ENTRYMESSAGEQUEUESIZE);
    // the message sender thread attempting to send message if needed
    MessageSender messageSender;
    // handler for received bytes.
    BusListener busListener;

    // buffer receiving full frames
    ByteBuffer bytesIn = ByteBuffer.allocate(256);
    //  buffer for pre-formatted outgoing messages
    ByteBuffer bytesOut = ByteBuffer.allocate(256);
    // buffer byte array to receive bytes comming from the serial link input stream.
    ByteArrayOutputStream bytesInRaw = new ByteArrayOutputStream();

    int lockCounterMaxValue = LOCKCOUNTERMAXVALUE;
    // ebus spec lock counter. indicate when allowed to get hold of bus
    int lockCounter = lockCounterMaxValue;
    // the id of this master node
    byte masterId;
    // flag that indicate that a collision was detected previous attemps to take bus, but alloed to retry
    boolean collisionDetectedPreviousAllowedRetry = false;

    // lock to notify sync message received
    Object syncLock = new Object();
    // sync hcar position when last notified;
    int syncPosition = -1;
    // lock to notify incoming bytes
    Object byteLock = new Object();
    // lock to notify ack incoming byte
    Object ackLock = new Object();
    int ackPosition = -1;
    Object responseLock = new Object();
    int responsePosition = -1;


    void start(byte masterId, int lockCounterMaxValue){
        this.masterId = masterId;
        this.lockCounterMaxValue = lockCounterMaxValue;
        resetLockCounter();
        if( messageSender == null){
            messageSender = new MessageSender();
        }
    }

    /**
     *
     */
    void stop() {
        messageSender.stop();
    }

    /**
     * Send a message through the ebus asynchronously. return a listener that will notify when message is sent and
     * response received.
     *
     * @param message the message to be sent
     * @return EbusResponseListener  a listener that notify when response received.
     */
    public EbusResponseListener addMessageToSend(EbusMessage message) {
        EbusResponseListener listener = new EbusResponseListener();
        messagesQueue.add( new MessageEntry(message, listener));
        return listener;
    }

    /**
     * Called from serial connection handler with received bytes from the connections
     * @param inBytes
     * @param nbToRead
     */
    protected  void receivedBytes( byte[] inBytes, int nbToRead{
        bytesInRaw.write( inBytes, 0, nbToRead);
    }

    /**
     * To be implemented by the extending class that handle serial port.
     * Manage sending a byte through the the connection.
     * @param byteOut
     * @throws IOException
     */
    abstract void sendByte(byte byteOut) throws IOException;

    /**
     * To be implemented by the extending class that handle serial port.
     * Manage sending byte sthrough the the connection.
     * @param bytesOut
     * @throws IOException
     */
    abstract void sendBytes(byte[] bytesOut) throws IOException;

    void notifyAck(int position) {
        synchronized (ackLock){
            ackPosition = position;
            notifyAll();
        }
    }

    int waitForAck() throws InterruptedException {
        synchronized(ackLock) {
            wait();
            return ackPosition;
        }
    }

    int waitForSync() throws  InterruptedException{
        synchronized (syncLock){
            wait();
            return syncPosition;
        }
    }

    void notifySync(int position) {
        synchronized (syncLock){
            syncPosition = position;
            notifyAll();
        }
    }

    void notifyByte() {
        synchronized (byteLock){
            notifyAll();
        }
    }

    void waitForByte() throws InterruptedException {
        synchronized(byteLock) {
            wait();
        }
    }

    void notifyResponse( int position){
        synchronized (responseLock){
            responsePosition = position;
            notifyAll();
        }
    }

    int waiForResponse() throws InterruptedException{
        synchronized (responseLock){
            wait();
            return responsePosition;
        }
    }

    protected void resetLockCounter(){
        this.lockCounter = this.lockCounterMaxValue;
    }

    byte getMasterId(){
        return masterId;
    }

    protected void resetBusListener(){
        bytesIn.reset();
        syncPosition = -1;
        ackPosition = -1;
        resetLockCounter();
    }

    /**
     * Runnable class that attempt to send messages present in the fifo queue.
     * It synchronized itselsf on sync signals comming on the bus.
     *
     * Prior to send a message on the bus, senders must take hold of the bus by arbitration, following a sync
     * byte appearing on the bus, by sending their bus id  and checking that there is no collision.
     *
     * Sender can only send a message once a lock counter reach zero. The lock counter is initialized with the
     * of participant on the bus and decreased each time a sync appear.
     */
    public class MessageSender implements Runnable {

        // the thread that run this
        Thread messageSenderThread;

        MessageSender(){
            messageSenderThread = new Thread(this);
            messageSenderThread.start();
        }
        /**
         * Main loop of the runnable. Checks for incoming sync and send message if there are to send.
         *
         */
        public void run() {

            while(!messageSenderThread.isInterrupted()){
                // continuously wait for a sync byte on the bus
                try {
                    int syncNextPostion = waitForSync();

                    if( lockCounter > 0){
                        lockCounter--;
                    }

                    if( lockCounter == 0 && messagesQueue.size() > 0){
                        sendByte(masterId);
                        waitForByte();
                        byte senderId = bytesIn.get();
                        if( senderId != masterId ){
                            // collision detected
                            if( (senderId & 0x0F) == (masterId & 0x0F)){
                                // priority class matchs that on the bus, another attemps alloxed next auto-sync
                                collisionDetectedPreviousAllowedRetry = true;
                            } else {
                                collisionDetectedPreviousAllowedRetry = false;
                                resetLockCounter();
                            }
                        } else {
                            sendMessage();
                        }
                    }

                } catch ( InterruptedException ex ) {

                } catch ( IOException ex) {

                }

            }
        }

        /**
         * Send a message on the bus. Message is taken from the queue, and dequeud is send correctly.
         *
         * Three types of messages can be sent: broadcast, master to master, master to slave. Only master to slave
         * is expecting a reply. Master to master is expecting only an ack, and broadcast nothing
         *
         */
        private void sendMessage(){
            // get the message at the top of the fifo
            MessageEntry entry = messagesQueue.peek();
            bytesOut.reset();
            bytesOut.put(getMasterId());
            bytesOut.put(EbusUtils.encodeStream(entry.message.toBytes()));
            bytesOut.put(EbusUtils.calculateCRC(bytesOut.array()));

            Activator.log(LogService.LOG_DEBUG, "Sending frame: " + EbusUtils.writeHex(bytesOut.array()));

            try {
                sendBytes(bytesOut.array());

                if( entry.message.isBroadcastMessage()){
                    // if broadcase message just send SYNC
                    sendByte((byte) EbusMessage.SYNC);
                    // notify listener that message is sent
                    entry.listener.setResponse(null);
                    // remove the message at the top of the queue.
                    messagesQueue.remove();
                    // and reset ebus frames flages
                    resetBusListener();
                    return;
                }

            } catch (IOException ex){

            }

            try {
                waitForAck();
                // check if messaga is master to master
                if (entry.message.isMasterToMasterMessage()) {
                    // if yes just send sync - no reply expected
                    sendByte((byte) EbusMessage.SYNC);
                    // notify listener that message is sent.
                    entry.listener.setResponse(null);
                    // remove the message at the top of the queue.
                    messagesQueue.remove();
                    // and reset ebus frames flages
                    resetBusListener();
                    return;
                } else {
                    // message is master to slave. Expect response.
                    int responseStart = waiForResponse();
                    int length = bytesIn.get(responseStart);
                    try {
                        EbusResponse response = EbusResponse.fromBytes(bytesIn.array(), responseStart + 1,
                                length, bytesIn.get(responseStart + length + 1));
                        sendByte((byte) EbusMessage.ACK);
                        sendByte((byte) EbusMessage.SYNC);
                        // notify listener with the response
                        entry.listener.setResponse(response);
                        // remove the message at the top of the queue.
                        messagesQueue.remove();
                        // and reset ebus frames flages
                        resetBusListener();
                    } catch (BadCrcException ex ){
                        // need to try resend
                    }
                }

            } catch (InterruptedException ex) {

            } catch (IOException ex ) {

            }

        }

        /**
         * Stop the message sender thread
         */
        public void stop(){
            messageSenderThread.interrupt();
        }
    }

    /**
     * Incoming messages to send are stored in a fifo queue in such entry. each entry stores the message
     * and a listener to notify when the message is sent.
     *
     */
    public class MessageEntry {

        EbusMessage message;
        EbusResponseListener listener;

        /**
         * Create a queue entry
         * @param message the message to sent
         * @param listener the listener to notify when message is sent.
         */
        MessageEntry(EbusMessage message, EbusResponseListener listener){
            this.message = message;
            this.listener = listener;
        }
    }

    /**
     * Runnable class that listen to incoming bytes on the bus, buffer them on internal  buffer, and notify on
     * important signals : SYNC, ACK , NACK, ...
     *
     *
     */
    public class BusListener implements Runnable {
        // the thread that run this
        Thread listenerThread;
        boolean lastByteSyncEscape = false;
        BusListener(){
            listenerThread = new Thread(this);
            listenerThread.start();
        }

        /**
         * Method implementing the Runnable interface. Get byte from the serial connection and filter for actions.
         *
         */
        public void run() {
            while(!listenerThread.isInterrupted()) {
                try {
                    // get next packet of bytes from the serial input.
                    synchronized( bytesInRaw){
                        if( bytesInRaw.size() <= 0){
                            wait();
                        }
                    }
                    // stored the raw incoming frame in a tempory buffer.
                    byte[] buff = bytesInRaw.toByteArray();
                    // reset the bytesInRaw buffer. this in fact clears it of any previous content.
                    bytesInRaw.reset();

                    // parse received data
                    for( int i = 0; i < buff.length; i++) {
                        byte nextByte = buff[i];
                        if( nextByte == (byte) (EbusMessage.SYNC & 0x00FF)){
                            bytesIn.put(nextByte);
                            notifySync(bytesIn.position() - 1);
                        } else if ( nextByte == (byte)(EbusMessage.ACK & 0x00FF)){
                            bytesIn.put(nextByte);
                            notifyAck(bytesIn.position() -1);
                        } else if ( nextByte == (byte)(EbusMessage.NACK& 0x00FF)){
                            bytesIn.put(nextByte);
                            notifyAck(bytesIn.position() -1);
                        } else if ( nextByte == (byte) (EbusMessage.SYNC_ESCAPE & 0x00FF)) {
                            lastByteSyncEscape = true;
                        } else {
                            //special handling of 0xA9
                            if( lastByteSyncEscape) {
                                if( nextByte == 0x01 ) {
                                    bytesIn.put((byte) (EbusMessage.SYNC & 0x00FF));
                                } else if ( nextByte == 0x00){
                                    bytesIn.put((byte) (EbusMessage.SYNC_ESCAPE & 0x00FF));
                                }
                                lastByteSyncEscape = false;
                            } else {
                                bytesIn.put(nextByte);
                            }
                            notifyByte();
                            // if we received previously an ack, this must be part of a response
                            if( ackPosition > 0 && bytesIn.position() > (ackPosition + 1)){
                                byte messageLenght = bytesIn.get( ackPosition + 1);
                                if(( bytesIn.position() ==  ackPosition + 1 + messageLenght + 1 + 1) ){
                                    // end of message after nn + message bytes + crc + 1
                                    notifyResponse( ackPosition + 1);
                                }
                            }
                        }

                    }

                } catch (InterruptedException ex) {

                }
            }
        }

        /**
         * Stop the bus listener thread
         */
        public void stop(){
            listenerThread.interrupt();
        }
    }
}


