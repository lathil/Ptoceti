package com.ptoceti.osgi.ebus.impl.connection;

import com.ptoceti.osgi.ebus.impl.message.EbusResponse;

public class  EbusResponseListener {

    Object waitResponseLock = new Object();
    EbusResponse response =  null;
    boolean hasResponse = false;
    boolean messageFailed = false;

    public EbusResponseListener(){

    }

    public EbusResponse getResponse() throws  InterruptedException {
        synchronized (waitResponseLock) {
            while (hasResponse == false) {
                waitResponseLock.wait();
            }
        }
        return response;
    }

    protected void setResponse(EbusResponse message){
        synchronized (waitResponseLock){
            response = message;
            hasResponse = true;
            waitResponseLock.notify();
        }
    }

    protected void setMessageFailed(){
        synchronized (waitResponseLock){
            response = null;
            hasResponse = false;
            messageFailed = true;
            waitResponseLock.notify();
        }
    }
}
