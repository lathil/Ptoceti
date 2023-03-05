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
   // BusListener busListener;

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


    public void start(byte masterId, int lockCounterMaxValue){
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
    public void stop() {
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
     *
     * @param inBytes  array of bytes to read from
     * @param nbToRead number of bytes to read form the array
     */
    protected  void receivedBytes( byte[] inBytes, int nbToRead ){
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
            ackLock.notifyAll();
        }
    }

    int waitForAck() throws InterruptedException {
        synchronized(ackLock) {
            while( ackPosition < 0) {
                ackLock.wait();
            }
            return ackPosition;
        }
    }

    int waitForSync() throws  InterruptedException{
        synchronized (syncLock){
            while(syncPosition < 0) {
                syncLock.wait();
            }
            return syncPosition;
        }
    }

    void notifySync(int position) {
        synchronized (syncLock){
            syncPosition = position;
            syncLock.notifyAll();
        }
    }

    void notifyByte() {
        synchronized (byteLock){
            byteLock.notifyAll();
        }
    }

    void waitForByte() throws InterruptedException {
        synchronized(byteLock) {
            byteLock.wait();
        }
    }

    void notifyResponse( int position){
        synchronized (responseLock){
            responsePosition = position;
            responseLock.notifyAll();
        }
    }

    int waiForResponse() throws InterruptedException{
        synchronized (responseLock){
            responseLock.wait();
            return responsePosition;
        }
    }

    byte getMasterId(){
        return masterId;
    }

    /**
     * Reset in buffer and position flags to not detected
     */
    protected void resetBusListener(){
        bytesIn.reset();
        syncPosition = -1;
        ackPosition = -1;
        responsePosition = -1;
    }

    /**
     * Reset the lock counter to default value.
     */
    protected void resetLockCounter(){
        this.lockCounter = this.lockCounterMaxValue;
    }

    /**
     * Check if we can retry sending the message one time
     * Limit is 1 retry allowed
     * @param entry MessageEntry
     */
    protected void handleRetry(MessageEntry entry){
        if( entry.getRetryCount() > 0) {
            // notify we failed to send message
            entry.listener.setMessageFailed();
            // remove from queue
            messagesQueue.remove();
            // and reset ebus frames flags
            resetBusListener();
            resetLockCounter();
        } else {
            // retry one more time
            entry.incrementRetryCount();
            resetBusListener();
        }
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
                    int syncPosition = waitForSync();

                    // if not a sync that follow a collision (SYNC-ADD-AUTOSYNC)
                    if( lockCounter > 0 && syncPosition == 0){
                        lockCounter--;
                    }
                    // got message to send and it's our go
                    if( lockCounter == 0 && messagesQueue.size() > 0){
                        sendByte(masterId);
                        waitForByte();
                        byte senderId = bytesIn.get();
                        if( senderId != masterId ){
                            // collision detected, check if priority class match
                            if( !((senderId & 0x0F) == (masterId & 0x0F))){
                                // priority class does not matchs that on the bus
                                resetLockCounter();
                            }
                        } else {
                            // no collision, .. send message
                            sendMessage();
                        }
                    } else {
                        // nothing to send or not or turn to send, there might be another node starting to talk. listen here
                        // if we want to.
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
        protected void sendMessage(){
            // get the message at the top of the fifo
            MessageEntry entry = messagesQueue.peek();
            bytesOut.reset();
            bytesOut.put(getMasterId());
            bytesOut.put(EbusUtils.encodeStream(entry.message.toBytes()));
            bytesOut.put(EbusUtils.calculateCRC(bytesOut.array()));

            Activator.getLogger().debug("Sending frame: " + EbusUtils.writeHex(bytesOut.array()));

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
                    resetLockCounter();
                    return;
                }

            } catch (IOException ex){
                Activator.getLogger().error("Error sending frame: " + EbusUtils.writeHex(bytesOut.array()) + ", ex: " + ex.toString());
                // retry one more time
                handleRetry(entry);
                return;
            }

            try {
                int ackPosition = waitForAck();
                if( bytesIn.get( ackPosition) == ((byte)EbusMessage.NACK & 0xFF)) {
                    // we got a nack
                    handleRetry(entry);
                    return;
                }
                // check if messaga is master to master
                if (entry.message.isMasterToMasterMessage()) {
                    // if yes just send sync - no reply expected
                    sendByte((byte) EbusMessage.SYNC);
                    // notify listener that message is sent.
                    entry.listener.setResponse(null);
                    // remove the message at the top of the queue.
                    messagesQueue.remove();
                    // and reset ebus frames flags
                    resetBusListener();
                    resetLockCounter();
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
                        resetLockCounter();
                    } catch (BadCrcException ex ){
                        // need to try resend
                        Activator.getLogger().info("Bad Crc received: Check retry sending message");
                        handleRetry(entry);
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
        int retryCount;

        /**
         * Create a queue entry
         * @param message the message to sent
         * @param listener the listener to notify when message is sent.
         */
        MessageEntry(EbusMessage message, EbusResponseListener listener){
            this.message = message;
            this.listener = listener;
            this.retryCount = 0;
        }

        void incrementRetryCount(){
            retryCount++;
        }

        int getRetryCount(){
            return retryCount;
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
                            if( bytesIn.position() >= 2){
                                // if this is not a SYNC-SENDERID-SYNC sync, it is the end of a frame. reset input buffer
                                resetBusListener();
                            }
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


